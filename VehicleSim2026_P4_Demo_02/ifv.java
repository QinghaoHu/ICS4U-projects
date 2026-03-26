import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Write a description of class ifv here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ifv extends Vehicle {
    protected ifvTower ifvtower;
    protected HashMap<MineDroper, Boolean> mineDroperCnt;

    public ifv(VehicleSpawner origin) {
        super(origin, 150);
        initializeSpeed(1.5 + ((Math.random() * 10) / 4.0));
        ifvtower = null;
        mineDroperCnt = new HashMap<MineDroper, Boolean>();
    }

    public void addedToWorld(World w) {
        super.addedToWorld(w);
        if (ifvtower == null) {
            ifvtower = new ifvTower(this);
            w.addObject(ifvtower, getX(), getY());
        }
    }
    /**
     * Act - do whatever the ifv wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // Add your action code here.
        super.act();
    }

    protected boolean checkHitPedestrian() {
        int xOffSet = direction * ((int) speed + getImage().getWidth() / 2);
        int[] yOffSets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        for (int i = 0; i < 5; i++) {
            MineDroper p = (MineDroper) getOneObjectAtOffset(xOffSet, yOffSets[i], MineDroper.class);
            if (p != null) {
                if (!mineDroperCnt.containsKey(p)) {
                    if (p.isAwake()) {
                        p.knockDownByCar(direction);
                    } else {
                        p.damage(50);
                    }
                    mineDroperCnt.put(p, true);
                    return true;
                }
            }
        }

        ArrayList<Cvilian> intersectingCvilian = (ArrayList<Cvilian>) getIntersectingObjects(Cvilian.class);
        for (Cvilian civ : intersectingCvilian) {
            if (civ.isAwake()) {
                civ.speed = 0;
                sleepFor(20);
                getWorld().removeObject(civ);
                return true;
            } else {
                civ.sleepFor(20);
                getWorld().removeObject(civ);
                return true;
            }
        }

        return false;
    }
}
