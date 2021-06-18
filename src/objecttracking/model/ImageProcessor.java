package objecttracking.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import objecttracking.utils.ImageUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImageProcessor {

//    public static BufferedImage blur(BufferedImage srcImg) {
//        Mat blurredImg = new Mat();
//        Imgproc.GaussianBlur(ImageUtil.convertBufferedImageToMat(srcImg), blurredImg, new Size(0, 0), 9);
//        return ImageUtil.convertMatToBufferedImage(blurredImg);
//    }

    public ImageProcessor() {
    }

    public static Mat getGrayImg(Mat img) {
        Mat grayImg = new Mat(img.rows(), img.cols(), img.type());

        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
        Core.normalize(grayImg, grayImg, 0, 255, Core.NORM_MINMAX);

        return grayImg;
    }

    public BufferedImage blurAndSharpen(BufferedImage srcImg) {
        Mat outputImg = new Mat();
        Imgproc.GaussianBlur(ImageUtil.convertBufferedImageToMat(srcImg), outputImg, new Size(0, 0), 9);
        Core.addWeighted(ImageUtil.convertBufferedImageToMat(srcImg), 1.7, outputImg, -0.5, 0, outputImg);

        return ImageUtil.convertMatToBufferedImage(outputImg);
    }

    public void blurAndSharpen(Mat srcImg, Mat dstImg) {
        Imgproc.GaussianBlur(srcImg, dstImg, new Size(0, 0), 9);
        Core.addWeighted(srcImg, 1.7, dstImg, -0.5, 0, dstImg);
    }

    public BufferedImage redFiltering(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));
                Color c2 = new Color(c.getRed(), 0, 0);
                result.setRGB(x, y, c2.getRGB());
            }
        }
        return result;
    }

    public BufferedImage greenFiltering(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));
                Color c2 = new Color(0, c.getGreen(), 0);
                result.setRGB(x, y, c2.getRGB());
            }
        }
        return result;
    }

    public BufferedImage blueFiltering(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));
                Color c2 = new Color(0, 0, c.getBlue());
                result.setRGB(x, y, c2.getRGB());
            }
        }
        return result;
    }

    public BufferedImage[] getFilteredImages(BufferedImage queryImg, BufferedImage trainImg, boolean redFilter, boolean greenFilter, boolean blueFilter) {
        BufferedImage[] filteredImages = new BufferedImage[6];

        if (redFilter) {
            // apply red filtering
            filteredImages[0] = redFiltering(queryImg);
            filteredImages[1] = redFiltering(trainImg);
        }
        if (greenFilter) {
            // apply green filtering
            filteredImages[2] = greenFiltering(queryImg);
            filteredImages[3] = greenFiltering(trainImg);
        }
        if (blueFilter) {
            // apply blue filtering
            filteredImages[4] = blueFiltering(queryImg);
            filteredImages[5] = blueFiltering(trainImg);
        }

        return filteredImages;
    }
}
