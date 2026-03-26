import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.ArrayList;

public class tank extends Vehicle {
    protected tankTower tanktower;

    public tank(VehicleSpawner origin) {
        super(origin, 300);
        initializeSpeed(1.5 + ((Math.random() * 10) / 9.0));
        tanktower = null;
    }

    @Override
    public void addedToWorld(World w) {
        super.addedToWorld(w);
        if (tanktower == null) {
            tanktower = new tankTower(this);
            w.addObject(tanktower, getX(), getY());
        }
    }

    public void act() {
        super.act();
    }

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
        if (tanktower != null && tanktower.getWorld() != null) {
            tanktower.getWorld().removeObject(tanktower);
        }
        if (getWorld() != null) {
            getWorld().removeObject(this);
        }
    }

}
