import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.World;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the abstract superclass for all Vehicles in the simulation.
 * <p>
 * Vehicles move horizontally across the screen in lanes, following traffic rules
 * such as maintaining following distance and adjusting speed based on traffic ahead.
 * All Vehicle subclasses must implement the checkHitPedestrian() method to define
 * their specific interaction with pedestrians.
 *
 * @author Jordan Cohen
 * @version 2023
 */
public abstract class Vehicle extends SuperSmoothMover {
    private static final int VEHICLE_DESTRUCTION_VOLUME = 22;

    protected double maxSpeed;
    protected double speed;
    protected int direction; // 1 = right, -1 = left
    protected boolean moving;
    protected boolean isNew;
    protected int yOffset;
    protected VehicleSpawner origin;
    protected int followingDistance; // extra look-ahead distance when checking traffic ahead
    protected int myLaneNumber;

    protected int health;
    protected int maxHealth;

    protected int [] changingLaneDirection = {1, -1, 1, -1};
    protected int [] changingLaneAngle = {-1, 1, 1, -1};
    protected boolean isChangingLane;
    protected int targetY;
    protected int fromY;
    protected int turnAngle;
    protected int changeLaneCoolDown;
    protected int maxChangeLaneCoolDown;

    protected SuperStatBar hpBar;

    public Vehicle(VehicleSpawner origin, int health) {
        // remember the VehicleSpawner I came from. This includes information
        // about which lane I'm in and which direction I should face
        this.origin = origin;
        moving = true;
        // ask the Spawner that spawned me what my lane number is
        myLaneNumber = origin.getLaneNumber();
        // Determine if this lane is facing towards the right and
        // set the direction accordingly
        if (origin.facesRightward()) { // Right facing vehicles
            direction = 1;
        } else { // left facing Vehicles
            direction = -1;
            // Reverse the image so it appears correct when moving the opposite direction
            getImage().mirrorHorizontally();
        }
        // NOTE: subclass constructors set maxSpeed after calling super(...), so the
        // spawner speed modifier is applied later via initializeSpeed(...).

        isNew = true; // this boolean serves to make sure this Vehicle is only placed in
        // it's starting position once. Vehicles are removed and re-added
        // to the world (instantly, not visibly) by the z-sort, and without this,
        // they would continue to return to their start points.
        this.health = health;
        maxHealth = health;

        isChangingLane = false;
        followingDistance = 5;
        maxChangeLaneCoolDown = 30;

        hpBar = new SuperStatBar(maxHealth, health, this, 30, 8, -32, Color.BLUE, Color.RED, true, Color.WHITE, 1);
    }


    protected abstract boolean checkHitPedestrian();

    /**
     * Set a subclass's base top speed and apply the lane's speed modifier.
     * Call this from Vehicle subclass constructors after super(origin).
     *
     * @param baseMaxSpeed the subclass's intended max speed before lane modifiers
     */
    protected void initializeSpeed(double baseMaxSpeed) {
        maxSpeed = baseMaxSpeed * origin.getSpeedModifier();
        speed = maxSpeed;
    }

    /**
     * This method is called automatically when the Vehicle is added to the World, and places
     * the Vehicle just off screen (centered 100 pixels beyond the center of the lane spawner)
     * so it will appear to roll onto the screen smoothly.
     */
    public void addedToWorld(World w) {
        if (isNew) {
            setLocation(origin.getX() - (direction * 100), origin.getY() - yOffset);
            isNew = false;
            w.addObject(hpBar, getX(), getY());
        }
    }

    /**
     * The superclass Vehicle's act() method. This can be called by a Vehicle subclass object
     * (for example, by a Car) in two ways:
     * - If the Vehicle simply does NOT have a method called public void act(), this will be called
     * instead.
     * - subclass' act() method can invoke super.act() to call this, as is demonstrated here.
     */
    public void act() {
        if (changeLaneCoolDown > 0) changeLaneCoolDown--;
        if (isChangingLane) {
            // if change lane accomplished, turn back the angle, update lane number, set change lane cool down.
            if ((fromY < targetY && this.getY() >= targetY) || (fromY > targetY && this.getY() <= targetY)) {
                isChangingLane = false;
                turn(-turnAngle);
                setLocation(this.getX(), targetY);
                targetY = 0;
                myLaneNumber += changingLaneDirection[myLaneNumber];
                changeLaneCoolDown = maxChangeLaneCoolDown;
            }
        }

        drive();
        if (getWorld() == null) {
            return;
        }
        if (!checkHitPedestrian()) {
            repelPedestrians();
        }
        if (getWorld() == null) {
            return;
        }

        if (checkEdge()) {
            removeObject();
            return;
        }

        if (getWorld() == null) {
            return;
        }

        triggerMines();

        if (getWorld() == null) {
            return;
        }

        crashCar();

        if (getWorld() == null) {
            return;
        }

        hpBar.update(health);
    }

    /**
     * A method used by all Vehicles to check if they are at the edge.
     * <p>
     * Note that this World is set to unbounded (The World's super class is (int, int, int, FALSE) which means
     * that objects should not be stopped from leaving the World. However, this introduces a challenge as there
     * is the potential for objects to disappear off-screen but still be fully acting and thus wasting resources
     * and affecting the simulation even though they are not visible.
     */
    protected boolean checkEdge() {
        if (direction == 1) { // if moving right, check 200 pixels to the right (above max X)
            if (getX() > getWorld().getWidth() + 200) {
                return true;
            }
        } else { // if moving left, check 200 pixels to the left (negative values)
            if (getX() < -200) {
                return true;
            }
        }
        return false;
    }

    // The Repel Pedestrian Experiment - Currently a work in Progress (Feb 2023)
    public void repelPedestrians() {
        List<Pedestrian> pedsTouching = getIntersectingObjects(Pedestrian.class);

        ArrayList<Actor> actorsTouching = new ArrayList<Actor>();

        // this works, but doesn't ignore knocked down Pedestrians
        //actorsTouching.addAll(pedsTouching);
        for (Pedestrian p : pedsTouching) {
            if (p.isAwake()) {
                actorsTouching.add(p);
            }
        }

        pushAwayFromObjects(actorsTouching, 4);
    }

    /**
     * New repel method! Seems to work well. Can be used in both directions, but for now
     * commented out movement on x so players are only "repelled" in a y-direction.
     *
     * @author Mr Cohen
     * @since February 2023
     */
    public void pushAwayFromObjects(ArrayList<Actor> nearbyObjects, double minDistance) {
        // Get the current position of this actor
        int currentX = getX();
        int currentY = getY();

        // Iterate through the nearby objects
        for (Actor object : nearbyObjects) {
            // Get the position and bounding box of the nearby object
            int objectX = object.getX();
            int objectY = object.getY();
            int objectWidth = object.getImage().getWidth();
            int objectHeight = object.getImage().getHeight();

            // Calculate the distance between this actor and the nearby object's bounding oval
            double distance = Math.sqrt(Math.pow(currentX - objectX, 2) + Math.pow(currentY - objectY, 2));

            // Calculate the effective radii of the bounding ovals
            double thisRadius = Math.max(getImage().getWidth() / 2.0, getImage().getHeight() / 2.0);
            double objectRadius = Math.max(objectWidth / 2.0, objectHeight / 2.0);

            // Check if the distance is less than the sum of the radii
            if (distance < (thisRadius + objectRadius + minDistance)) {
                // Calculate the direction vector from this actor to the nearby object
                int deltaX = objectX - currentX;
                int deltaY = objectY - currentY;

                // Calculate the unit vector in the direction of the nearby object
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (length == 0) {
                    continue;
                }
                double unitX = deltaX / length;
                double unitY = deltaY / length;

                // Calculate the amount by which to push the nearby object
                double pushAmount = (thisRadius + objectRadius + minDistance) - distance;

                // Update the position of the nearby object to push it away

                object.setLocation(objectX, objectY + (int) (pushAmount * unitY));

                // 2d version, allows pushing on x and y axis, commented out for now but it works, just not the
                // effect I'm after:
                //object.setLocation(objectX + (int)(pushAmount * unitX), objectY + (int)(pushAmount * unitY));
            }
        }
    }


    /**
     * Method that deals with movement. Speed can be set by individual subclasses in their constructors
     */
    public void drive() {
        // Ahead is a generic vehicle - we don't know what type BUT
        // since every Vehicle "promises" to have a getSpeed() method,
        // we can call that on any vehicle to find out it's speed
        // Three sets of detector for front cars
        // To prevent some extreme cases, I add two extra detector with 2 different distance
        int lookAheadDistance0 = direction * (int) (speed * 10 + getImage().getWidth() / 2 + followingDistance);
        Vehicle aheadVehicle = (Vehicle) getOneObjectAtOffset(lookAheadDistance0, 0, Vehicle.class);

        int lookAheadDistance01 = direction * (int) (speed + getImage().getWidth() / 2 + followingDistance);
        Vehicle aheadVehicle1 = (Vehicle) getOneObjectAtOffset(lookAheadDistance01, 0, Vehicle.class);

        int lookAheadDistance02 = direction * (int) (speed + getImage().getWidth() / 2);
        Vehicle aheadVehicle2 = (Vehicle) getOneObjectAtOffset(lookAheadDistance02, 0, Vehicle.class);

        // five sets of mine detectpr at the front of the car
        Mine aheadMine0 = (Mine) getOneObjectAtOffset(lookAheadDistance0, -getImage().getWidth() / 2, Mine.class);
        Mine aheadMine1 = (Mine) getOneObjectAtOffset(lookAheadDistance0, 0, Mine.class);
        Mine aheadMine2 = (Mine) getOneObjectAtOffset(lookAheadDistance0, getImage().getWidth() / 2, Mine.class);
        Mine aheadMine3 = (Mine) getOneObjectAtOffset(lookAheadDistance0, getImage().getWidth() / 4, Mine.class);
        Mine aheadMine4 = (Mine) getOneObjectAtOffset(lookAheadDistance0, -getImage().getWidth() / 4, Mine.class);

        // if detect mines of car in the front, and with a distance of change lane safety distance
        if (!isChangingLane) {
            if (aheadVehicle != null || aheadMine0 != null || aheadMine1 != null || aheadMine2 != null || aheadMine3 != null || aheadMine4 != null) {
                if (changeLine()) {
                    return;
                }
            }
        }
        

        // stop if the distance is not enough or cannot change lane
        double otherVehicleSpeed = -1;
        if (aheadVehicle != null) {
            otherVehicleSpeed = aheadVehicle.getSpeed();
        }
        if (aheadVehicle1 != null) {
            otherVehicleSpeed = aheadVehicle1.getSpeed();
        }
        if (aheadVehicle2 != null) {
            otherVehicleSpeed = aheadVehicle2.getSpeed();
        }

        // Various things that may slow down driving speed 
        // You can ADD ELSE IF options to allow other 
        // factors to reduce driving speed.

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed) {// Vehicle ahead is slower?
            speed = otherVehicleSpeed;
        } else {
            speed = maxSpeed; // nothing impeding speed, so go max speed
        }
        if (moving) {
            move(speed * direction);
        }
    }

    public void damage(int damageAmount) {
        health -= damageAmount;
        if (health <= 0) {
            getWorld().addObject(new Explosion(5, 10, 120, 1, Color.RED, 30, false, VEHICLE_DESTRUCTION_VOLUME), getX(), getY());
            removeObject();
        }
    }

    // Damage by air strike does not lead to second explosion
    public void damageByAirStrike(int damageAmount) {
        health -= damageAmount;
        if (health <= 0) {
            removeObject();
        }
    }

    protected void triggerMines() {
        ArrayList<Mine> mines = (ArrayList<Mine>) getIntersectingObjects(Mine.class);
        for (Mine mine : mines) {
            if (mine != null && mine.getWorld() != null) {
                mine.explode();
            }
        }
    }

    // When chang lanes, if two car crash together
    protected void crashCar() {
        ArrayList<Vehicle> touchingVehicle = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);
        if (touchingVehicle.isEmpty()) {
            return;
        }
        if (!isChangingLane) {
            return;
        }
        for (Vehicle v : touchingVehicle) {
            getWorld().addObject(new Explosion(1, 10, 50, 0.4, Color.BLUE, 0, false, 0), v.getX(), v.getY());
            v.removeObject();
        }
        getWorld().addObject(new Explosion(1, 10, 50, 0.4, Color.BLUE, 0, false, 0), this.getX(), this.getY());
        removeObject();
    }

    /**
     * An accessor that can be used to get this Vehicle's speed. Used, for example, when a vehicle wants to see
     * if a faster vehicle is ahead in the lane.
     */
    public double getSpeed() {
        if (moving)
            return speed;
        return 0;
    }

    // change lane
    protected boolean changeLine() {
        if (changeLaneCoolDown != 0) {
            return false;
        }
        LaneChecker checker = new LaneChecker(getImage().getWidth());
        getWorld().addObject(checker, this.getX(), this.getY() + changingLaneDirection[myLaneNumber] * 98);

        if (checker.isTouching()) {
            return false;
        }

        targetY = this.getY() + changingLaneDirection[myLaneNumber] * 98;
        isChangingLane = true;

        // make sure the turn complete in 21 acts
        turnAngle = (int)Math.toDegrees(Math.atan2((double)98, speed * 21)) * changingLaneAngle[myLaneNumber];
        turn(turnAngle);
        fromY = this.getY();
        return true;
    }

    public void stop() {
        moving = false;
    }

    public void removeObject() {
        getWorld().removeObject(this);
    }
}
