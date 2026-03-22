import greenfoot.*;
import java.util.*;
/**
 * Write a description of class Explosion here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Explosion extends Actor {
    private int size;
    private int maxSize;
    private int transperency = 255;
    private HashMap<Vehicle, Boolean> vehicleCnt;
    private HashMap<Pedestrian, Boolean> pedestrianCnt;
    private Color explosionColor;
    private int damage;
    private int transperencyDecrease;
    private int sizeIncrease;

    public Explosion(int size, int time, int damage) {
        this.size = size;
        transperencyDecrease = (int)(60 * 255/time);
        this.explosionColor = Color.RED;

        vehicleCnt = new HashMap<Vehicle, Boolean>();
        pedestrianCnt = new HashMap<Pedestrian, Boolean>();
        this.damage = damage;

        updateImage();
    }

    public Explosion(int size, int sizeIncrease, double time, Color explosionColor, int damage) {
        this.size = size;
        this.explosionColor = explosionColor;

        int frame = (int)(time * 60);
        transperencyDecrease = 255/frame;
        vehicleCnt = new HashMap<Vehicle, Boolean>();
        pedestrianCnt = new HashMap<Pedestrian, Boolean>();
        this.damage = damage;

        this.sizeIncrease = sizeIncrease;

        if (transperencyDecrease == 0) {
            transperencyDecrease = 1;
        }

        updateImage();
    }

    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // Add your action code here.
        size += 5;
        transperency -= transperencyDecrease;

        if (transperency <= 0) {
            getWorld().removeObject(this);
        } else {
            updateImage();
            impactVehicle();
            impactPedestrian();
        }
    }

    private void updateImage() {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(explosionColor.getRed(), explosionColor.getGreen(), explosionColor.getBlue(), transperency));
        image.fillOval(0, 0, size, size);
        setImage(image);
    }

    private void impactVehicle() {
        ArrayList<Vehicle> intersectingVehicle = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);

        if (intersectingVehicle.isEmpty()) {
            return;
        }

        for (Vehicle x : intersectingVehicle) {
            if (!vehicleCnt.containsKey(x)) {
                x.damage(damage);
                vehicleCnt.put(x, true);
            }
        }
    }

    private void impactPedestrian() {
        ArrayList<Pedestrian> intersectingPedestrian = (ArrayList<Pedestrian>) getIntersectingObjects(Pedestrian.class);

        if (intersectingPedestrian.isEmpty()) {
            return;
        }

        for (Pedestrian x : intersectingPedestrian) {
            if (!pedestrianCnt.containsKey(x)) {
                x.damage(damage);
                pedestrianCnt.put(x, true);
            }
        }
    }
}
