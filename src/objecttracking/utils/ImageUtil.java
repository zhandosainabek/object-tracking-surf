package objecttracking.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import objecttracking.gui.ImagePanel;
import objecttracking.model.VideoCapturer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImageUtil {

    public static BufferedImage getImgFromPanel(JPanel p) {
        BufferedImage img = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.createGraphics();
        p.paint(g);
        g.dispose();

        return img;
    }

    public static void setImgToImagePanel(ImagePanel p, BufferedImage img) {
        p.setImg(img);
        p.paintComponent(p.getGraphics());
    }

    public static BufferedImage convertMatToBufferedImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        BufferedImage bi = null;

        if (!mat.empty()) {
            Highgui.imencode(".jpg", mat, matOfByte);
            try {
                bi = (BufferedImage) ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
            } catch (IOException ex) {
                Logger.getLogger(VideoCapturer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return bi;
    }

    public static Mat convertBufferedImageToMat(BufferedImage image) {
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

        return mat;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
