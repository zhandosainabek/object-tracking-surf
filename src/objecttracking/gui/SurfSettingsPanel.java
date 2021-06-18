package objecttracking.gui;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
public class SurfSettingsPanel extends JPanel implements ChangeListener {

    private JCheckBox blurAndSharpenCheckBox, processNeighbourCheckbox, redFilterCheckbox,
            blueFilterCheckBox, greenFilterCheckBox, clusterByDistCheckbox;
    private JSpinner radiusSpinner, ratioThresholdSpinner, minDispThresholdSpinner,
            maxDispThresholdSpinner, zValueSpinner;

    public SurfSettingsPanel() {
        super(null);
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setSize(379, 215);

        blurAndSharpenCheckBox = new JCheckBox("Blur and Sharpen frames");
        blurAndSharpenCheckBox.setBounds(6, 20, 202, 23);
        add(blurAndSharpenCheckBox);

        redFilterCheckbox = new JCheckBox("Red filtering");
        redFilterCheckbox.setBounds(6, 45, 118, 23);
        add(redFilterCheckbox);

        greenFilterCheckBox = new JCheckBox("Green filtering");
        greenFilterCheckBox.setBounds(126, 45, 122, 23);
        add(greenFilterCheckBox);

        blueFilterCheckBox = new JCheckBox("Blue filtering");
        blueFilterCheckBox.setBounds(255, 45, 120, 23);
        add(blueFilterCheckBox);

        clusterByDistCheckbox = new JCheckBox("Apply extra clustering (by distance)");
        clusterByDistCheckbox.setBounds(6, 70, 260, 23);
        add(clusterByDistCheckbox);
        clusterByDistCheckbox.addChangeListener(this);

        JLabel zValueLbl = new JLabel("Z:");
        zValueLbl.setBounds(295, 73, 30, 16);
        add(zValueLbl);

        zValueSpinner = new JSpinner(new SpinnerNumberModel(3.0, 1.0, 8.0, 0.2));
        zValueSpinner.setBounds(310, 66, 60, 30);
        add(zValueSpinner);
        zValueSpinner.setEnabled(false);

        processNeighbourCheckbox = new JCheckBox("Apply SURF on initial matching point neighbourhood");
        processNeighbourCheckbox.setBounds(6, 95, 409, 23);
        add(processNeighbourCheckbox);
        processNeighbourCheckbox.addChangeListener(this);

        radiusSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 60, 5));
        radiusSpinner.setBounds(212, 116, 60, 30);
        add(radiusSpinner);
        radiusSpinner.setEnabled(false);

        JLabel neighbourRadiusLbl = new JLabel("Neighbourhood Radius: ");
        neighbourRadiusLbl.setBounds(33, 123, 152, 16);
        add(neighbourRadiusLbl);

        JLabel ratioThresholdLbl = new JLabel("Ratio Threshold: ");
        ratioThresholdLbl.setBounds(17, 155, 152, 16);
        add(ratioThresholdLbl);

        ratioThresholdSpinner = new JSpinner(new SpinnerNumberModel(2.5, 1.0, 10.0, 0.1));
        ratioThresholdSpinner.setBounds(212, 148, 60, 30);
        add(ratioThresholdSpinner);

        JLabel displacementThresholdLbl = new JLabel("Displacement Threshold: ");
        displacementThresholdLbl.setBounds(17, 180, 170, 16);
        add(displacementThresholdLbl);

        JLabel minDispThresholdLbl = new JLabel("min:");
        minDispThresholdLbl.setBounds(185, 180, 30, 16);
        add(minDispThresholdLbl);

        minDispThresholdSpinner = new JSpinner(new SpinnerNumberModel(3.0, 1.0, 10.0, 0.5));
        minDispThresholdSpinner.setBounds(212, 173, 60, 30);
        add(minDispThresholdSpinner);

        JLabel maxDispThresholdLbl = new JLabel("max:");
        maxDispThresholdLbl.setBounds(281, 180, 32, 16);
        add(maxDispThresholdLbl);

        maxDispThresholdSpinner = new JSpinner(new SpinnerNumberModel(25.0, 3.0, 400.0, 0.5));
        maxDispThresholdSpinner.setBounds(310, 173, 60, 30);
        add(maxDispThresholdSpinner);
    }

    /**
     * @return the blurAndSharpenCheckBox
     */
    public JCheckBox getBlurAndSharpenCheckBox() {
        return blurAndSharpenCheckBox;
    }

    /**
     * @return the processNeighbourCheckbox
     */
    public JCheckBox getProcessNeighbourCheckbox() {
        return processNeighbourCheckbox;
    }

    /**
     * @return the redFilterCheckbox
     */
    public JCheckBox getRedFilterCheckbox() {
        return redFilterCheckbox;
    }

    /**
     * @return the blueFilterCheckBox
     */
    public JCheckBox getBlueFilterCheckBox() {
        return blueFilterCheckBox;
    }

    /**
     * @return the greenFilterCheckBox
     */
    public JCheckBox getGreenFilterCheckBox() {
        return greenFilterCheckBox;
    }

    /**
     * @return the radiusSpinner
     */
    public JSpinner getRadiusSpinner() {
        return radiusSpinner;
    }

    /**
     * @return the ratioThresholdSpinner
     */
    public JSpinner getRatioThresholdSpinner() {
        return ratioThresholdSpinner;
    }

    /**
     * @return the minDispThresholdSpinner
     */
    public JSpinner getMinDispThresholdSpinner() {
        return minDispThresholdSpinner;
    }

    /**
     * @return the maxDispThresholdSpinner
     */
    public JSpinner getMaxDispThresholdSpinner() {
        return maxDispThresholdSpinner;
    }

    public JCheckBox getClusterByDistCheckbox() {
        return clusterByDistCheckbox;
    }

    public JSpinner getZValueSpinner() {
        return zValueSpinner;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(clusterByDistCheckbox)) {
            zValueSpinner.setEnabled(clusterByDistCheckbox.isSelected());
        } else if (e.getSource().equals(processNeighbourCheckbox)) {
            radiusSpinner.setEnabled(processNeighbourCheckbox.isSelected());
        }
    }

}
