package objecttracking.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImageSubtractor {

    public ImageSubtractor() {
    }

    public BufferedImage subtractImages(BufferedImage image1, BufferedImage image2,
            int rThreshold, int gThreshold, int bThreshold) {

        try {
            BufferedImage resultImg = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());
            int redDif, greenDif, blueDif;

            for (int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {
                    Color c = new Color(image1.getRGB(x, y));
                    Color c2 = new Color(image2.getRGB(x, y));

                    redDif = Math.abs(c.getRed() - c2.getRed());
                    greenDif = Math.abs(c.getGreen() - c2.getGreen());
                    blueDif = Math.abs(c.getBlue() - c2.getBlue());

                    if (redDif > rThreshold && greenDif > gThreshold && blueDif > bThreshold)
                        resultImg.setRGB(x, y, 0);// fill pixel with black color //Math.abs(colorDiff));
                    else
                        resultImg.setRGB(x, y, -1); // fill pixel with white color
                }
            }

            return resultImg;
        } catch (Exception ex) {
            Logger.getLogger(ImageSubtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BufferedImage subtractImages(BufferedImage image1, BufferedImage image2, int threshold) {
        try {
            BufferedImage resultImg = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());
            int colorDiff;

            for (int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {
                    colorDiff = Math.abs(image2.getRGB(x, y) - image1.getRGB(x, y));
                    if (colorDiff > threshold)
                        resultImg.setRGB(x, y, 0);// fill pixel with white color //Math.abs(colorDiff));
                    else
                        resultImg.setRGB(x, y, -1); // fill pixel with black color
                }
            }

            return resultImg;
        } catch (Exception ex) {
            Logger.getLogger(ImageSubtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
