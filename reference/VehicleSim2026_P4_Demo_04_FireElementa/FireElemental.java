import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class FireElemental here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FireElemental extends Pedestrian
{
    private GreenfootImage image;
    private boolean isHoldingRock;
    private boolean isWalking;
    private int countdown;

    public FireElemental (int direction){
        super(direction);

        image = new GreenfootImage("fire_elemental.png");

        setImage(image);

        isWalking = true;
        countdown = 0;

    }

    public void act () {
        if (isWalking){
            super.act(); // do the normal Pedestrian things
        }
        System.out.println(countdown + " " + isWalking);
        if (countdown > 0){
            countdown--;
            if (countdown == 0){
                isWalking = true;
            }
        }
        // If I'm alive and not already shooting  - have a small chance to
        // stop and shoot
        if (isWalking && awake && Greenfoot.getRandomNumber(150) == 0){
            shoot();
        }

        if (getWorld() == null){
            return;
        }

    }

    private void shoot () {
        Vehicle v;

        ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>)getObjectsInRange(300, Vehicle.class);
        if (vehicles.size() == 0){
            return; // finish - dont' want to stop
        }

        v = vehicles.get(0);
        getWorld().addObject(new Fireball(v), getX(), getY());

        isWalking = false;

        countdown = 60;
    }

}
