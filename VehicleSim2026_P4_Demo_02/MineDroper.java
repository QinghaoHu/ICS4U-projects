import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

import java.util.List;

public class MineDroper extends Pedestrian {
    private int direction;
    protected boolean [] haveDropedMine;
    protected int [] lanePosition;

    public MineDroper(int direction, int [] lanePosition) {
        super(direction, "move/survivor-move_knife_", new GreenfootImage("soilderKnock-removebg.png"));
        this.direction = direction;
        haveDropedMine = new boolean[] {false, false, false, false};
        this.lanePosition = lanePosition;
    }

    public void act() {
        super.act();

        if (getWorld() == null) {
            return;
        }

        if (isAwake()) {
            dropMine();
        }

    }

    private void dropMine() {
        List<Mine> mines = getIntersectingObjects(Mine.class);
        if (!mines.isEmpty()) {
            return;
        }

        int randomNumber = Greenfoot.getRandomNumber(30);
        if (randomNumber == 0) {
            for (int i = 0; i < 4; i++) {
                if (this.getY() >= lanePosition[i] - 38 && this.getY() <= lanePosition[i] + 38 && !haveDropedMine[i]) {
                    getWorld().addObject(new Mine(), this.getX(), this.getY());
                    haveDropedMine[i] = true;
                    break;
                }
            }
        }
    }
}
