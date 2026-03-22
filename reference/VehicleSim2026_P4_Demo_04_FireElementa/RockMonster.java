import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RockMonster here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RockMonster extends Pedestrian
{
    private GreenfootImage rockImage, noRockImage;
    private boolean isHoldingRock;
    
    public RockMonster (int direction){
        super(direction);
        
        rockImage = new GreenfootImage("rock_02.png");
        noRockImage = new GreenfootImage("rock_01.png");
        setImage(noRockImage);
        
    }
    
    public void act () {
        super.act(); // do the normal Pedestrian things
        
        if (getWorld() == null){
            return;
        }
        
        Rock r = (Rock)getOneIntersectingObject(Rock.class);
        if (r != null && awake && !isHoldingRock){
            isHoldingRock = true;
            setImage(rockImage);
            getWorld().removeObject(r);
        }
    }
    
    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    public void knockDown () {
        if (isHoldingRock){
            return;
        }
        speed = 0;
        setRotation (direction * 90);
        awake = false;
    }
    
    public boolean isWindResistant (){
        return awake && !isHoldingRock;
    }
 
}
