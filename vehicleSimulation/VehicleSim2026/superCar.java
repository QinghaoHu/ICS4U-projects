import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A faster vehicle that pauses briefly after hitting a pedestrian.
 */
public class superCar extends Vehicle {
    private static final int STOP_TICKS = 120; // ~2 seconds at 60 acts per second
    private int stopCounter;

    public superCar(VehicleSpawner origin) {
        super(origin);
        initializeSpeed(2.2 + ((Math.random() * 70) / 10.0));
        yOffset = 4;
        followingDistance = 8;
        stopCounter = 0;
    }

    @Override
    public void act() {
        if (stopCounter > 0) {
            moving = false;
            stopCounter--;
        } else {
            moving = true;
        }
        super.act();
    }

    @Override
    public boolean checkHitPedestrian() {
        int hitCheckOffset = direction * ((int)speed + getImage().getWidth() / 2);
        Pedestrian p = (Pedestrian)getOneObjectAtOffset(hitCheckOffset, 0, Pedestrian.class);
        if (p != null && p.isAwake()) {
            p.knockDown();
            stopCounter = STOP_TICKS;
            moving = false;
            return true;
        }
        return false;
    }
}
