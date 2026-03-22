# VehicleSim2026 Handoff

## Summary

This repository is a Greenfoot Java scenario built around a lane-based road simulation with custom gameplay elements:

- `MineCleaner` vehicles drive horizontally and remove mines from lanes.
- `MineDroper` pedestrians walk vertically and may place mines.
- `Cvilian` pedestrians walk vertically without dropping mines.
- `Windstorm` can push pedestrians sideways.
- A background collision mask blocks pedestrians from spawning or moving inside scenery.

The project is mid-development. It is playable in Greenfoot, but it still contains naming inconsistencies, mixed asset conventions, and constructor coupling across pedestrian subclasses.

## How To Run

1. Open the project folder in Greenfoot.
2. Allow Greenfoot to compile the project.
3. Start the scenario with `VehicleWorld`.

Assumptions:

- The project is run inside Greenfoot.
- `project.greenfoot` remains present so class-image mappings continue to work.
- Assets are loaded from the `images/` folder using Greenfoot image lookup rules.

## Runtime Overview

### World

- Start world: `VehicleWorld`
- World size: `1024 x 800`
- World is unbounded
- Background image: `images/background.png`
- Background collision mask: `images/background_collision.png`

### Lane Setup

- `VehicleWorld` creates 4 lanes.
- Traffic is two-way and split at center.
- Lanes `0-1` move leftward.
- Lanes `2-3` move rightward.
- Lane `0` uses a slower speed modifier.

### Spawn Flow

- Vehicles:
  - Each act has a `1 / (laneCount * 5)` chance to attempt a vehicle spawn.
  - A random lane is chosen.
  - Spawn is blocked if the lane spawner is still touching a vehicle.
  - Spawned vehicle type is always `MineCleaner`.

- Pedestrians:
  - Each act has a `1 / 20` chance to attempt a pedestrian spawn.
  - The spawn side is chosen randomly from top or bottom.
  - The world retries up to `MAX_PEDESTRIAN_SPAWN_ATTEMPTS` times to find a valid x-position.
  - Spawned type is selected in `VehicleWorld.createPedestrian(int)`:
    - about 40% `MineDroper`
    - about 60% `Cvilian`
  - The world currently defines `MAX_PEDESTRIANS`, but the limit is not enforced in spawn logic.

## Key Class Responsibilities

### `VehicleWorld`

- Builds road lanes and spawners
- Owns the spawn loop
- Owns pedestrian collision-mask checks
- Chooses which pedestrian subtype to spawn
- Runs z-sorting each act

Primary file for:

- spawn rates
- pedestrian type mix
- lane layout
- blocked-scenery rules

### `Vehicle`

- Base class for all vehicles
- Handles driving, following behavior, edge cleanup, and pedestrian repelling

Important detail:

- Vehicle following logic uses object checks ahead of the current vehicle.
- Bugs around stopping and overlapping are usually rooted here or in a subclass's `act()` flow.

### `MineCleaner`

- Extends `Vehicle`
- Detects nearby mines
- Stops for `sleepCount` acts before removing a mine

Important detail:

- Mine-removal timing and vehicle-following behavior can interact in ways that allow visual overlap if follower logic assumes the front vehicle is still moving.

### `Pedestrian`

- Base class for vertical pedestrian movement
- Stores movement direction, animation prefix, current animation frame, and knockdown image
- Applies shared move logic, animation cycling, and blocked-background checks

Current constructor contract:

- `Pedestrian(int direction, String imagePrefix, GreenfootImage knockDownImage)`

This contract matters because every pedestrian subclass must pass both the animation prefix and the knockdown image.

### `MineDroper`

- Extends `Pedestrian`
- Uses `move/survivor-move_knife_*.png`
- Drops at most one mine per lane per pedestrian instance

### `Cvilian`

- Extends `Pedestrian`
- Uses `civilian-move/civilian-move_*.png`
- Uses `civilianKnockDown.png` for the downed state
- Intended to be a non-mine-dropping pedestrian

### `Windstorm`

- Applies sideways push to pedestrians
- Uses legacy rain naming in some variables even though the gameplay concept is wind

## Asset Conventions

### Soldier / MineDroper Animation

- Folder: `images/move/`
- Naming: `survivor-move_knife_<frame>.png`
- Frame range: `0..19`

### Civilian Animation

- Folder: `images/civilian-move/`
- Naming: `civilian-move_<frame>.png`
- Frame range: `0..19`
- Output size: `45 x 59`
- Source image backup: `images/civilian-move/original/civilian.png`
- Regeneration script: `scripts/generate_civilian_move_frames.py`

### Knockdown Images

- MineDroper: `soilderKnock-removebg.png`
- Cvilian: `civilianKnockDown.png`

## Known Code Realities

- Naming is inconsistent:
  - `MineDroper` is misspelled
  - `Cvilian` is misspelled
  - some rain variable names are used for wind behavior
- Source, assets, Greenfoot metadata, generated docs, and compiled `.class` files live in one folder tree.
- `Pedestrian` is now a stronger base class than before because it owns animation setup, not just movement.
- Constructor coupling is high between `Pedestrian`, `MineDroper`, and `Cvilian`.
- The civilian animation pipeline is script-generated rather than hand-authored.

## Known Risks And Handoff Warnings

- If `Pedestrian` constructor parameters change, every subclass must be updated immediately.
- In Java, `super(...)` must be the first statement in a constructor.
- `MAX_PEDESTRIANS` currently exists as a constant, but does not actually limit spawns.
- `codex.md` should stay shorter than this file; use this file for handoff detail and `codex.md` for session bootstrap.
- Greenfoot-only behavior, especially class image defaults from `project.greenfoot`, can be easy to break if the project is moved outside Greenfoot.

## Recommended Reading Order For A New Maintainer

1. `README.TXT`
2. `HANDOFF.md`
3. `VehicleWorld.java`
4. `Pedestrian.java`
5. `Vehicle.java`
6. `MineDroper.java`
7. `Cvilian.java`
8. `MineCleaner.java`
9. `pedestrian-background-collision.md`

## Safe Next Steps

- If you are adjusting gameplay balance, start in `VehicleWorld.java`.
- If you are adjusting walking or animation behavior, start in `Pedestrian.java` plus the relevant subclass.
- If you are adjusting civilian animation art, rerun `scripts/generate_civilian_move_frames.py`.
- If you are debugging vehicle overlap during mine removal, inspect both `MineCleaner.act()` and `Vehicle.drive()`.
