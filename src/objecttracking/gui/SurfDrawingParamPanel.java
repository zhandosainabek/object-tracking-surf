package objecttracking.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import objecttracking.utils.ImageUtil;

/**
 *
 * @author Zhandos Ainabek
 */
public class SurfDrawingParamPanel extends JPanel implements ItemListener {

    private List<BufferedImage[]> surfResultList;
    private ImagePanel[] framePanels;
    private ImagePanel surfResultPanel;
    private JRadioButton drawLinesRadio, drawAllKeypointsRadio,
            drawMatchingPointsRadio, drawConvexHullRadio;

    public SurfDrawingParamPanel(ImagePanel[] framePanels, ImagePanel surfResultPanel) {
        super(null);
        this.framePanels = framePanels;
        this.surfResultPanel = surfResultPanel;

        setBorder(new TitledBorder(null, "Drawing parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setSize(379, 130);

        drawLinesRadio = new JRadioButton("Draw matching lines");
        drawLinesRadio.setBounds(6, 20, 192, 23);
        add(drawLinesRadio);
        drawLinesRadio.addItemListener(this);

        drawAllKeypointsRadio = new JRadioButton("Draw all keypoints");
        drawAllKeypointsRadio.setBounds(6, 45, 192, 23);
        add(drawAllKeypointsRadio);
        drawAllKeypointsRadio.addItemListener(this);

        drawMatchingPointsRadio = new JRadioButton("Draw matching keypoints only");
        drawMatchingPointsRadio.setBounds(6, 70, 250, 23);
        add(drawMatchingPointsRadio);
        drawMatchingPointsRadio.addItemListener(this);

        drawConvexHullRadio = new JRadioButton("Draw detected object regions");
        drawConvexHullRadio.setBounds(6, 95, 250, 23);
        add(drawConvexHullRadio);
        drawConvexHullRadio.setSelected(true);
        drawConvexHullRadio.addItemListener(this);

        ButtonGroup drawBtnGroup = new ButtonGroup();
        drawBtnGroup.add(drawLinesRadio);
        drawBtnGroup.add(drawAllKeypointsRadio);
        drawBtnGroup.add(drawMatchingPointsRadio);
        drawBtnGroup.add(drawConvexHullRadio);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (surfResultList != null) {
            for (BufferedImage[] result : surfResultList) {
                int index = surfResultList.indexOf(result);
                if (result != null && result.length == 7) {
                    if (e.getItem().equals(getDrawLinesRadio())) {
                        surfResultPanel.setVisible(true);
                        for (ImagePanel framePanel : framePanels) {
                            framePanel.setVisible(false);
                        }

                        ImageUtil.setImgToImagePanel(surfResultPanel, result[0]);
                    } else if (e.getItem().equals(getDrawAllKeypointsRadio())) {
                        surfResultPanel.setVisible(false);
                        for (ImagePanel framePanel : framePanels) {
                            framePanel.setVisible(true);
                        }

                        ImageUtil.setImgToImagePanel(framePanels[index], result[1]);
                        ImageUtil.setImgToImagePanel(framePanels[index + 1], result[2]);
                    } else if (e.getItem().equals(getDrawMatchingPointsRadio())) {
                        surfResultPanel.setVisible(false);
                        for (ImagePanel framePanel : framePanels) {
                            framePanel.setVisible(true);
                        }

                        ImageUtil.setImgToImagePanel(framePanels[index], result[3]);
                        ImageUtil.setImgToImagePanel(framePanels[index + 1], result[4]);
                    } else if (e.getItem().equals(getDrawConvexHullRadio())) {
                        surfResultPanel.setVisible(false);
                        for (ImagePanel framePanel : framePanels) {
                            framePanel.setVisible(true);
                        }

                        ImageUtil.setImgToImagePanel(framePanels[index], result[5]);
                        ImageUtil.setImgToImagePanel(framePanels[index + 1], result[6]);
                    }
                }
            }
        }
    }

    /**
     * @return the drawLinesRadio
     */
    public JRadioButton getDrawLinesRadio() {
        return drawLinesRadio;
    }

    /**
     * @return the drawAllKeypointsRadio
     */
    public JRadioButton getDrawAllKeypointsRadio() {
        return drawAllKeypointsRadio;
    }

    /**
     * @return the drawMatchingPointsRadio
     */
    public JRadioButton getDrawMatchingPointsRadio() {
        return drawMatchingPointsRadio;
    }

    /**
     * @return the drawConvexHullRadio
     */
    public JRadioButton getDrawConvexHullRadio() {
        return drawConvexHullRadio;
    }

    /**
     * @param surfResultList the surfResultList to set
     */
    public void setSurfResultList(List<BufferedImage[]> surfResultList) {
        this.surfResultList = surfResultList;
    }

    public void setFramePanels(ImagePanel[] framePanels) {
        this.framePanels = framePanels;
    }

    public void setSurfResultPanel(ImagePanel surfResultPanel) {
        this.surfResultPanel = surfResultPanel;
    }
}
