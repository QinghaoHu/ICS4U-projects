import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class Windstorm here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Windstorm extends Effect
{
    private ArrayList<Pedestrian> pedestrians;
    private GreenfootImage rainImage;
    private int leftBound, rightBound;
    private int direction;
    private double speed;
    
    public Windstorm (){
        
    }
    
    public void addedToWorld (World w){
        int width = w.getWidth();
        int height = w.getHeight();
        rainImage = drawRain (3 * width, height, 1000);
        //new GreenfootImage(3 * width, height);
        setImage(rainImage);
        
        leftBound = -(w.getWidth() / 2);
        rightBound = w.getWidth() + (w.getWidth() / 2);
        
        direction = 1;
        speed = 3.0;
        
    }
    
    
    
    /**
     * Act - do whatever the Windstorm wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
            
        // get list of ALL pedestrians
        pedestrians = (ArrayList<Pedestrian>)getWorld().getObjects(Pedestrian.class);
        for (Pedestrian p : pedestrians){
            p.blowMeAround (direction * speed);
        }
        setLocation (getX() + direction * speed, getY());
        if (Greenfoot.getRandomNumber(60) == 0){
            direction *= -1;
        }
        if (direction == 1 && getX() > rightBound - speed){
            direction *= -1;
        } else if (direction == -1 && getX() < leftBound - speed){
            direction *= -1;
        }
    }
    
    /**
     * density should be 1-100. 100 will be almost completely white
     */
    public static GreenfootImage drawRain (int width, int height, int density){

        Color[] swatch = new Color [32];
        int red = 128;
        int blue = 192;

        // Build a color pallete out of shades of near-white yellow and near-white blue      
        for (int i = 0; i < swatch.length; i++){ // first half blue tones
            swatch[i] = new Color (red, 240, 255);
            red += 2;
        }
        

        // The temporary image, my canvas for drawing
        GreenfootImage temp = new GreenfootImage (width, height);

        // Run this loop one time per "density"

        for (int i = 0; i < density; i++){
            for (int j = 0; j < 100; j++){ // draw 100 circles
                int randSize;
                // Choose a random colour from my swatch, and set its tranparency randomly
                int randColor = Greenfoot.getRandomNumber(swatch.length);
                int randTrans = Greenfoot.getRandomNumber(220) + 35; // around half transparent
                temp.setColor (swatch[randColor]);

                //setTransparency(randTrans);
                // random locations for our dot
                int randX = Greenfoot.getRandomNumber (width);
                int randY = Greenfoot.getRandomNumber (height);

                int tempVal = Greenfoot.getRandomNumber(250);
                if (tempVal >= 1){
                    //randSize = 2;
                    temp.drawRect (randX, randY, 0, 0);
                }else{
                    randSize = Greenfoot.getRandomNumber (2) + 2;
                    temp.fillOval (randX, randY, randSize, randSize);
                }
                // silly way to draw a dot..
            }
        }

        return temp;
    }

}
