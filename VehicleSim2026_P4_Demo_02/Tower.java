import greenfoot.GreenfootImage;
import greenfoot.GreenfootSound;
import greenfoot.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The whole file is originally by me, but improved performance by codex
 */

/**
 * Write a description of class Tower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Tower extends SuperSmoothMover {
    private static final int TANK_FIRE_VOLUME = 32;

    GreenfootImage towerImage;
    protected Vehicle body;
    protected int coolDown;
    protected int angle;
    protected ArrayList<MineDropper> mineDroppers;
    protected MineDropper target;
    protected int fireCoolDown;
    protected int maxCoolDown;
    protected int typeNumber;
    protected int currentTypeNumber;
    protected int maxRotatePerAct;
    protected int scoutModeCnt;

    protected static GreenfootSound [] shootSoundEffects = createSoundEffect();
    protected static int soundEffectIndex = 0;

    protected ArrayList<TankShell> firedShell;
    private int targetSearchCooldown;

    public Tower(Vehicle body, GreenfootImage towerImage, int fireCoolDown, int typeNumber, int maxRotatePerAct) {
        this.body = body;
        this.towerImage = towerImage;

        setImage(towerImage);
        coolDown = 0;
        // max fire cool down
        this.fireCoolDown = fireCoolDown;
        maxCoolDown = fireCoolDown;
        // type of bullets shoot
        this.typeNumber = typeNumber;
        currentTypeNumber = typeNumber;

        this.maxRotatePerAct = maxRotatePerAct;

        // track firedshell status
        firedShell = new ArrayList<TankShell>();
        scoutModeCnt = 0;
        targetSearchCooldown = 0;
    }

    /**
     * Act - do whatever the Tower wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        if (body == null || body.getWorld() == null) {
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
            return;
        }

        // stick tot he body
        setLocation(body.getX(), body.getY());

        if (body == null || body.getWorld() == null || getWorld() == null) {
            return;
        }

        // update the shooting status
        updateGunState();

        if (coolDown > 0) {
            coolDown--;
        }

        // The frequency of detect target
        if (target == null || !target.isAwake() || target.getWorld() == null) {
            if (targetSearchCooldown > 0) {
                targetSearchCooldown--;
                return;
            }
            target = nearestMineDropper();
            targetSearchCooldown = 1;
        }

        if (target == null) {
            return;
        }

        // This part is created by codex
        angle = (int) Math.toDegrees(Math.atan2(target.getY() - getY(), target.getX() - getX()));

        int currentRotation = getRotation();
        int delta = normalizeAngle(angle - currentRotation);
        int rotation = Math.max(-maxRotatePerAct, Math.min(maxRotatePerAct, delta));
        setRotation(currentRotation + rotation);
        // END

        if (coolDown == 0 && Math.abs(delta) <= 10) {
            fire();
            coolDown = fireCoolDown;
            // switch to scout mode after each bullet fired
            toScoutMode();
        }
        if (currentTypeNumber == 3) {
            scoutModeCnt++;
        } else {
            scoutModeCnt = 0;
        }
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
    // cut off

    private void fire() {
        if (getWorld() == null) {
            return;
        }

        // Only tank shooting has sound effect
        if (currentTypeNumber == 2) {
            playSoundEffects(TANK_FIRE_VOLUME);
        }


        int firingAngle = getRotation();
        TankShell shell = new TankShell(firingAngle, body, currentTypeNumber);
        firedShell.add(shell);
        getWorld().addObject(shell, getX(), getY());
    }

    private void updateGunState() {
        boolean hitPublic = false;
        boolean hitMineDropper = false;

        for (int i = 0; i < firedShell.size(); i++) {
            TankShell shell = firedShell.get(i);
            // If the shell does not exist, remove it
            if (shell == null) {
                firedShell.remove(i);
                // decrease the index
                i--;
                continue;
            }

            // if hit public, the next bullet you shoot has a high chance also hit the public .
            if (shell.hitPublic()) {
                hitPublic = true;
                hitMineDropper = false;
            } else if (shell.hitMineDropper()) {
                // if hit mine dropper, the next bullet you shoot has a high chance also hit the mine dropper
                hitMineDropper = true;
                hitPublic = false;

            }

            // The shell shooted is not in this world anymore.
            if (shell.isCooked() || shell.getWorld() == null) {
                firedShell.remove(i);
                i--;
            }
        }

        if (hitPublic) {
            toScoutMode();
            // To scout mode after hit a public
        } else if (hitMineDropper) {
            // To live mode if hit a mine dropper
            toLiveMode();
        }
    }

    private void toScoutMode() {
        if (currentTypeNumber == 3) {
            return;
        }
        currentTypeNumber = 3;
        fireCoolDown = 1;
        coolDown = Math.min(coolDown, fireCoolDown);
    }

    private void toLiveMode() {
        currentTypeNumber = typeNumber;
        fireCoolDown = maxCoolDown;
        coolDown = Math.max(0, fireCoolDown - scoutModeCnt);
        targetSearchCooldown = 0;
    }

    // Find the nearest mine dropper through analytical geometry
    protected MineDropper nearestMineDropper() {
        List<MineDropper> mineDroppers = getWorld().getObjects(MineDropper.class);

        // We can easily go through every Mine Dropper in this world
        // Find the Mine Dropper who has the least distance to the tower.
        // Some idea from Dijkstra's Algorithm
        MineDropper nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MineDropper mineDropper : mineDroppers) {
            if (mineDropper == null || mineDropper.getWorld() == null || !mineDropper.isAwake()) {
                continue;
            }

            // distance = $\sqrt{dx^2 + dy^2}$
            double dx = mineDropper.getX() - getX();
            double dy = mineDropper.getY() - getY();
            double distance = (dx * dx) + (dy * dy);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = mineDropper;
            }
        }

        return nearest;
    }

    protected static GreenfootSound [] createSoundEffect() {
        GreenfootSound [] sounds = new GreenfootSound[25];
        GreenfootSound sound = new GreenfootSound("shooting1.wav");
        for (int i = 0; i < sounds.length; i++) {
            sounds[i] = sound;
        }
        return sounds;
    }

    protected static void playSoundEffects(int volume) {
        GreenfootSound sound = shootSoundEffects[soundEffectIndex];
        sound.setVolume(volume);
        sound.play();
        soundEffectIndex = (soundEffectIndex + 1) % shootSoundEffects.length;
    }
}
