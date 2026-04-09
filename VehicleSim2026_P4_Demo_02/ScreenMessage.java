// This part is created by codex
// A simple screen show warning to the user
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class ScreenMessage extends Effect {
    private int actsLeft;

    public ScreenMessage(String text, int actsLeft) {
        this.actsLeft = actsLeft;
        setImage(buildImage(text));
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }

        actsLeft--;
        if (actsLeft <= 0) {
            getWorld().removeObject(this);
        }
    }

    private GreenfootImage buildImage(String text) {
        GreenfootImage textImage = new GreenfootImage(text, 36, Color.WHITE, new Color(0, 0, 0, 0));
        int paddingX = 24;
        int paddingY = 14;
        GreenfootImage image = new GreenfootImage(textImage.getWidth() + paddingX * 2, textImage.getHeight() + paddingY * 2);
        image.setColor(new Color(20, 20, 20, 180));
        image.fillRect(0, 0, image.getWidth(), image.getHeight());
        image.setColor(new Color(255, 255, 255, 210));
        image.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
        image.drawImage(textImage, paddingX, paddingY);
        return image;
    }
}
// END