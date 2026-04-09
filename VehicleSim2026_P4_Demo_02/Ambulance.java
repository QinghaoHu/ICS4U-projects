import greenfoot.GreenfootImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Write a description of class Ambulance here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Ambulance extends Vehicle {
    private HashMap<Pedestrian, Boolean> hitPedestrians;
    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Ambulance(VehicleSpawner origin) {
        super(origin, 100);
        initializeSpeed(2 + ((Math.random() * 10) / 5.0));

        hitPedestrians = new HashMap<Pedestrian, Boolean>();
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
            if (hitPedestrians.containsKey(p)) {
                continue;
            }

            // Heal peoples (Both civilian and Pedestrians) when touch them
            if (!p.isAwake()) {
                p.healMe();
            } else {
            }
            hitPedestrians.put(p, true);
            return true;
        }
        return false;
    }
}
