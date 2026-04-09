import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.util.List;

public class TankShell extends SuperSmoothMover {
    private static final int SHELL_EXPLOSION_VOLUME = 0;

    private int speed;
    private int damage;
    private Color explosionColor;
    private int angle;
    private  Vehicle ownVehicle;
    private int type;
    private boolean ishitPublic;
    private boolean ishitMineDropper;
    private boolean cooked;

    public TankShell(int angle, Vehicle ownerVehicle, int type) {
        this.ownVehicle = ownerVehicle;

        GreenfootImage image = new GreenfootImage(16, 16);
        if (type != 3) {
            image.setColor(new Color(212, 175, 55));
            image.fillRect(6, 5, 4, 8);
            image.setColor(new Color(204, 0, 0));
            image.fillPolygon(new int[] {6, 10, 8}, new int[] {5, 5, 1}, 3);
        }

        setImage(image);
        getImage().rotate(90);
        setRotation(angle);

        this.angle = angle;
        this.type = type;
        ishitPublic = false;
        ishitMineDropper = false;

        cooked = false;

        // type 1: fire by IFV
        // type 2: fire by tank
        // type 3, Reconnaissance Bullets
        if (type == 1) {
            speed = 30;
            damage = 10;
            explosionColor = Color.RED;
        } else if (type == 2){
            speed = 20;
            damage = 100;
            explosionColor = Color.BLACK;
        } else {
            speed = 70;
            // Reconnaissance bullets does not make damage
            damage = 0;
        }
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }

        move(speed);

        if (type != 3) {
            // Performance improved
            getWorld().addObject(new TrailPiece(getImage(), 3, angle), getX(), getY());
        }

        Vehicle hitVehicle = findIntersectingVehicle();
        if (hitVehicle != null) {
            ishitPublic = true;
            ishitMineDropper = false;
            explodeRemove();
            return;
        }

        MineDropper hitMineDropper = (MineDropper) getOneIntersectingObject(MineDropper.class);
        if (hitMineDropper != null && hitMineDropper.isAwake()) {
            ishitMineDropper = true;
            ishitPublic = false;
            explodeRemove();
            return;
        }

        Civilian hitCivilian = (Civilian) getOneIntersectingObject(Civilian.class);
        if (hitCivilian != null && hitCivilian.isAwake()) {
            ishitPublic = true;
            ishitMineDropper = false;
            explodeRemove();
            return;
        }

        if (isAtEdge()) {
            ishitMineDropper = true;
            cooked = true;
            getWorld().removeObject(this);
        }
    }

    public boolean hitPublic() {
        return ishitPublic;
    }

    public boolean hitMineDropper() {
        return ishitMineDropper;
    }

    public boolean isCooked() {
        return cooked;
    }

    // Detect if the shell touch a vehicle.
    private Vehicle findIntersectingVehicle() {
        List<Vehicle> hitVehicles = getIntersectingObjects(Vehicle.class);

        for (Vehicle vehicle : hitVehicles) {
            if (vehicle != ownVehicle) {
                return vehicle;
            }
        }
        return null;
    }

    private void explodeRemove() {
        cooked = true;
        if (getWorld() == null) {
            return;
        }
        if (type == 1) {
            getWorld().addObject(new Explosion(1, 5, 10, 0.2, explosionColor, damage, false, SHELL_EXPLOSION_VOLUME), getX(), getY());
        } else if (type == 2) {
            getWorld().addObject(new Explosion(1, 10, 50, 0.6, explosionColor, damage, false, SHELL_EXPLOSION_VOLUME), getX(), getY());
        }
        getWorld().removeObject(this);
    }
}
