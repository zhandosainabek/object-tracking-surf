package objecttracking.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Zhandos Ainabek
 */
public class EvaluationTool {

    // detected image and ground truth image paths must be adjusted
    public static void main(String[] args) {
        BufferedImage origImg = null, gtImg = null;

        try {
            origImg = ImageIO.read(new File("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/new/Turbulence11_orig.jpeg"));
            gtImg = ImageIO.read(new File("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/new/groundT11.png"));
        } catch (IOException ex) {
            Logger.getLogger(EvaluationTool.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int x = 0; x < origImg.getWidth(); x++) {
            for (int y = 0; y < origImg.getHeight(); y++) {
                int rgb1 = origImg.getRGB(x, y);
                int rgb2 = gtImg.getRGB(x, y);

                Color clr1 = new Color(rgb1);
                Color clr2 = new Color(rgb2);

                if (clr1.getRed() > 50 && clr1.getGreen() > 50 && clr1.getBlue() > 50) {
                    // the first one is white
                    if (clr2.getRed() > 50 && clr2.getGreen() > 50 && clr2.getBlue() > 50) {
                        tp++;
                    } else {
                        fp++;
                    }
                } else {
                    if (clr2.getRed() > 50 && clr2.getGreen() > 50 && clr2.getBlue() > 50) {
                        fn++;
                    } else {
                        tn++;
                    }
                }

            }
        }

        System.out.println("TP = " + tp);
        System.out.println("TN = " + tn);
        System.out.println("FP = " + fp);
        System.out.println("FN = " + fn);

        double re = tp/(double)(tp + fn);
        double fpr = fp/(double)(fp + tn);
        double pwc = (fp+fn)/(double)(tp+tn+fp+fn);
        double pr = tp*100/(double)(tp+fp);

        System.out.println("Re = " + re);
        System.out.println("FPR = " + fpr);
        System.out.println("PWC = " + pwc*100);
        System.out.println("Pr = " + pr);
    }
}
