import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class TrailPiece extends Actor {
    private GreenfootImage image;
    private int actsToFade, actsLeft;
    private static final int START_TRANSPARENCY = 128;

    public TrailPiece(GreenfootImage image, int actsToFade, int angle) {
        this.actsToFade = actsToFade;

        // Create a COPY of the image
        this.image = new GreenfootImage(image);

        // Apply rotation based on the input angle
        this.image.rotate(angle); // Rotate the image by the specified angle

        this.image.setTransparency(START_TRANSPARENCY);
        setImage(this.image);
        actsLeft = actsToFade;
    }

    /**
     * Act - do whatever the TrailPiece wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        actsLeft--;
        if (actsLeft == 0) {
            getWorld().removeObject(this);
            return;
        }

        // Fade the transparency based on the remaining acts
        int opacity = (int)(((double) actsLeft / actsToFade) * START_TRANSPARENCY);
        image.setTransparency(opacity);
        setImage(image);
    }
}
