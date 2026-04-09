import greenfoot.GreenfootImage;

/**
 * Write a description of class Civilian here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Civilian extends Pedestrian {
    private static int civilianKilledCount = 0;
    private int direction;
    private Ifv ifvPickMeUp;

    /**
     * Act - do whatever the Civilian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Civilian(int direction) {
        super(direction, "civilian-move/civilian-move_", new GreenfootImage("civilianKnockDown.png"), 70);
        this.direction = direction;
        speed = Math.random() * 2 + 0.5;
    }

    public static int getCivilianKilledCount() {
        return civilianKilledCount;
    }

    public static void resetCivilianKilledCount() {
        civilianKilledCount = 0;
    }

    public void act() {
        // Add your action code here.
        super.act();

        // If the ifv which intent to pick up civilian disappear, (Maybe be hit, or explode) Pedestrian continue move
        if (ifvPickMeUp != null && ifvPickMeUp.getWorld() == null) {
            speed = Math.random() * 2 + 0.5;
        }
    }

    protected void die() {
        if (death || getWorld() == null) {
            return;
        }

        civilianKilledCount++;
        super.die();
    }

    public void getIfvPickMeUp(Ifv vehicle) {
        ifvPickMeUp = vehicle;
    }
}
