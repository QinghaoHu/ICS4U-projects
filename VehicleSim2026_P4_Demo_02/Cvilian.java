import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class cvilian here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Cvilian extends Pedestrian
{
    private int direction;
    /**
     * Act - do whatever the cvilian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Cvilian(int direction) {
        this.direction = direction;
        super(direction, "civilian-move/civilian-move_", new GreenfootImage("civilianKnockDown.png"));
    }

    public void act()
    {
        // Add your action code here.
        super.act();
    }
}
