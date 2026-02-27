import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Pedestrian that tries to walk across the street.
 * Pedestrians move vertically across lanes, avoiding vehicles when possible.
 * They can be knocked down by vehicles and later healed.
 *
 * NOTE: This class is designed to be converted into a superclass for different
 * types of pedestrians (e.g., Person, Animal, etc.) as part of the student assignment.
 *
 * @author Jordan Cohen
 * @version 2023
 */
public class Pedestrian extends SuperSmoothMover
{
    private double speed;
    private double maxSpeed;
    private int direction; // direction is always -1 or 1, for moving down or up, respectively
    private boolean awake;

    public Pedestrian(int direction) {
        // choose a random speed
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed;
        // start as awake
        awake = true;
        // Keep direction valid even if a student accidentally passes 0 or another value.
        this.direction = direction >= 0 ? 1 : -1;
    }

    /**
     * Act - do whatever the Pedestrian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    @Override
    public void act()
    {
        if (getWorld() == null){
            return;
        }
        // Awake is false if the Pedestrian is "knocked down"
        if (awake){
            // Check in the direction I'm moving vertically for a Vehicle -- and only move if there is no Vehicle in front of me.
            int lookAheadY = (direction * getImage().getHeight()/2) + (int)(direction * speed);
            if (getOneObjectAtOffset(0, lookAheadY, Vehicle.class) == null){
                setLocation (getX(), getY() + (speed*direction));
            }
            if (direction == -1 && getY() < 100){
                getWorld().removeObject(this);
                return;
            } else if (direction == 1 && getY() > getWorld().getHeight() - 30){
                getWorld().removeObject(this);
                return;
            }

        }
    }

    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    public void knockDown () {
        speed = 0;
        setRotation (direction * 90);
        awake = false;
    }

    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe () {
        speed = maxSpeed;
        setRotation (0);
        awake = true;
    }

    public boolean isAwake () {
        return awake;
    }
}
