import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Write a description of class laneChecker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class laneChecker extends Actor
{
    /**
     * Act - do whatever the laneChecker wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private int width;

    public laneChecker(int width) {
        this.width = width;
        GreenfootImage image = new GreenfootImage((int)(1.7 * width), 72);
        setImage(image);
    }

    public void act()
    {
        // Add your action code here.
    }

    private ArrayList<Vehicle> getObjects() {
        ArrayList<Vehicle> touchingVehicle = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);
        return  touchingVehicle;
    }

    public boolean isTouching() {
        ArrayList<Vehicle> p = getObjects();
        if (p.isEmpty()) {
            getWorld().removeObject(this);
            return false;
        }
        getWorld().removeObject(this);
        return true;
    }
}
