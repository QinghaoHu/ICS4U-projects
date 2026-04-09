import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.GreenfootSound;

import java.util.List;

public class MineDropper extends Pedestrian {;
    private static final int DROP_MINE_VOLUME = 34;

    private static final GreenfootSound[] dropMineSounds = createDropMineSounds();
    private static int dropMineSoundIndex = 0;

    private int direction;
    protected boolean [] hasDroppedMine;
    protected int [] lanePosition;

    protected static int mineDropperKilledCount = 0;

    public MineDropper(int direction, int [] lanePosition) {
        super(direction, "move/survivor-move_knife_", new GreenfootImage("soilderKnock-removebg.png"), 100);
        this.direction = direction;
        hasDroppedMine = new boolean[] {false, false, false, false};
        this.lanePosition = lanePosition;
    }

    public void act() {
        super.act();

        if (getWorld() == null) {
            return;
        }

        // Dorp mine only if awake
        if (isAwake()) {
            dropMine();
        }

    }

    private void dropMine() {
        // Do not drop mine on another mine
        List<Mine> mines = getIntersectingObjects(Mine.class);
        if (!mines.isEmpty()) {
            return;
        }

        int randomNumber = Greenfoot.getRandomNumber(100);
        if (randomNumber == 0) {
            for (int i = 0; i < 4; i++) {
                // Only drop mines in the lane, not between the lane
                if (this.getY() >= lanePosition[i] - 38 && this.getY() <= lanePosition[i] + 38 && !hasDroppedMine[i]) {
                    getWorld().addObject(new Mine(), this.getX(), this.getY());
                    playDropMineSound();
                    hasDroppedMine[i] = true;
                    break;
                }
            }
        }
    }

    // Drop Mines sound
    private static GreenfootSound[] createDropMineSounds() {
        GreenfootSound[] sounds = new GreenfootSound[30];
        GreenfootSound sound = new GreenfootSound("dropMineSound.wav");
        for (int i = 0; i < sounds.length; i++) {
            sounds[i] = sound;
            sounds[i].setVolume(DROP_MINE_VOLUME);
        }
        return sounds;
    }

    private static void playDropMineSound() {
        GreenfootSound sound = dropMineSounds[dropMineSoundIndex];
        dropMineSoundIndex = (dropMineSoundIndex + 1) % dropMineSounds.length;
        sound.play();
    }

    public static int getMineDropperKilled() {
        return mineDropperKilledCount;
    }

    public static void resetMineDropperKilled() {
        mineDropperKilledCount = 0;
    }

    protected void die() {
        if (death || getWorld() == null) {
            return;
        }

        // if die
        mineDropperKilledCount++;
        super.die();
    }

    // Die due to airstrike explosion does not count
    protected void dieUnderAirStrike() {
        if (death || getWorld() == null) {
            return;
        }
        super.die();
    }

    // rewrite of damage from pedestrian
    public void damageByAirStrike(int damageAmount) {
        if (death) {
            return;
        }
        health -= damageAmount;
        if (health <= 0) {
            health = 0;
            dieUnderAirStrike();
        } else if (health <= 50) {
            if (!awake) {
                return;
            }
            int rand = Greenfoot.getRandomNumber(2);
            if (rand == 0) {
                knockDown(1);
            } else {
                knockDown(-1);
            }
        }
    }
}
