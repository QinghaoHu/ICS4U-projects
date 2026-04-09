import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.World;

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
public abstract class Pedestrian extends SuperSmoothMover {
    protected double speed;
    protected double maxSpeed;
    protected int direction; // direction is always -1 or 1, for moving down or up, respectively
    protected boolean awake;

    protected int health;
    protected int maxHealth;

    protected GreenfootImage image;
    protected String imagePrefix;
    protected int countDown, baseCountDown = 1;
    protected int imageNumber;
    protected GreenfootImage knockDownImage;
    protected Boolean death;

    protected int loseHealthCountDown;
    protected int deathCountDown;
    protected SuperStatBar hpBar;


    public Pedestrian(int direction, String imagePrefix, GreenfootImage knockDownImage, int health) {
        // choose a random speed
        maxSpeed = Math.random() * 2 + 2;
        speed = maxSpeed;
        // start as awake
        awake = true;
        // Keep direction valid even if a student accidentally passes 0 or another value.
        this.direction = direction >= 0 ? 1 : -1;
        // Inital health for each pedestrian is 100
        this.health = health;
        this.maxHealth = health;

        // Set image for the Pedestrian
        this.imagePrefix = imagePrefix;
        setImage(imagePrefix + 0 + ".png");

        if (direction == 1) {
            getImage().rotate(180);
        }

        // The first frame is image 0
        imageNumber = 0;
        // count down for frame change
        countDown = 1;
        // setup knockdown image
        this.knockDownImage = knockDownImage;

        death = false;
        // after died, count down before people disappear
        deathCountDown = 0;

        loseHealthCountDown = 0;
        hpBar = new SuperStatBar(maxHealth, health, this, 30, 8, -32, Color.BLUE, Color.RED, true, Color.WHITE, 1);
    }

    public void addedToWorld(World w) {
        w.addObject(hpBar, getX(), getY());
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

        // 60 seconds after death, body disappear
        if (death) {
            hpBar.update(0);
            deathCountDown--;
            if (deathCountDown == 0) {
                getWorld().removeObject(this);
            }
            return;
        }

        if (getWorld() == null) {
            return;
        }

        // if knockdown, lose 5 health per 60 acts
        if (loseHealthCountDown > 0) {
            loseHealthCountDown--;
        } else if (loseHealthCountDown == 0 && !isAwake()) {
            damage(5);
            loseHealthCountDown = 60;
        }

        hpBar.update(health);

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

            // If repeal by vehicle, sleep for  0.5 second
            ArrayList<Vehicle> p = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);
            if (p.isEmpty()) {
                changeFrame();
            } else {
                sleepFor(30);
            }
        }
    }

    // Improved animation, change frames for pedestrian for each act.
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

    // after health == 0, the pedestrian is die,
    protected void die() {
        health = 0;
        deathCountDown = 100;
        getWorld().removeObject(hpBar);

        // The healthbar become Gray after die
        hpBar = new SuperStatBar(maxHealth, 0, this, 30, 8, -32, Color.BLUE, Color.BLACK, true, Color.WHITE, 1);
        getWorld().addObject(hpBar, 0, 0);

        // if not already knock down, knock down
        if (this.isAwake()) {
            if (Greenfoot.getRandomNumber(1) == 0)
            knockDown(1);
        } else {
            knockDown(-1);
        }
        death = true;
    }

    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    protected void knockDown(int direction) {
        if (death) {
            return;
        }
        speed = 0;
        if (isAwake()) {
            awake = false;
            setImage(knockDownImage);
            loseHealthCountDown = 60;

            // Knock down by certain car.
            if (direction == -1) {
                getImage().rotate(180);
            }
        }
    }

    public void knockDownByCar(int direction) {
        if (death) {
            return;
        }
        speed = 0;

        health -= 50;

        if (health <= 0) {
            die();
            return;
        }

        if (!isAwake()) {
            return;
        }

        awake = false;

        setImage(knockDownImage);

        if (direction == -1) {
            getImage().rotate(180);
        }
    }

    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe() {
        if (death) {
            return;
        }
        speed = maxSpeed;
        awake = true;
        loseHealthCountDown = -1;

        // After heal, switch back to original awake animation
        String imageName = new String(imagePrefix + imageNumber + ".png");
        image = new GreenfootImage(imageName);
        setImage(image);
        health = maxHealth;
        if (this.direction == 1) {
            getImage().rotate(180);
        }
    }

    public boolean isAwake() {
        return awake;
    }

    public void damage(int damageAmount) {
        if (death) {
            return;
        }
        health -= damageAmount;
        if (health <= 0) {
            health = 0;
            die();
        } else if (health <= 50) {
            // if health <=50, knock down
            if (!awake) {
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
