import greenfoot.GreenfootImage;

import java.util.ArrayList;
import java.util.HashMap;

public class MineCleaner extends Vehicle {
    protected static GreenfootImage image;
    private Mine mineToRemove;
    private int sleepCount;
    private HashMap<Pedestrian, Integer> pedestrianCnt;

    public MineCleaner(VehicleSpawner origin) {
        super(origin, 150);
        initializeSpeed(2 + ((Math.random() * 10) / 5.0));
        mineToRemove = null;
        sleepCount = 0;

        pedestrianCnt = new HashMap<Pedestrian, Integer>();
        health = 150;
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }

        if (sleepCount > 0) {
            sleepCount--;
            speed = 0;
            if (sleepCount == 0) {
                finishMineRemoval();
            }
            return;
        }

        super.act();

        if (getWorld() == null) {
            return;
        }

        removeMines();
    }

    public void drive() {
        // Ahead is a generic vehicle - we don't know what type BUT
        // since every Vehicle "promises" to have a getSpeed() method,
        // we can call that on any vehicle to find out it's speed
        int lookAheadDistance0 = direction * (int) (speed * 15 + getImage().getWidth() / 2 + followingDistance);
        int lookAheadDistance1 = direction * (int) (speed + getImage().getWidth() / 2 + followingDistance);
        Vehicle aheadVehicle = (Vehicle) getOneObjectAtOffset(lookAheadDistance0, 0, Vehicle.class);
        Vehicle aheadVehicle1 = (Vehicle) getOneObjectAtOffset(lookAheadDistance1, 0, Vehicle.class);
        int lookAheadDistance02 = direction * (int) (speed + getImage().getWidth() / 2);
        Vehicle aheadVehicle2 = (Vehicle) getOneObjectAtOffset(lookAheadDistance02, 0, Vehicle.class);

        // Similar to vehicle's change lane, but do not change lane to avoid mines.
        double otherVehicleSpeed = -1;
        if (aheadVehicle != null) {
            if (!isChangingLane) {
                if (changeLine()) {
                    return;
                }
            }
        }
        if (aheadVehicle != null) {
            otherVehicleSpeed = aheadVehicle.getSpeed();
        }
        if (aheadVehicle1 != null) {
            otherVehicleSpeed = aheadVehicle1.getSpeed();
        }
        if (aheadVehicle2 != null) {
            otherVehicleSpeed = aheadVehicle2.getSpeed();
        }


        // Various things that may slow down driving speed
        // You can ADD ELSE IF options to allow other
        // factors to reduce driving speed.

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed) { // Vehicle ahead is slower?
            speed = otherVehicleSpeed;
        } else {
            speed = maxSpeed; // nothing impeding speed, so go max speed
        }
        if (moving) {
            move(speed * direction);
        }
    }

    protected boolean checkHitPedestrian() {
        int xOffset = direction * ((int) speed + getImage().getWidth() / 2);
        int[] yOffsets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        for (int i = 0; i < 5; i++) {
            Pedestrian p = (Pedestrian) getOneObjectAtOffset(xOffset, yOffsets[i], Pedestrian.class);
            if (p != null) {
                if (!pedestrianCnt.containsKey(p)) {
                    if (p.isAwake()) {
                        p.knockDownByCar(direction);
                    } else {
                        p.damage(50);
                    }
                    pedestrianCnt.put(p, 1);
                    return true;
                }
            }
        }

        return false;
    }

    // When touch a mine, wait for 0.5 second and remove it.
    protected void removeMines() {
        int xOffset = direction * ((int)getImage().getWidth() / 2);
        int[] yOffsets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        for (int i = 0; i < 5; i++) {
            Mine p = (Mine) getOneObjectAtOffset(xOffset, yOffsets[i], Mine.class);
            if (p != null) {
                moving = false;
                sleepCount = 30;
                speed = 0;
                mineToRemove = p;
                return;
            }
        }
    }

    // After remove mines, back to normal speed.
    private void finishMineRemoval() {
        if (getWorld() == null) {
            return;
        }

        if (mineToRemove != null && mineToRemove.getWorld() != null) {
            getWorld().removeObject(mineToRemove);
        }

        mineToRemove = null;
        moving = true;
        speed = maxSpeed;
    }

    protected void triggerMines() {
        return;
    }
}
