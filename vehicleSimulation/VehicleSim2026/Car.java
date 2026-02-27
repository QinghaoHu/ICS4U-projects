import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The Car subclass represents a standard passenger vehicle.
 * Cars have moderate speed and will knock down pedestrians when they collide.
 *
 * @author Jordan Cohen
 * @version 2023
 */
public class Car extends Vehicle
{
    
    public Car(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        initializeSpeed(1.5 + ((Math.random() * 30) / 5.0));
        yOffset = 4;
        followingDistance = 6;
        
    }

    @Override
    public void act()
    {
        super.act();
        // make car different - put code here
    }

    /**
     * When a Car hit's a Pedestrian, it should knock it over
     */
    @Override
    public boolean checkHitPedestrian () {
        int hitCheckOffset = direction * ((int)speed + getImage().getWidth()/2);
        Pedestrian p = (Pedestrian)getOneObjectAtOffset(hitCheckOffset, 0, Pedestrian.class);
        if (p != null)
        {
            p.knockDown();
            return true;
        }
        return false;
        
    }
}
