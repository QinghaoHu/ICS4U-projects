import greenfoot.GreenfootImage;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Write a description of class Ambulance here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Ambulance extends Vehicle {
    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Ambulance(VehicleSpawner origin) {
        super(origin);
        initializeSpeed(1.5 + ((Math.random() * 10) / 5.0));
        health = 100;
    }

    public void act() {
        // Add your action code here.
        super.act();

        if (getWorld() == null) {
            return;
        }
    }

    protected boolean checkHitPedestrian() {
        ArrayList<Pedestrian> pedestrians = (ArrayList<Pedestrian>) getIntersectingObjects(Pedestrian.class);
        for (Pedestrian p : pedestrians) {
            if (!p.isAwake()) {
                p.healMe();
                return true;
            }
        }
        return false;
    }
}
