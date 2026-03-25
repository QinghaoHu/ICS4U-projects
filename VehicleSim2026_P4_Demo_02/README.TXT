VehicleSim2026_P4_Demo_02
=========================

Project Title:
VehicleSim2026_P4_Demo_02

Purpose Of Project:
This is a Greenfoot-based traffic and pedestrian simulation. The current active
version centers on lane-based `tank` vehicles with independently rotating
`tankTower` actors, vertically moving pedestrians, mine-dropping enemies, and
projectile combat layered onto the original classroom traffic scenario.

Current Version:
2026-03-25 documentation refresh

How To Start This Project:
1. Open the project folder in Greenfoot.
2. Let Greenfoot compile the source.
3. Run the default world `VehicleWorld`.

Authors:
- Original Greenfoot scenario foundation: Jordan Cohen and classroom starter code
- Current project customization: repository owner and subsequent contributors

User Instructions:
- Click `Run` in Greenfoot to start the simulation.
- Tanks spawn automatically in road lanes.
- Each tank owns a separate rotating tower that targets the nearest awake `MineDroper`.
- Tanks fire shell projectiles that can damage both vehicles and pedestrians.
- Pedestrians spawn automatically from the top or bottom of the map.
- Some pedestrians are `MineDroper` units that can place mines on the road.
- Other pedestrians are `Cvilian` units that simply cross the road.
- Background obstacles are enforced by `background_collision.png`, so pedestrians
  cannot spawn or move through blocked scenery.
- An on-screen FPS counter is shown near the top-left.

Project Structure:
- `VehicleWorld.java`: world setup, spawning, collision-mask checks, z-sort, FPS counter
- `Vehicle.java`: shared lane-driving logic for vehicles
- `tank.java`: active spawned combat vehicle
- `tower.java`: base class for vehicle-mounted towers
- `tankTower.java`: targeting, rotation, and firing behavior
- `TankShell.java`: projectile behavior and hit handling
- `Pedestrian.java`: shared pedestrian movement and animation base
- `MineDroper.java`: animated mine-dropping pedestrian
- `Cvilian.java`: animated civilian pedestrian
- `Mine.java`: mine actor placed on road lanes
- `Explosion.java`: area-damage effect spawned by explosions
- `Windstorm.java`: world effect that pushes pedestrians sideways
- `VehicleSpawner.java`: lane spawn points and lane metadata
- `Counter.java`: on-screen numeric display used for FPS
- `TrailPiece.java`: fading shell trail effect
- `SuperSmoothMover.java`: precise-movement base class for moving actors
- `images/`: runtime assets

Important Documentation:
- `HANDOFF.md`: primary developer handoff document
- `codex.md`: short project memory for future coding sessions
- `pedestrian-background-collision.md`: notes on collision-mask behavior

Important Notes For Maintainers:
- This is a Greenfoot-first project, not a Maven or Gradle application.
- Greenfoot metadata is stored in `project.greenfoot` and `*.ctxt`.
- Source, compiled classes, assets, and generated docs are all stored together.
- `MineCleaner.java` and `Ambulance.java` still exist, but they are not currently
  part of the default spawn loop.
