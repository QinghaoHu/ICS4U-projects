# VehicleSim2026_P4_Demo_02

## Purpose

This repository is a Greenfoot Java scenario built around lane-based vehicles and vertically moving pedestrians. The active gameplay has moved to armed `Tank` vehicles with separate rotating `TankTower` actors that target awake `MineDropper` pedestrians.

This file is the short project memory for future coding sessions. For deeper maintainer context, read `HANDOFF.md`.

## Current Runtime Truth

- start world: `VehicleWorld`
- world size: `1024 x 800`
- background: `images/background.png`
- pedestrian collision mask: `images/background_collision.png`
- world is unbounded
- explicit paint order: `Explosion`, `TankShell`, `Tower`, `Vehicle`, `Pedestrian`
- `VehicleWorld` also re-runs z-sort every act for non-`Effect` actors
- an FPS `Counter` is shown on screen

## Active Gameplay

- there are 4 road lanes with split two-way traffic
- vehicle spawning currently creates only `Tank`
- vehicle spawn chance is `1 / (laneCount * 10)`
- pedestrian spawn chance is `1 / 30`
- `VehicleWorld.createPedestrian(int)` currently mixes:
  - about 40% `MineDropper`
  - about 60% `Civilian`
- pedestrian spawn positions are retried up to `MAX_PEDESTRIAN_SPAWN_ATTEMPTS` times if blocked by the collision mask
- `MAX_PEDESTRIANS` is defined but not enforced

## Tank Combat

- `Tank` creates a paired `TankTower` in `addedToWorld(...)`
- `TankTower` tracks the nearest awake `MineDropper`
- Tower turn rate is capped at `3` degrees per act
- firing cooldown is currently `240` acts
- `TankShell` moves at speed `10`, deals `100` damage, leaves a `TrailPiece`, and can hit other vehicles and pedestrians

## Important Source Files

- `VehicleWorld.java`: world setup, lane creation, spawn loop, z-sort, collision-mask checks, FPS counter
- `Vehicle.java`: shared vehicle drive and follow logic
- `Tank.java`: active spawned combat vehicle
- `Tower.java`: Tower follow and target-acquisition base
- `TankTower.java`: rotation and firing logic
- `TankShell.java`: projectile and hit logic
- `Pedestrian.java`: shared pedestrian movement, animation, and damage state
- `MineDropper.java`: mine-dropping pedestrian subclass
- `Civilian.java`: civilian pedestrian subclass

## Asset Notes

- `project.greenfoot` maps `class.Tank.image=tankbody2.png`
- active Tower art is `images/TankTower.png`
- edited Tank art backups are in `images/backup/`
- `TrailPiece` is now active for Tank shell trails

## High-Risk Areas

- `Pedestrian` constructor changes affect every pedestrian subclass
- `Tank.checkHitPedestrian()` removes intersecting pedestrians directly
- `TankShell` friendly fire is intentional current behavior
- Greenfoot metadata and source code both affect runtime behavior

## Known Realities

- naming is inconsistent: `MineDropper`, `Civilian`, `Tank`, and `Tower`
- the project mixes source, compiled classes, metadata, generated docs, and assets in one directory
- `README.TXT` is the quickstart, `HANDOFF.md` is the detailed maintainer guide, and this file should remain short
