import greenfoot.GreenfootImage;

import java.util.ArrayList;
import java.util.HashMap;

public class MineCleaner extends Vehicle {
    protected static GreenfootImage image;
    private Mine mineToRemove;
    private int sleepCount;
    HashMap<Pedestrian, Integer> pedestrianCnt;

    public MineCleaner(VehicleSpawner origin) {
        super(origin);
        initializeSpeed(1 + ((Math.random() * 10) / 5.0));
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
            return;
        } else {
            speed = maxSpeed;
        }

        super.act();

        if (getWorld() == null) {
            return;
        }

        if (mineToRemove != null) {
            ArrayList<Mine> mines = (ArrayList<Mine>) getIntersectingObjects(Mine.class);
            for (Mine mine : mines) {
                if (mine == mineToRemove) {
                    getWorld().removeObject(mineToRemove);
                    ;
                    return;
                }
            }

            mineToRemove = null;
        }

        if (getWorld() == null) {
            return;
        }

        removeMines();
    }

    protected boolean checkHitPedestrian() {
        int xOffSet = direction * ((int) speed + getImage().getWidth() / 2);
        int[] yOffSets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        for (int i = 0; i < 5; i++) {
            Pedestrian p = (Pedestrian) getOneObjectAtOffset(xOffSet, yOffSets[i], Pedestrian.class);
            if (p != null) {
                if (!pedestrianCnt.containsKey(p)) {
                    p.damage(50);
                    pedestrianCnt.put(p, 1);
                    return true;
                }
            }
        }

        return false;
    }

    protected void removeMines() {
        int xOffSet = direction * ((int) speed + getImage().getWidth() / 2);
        int[] yOffSets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        for (int i = 0; i < 5; i++) {
            Mine p = (Mine) getOneObjectAtOffset(xOffSet, yOffSets[i], Mine.class);
            if (p != null) {
                sleepCount = 60;
                speed = 0;
                mineToRemove = p;
                return;
            }
        }
    }

    protected void trigerMines() {
        return;
    }
}
