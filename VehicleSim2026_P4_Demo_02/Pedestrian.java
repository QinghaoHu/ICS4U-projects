import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

import java.util.ArrayList;

/**
 * A Pedestrian that tries to walk across the street.
 * Pedestrians move vertically across lanes, avoiding vehicles when possible.
 * They can be knocked down by vehicles and later healed.
 * <p>
 * NOTE: This class is designed to be converted into a superclass for different
 * types of pedestrians (e.g., Person, Animal, etc.) as part of the student assignment.
 *
 * @author Jordan Cohen
 * @version 2023
 */
public class Pedestrian extends SuperSmoothMover {
    protected double speed;
    protected double maxSpeed;
    protected int direction; // direction is always -1 or 1, for moving down or up, respectively
    protected boolean awake;
    protected int health;
    protected GreenfootImage image;
    protected String imagePrefix;
    protected int countDown, baseCountDown = 1;
    protected int imageNumber;
    protected GreenfootImage knockDownImage;

    public Pedestrian(int direction, String imagePrefix, GreenfootImage knockDownImage) {
        // choose a random speed
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed;
        // start as awake
        awake = true;
        // Keep direction valid even if a student accidentally passes 0 or another value.
        this.direction = direction >= 0 ? 1 : -1;
        // Inital health for each pedestrian is 100
        health = 100;

        // Set image for the Pedestrian
        this.imagePrefix = imagePrefix;
        setImage(imagePrefix + 0 + ".png");

        if (direction == 1) {
            getImage().rotate(180);
        }

        imageNumber = 0;
        countDown = 1;
        this.knockDownImage = knockDownImage;
    }

    /**
     * Act - do whatever the Pedestrian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    @Override
    public void act() {
        if (getWorld() == null) {
            return;
        }
        // Awake is false if the Pedestrian is "knocked down"
        if (awake) {
            // Check in the direction I'm moving vertically for a Vehicle -- and only move if there is no Vehicle in front of me.
            int lookAheadY = (direction * getImage().getHeight() / 2) + (int) (direction * speed);
            int nextY = (int) Math.round(getY() + (speed * direction));
            if (getOneObjectAtOffset(0, lookAheadY, Vehicle.class) == null && canMoveTo(getX(), nextY)) {
                setLocation(getX(), nextY);
            } else if (getOneObjectAtOffset(0, lookAheadY, Vehicle.class) == null) {
                getWorld().removeObject(this);
                return;
            }
            if (direction == -1 && getY() < 100) {
                getWorld().removeObject(this);
                return;
            } else if (direction == 1 && getY() > getWorld().getHeight() - 30) {
                getWorld().removeObject(this);
                return;
            }

            ArrayList<Vehicle> p = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);
            if (p.isEmpty()) {
                changeFrame();
            } else {
                sleepFor(30);
            }
        }
    }

    protected void changeFrame() {
        if (countDown > 0) {
            countDown--;
        } else if (countDown == 0) {
            if (imageNumber == 19) {
                imageNumber = 0;
            } else {
                imageNumber++;
            }
            String imageName = new String(imagePrefix + imageNumber + ".png");
            image = new GreenfootImage(imageName);
            setImage(image);

            if (direction == 1) {
                getImage().rotate(180);
            }

            countDown = baseCountDown;
        }
    }

    public void blowMeAround(double intensity) {
        // to add randomness, every 6 acts on average, do nothing
        // instead of moving with the wind
        if (Greenfoot.getRandomNumber(6) == 0) {
            return;
        }
        int nextX = (int) Math.round(getX() + intensity);
        if (isWindResistant() && canMoveTo(nextX, getY())) {
            setLocation(nextX, getY());
        } else if (isWindResistant()) {
            getWorld().removeObject(this);
        }
    }

    public boolean isWindResistant() {
        return awake;
    }

    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    private void knockDown(int direction) {
        speed = 0;
        awake = false;

        setImage(knockDownImage);

        if (direction == -1) {
            getImage().rotate(180);
        }
    }

    public void knockDownByCar(int direction) {
        speed = 0;
        awake = false;

        health -= 50;

        if (health <= 0) {
            getWorld().removeObject(this);
            return;
        }

        setImage(knockDownImage);

        if (direction == -1) {
            getImage().rotate(180);
        }
    }

    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe() {
        speed = maxSpeed;
        awake = true;
        String imageName = new String(imagePrefix + imageNumber + ".png");
        image = new GreenfootImage(imageName);
        setImage(image);
        health = 100;
        if (this.direction == 1) {
            getImage().rotate(180);
        }
    }

    public boolean isAwake() {
        return awake;
    }

    public void damage(int damageAmount) {
        health -= damageAmount;
        if (health <= 0) {
            getWorld().removeObject(this);
        } else if (health <= 50) {
            if (!isAwake()) {
                return;
            }
            int rand = Greenfoot.getRandomNumber(2);
            if (rand == 0) {
                knockDown(1);
            } else {
                knockDown(-1);
            }
        }
    }

    /**
     * Codex change: allow movement only if the next position does not overlap a
     * blocked pixel in the background collision mask.
     *
     * @param nextX the candidate x-coordinate for the pedestrian center
     * @param nextY the candidate y-coordinate for the pedestrian center
     * @return true if the movement is allowed
     */
    protected boolean canMoveTo(int nextX, int nextY) {
        if (!(getWorld() instanceof VehicleWorld)) {
            return true;
        }
        VehicleWorld world = (VehicleWorld) getWorld();
        return !world.isPedestrianAreaBlocked(nextX, nextY, getImage());
    }
}
