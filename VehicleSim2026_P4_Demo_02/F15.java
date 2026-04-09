import greenfoot.GreenfootImage;

/**
 * Write a description of class F15 here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class F15 extends SuperSmoothMover {
    GreenfootImage image = new GreenfootImage("f15rm.png");

    public F15() {
        setImage(image);
    }

    public void act() {
        // Add your action code here.
        if (getWorld() == null) {
            return;
        }

        move(-30);

        if (getX() < - getImage().getWidth()) {
            getWorld().removeObject(this);
        }
    }
}
