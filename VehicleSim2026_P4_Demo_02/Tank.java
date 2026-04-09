import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.ArrayList;

public class Tank extends Vehicle {
    protected TankTower tankTower;

    public Tank(VehicleSpawner origin) {
        super(origin, 350);
        initializeSpeed(1.8 + ((Math.random() * 10) / 6.0));
        tankTower = null;
    }

    public void addedToWorld(World w) {
        super.addedToWorld(w);
        if (tankTower == null) {
            tankTower = new TankTower(this);
            w.addObject(tankTower, getX(), getY());
        }
    }

    public void act() {
        super.act();
    }

    // When hit pedestrian in any angle, knock them down.
    protected boolean checkHitPedestrian() {
        ArrayList<Pedestrian> pedestrians = (ArrayList<Pedestrian>) getIntersectingObjects(Pedestrian.class);
        for (Pedestrian p : pedestrians) {
            if (p.getWorld() != null) {
                if (p.isAwake()) {
                    p.knockDownByCar(this.direction);
                }
                p.die();
            }
        }

        if (!pedestrians.isEmpty()) {
            return true;
        }

        return false;
    }

    public void removeObject() {
        if (tankTower != null && tankTower.getWorld() != null) {
            tankTower.getWorld().removeObject(tankTower);
        }
        if (getWorld() != null) {
            getWorld().removeObject(this);
        }
    }

}
