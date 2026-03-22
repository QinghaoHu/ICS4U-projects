import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class Explosion here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Explosion extends Effect
{
    private GreenfootImage image;
    private int duration, actsLeft;
    private int diameter;
    
    
    
    public Explosion (int diameter) {
        duration = 120;
        actsLeft = duration;
        this.diameter = diameter;
        image = new GreenfootImage (diameter, diameter);
        setImage(image);
        drawExplosion (image, (double)actsLeft/duration);
    }
    
    private void drawExplosion (GreenfootImage image, double percent){
        image.clear();
        Color drawColor = new Color (255, 0, 0, (int)(255 * percent));
        image.setColor(drawColor);
        image.fillOval (0, 0, image.getWidth() -2, image.getHeight() -2);
        setImage(image);
    }
    
    
    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // if it's the first act
        if (actsLeft == duration){
            World w = getWorld();
            ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);
            for (Vehicle v : vehicles){       
                w.addObject (new Explosion ((int)(diameter * 0.8)),v.getX(), v.getY());
                w.removeObject(v);
            }
        }
        
        actsLeft--;
        drawExplosion (image, (double)actsLeft/duration);
        if (actsLeft == 0){
             getWorld().removeObject(this);   
        }
    }
}
