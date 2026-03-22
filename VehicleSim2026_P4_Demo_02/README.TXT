VehicleSim2026_P4_Demo_02
=========================

Project Title:
VehicleSim2026_P4_Demo_02

Purpose Of Project:
This is a Greenfoot-based traffic and pedestrian simulation. Vehicles move across
lane-based roads, pedestrians cross vertically, mines can be dropped onto lanes,
and MineCleaner vehicles can stop to remove those mines. The project is being
extended from the original classroom scenario into a custom game/simulation.

Current Version:
2026-03-20 documentation refresh

How To Start This Project:
1. Open the project folder in Greenfoot.
2. Let Greenfoot compile the source.
3. Run the default world `VehicleWorld`.

Authors:
- Original Greenfoot scenario foundation: Jordan Cohen and classroom starter code
- Current project customization: repository owner and subsequent contributors

User Instructions:
- Click `Run` in Greenfoot to start the simulation.
- Vehicles spawn automatically in road lanes.
- Pedestrians spawn automatically from the top or bottom of the map.
- Some pedestrians are `MineDroper` units that can place mines on the road.
- `MineCleaner` vehicles remove mines after stopping on top of them.
- Background obstacles are enforced by `background_collision.png`, so pedestrians
  cannot spawn or move through blocked scenery.

Project Structure:
- `VehicleWorld.java`: world setup, spawning, collision-mask checks, z-sort
- `Vehicle.java`: shared lane-driving logic for vehicles
- `MineCleaner.java`: vehicle that removes mines
- `Pedestrian.java`: shared pedestrian movement and animation base
- `MineDroper.java`: animated mine-dropping pedestrian
- `Cvilian.java`: animated civilian pedestrian
- `Mine.java`: mine actor placed on road lanes
- `Windstorm.java`: world effect that pushes pedestrians sideways
- `images/`: runtime assets
- `scripts/generate_civilian_move_frames.py`: regenerates civilian animation frames

Important Documentation:
- `HANDOFF.md`: primary developer handoff document
- `codex.md`: short project memory for future coding sessions
- `pedestrian-background-collision.md`: notes on collision-mask behavior

Important Notes For Maintainers:
- This is a Greenfoot-first project, not a Maven or Gradle application.
- Greenfoot metadata is stored in `project.greenfoot` and `*.ctxt`.
- Source, compiled classes, assets, and generated docs are all stored together.
- If pedestrian subclasses change, keep their constructors aligned with
  `Pedestrian.java`.
