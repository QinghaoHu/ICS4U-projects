import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Fireball here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Fireball extends SuperSmoothMover
{
   private GreenfootImage image;
   private Vehicle target;
   private double speed;
   
   
   public Fireball (Vehicle target) {
       image  = drawImage();
       setImage(image);
       
       this.target = target;
       speed = 4.0;
   }
   
   public void act () {
       if (target.getWorld() == null){
           explode();  
           return;
       }
       turnTowards (target.getX(),  target.getY());
       move(speed);
       if (intersects(target)){
          explode();
       }
   }
   
   private void explode () {
        getWorld().addObject(new Explosion(250), getX(), getY());
        getWorld().removeObject(this);
   }
   
   
   private static GreenfootImage drawImage () {
       GreenfootImage image = new GreenfootImage(64, 64);
       image.setColor (Color.ORANGE);
       image.fillOval (0,0, image.getWidth()-2, image.getHeight() - 2);
       return image;
   }
   
   
}
