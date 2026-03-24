# Lane Change Animation Plan

## Summary

This document describes a lane-change animation system for the current Greenfoot vehicle model. It assumes lane eligibility is already handled elsewhere and focuses only on animation and state flow.

The recommended approach is a `steer and settle` lane change:

- vehicles keep their normal horizontal forward movement
- a temporary lane-change state smoothly moves them vertically to the target lane
- the sprite tilts toward the destination lane during the move
- the vehicle settles back to level once aligned with the new lane

This design fits the current project because `Vehicle` already owns shared driving behavior and `VehicleWorld` already exposes lane centers through `getLaneY(...)`.

## Goals

- Preserve the current `Vehicle.drive()` forward-motion model.
- Add lane-change animation without rewriting traffic movement.
- Make the feature reusable by both `MineCleaner` and `Ambulance`.
- Keep implementation in the shared `Vehicle` base class as much as possible.
- Use lane centers from `VehicleWorld.getLaneY(int lane)`.
- Assume a separate `canChangeLane(...)` or equivalent function already exists.

## Recommended Behavior

Use a fixed-duration maneuver of about `18-24` acts total.

The animation should run in three phases:

1. `start steer`
   The vehicle begins the lane change, records source and target lane data, and starts tilting toward the destination lane.

2. `cross`
   The vehicle keeps moving forward normally while its `y` position eases toward the target lane center.

3. `settle`
   The vehicle reduces the tilt back to zero, snaps exactly onto the lane center, and clears the lane-change state.

Lock these rules:

- Horizontal motion continues during the entire lane change.
- Vertical motion is animation-driven and does not replace the current forward drive logic.
- Only one lane change can be active per vehicle at a time.
- `myLaneNumber` does not change immediately when the maneuver starts.
- `myLaneNumber` should switch near the midpoint of the animation, once the vehicle is meaningfully committed to the target lane.
- When the animation ends, the vehicle should be forced exactly onto `getLaneY(targetLane)`.

## Vehicle State To Add

Add lane-change state to `Vehicle`:

- `boolean isChangingLanes`
- `int sourceLane`
- `int targetLane`
- `int laneChangeTicks`
- `int laneChangeDuration`
- `double laneChangeStartY`
- `double laneChangeTargetY`
- `int laneCommitTick`
- `double visualTiltDegrees`

Purpose of the state:

- remember where the vehicle started
- remember which lane it is moving to
- track timing for interpolation
- know when the logical lane ownership should switch
- support sprite tilt during the animation

## Flow Integration

Keep the current shared drive model and layer lane-change animation around it.

Recommended helper methods:

- `beginLaneChange(int newLane)`
- `updateLaneChange()`
- `finishLaneChange()`

Recommended act flow:

1. Check whether a lane change should start.
2. If yes, call `beginLaneChange(...)` once.
3. Run normal forward drive behavior.
4. If `isChangingLanes` is true, run `updateLaneChange()`.
5. If the timer finishes, call `finishLaneChange()`.

Important integration choices:

- Do not allow another lane change request while `isChangingLanes` is true.
- Do not pause or replace forward movement.
- The lane-change layer should only manage vertical transition, tilt, and lane-state commit.

## Movement And Animation Rules

### Vertical Motion

Use eased interpolation for `y`, not constant vertical speed.

Preferred behavior:

- slow start
- faster middle
- soft finish

Any simple ease-in-out curve is acceptable as long as the movement looks less robotic than a fixed diagonal.

### Tilt / Steering Look

Use a small tilt toward the target lane while the vehicle is crossing.

Recommended visual range:

- peak tilt around `6-10` degrees

Recommended tilt timing:

- increase tilt during the first half of the maneuver
- reduce tilt back toward zero during the second half

If Greenfoot image rotation becomes awkward for the existing sprites, use this fallback:

- keep the smooth `y` interpolation
- skip tilt for v1

### Lane Commit

Do not update `myLaneNumber` at the start of the animation.

Instead:

- commit `myLaneNumber = targetLane` around the midpoint
- use a dedicated `laneCommitTick` so the switch happens once and only once

### Finish

At the end of the lane change:

- snap the vehicle exactly to `VehicleWorld.getLaneY(targetLane)`
- clear tilt
- clear lane-change state
- allow future lane changes again

## Occupancy And Safety Rules

During the transition, treat the vehicle as occupying both the source lane and the target lane for traffic-safety logic.

For v1, explicitly choose these defaults:

- no cancellation once the lane change begins
- no stacking of multiple lane changes
- lane-availability logic stays separate from animation logic

This keeps the feature predictable and easier to debug.

## Pseudocode

```text
if not isChangingLanes and canChangeLane(...):
    beginLaneChange(targetLane)

drive forward normally

if isChangingLanes:
    updateLaneChange()
```

Suggested update logic:

```text
updateLaneChange():
    laneChangeTicks++

    progress = laneChangeTicks / laneChangeDuration
    easedProgress = easeInOut(progress)

    newY = interpolate(laneChangeStartY, laneChangeTargetY, easedProgress)
    setLocation(getX(), newY)

    update visual tilt based on progress

    if laneChangeTicks >= laneCommitTick and myLaneNumber != targetLane:
        myLaneNumber = targetLane

    if laneChangeTicks >= laneChangeDuration:
        finishLaneChange()
```

Suggested finish logic:

```text
finishLaneChange():
    setLocation(getX(), world.getLaneY(targetLane))
    clear tilt
    isChangingLanes = false
    sourceLane = targetLane
```

## Public Interface Expectations

This plan does not require a new public API outside the vehicle system.

Expected internal additions are limited to:

- lane-change state fields in `Vehicle`
- helper methods in `Vehicle`
- a call site that triggers lane changes when `canChangeLane(...)` says yes

If needed, `VehicleWorld.getLaneY(...)` remains the source of truth for lane centers.

## Test Plan

Verify these scenarios:

1. A right-moving vehicle changes one lane downward and completes smoothly.
2. A left-moving vehicle changes one lane upward and ends at the correct lane center.
3. The vehicle keeps moving forward while changing lanes.
4. A second lane change cannot start while one is already active.
5. `myLaneNumber` changes once during the maneuver and stays correct afterward.
6. The vehicle ends exactly centered on the destination lane.
7. The slower lane still animates correctly when the vehicle changes in or out of it.
8. Both `MineCleaner` and `Ambulance` reuse the same lane-change behavior path.
9. If tilt is enabled, the vehicle returns to its neutral orientation after the animation.

## Defaults Chosen

- File path stays exactly `planning/lane-change-animatino.md`, preserving the requested spelling.
- The implementation target is the shared `Vehicle` base class.
- The lane change is fixed-duration, not distance-based.
- Horizontal motion continues during the maneuver.
- Logical lane commit happens near the midpoint.
- No cancellation logic is included in v1.
- Smooth `y` interpolation is mandatory; sprite tilt is preferred but may fall back to no tilt if Greenfoot sprite handling becomes messy.
