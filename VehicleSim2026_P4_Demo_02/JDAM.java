/**
 * Write a description of class JDAM here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import greenfoot.*;

import java.util.List;

public class JDAM extends SuperSmoothMover {
    private static final int AIR_STRIKE_EXPLOSION_VOLUME = 18;

    /**
     * Act - do whatever the JDAM wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private Vehicle target;
    private static final int SPEED = 50;

    public JDAM(Vehicle target) {
        this.target = target;

        // This part is created by CodeX
        // Draw the JDAM
        GreenfootImage image = new GreenfootImage(28, 12);
        image.setColor(new Color(80, 80, 80));
        image.fillRect(2, 3, 18, 6);
        image.setColor(new Color(180, 180, 180));
        image.fillPolygon(new int[] {20, 27, 20}, new int[] {2, 6, 10}, 3);
        image.fillRect(6, 0, 4, 12);
        setImage(image);
        // end
    }

    public void act() {
        // Add your action code here.
        if (getWorld() == null) {
            return;
        }

        // If target no longer exist, explode
        if (target == null || target.getWorld() == null) {
            getWorld().addObject(new Explosion(24, 18, 220, 1.0, new Color(255, 170, 60), 0, true, AIR_STRIKE_EXPLOSION_VOLUME), getX(), getY());
            getWorld().removeObject(this);
            return;
        }

        turnTowards(target.getX(), target.getY());
        move(SPEED);

        // If touching the target, explode
        List<Vehicle> intersectingVehicles = getIntersectingObjects(Vehicle.class);
        for (Vehicle v : intersectingVehicles) {
            if (v == target) {
                getWorld().addObject(new Explosion(24, 18, 220, 1.0, Color.RED, 1000, true, AIR_STRIKE_EXPLOSION_VOLUME), getX(), getY());
                getWorld().removeObject(this);
                return;
            }
        }
    }
}
