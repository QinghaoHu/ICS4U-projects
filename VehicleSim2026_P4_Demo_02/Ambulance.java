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
    private HashMap<Pedestrian, Boolean> hittedPedestrians;
    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Ambulance(VehicleSpawner origin) {
        super(origin, 100);
        initializeSpeed(1.5 + ((Math.random() * 10) / 5.0));

        hittedPedestrians = new HashMap<Pedestrian, Boolean>();
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
            if (hittedPedestrians.containsKey(p)) {
                continue;
            }
            if (!p.isAwake()) {
                sleepFor(10);
                p.healMe();
            } else {
//                p.knockDownByCar(this.direction);
            }
            hittedPedestrians.put(p, true);
            return true;
        }
        return false;
    }
}
