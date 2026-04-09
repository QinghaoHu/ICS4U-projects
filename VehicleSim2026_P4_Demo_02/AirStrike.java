import greenfoot.GreenfootImage;
import greenfoot.GreenfootSound;

/**
 * Write a description of class AirStrike here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AirStrike extends Effect {
    private static final int JET_VOLUME = 26;

    /**
     * Act - do whatever the AirStrike wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    protected int delayAfterF15;
    protected int delayBeforeF15;
    private int flyOverY;
    private GreenfootSound jetSound;

    private int phase;
    private F15 f15;

    public AirStrike(int flyOverY, GreenfootSound jetSound) {
        // set acts before f15 come
        delayBeforeF15 = 120;
        // set acts after f15 leave
        delayAfterF15 = 60;

        this.jetSound = jetSound;
        setImage(new GreenfootImage(1, 1));
        this.flyOverY = flyOverY;
        phase = 0;
        jetSound.setVolume(JET_VOLUME);
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }

        // As a start, play music
        if (phase == 0) {
            jetSound.play();
            phase = 1;
        }

        // during phase 1, wait for f15 come
        if (phase == 1) {
            if (delayBeforeF15 > 0) {
                delayBeforeF15--;
                return;
            } else {
                f15 = new F15();
                getWorld().addObject(f15, getWorld().getWidth() + 140, flyOverY);
                phase = 2;
                return;
            }
        }

        // F15 go through the world
        if (phase == 2) {
            if (f15 == null || f15.getWorld() == null) {
                phase = 3;
                return;
            }
        }

        // During phase 3, f15 leave. Wiat for the JDAM bombs come
        if (phase == 3) {
            if (delayAfterF15 > 0) {
                delayAfterF15--;
            } else {
                phase = 4;
                return;
            }
        }

        // JDAM comes, aim for the target
        if (phase == 4) {
            for (Vehicle target : getWorld().getObjects(Vehicle.class)) {
                if (target != null && target.getWorld() != null) {
                    getWorld().addObject(new JDAM(target), getWorld().getWidth() + 40, target.getY());
                }
            }
            phase = 5;
            return;
        }

        // Everything done
        if (phase == 5 && getWorld().getObjects(JDAM.class).isEmpty()) {
            getWorld().removeObject(this);
        }
    }
}
