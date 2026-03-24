import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

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
    private int height;
    private int xPosition;
    private int yPosition;

    public laneChecker(int width, int height, int xPosition, int yPosition) {
        this.width = width;
        this.height = height;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public void act()
    {
        // Add your action code here.
    }

    private ArrayList<Vehicle> getObjects() {
        
    }
}
