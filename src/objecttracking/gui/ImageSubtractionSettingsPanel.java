package objecttracking.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImageSubtractionSettingsPanel extends JPanel implements ChangeListener {

    private MainPanel parentPanel;
    private JRadioButton noThresholdRadioBtn, thresholdRadioBtn, rgbThresholdRadioBtn;
    private JSpinner thresholdSpinner, redSpinner, greenSpinner, blueSpinner;

    public ImageSubtractionSettingsPanel(MainPanel parentPanel) {
        super(null);
        this.parentPanel = parentPanel;
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setSize(385, 120);

        noThresholdRadioBtn = new JRadioButton("No threshold");
        noThresholdRadioBtn.setBounds(6, 20, 200, 25);
        add(noThresholdRadioBtn);

        thresholdRadioBtn = new JRadioButton("Threshold value");
        thresholdRadioBtn.setBounds(6, 50, 140, 25);
        add(thresholdRadioBtn);

        thresholdSpinner = new JSpinner(new SpinnerNumberModel(1000000, 0, 10000000, 100000));
        thresholdSpinner.setBounds(168, 47, 200, 30);
        add(thresholdSpinner);
        thresholdSpinner.addChangeListener(this);

        rgbThresholdRadioBtn = new JRadioButton("RGB threshold values");
        rgbThresholdRadioBtn.setBounds(6, 80, 165, 25);
        add(rgbThresholdRadioBtn);
        rgbThresholdRadioBtn.setSelected(true);

        redSpinner = new JSpinner(new SpinnerNumberModel(12, 0, 50, 3));
        redSpinner.setBounds(168, 77, 70, 30);
        add(redSpinner);
        redSpinner.addChangeListener(this);

        greenSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 50, 3));
        greenSpinner.setBounds(238, 77, 70, 30);
        add(greenSpinner);
        greenSpinner.addChangeListener(this);

        blueSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 50, 3));
        blueSpinner.setBounds(308, 77, 70, 30);
        add(blueSpinner);
        blueSpinner.addChangeListener(this);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(noThresholdRadioBtn);
        buttonGroup.add(thresholdRadioBtn);
        buttonGroup.add(rgbThresholdRadioBtn);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(getThresholdSpinner())) {
            getThresholdRadioBtn().setSelected(true);
            parentPanel.subtractImages();
        } else if (e.getSource().equals(getRedSpinner()) || e.getSource().equals(getGreenSpinner())
                || e.getSource().equals(getBlueSpinner())) {
            getRgbThresholdRadioBtn().setSelected(true);
            parentPanel.subtractImages();
        }
    }

    /**
     * @return the noThresholdRadioBtn
     */
    public JRadioButton getNoThresholdRadioBtn() {
        return noThresholdRadioBtn;
    }

    /**
     * @return the thresholdRadioBtn
     */
    public JRadioButton getThresholdRadioBtn() {
        return thresholdRadioBtn;
    }

    /**
     * @return the rgbThresholdRadioBtn
     */
    public JRadioButton getRgbThresholdRadioBtn() {
        return rgbThresholdRadioBtn;
    }

    /**
     * @return the thresholdSpinner
     */
    public JSpinner getThresholdSpinner() {
        return thresholdSpinner;
    }

    /**
     * @return the redSpinner
     */
    public JSpinner getRedSpinner() {
        return redSpinner;
    }

    /**
     * @return the greenSpinner
     */
    public JSpinner getGreenSpinner() {
        return greenSpinner;
    }

    /**
     * @return the blueSpinner
     */
    public JSpinner getBlueSpinner() {
        return blueSpinner;
    }
}
