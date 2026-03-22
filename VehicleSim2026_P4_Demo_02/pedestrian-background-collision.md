# Pedestrian Background Collision Notes

## Goal

Prevent `Pedestrian` actors from overlapping the stone and decoration shapes that are painted directly into `background.png`.

## Problem With The Old Approach

The previous fix only blocked a few hard-coded spawn rectangles near the top and bottom edges.

That meant:

- pedestrians could avoid some bad spawn positions
- but they could still move into background stones later
- wind could still push them into blocked-looking art

## New Approach

This project now uses a dedicated collision mask image:

- `images/background_collision.png`

The mask matches the background image size.

Rules:

- transparent pixels = walkable
- opaque pixels = blocked for pedestrians
- the middle cactus is intentionally left transparent, so overlap there is allowed

## Code Changes

### `VehicleWorld`

- loads `background_collision.png`
- exposes `isPedestrianAreaBlocked(int centerX, int centerY, GreenfootImage image)`
- uses the mask during pedestrian spawn validation

### `Pedestrian`

- checks the collision mask before normal vertical movement
- checks the collision mask before wind movement in `blowMeAround(...)`
- if the next move would enter a blocked pixel, the pedestrian is removed instead of stopping in place

## Result

Pedestrians now avoid background stone overlap in two cases:

- when spawning
- when moving afterward

If a pedestrian tries to move into a blocked background stone area:

- it is deleted immediately
- this applies to both normal walking and wind push

## Limitation

This change only applies to `Pedestrian` subclasses right now.

Vehicles and other actor types still do not use the background collision mask.
