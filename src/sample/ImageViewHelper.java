package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewHelper extends ImageView {
    public String smileName;

    public ImageViewHelper(Image image, String smileName) {
        super(image);
        this.smileName = smileName;
    }
}
