import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.List;

/**
 * The Bus subclass represents a large public transit vehicle.
 * Buses are slower than cars and taller, requiring a y-offset for proper positioning.
 * Currently does not interact with pedestrians (students can implement this).
 *
 * @author Jordan Cohen
 * @version 2023
 */
public class Bus extends Vehicle
{
    private int cnt = 0;
    public Bus(VehicleSpawner origin){
        super(origin); // call the superclass' constructor first

        // Set up values for Bus
        initializeSpeed(1.5 + ((Math.random() * 10) / 5.0));
        // because the Bus graphic is tall, offset it up (this may result in some collision check issues)
        yOffset = 15;
        followingDistance = 10; // Buses need more following distance due to size
    }

    /**
     * Act - do whatever the Bus wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    @Override
    public void act() {
        if (cnt > 0) {
            moving = false;
            cnt--;
        } else {
            moving = true;
        }

        if (checkHitPedestrian()) {
            cnt = 60;
            List<Pedestrian> ped = getIntersectingObjects(Pedestrian.class);
            for (Pedestrian p : ped) {
                getWorld().removeObject(p);
            }
            moving = false;
        }

        super.act();
    }

    /**
     * Check if this Bus hits a Pedestrian. This method is intentionally left empty
     * for students to implement their own pedestrian interaction logic.
     *
     * @return boolean true if a pedestrian was hit, false otherwise
     */
    @Override
    public boolean checkHitPedestrian () {
        // TODO: Students should implement Bus-specific pedestrian interaction here
        Pedestrian hittedPedestrian = (Pedestrian) getOneIntersectingObject(Pedestrian.class);

        if (hittedPedestrian != null) {
            return true;
        }
        return false;
    }
    
    /**
     * Helper for student experiments (for example, bus-stop behavior).
     */
    public void stopMe () {
        moving = false;
        speed = 0;
    }
}
