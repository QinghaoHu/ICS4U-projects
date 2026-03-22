# VehicleSim2026_P4_Demo_02

## Purpose

This repository is a Greenfoot Java scenario built around lane-based vehicles and vertically moving pedestrians. The current gameplay includes mine-dropping pedestrians, mine-removing vehicles, a civilian pedestrian variant, and a background collision mask for blocked scenery.

This file is the short project memory for future coding sessions. For deeper developer context, read `HANDOFF.md`.

## Current Runtime Truth

- Start world: `VehicleWorld`
- World size: `1024 x 800`
- Background: `images/background.png`
- Pedestrian collision mask: `images/background_collision.png`
- World is unbounded
- Paint order: `Effect`, then `Vehicle`, then `Pedestrian`

## Important Source Files

- `VehicleWorld.java`: world setup, lane creation, spawn loop, z-sort, collision-mask checks
- `VehicleSpawner.java`: lane spawn points and lane metadata
- `Vehicle.java`: shared vehicle drive and follow logic
- `MineCleaner.java`: vehicle that stops and removes mines
- `Pedestrian.java`: shared pedestrian movement, animation, and knockdown behavior
- `MineDroper.java`: mine-dropping pedestrian subclass
- `Cvilian.java`: civilian pedestrian subclass
- `Mine.java`: static mine actor
- `Windstorm.java`: pedestrian wind effect
- `SuperSmoothMover.java`: precise movement base class

## Current Gameplay

- There are 4 road lanes with split two-way traffic.
- Vehicles are always `MineCleaner`.
- Each act has a `1 / 20` chance to attempt a vehicle spawn.
- Each act has a `1 / 20` chance to attempt a pedestrian spawn.
- `VehicleWorld.createPedestrian(int)` currently mixes:
  - about 40% `MineDroper`
  - about 60% `Cvilian`
- Pedestrian spawn positions are retried up to `MAX_PEDESTRIAN_SPAWN_ATTEMPTS` times if blocked by the collision mask.
- `MAX_PEDESTRIANS` is defined but not currently enforced.

## Asset Notes

- `MineCleaner` still depends on Greenfoot image metadata in `project.greenfoot`.
- Soldier walk frames live in `images/move/`.
- Civilian walk frames live in `images/civilian-move/`.
- Civilian frames are generated from `images/civilian.png` by `scripts/generate_civilian_move_frames.py`.
- Civilian knockdown art is `images/civilianKnockDown.png`.

## High-Risk Areas

- `Pedestrian` constructor changes affect every pedestrian subclass.
- Constructor order matters: `super(...)` must be the first statement.
- Vehicle overlap bugs during mine removal are most likely in `MineCleaner.act()` plus `Vehicle.drive()`.
- Greenfoot project metadata and source code both affect runtime behavior.

## Reading Guide

- Spawn behavior: `VehicleWorld.java`
- Pedestrian logic or animation: `Pedestrian.java`, then subclass
- Vehicle following or overlap: `Vehicle.java`, then `MineCleaner.java`
- Asset regeneration: `scripts/generate_civilian_move_frames.py`
- Collision-mask behavior: `pedestrian-background-collision.md`

## Known Realities

- Naming is inconsistent: `MineDroper` and `Cvilian` are both misspelled.
- The project mixes source, compiled classes, Greenfoot metadata, generated docs, and assets in one directory.
- `README.TXT` is now the quickstart, `HANDOFF.md` is the detailed maintainer guide, and this file should remain short.
