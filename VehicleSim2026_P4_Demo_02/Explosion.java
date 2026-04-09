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
    private int transparency = 255;
    private HashMap<Vehicle, Boolean> vehicleCnt;
    private HashMap<Pedestrian, Boolean> pedestrianCnt;
    private Color explosionColor;
    private int damage;
    private int transparencyDecrease;
    private int sizeIncrease;
    private boolean isAirStrike;

    private static GreenfootSound [] explosionSoundEffects = createSoundEffects() ;
    private static int indexSoundEffect = 0;

    public Explosion(int size, int sizeIncrease, int maxSize, double time, Color explosionColor, int damage, Boolean isAirStrike, int volume) {
        // init size of the explosion
        this.size = size;
        this.explosionColor = explosionColor;

        // Calculate, transpaciency decrease = total transparency / time you want to stay
        int frame = (int)(time * 60);
        transparencyDecrease = 255/frame;
        vehicleCnt = new HashMap<Vehicle, Boolean>();
        pedestrianCnt = new HashMap<Pedestrian, Boolean>();
        this.damage = damage;
        this.maxSize = maxSize;

        this.sizeIncrease = sizeIncrease;

        if (transparencyDecrease == 0) {
            transparencyDecrease = 1;
        }

        this.isAirStrike = isAirStrike;

        if (volume != 0) {
            playSoundEffect(volume);
        }

        updateImage();
    }

    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // Add your action code here.
        size += sizeIncrease;
        transparency -= transparencyDecrease;

        if (size >= maxSize) {
            sizeIncrease = 0;
            damage = 0;
        }

        if (transparency <= 0) {
            getWorld().removeObject(this);
        } else {
            updateImage();
            if (!isAirStrike) {
                impactVehicle();
                impactPedestrian();
            } else {
                impactVehicleByAirStrike();
                impactPedestrianByAirStrike();
            }
        }
    }

    // Expansion animation
    private void updateImage() {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(explosionColor.getRed(), explosionColor.getGreen(), explosionColor.getBlue(), transparency));
        image.fillOval(0, 0, size, size);
        setImage(image);
    }

    private void impactVehicle() {
        ArrayList<Vehicle> intersectingVehicle = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);

        if (intersectingVehicle.isEmpty()) {
            return;
        }

        // if the impact affect vehicle , bring damage to the vehcile
        for (Vehicle x : intersectingVehicle) {
            if (!vehicleCnt.containsKey(x)) {
                x.damage(damage);
                // Use a hash map to remember this vehicle, prevent an explosion damage an object twice
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

    // Air Strike version
    private void impactVehicleByAirStrike() {
        ArrayList<Vehicle> intersectingVehicle = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);

        if (intersectingVehicle.isEmpty()) {
            return;
        }

        for (Vehicle x : intersectingVehicle) {
            if (!vehicleCnt.containsKey(x)) {
                x.damageByAirStrike(damage);
                vehicleCnt.put(x, true);
            }
        }
    }

    //Air strike version
    private void impactPedestrianByAirStrike() {
        ArrayList<Civilian> intersectingCivilian = (ArrayList<Civilian>) getIntersectingObjects(Civilian.class);
        ArrayList<MineDropper> intersectingMineDropper = (ArrayList<MineDropper>) getIntersectingObjects(MineDropper.class);

        for (Civilian x : intersectingCivilian) {
            if (!pedestrianCnt.containsKey(x)) {
                x.damage(damage);
                pedestrianCnt.put(x, true);
            }
        }

        for (MineDropper x : intersectingMineDropper) {
            if (!pedestrianCnt.containsKey(x)) {
                x.damageByAirStrike(damage);
                pedestrianCnt.put(x, true);
            }
        }
    }

    // really basic greenfoot sound technique
    private static GreenfootSound [] createSoundEffects() {
        GreenfootSound [] sounds = new GreenfootSound[30];
        GreenfootSound sound = new GreenfootSound("Explosion1.wav");
        for (int i = 0; i < sounds.length; i++) {
            sounds[i] = sound;
        }
        return sounds;
    }

    private static void playSoundEffect(int volume) {
        GreenfootSound sound = explosionSoundEffects[indexSoundEffect];
        sound.setVolume(volume);
        sound.play();
        indexSoundEffect = (indexSoundEffect + 1) % explosionSoundEffects.length;
    }
}
