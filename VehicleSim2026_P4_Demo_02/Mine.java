import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class Mine extends Actor {
    private static final int MINE_EXPLOSION_VOLUME = 14;

    private static GreenfootImage image;

    public Mine() {
        image = new GreenfootImage("landMine.png");
        setImage(image);
    }

    public void act() {

    }

    public void explode() {
        // If trigger, add an explosion, remove the mine.
        getWorld().addObject(new Explosion(10, 30, 110,  2, new Color(255, 69, 0), 50, false, MINE_EXPLOSION_VOLUME), this.getX(), this.getY());
        getWorld().removeObject(this);
    }
}
