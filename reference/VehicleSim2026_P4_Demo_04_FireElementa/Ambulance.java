import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The Ambulance subclass represents an emergency medical vehicle.
 * Ambulances are faster than regular cars and have the potential for special behaviors
 * like emergency lights and healing pedestrians (students can implement these features).
 *
 * @author Jordan Cohen
 * @version 2023
 */
public class Ambulance extends Vehicle
{
    public Ambulance(VehicleSpawner origin){
        super(origin); // call the superclass' constructor first

        initializeSpeed(2.5);
        followingDistance = 8; // Ambulances need more space due to urgency
    }

    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    @Override
    public void act()
    {
        super.act();
        
    }

    /**
     * Check if this Ambulance hits a Pedestrian. This method is intentionally left empty
     * for students to implement their own pedestrian interaction logic.
     *
     * @return boolean true if a pedestrian was hit, false otherwise
     */
    @Override
    public boolean checkHitPedestrian () {
        // TODO: Students should implement Ambulance-specific pedestrian interaction here
        // (Hint: Ambulances might heal pedestrians instead of harming them)
        return false;
    }
}
