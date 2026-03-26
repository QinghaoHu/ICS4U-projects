import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ifvTower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ifvTower extends tower
{
    private static final int MAX_ROTATION_PER_ACT = 3;
    private static int fireCoolDown = 20;

    public ifvTower(Vehicle body) {
        super(body, new GreenfootImage("ifvTower1.png"));
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

        if (target == null || !target.isAwake() || target.getWorld() == null) {
            target = nearestMineDroper();
        }

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
        if (getWorld() == null) {
            return;
        }

        int firingAngle = getRotation();
        TankShell shell = new TankShell(firingAngle, body, 1);
        getWorld().addObject(shell, getX(), getY());
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
