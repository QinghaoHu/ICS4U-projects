import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class Mine extends Actor {
    private static GreenfootImage image;

    public Mine() {
        image = new GreenfootImage("landMine.png");
        setImage(image);
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }
    }

    public void explode() {
        getWorld().addObject(new Explosion(10, 30, 110,  2, new Color(255, 69, 0), 50), this.getX(), this.getY());
        getWorld().removeObject(this);
    }
}
