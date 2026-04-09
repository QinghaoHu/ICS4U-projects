import greenfoot.GreenfootImage;
import greenfoot.GreenfootSound;
import greenfoot.World;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Write a description of class Ifv here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Ifv extends Vehicle {
    private static final int SAVE_CIVILIAN_VOLUME = 24;

    protected IfvTower ifvTower;
    protected HashMap<MineDropper, Boolean> mineDropperCnt;
    protected static int civilianSaved = 0;
    protected Civilian civilianToBeSaved;
    protected int saveCountDown;
    protected static GreenfootSound [] saveCivilianSound = createSoundEffect();
    protected static int soundEffectIndex = 0;

    public Ifv(VehicleSpawner origin) {
        super(origin, 200);
        initializeSpeed(2 + ((Math.random() * 10) / 4.0));
        ifvTower = null;
        mineDropperCnt = new HashMap<MineDropper, Boolean>();
        civilianToBeSaved = null;
        saveCountDown = 0;
    }

    public void addedToWorld(World w) {
        super.addedToWorld(w);
        if (ifvTower == null) {
            ifvTower = new IfvTower(this);
            w.addObject(ifvTower, getX(), getY());
        }
    }
    /**
     * Act - do whatever the Ifv wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        if (civilianToBeSaved != null) {
            moving = false;
        } else {
            moving = true;
        }

        super.act();

        if (getWorld() == null) {
            return;
        }

        if (civilianToBeSaved == null) {
            return;
        }

        if (civilianToBeSaved.getWorld() == null) {
            civilianToBeSaved = null;
            saveCountDown = 0;
            moving = true;
            return;
        }

        if (saveCountDown > 0) {
            saveCountDown--;
            return;
        }

        getWorld().removeObject(civilianToBeSaved);
        civilianToBeSaved = null;
        saveCountDown = 0;
        playSoundEffect();
        civilianSaved++;
        moving = true;
    }

    protected boolean checkHitPedestrian() {
        int xOffset = direction * ((int) speed + getImage().getWidth() / 2);
        int[] yOffsets = {getImage().getHeight() / 2 , getImage().getHeight() / 4, 0, -getImage().getHeight() / 2, -getImage().getHeight() / 2};

        // if hit mine dropper, knock them down
        for (int i = 0; i < 5; i++) {
            MineDropper p = (MineDropper) getOneObjectAtOffset(xOffset, yOffsets[i], MineDropper.class);
            if (p != null) {
                if (!mineDropperCnt.containsKey(p)) {
                    if (p.isAwake()) {
                        p.knockDownByCar(direction);
                    } else {
                        p.damage(50);
                    }
                    mineDropperCnt.put(p, true);
                    return true;
                }
            }
        }

        if (civilianToBeSaved != null) {
            return true;
        }

        // If touch a civilian. stop for 60 seconds, pick them up.
        ArrayList<Civilian> intersectingCivilian = (ArrayList<Civilian>) getIntersectingObjects(Civilian.class);
        for (Civilian civ : intersectingCivilian) {
            if (civ.getWorld() != null) {
                if (civ.isAwake()) {
                    civ.speed = 0;
                }
                civilianToBeSaved = civ;
                saveCountDown = 40;
                moving = false;
                return true;
            }
        }

        return false;
    }

    private static GreenfootSound[] createSoundEffect() {
        GreenfootSound [] soundEffects = new GreenfootSound[6];
        GreenfootSound sound = new GreenfootSound("health.wav");
        for (int i = 0; i < soundEffects.length; i++) {
            soundEffects[i] = sound;
            soundEffects[i].setVolume(SAVE_CIVILIAN_VOLUME);
        }
        return soundEffects;
    }

    private static void playSoundEffect() {
        GreenfootSound sound = saveCivilianSound[soundEffectIndex];
        soundEffectIndex = (soundEffectIndex + 1) % saveCivilianSound.length;
        sound.play();
    }

    public static int getCivilianSaved() {
        return civilianSaved;
    }

    public static void resetCivilianSaved() {
        civilianSaved = 0;
    }

    public void removeObject() {
        if (ifvTower != null && ifvTower.getWorld() != null ) {
            getWorld().removeObject(ifvTower);
        }
        getWorld().removeObject(this);
    }
}
