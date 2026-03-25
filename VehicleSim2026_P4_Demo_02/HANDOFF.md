# VehicleSim2026 Handoff

## Summary

This repository is a Greenfoot Java scenario built around lane-based road traffic and vertically moving pedestrians. The current runtime has shifted away from the older `MineCleaner` / `Ambulance` mix and now primarily spawns armed `tank` vehicles with separate `tankTower` actors.

Current gameplay features:

- `tank` vehicles drive horizontally across the road lanes.
- each `tank` owns a separate `tankTower` actor that rotates independently
- `tankTower` targets the nearest awake `MineDroper`
- `TankShell` projectiles damage both vehicles and pedestrians, so friendly fire is possible
- `MineDroper` pedestrians walk vertically and may place mines
- `Cvilian` pedestrians walk vertically without dropping mines
- `Windstorm` still exists and can push pedestrians sideways, but rain is effectively disabled by configuration
- a background collision mask blocks pedestrians from spawning or moving inside scenery
- an on-screen `Counter` currently displays FPS in the world

The project is still Greenfoot-first and still contains naming inconsistencies, mixed generations of gameplay code, and a mixture of active and dormant gameplay classes.

## How To Run

1. Open the project folder in Greenfoot.
2. Allow Greenfoot to compile the source.
3. Start the scenario with `VehicleWorld`.

Assumptions:

- runtime is Greenfoot, not Maven or Gradle
- `project.greenfoot` remains present so class-image mappings continue to work
- assets are loaded from the `images/` folder using Greenfoot image lookup rules

## Runtime Overview

### World

- start world: `VehicleWorld`
- world size: `1024 x 800`
- world is unbounded
- background image: `images/background.png`
- pedestrian collision mask: `images/background_collision.png`
- paint order: `Explosion`, `TankShell`, `tower`, `Vehicle`, `Pedestrian`
- `VehicleWorld` also re-runs z-sort every act for non-`Effect` actors
- an FPS `Counter` is added near the top-left at `(100, 100)`

### Lane Setup

- `VehicleWorld` creates 4 lanes
- traffic is two-way and split at center
- lanes `0-1` spawn from the right and move leftward
- lanes `2-3` spawn from the left and move rightward
- lane `0` uses a slower speed modifier

### Spawn Flow

- Vehicles:
  - each act has a `1 / (laneCount * 10)` chance to attempt a vehicle spawn
  - a random lane is chosen
  - spawn is blocked if the lane spawner is still touching a vehicle
  - `VehicleWorld.spawn()` currently spawns only `tank`

- Pedestrians:
  - each act has a `1 / 30` chance to attempt a pedestrian spawn
  - spawn side is chosen randomly from top or bottom
  - the world retries up to `MAX_PEDESTRIAN_SPAWN_ATTEMPTS` times to find a valid x-position
  - spawned type is selected in `VehicleWorld.createPedestrian(int)`:
    - about 40% `MineDroper`
    - about 60% `Cvilian`
  - `MAX_PEDESTRIANS` is still defined but not enforced

### Tank Combat Flow

- `tank.addedToWorld(...)` creates a paired `tankTower`
- `tankTower.act()` keeps the tower centered on the tank body
- the tower searches for the nearest awake `MineDroper`
- it rotates toward that target at a maximum of `3` degrees per act
- when its cooldown reaches zero, it fires a `TankShell` in its current facing direction
- `TankShell` leaves a fading `TrailPiece` behind it each act
- shells ignore the firing tank itself but can damage:
  - other `Vehicle` instances
  - any `Pedestrian` instance

## Key Class Responsibilities

### `VehicleWorld`

- builds lanes and spawners
- owns the spawn loop
- owns pedestrian collision-mask checks
- chooses which pedestrian subtype to spawn
- runs z-sorting each act
- owns the FPS counter

Primary file for:

- spawn rates
- active vehicle mix
- pedestrian type mix
- lane layout
- blocked-scenery rules

### `Vehicle`

- base class for all vehicles
- handles driving, following behavior, lane changes, edge cleanup, and mine triggering

Important detail:

- older repo commentary about `MineCleaner` overlap bugs still applies to that class, but it is no longer the default spawned vehicle

### `tank`

- extends `Vehicle`
- owns a `tankTower`
- currently removes intersecting pedestrians outright in `checkHitPedestrian()`

Important detail:

- this class is lower-case (`tank`) rather than Java-standard `Tank`
- it depends on `project.greenfoot` for its default body image mapping

### `tower`

- abstract base class for tower actors
- follows the paired vehicle body
- provides nearest-awake-`MineDroper` target acquisition

### `tankTower`

- extends `tower`
- rotates independently from the tank body
- aims at the nearest awake `MineDroper`
- applies a turn-rate cap of `3` degrees per act
- fires on a cooldown of `240` acts

Important detail:

- firing currently uses the tower center as the shell spawn point, not an offset muzzle point
- the cooldown tuning value is stored as `static int fireCoolDown = 240`

### `TankShell`

- projectile fired by `tankTower`
- uses a procedurally drawn sprite
- moves at speed `10`
- deals `100` damage
- leaves a `TrailPiece`
- can friendly-fire other vehicles and pedestrians

### `Pedestrian`

- base class for vertical pedestrian movement
- stores movement direction, animation prefix, current animation frame, and knockdown image
- applies shared move logic, animation cycling, damage, heal, and blocked-background checks

Current constructor contract:

- `Pedestrian(int direction, String imagePrefix, GreenfootImage knockDownImage)`

This contract matters because every pedestrian subclass must pass both the animation prefix and the knockdown image.

### `MineDroper`

- extends `Pedestrian`
- uses `move/survivor-move_knife_*.png`
- drops at most one mine per lane per pedestrian instance
- is the only tower-targeted pedestrian type

### `Cvilian`

- extends `Pedestrian`
- uses `civilian-move/civilian-move_*.png`
- uses `civilianKnockDown.png` for the downed state

### `Mine`

- static lane obstacle dropped by `MineDroper`
- explodes into an `Explosion` when triggered

### `Explosion`

- expanding damage effect
- damages intersecting `Vehicle` and `Pedestrian` actors
- can chain-trigger nearby mines

### `Windstorm`

- applies sideways push to pedestrians
- still uses legacy rain-related naming internally
- is effectively disabled by current timing (`actsBetweenRain = Integer.MAX_VALUE`)

### `Counter`

- on-screen numeric display actor
- currently used by `VehicleWorld` to show FPS

### `TrailPiece`

- fading afterimage actor
- currently used by `TankShell`

## Asset Conventions

### Tank Assets

- `project.greenfoot` maps `class.tank.image=tankBody.png`
- `images/tankTower.png` is the active tower image
- `images/backup/` stores backups of edited tank art

### Soldier / MineDroper Animation

- folder: `images/move/`
- naming: `survivor-move_knife_<frame>.png`
- frame range: `0..19`

### Civilian Animation

- folder: `images/civilian-move/`
- naming: `civilian-move_<frame>.png`
- frame range: `0..19`
- regeneration script: `scripts/generate_civilian_move_frames.py`

### Knockdown Images

- `MineDroper`: `soilderKnock-removebg.png`
- `Cvilian`: `civilianKnockDown.png`

## Known Code Realities

- naming is inconsistent:
  - `MineDroper` is misspelled
  - `Cvilian` is misspelled
  - `tank` and `tower` are lower-case class names
- source, assets, Greenfoot metadata, generated docs, and compiled `.class` files live in one folder tree
- `Pedestrian` is a stronger base class now because it owns animation setup and damage state
- `MAX_PEDESTRIANS` exists but does not currently limit spawns
- `MineCleaner` and `Ambulance` still exist in the repo but are not currently part of the active spawn loop
- `Counter.java`, `TrailPiece.java`, and related image assets are present and active even though they are newer than the older written docs

## Known Risks And Handoff Warnings

- if `Pedestrian` constructor parameters change, every subclass must be updated immediately
- Greenfoot-only behavior, especially class-image defaults from `project.greenfoot`, can be easy to break if the project is moved outside Greenfoot
- `tank.checkHitPedestrian()` currently removes intersecting pedestrians directly, bypassing the softer knockdown/heal systems used elsewhere
- `TankShell` can damage any pedestrian, not just `MineDroper`, which is intentional current behavior
- shell trails can create many transient actors during heavy tank fire
- tank combat and mine-clearing/healing systems now coexist in the codebase, but only one of those systems is active by default

## Recommended Reading Order For A New Maintainer

1. `README.TXT`
2. `HANDOFF.md`
3. `VehicleWorld.java`
4. `tank.java`
5. `tower.java`
6. `tankTower.java`
7. `TankShell.java`
8. `Pedestrian.java`
9. `MineDroper.java`
10. `pedestrian-background-collision.md`

## Safe Next Steps

- if you are adjusting gameplay balance, start in `VehicleWorld.java`
- if you are adjusting tank combat feel, start in `tankTower.java` and `TankShell.java`
- if you are adjusting tower targeting, start in `tower.java`
- if you are adjusting walking or animation behavior, start in `Pedestrian.java` plus the relevant subclass
- if you are debugging vehicle overlap during mine removal, inspect both `MineCleaner.act()` and `Vehicle.drive()`
- if you are reactivating support-vehicle gameplay, inspect `VehicleWorld.spawn()`, `MineCleaner.java`, and `Ambulance.java`
