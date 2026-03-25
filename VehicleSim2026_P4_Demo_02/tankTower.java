import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.World;

/**
 * Write a description of class tankTower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class tankTower extends tower {
    private static final int MAX_ROTATION_PER_ACT = 3;
    private static int fireCoolDown = 240;
    protected GreenfootImage tankTowerImage = new GreenfootImage("tankTower.png");

    public tankTower(Vehicle body) {
        super(body, new GreenfootImage("tankTower.png"));
    }
    /**
     * Act - do whatever the tankTower wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        super.act();
        if (body == null || body.getWorld() == null || getWorld() == null) {
            return;
        }

        if (coolDown > 0) {
            coolDown--;
        }

        target = nearestMineDroper();
        if (target == null) {
            return;
        }

        angle = (int) Math.toDegrees(Math.atan2(target.getY() - getY(), target.getX() - getX()));

        int currentRotation = getRotation();
        int delta = normalizeAngle(angle - currentRotation);
        int rotation = Math.max(-MAX_ROTATION_PER_ACT, Math.min(MAX_ROTATION_PER_ACT, delta));
        setRotation(currentRotation + rotation);

        if (coolDown == 0) {
            fire();
            coolDown = fireCoolDown;
        }
    }

    private void fire() {
        World world = getWorld();
        if (world == null) {
            return;
        }

        int firingAngle = getRotation();
        TankShell shell = new TankShell(firingAngle, body);
        world.addObject(shell, getX(), getY());
    }

    // This part is created by codex
    private int normalizeAngle(int angle) {
        int normalized = angle % 360;
        if (normalized > 180) {
            normalized -= 360;
        } else if (normalized < -180) {
            normalized += 360;
        }
        return normalized;
    }
}
