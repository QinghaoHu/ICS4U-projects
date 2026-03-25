import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Write a description of class tankTower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class tower extends SuperSmoothMover {
    GreenfootImage towerImage;
    protected Vehicle body;
    protected int coolDown;
    protected int angle;
    protected ArrayList<MineDroper> mineDropers;
    protected MineDroper target;

    public tower(Vehicle body, GreenfootImage towerImage) {
        this.body = body;
        this.towerImage = towerImage;

        setImage(towerImage);
        coolDown = 0;
    }

    /**
     * Act - do whatever the tankTower wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        if (body == null || body.getWorld() == null) {
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
            return;
        }
        setLocation(body.getX(), body.getY());
    }

    protected MineDroper nearestMineDroper() {
        if (getWorld() == null) {
            return null;
        }

        List<MineDroper> mineDroppers = getWorld().getObjects(MineDroper.class);
        MineDroper nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MineDroper mineDroper : mineDroppers) {
            if (mineDroper == null || mineDroper.getWorld() == null || !mineDroper.isAwake()) {
                continue;
            }

            double dx = mineDroper.getX() - getX();
            double dy = mineDroper.getY() - getY();
            double distance = (dx * dx) + (dy * dy);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = mineDroper;
            }
        }

        return nearest;
    }
}
