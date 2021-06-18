package objecttracking.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImagePanel extends JPanel {

    private BufferedImage img;

    public ImagePanel(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), 0, 0, img.getWidth(), img.getHeight(), this);
        }
    }
}
