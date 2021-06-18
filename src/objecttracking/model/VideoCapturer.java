package objecttracking.model;

import java.awt.image.BufferedImage;
import java.io.File;
import objecttracking.utils.ImageUtil;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 *
 * @author Zhandos Ainabek
 */
public class VideoCapturer {

    private VideoCapture videoCapture;
    private Mat mat;
    private int frameWidth, frameHeight;
    private File[] files;
    private int counter;

    public VideoCapturer(VideoCapture videoCapture, int width, int height) {
        this.videoCapture = videoCapture;
        this.frameHeight = height;
        this.frameWidth = width;
        this.videoCapture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, frameWidth);
        this.videoCapture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, frameHeight);
        mat = new Mat();
    }

    public void setVideoCapture(VideoCapture videoCapture) {
        this.videoCapture = videoCapture;
        this.videoCapture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, frameWidth);
        this.videoCapture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, frameHeight);
        mat = new Mat();
        files = null;
    }

    public BufferedImage getFrame() {
        if (files == null) {
            if (videoCapture.read(mat)) {
                return ImageUtil.convertMatToBufferedImage(mat);
            }
        } else {
            if (counter >= files.length)
                counter = 0;
            mat = Highgui.imread(files[counter].getAbsolutePath());
            counter++;
            return ImageUtil.convertMatToBufferedImage(mat);

        }
        return null;
    }

    public void setFiles(File[] files) {
        this.files = files;
        counter = 0;
    }
}
