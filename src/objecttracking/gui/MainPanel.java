package objecttracking.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import objecttracking.model.ImageSubtractor;
import objecttracking.model.SurfProcessor;
import objecttracking.model.VideoCapturer;
import objecttracking.utils.ImageUtil;
import org.opencv.highgui.VideoCapture;

/**
 *
 * @author Zhandos Ainabek
 */
public class MainPanel extends JPanel implements ActionListener {

    private ImageSubtractor imgSubtractionProcessing;
    private SurfProcessor surfProcessing;
    private JPanel imagePanel;
    private JScrollPane imgScrollPanel;

    private SurfSettingsPanel surfSettingsPanel;
    private ImageSubtractionSettingsPanel imgSubtractionSettingsPanel;
    private SurfDrawingParamPanel surfDrawingParamPanel;
    private VideoSourcePanel videoSourcePanel;
    private ImageSubtractionProcessPanel imgSubtractionProcessPanel;
    private SurfProcessPanel surfProcessPanel;

    private ImagePanel videoPanel, surfResultPanel, subtractResultPanel;
    private ImagePanel[] framePanels;
    private BufferedImage[] originalFrame;
    private JComboBox frameNoCombo;
    private JButton captureFramesBtn;
    private VideoCapturer capt;
    private int frameNo;

    private static JTextArea outputArea;

    private final int MAX_FRAME_NO = 15;

    public MainPanel() {
        super(null);
        this.capt = new VideoCapturer(new VideoCapture(0), 480, 360);
        imgSubtractionProcessing = new ImageSubtractor();
        surfProcessing = new SurfProcessor();

        addVideoPanel();
        addCaptureBtn();
        addVideoSourceSettingsPanel();
        addOutputMsgPanel();
        addImagePanel();
        addSurfDrawingParamsPanel();
        addSurfSettingsPanel();
        addSurfProcessPanel();

        addImgSubtractionSettingsPanel();
        addImgSubtractionProcessPanel();
        addImgSubtractionResultPanel();

        showSurfPanel();
    }

    private void addVideoPanel() {
        videoPanel = new ImagePanel(null);
        videoPanel.setBounds(6, 305, 480, 360);
        add(videoPanel);
    }

    private void addVideoSourceSettingsPanel() {
        videoSourcePanel = new VideoSourcePanel(capt);
        videoSourcePanel.setLocation(498, 410);
        add(videoSourcePanel);
    }

    private void addOutputMsgPanel() {
        JPanel msgOutputPanel = new JPanel(null);
        msgOutputPanel.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        msgOutputPanel.setBounds(498, 530, 696, 138);
        add(msgOutputPanel);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scollableOutputArea = new JScrollPane(outputArea);
        scollableOutputArea.setBounds(10, 20, 675, 110);
        msgOutputPanel.add(scollableOutputArea);
    }

    private void addImagePanel() {
        imagePanel = new JPanel(null);
        imagePanel.setPreferredSize(new Dimension(getFrameNo() * 400, 290));

        imgScrollPanel = new JScrollPane(imagePanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imgScrollPanel.setBounds(6, 6, 800, 290);
        add(imgScrollPanel);

        framePanels = new ImagePanel[getFrameNo()];
        for (int i = 0; i < framePanels.length; i++) {
            framePanels[i] = new ImagePanel(null);
            framePanels[i].setBounds(i * 400, 0, 400, 290);
            imagePanel.add(framePanels[i]);
        }

        surfResultPanel = new ImagePanel(null);
        surfResultPanel.setBounds(0, 0, 800, 290);
        imagePanel.add(surfResultPanel);
        surfResultPanel.setVisible(false);
    }

    private void addSurfSettingsPanel() {
        surfSettingsPanel = new SurfSettingsPanel();
        surfSettingsPanel.setLocation(815, 140);
        add(surfSettingsPanel);
    }

    private void addSurfDrawingParamsPanel() {
        surfDrawingParamPanel = new SurfDrawingParamPanel(framePanels, surfResultPanel);
        surfDrawingParamPanel.setLocation(815, 6);
        add(surfDrawingParamPanel);
    }

    private void addCaptureBtn() {
        captureFramesBtn = new JButton("Capture frame sequence");
        captureFramesBtn.setBounds(497, 305, 200, 40);
        add(captureFramesBtn);
        captureFramesBtn.addActionListener(this);

        JLabel fromLbl = new JLabel("Frame number:");
        fromLbl.setBounds(703, 300, 200, 20);
        add(fromLbl);

        List<Integer> frameNoList = new ArrayList<>();
        for (int i = 2; i < MAX_FRAME_NO + 2; i++) {
            frameNoList.add(i);
        }
        frameNoCombo = new JComboBox(frameNoList.toArray());
        frameNoCombo.setBounds(700, 310, 74, 40);
        add(frameNoCombo);
        setFrameNo((int) frameNoCombo.getSelectedItem());
        frameNoCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFrameNo((int) frameNoCombo.getSelectedItem());
                imagePanel.setVisible(false);
                imgScrollPanel.setVisible(false);
                remove(imgScrollPanel);
                remove(imagePanel);
                addImagePanel();
                surfDrawingParamPanel.setFramePanels(framePanels);
                surfDrawingParamPanel.setSurfResultPanel(surfResultPanel);
                surfProcessPanel.getPerformSurfBtn().setEnabled(false);

                boolean visibility = imgSubtractionProcessPanel.isVisible();
                imgSubtractionProcessPanel.setVisible(false);
                remove(imgSubtractionProcessPanel);
                addImgSubtractionProcessPanel();
                imgSubtractionProcessPanel.getPerformImgSubtractionBtn().setEnabled(false);
                imgSubtractionProcessPanel.setVisible(visibility);
            }
        });

    }

    private void addImgSubtractionSettingsPanel() {
        imgSubtractionSettingsPanel = new ImageSubtractionSettingsPanel(MainPanel.this);
        imgSubtractionSettingsPanel.setLocation(810, 290);
        add(imgSubtractionSettingsPanel);
    }

    private void addImgSubtractionProcessPanel() {
        imgSubtractionProcessPanel = new ImageSubtractionProcessPanel(MainPanel.this, getFrameNo());
        imgSubtractionProcessPanel.setLocation(495, 355);
        add(imgSubtractionProcessPanel);
    }

    private void addImgSubtractionResultPanel() {
        subtractResultPanel = new ImagePanel(null);
        subtractResultPanel.setBounds(810, 3, 385, 285);
        add(subtractResultPanel);
    }

    private void addSurfProcessPanel() {
        surfProcessPanel = new SurfProcessPanel(MainPanel.this);
        surfProcessPanel.setLocation(700, 355);
        add(surfProcessPanel);
    }

    public final void showImageSubtractionPanel() {
        surfDrawingParamPanel.setVisible(false);
        surfResultPanel.setVisible(false);
        surfSettingsPanel.setVisible(false);
        surfProcessPanel.setVisible(false);

        imgSubtractionProcessPanel.setVisible(true);
        imgSubtractionSettingsPanel.setVisible(true);
        subtractResultPanel.setVisible(true);
    }

    public final void showSurfPanel() {
        imgSubtractionProcessPanel.setVisible(false);
        imgSubtractionSettingsPanel.setVisible(false);
        subtractResultPanel.setVisible(false);

        surfDrawingParamPanel.setVisible(true);
        surfResultPanel.setVisible(true);
        surfSettingsPanel.setVisible(true);
        surfProcessPanel.setVisible(true);
    }

    public void updatePanelImg() {
        BufferedImage im = capt.getFrame();
        if (im != null) {
            ImageUtil.setImgToImagePanel(videoPanel, im);
        }
    }

    public void subtractImages() {
        int from = (int) imgSubtractionProcessPanel.getChooseImg1Combo().getSelectedItem();
        int imgSel = (int) imgSubtractionProcessPanel.getChooseImg2Combo().getSelectedItem();
        BufferedImage resImg = subtractImages(from-1, imgSel-1, false);
        ImageUtil.setImgToImagePanel(subtractResultPanel, resImg);
    }

    public BufferedImage subtractImages(int firstImgIdx, int secondImgIdx, boolean isForSurf) {
        try {
            BufferedImage img1 = originalFrame[firstImgIdx];
            BufferedImage img2 = originalFrame[secondImgIdx];
            BufferedImage resImg;

            if (!isForSurf) {
                if (imgSubtractionSettingsPanel.getRgbThresholdRadioBtn().isSelected()) {
                    int redThreshold = (int) imgSubtractionSettingsPanel.getRedSpinner().getValue();
                    int greenThreshold = (int) imgSubtractionSettingsPanel.getGreenSpinner().getValue();
                    int blueThreshold = (int) imgSubtractionSettingsPanel.getBlueSpinner().getValue();
                    resImg = imgSubtractionProcessing.subtractImages(img1, img2,
                            redThreshold, greenThreshold, blueThreshold);
                } else if (imgSubtractionSettingsPanel.getThresholdRadioBtn().isSelected()) {
                    int threshold = (int) imgSubtractionSettingsPanel.getThresholdSpinner().getValue();
                    resImg = imgSubtractionProcessing.subtractImages(img1, img2, threshold);
                } else {
                    resImg = imgSubtractionProcessing.subtractImages(img1, img2, 0);
                }
            } else {
                resImg = imgSubtractionProcessing.subtractImages(img1, img2, 18, 15, 10);
            }

            return resImg;
        } catch (Exception ex) {
            System.err.println("Cannot find correct image selected " + ex.getMessage());
            return null;
        }
    }

    public void subtractAndSurf() {
        BufferedImage[] subtractedFrames = new BufferedImage[getFrameNo() - 1];

        for (int i = 0; i < subtractedFrames.length; i++) {
            subtractedFrames[i] = subtractImages(i + 1, i, true);
        }

        for (int i = 0; i < subtractedFrames.length - 1; i++) {
            int radius = -1;
            double zValue = -1;
            if (surfSettingsPanel.getProcessNeighbourCheckbox().isSelected()) {
                radius = (int) surfSettingsPanel.getRadiusSpinner().getValue();
            }

            if (surfSettingsPanel.getClusterByDistCheckbox().isSelected()) {
                zValue = (double) surfSettingsPanel.getZValueSpinner().getValue();
            }

            BufferedImage[] result = surfProcessing.runSurfAlg(subtractedFrames[i],
                    subtractedFrames[i+1], false, false, false, false, 1.5, radius, 0, 10, zValue);

            drawSurfResult(i, i + 1, result);
        }

        framePanels[framePanels.length-1].setVisible(false);
    }

    public void processSurf(int firstImgIdx, int secondImgIdx) {
        int radius = -1;
        double zValue = -1;
        if (surfSettingsPanel.getProcessNeighbourCheckbox().isSelected()) {
            radius = (int) surfSettingsPanel.getRadiusSpinner().getValue();
        }

        if (surfSettingsPanel.getClusterByDistCheckbox().isSelected()) {
            zValue = (double) surfSettingsPanel.getZValueSpinner().getValue();
        }

        BufferedImage[] result = surfProcessing.runSurfAlg(originalFrame[firstImgIdx],
                originalFrame[secondImgIdx], surfSettingsPanel.getBlurAndSharpenCheckBox().isSelected(),
                surfSettingsPanel.getRedFilterCheckbox().isSelected(),
                surfSettingsPanel.getGreenFilterCheckBox().isSelected(),
                surfSettingsPanel.getBlueFilterCheckBox().isSelected(),
                (double) surfSettingsPanel.getRatioThresholdSpinner().getValue(), radius,
                (double) surfSettingsPanel.getMinDispThresholdSpinner().getValue(),
                (double) surfSettingsPanel.getMaxDispThresholdSpinner().getValue(), zValue);

        drawSurfResult(firstImgIdx, secondImgIdx, result);
    }

    private void drawSurfResult(int firstImgIdx, int secondImgIdx, BufferedImage[] result) {
        surfProcessPanel.getSurfResultList().add(result);
        surfDrawingParamPanel.setSurfResultList(surfProcessPanel.getSurfResultList());

        if (surfDrawingParamPanel.getDrawLinesRadio().isSelected()) {
            surfResultPanel.setVisible(true);
            for (ImagePanel framePanel : framePanels) {
                framePanel.setVisible(false);
            }

            ImageUtil.setImgToImagePanel(surfResultPanel, result[0]);
        } else if (surfDrawingParamPanel.getDrawAllKeypointsRadio().isSelected()) {
            surfResultPanel.setVisible(false);
            for (ImagePanel framePanel : framePanels) {
                framePanel.setVisible(true);
            }

            ImageUtil.setImgToImagePanel(framePanels[firstImgIdx], result[1]);
            ImageUtil.setImgToImagePanel(framePanels[secondImgIdx], result[2]);
        } else if (surfDrawingParamPanel.getDrawMatchingPointsRadio().isSelected()) {
            surfResultPanel.setVisible(false);
            for (ImagePanel framePanel : framePanels) {
                framePanel.setVisible(true);
            }

            ImageUtil.setImgToImagePanel(framePanels[firstImgIdx], result[3]);
            ImageUtil.setImgToImagePanel(framePanels[secondImgIdx], result[4]);
        } else if (surfDrawingParamPanel.getDrawConvexHullRadio().isSelected()) {
            surfResultPanel.setVisible(false);
            for (ImagePanel framePanel : framePanels) {
                framePanel.setVisible(true);
            }

            ImageUtil.setImgToImagePanel(framePanels[firstImgIdx], result[5]);
            ImageUtil.setImgToImagePanel(framePanels[secondImgIdx], result[6]);
        }
    }

    public static void setOutputMessage(String text) {
        outputArea.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(captureFramesBtn)) {
            int w = framePanels[0].getWidth();
            int h = framePanels[0].getHeight();
            originalFrame = new BufferedImage[getFrameNo()];

            for (int i = 0; i < getFrameNo(); i++) {
                originalFrame[i] = capt.getFrame();
                if (videoSourcePanel.getVideoFileRadio().isSelected()) {
                    originalFrame[i] = ImageUtil.resize(originalFrame[i], w, h);
                }
                ImageUtil.setImgToImagePanel(framePanels[i], originalFrame[i]);
            }

            surfProcessPanel.getPerformSurfBtn().setEnabled(true);
            imgSubtractionProcessPanel.getPerformImgSubtractionBtn().setEnabled(true);
        }
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public void resetSurfProcessing() {
        surfProcessing = new SurfProcessor();
    }

    public ImagePanel getSubtractResultPanel() {
        return subtractResultPanel;
    }
}
