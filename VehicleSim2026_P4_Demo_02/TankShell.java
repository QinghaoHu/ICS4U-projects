import greenfoot.Color;
import greenfoot.GreenfootImage;

public class TankShell extends SuperSmoothMover {
    private static int speed = 10;
    private static int DAMAGE = 100;
    private int angle;
    private  Vehicle ownerVehicle;

    public TankShell(int angle, Vehicle ownerVehicle) {
        this.ownerVehicle = ownerVehicle;
        GreenfootImage image = new GreenfootImage(16, 16);
        image.setColor(new Color(212, 175, 55));
        image.fillRect(6, 5, 4, 8);
        image.setColor(new Color(204, 0, 0));
        image.fillPolygon(new int[] {6, 10, 8}, new int[] {5, 5, 1}, 3);
        setImage(image);
        getImage().rotate(90);
        setRotation(angle);

        this.angle = angle;
    }

    @Override
    public void act() {
        if (getWorld() == null) {
            return;
        }

        move(speed);

        getWorld().addObject(new TrailPiece(getImage(), 30, angle), getX(), getY());

        Vehicle hitVehicle = findVehicleHit();
        if (hitVehicle != null) {
            hitVehicle.damage(DAMAGE);
            explodeRemove();
            return;
        }

        Pedestrian hitPedestrian = (Pedestrian) getOneIntersectingObject(Pedestrian.class);
        if (hitPedestrian != null) {
            hitPedestrian.damage(DAMAGE);
            explodeRemove();
            return;
        }

        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }

    private Vehicle findVehicleHit() {
        for (Vehicle vehicle : getIntersectingObjects(Vehicle.class)) {
            if (vehicle != ownerVehicle) {
                return vehicle;
            }
        }
        return null;
    }

    private void explodeRemove() {
        getWorld().addObject(new Explosion(1, 10, 50, 0.4, Color.RED, 100), getX(), getY());
        getWorld().removeObject(this);
    }
}
