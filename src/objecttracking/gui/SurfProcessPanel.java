package objecttracking.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Zhandos Ainabek
 */
public class SurfProcessPanel extends JPanel implements ActionListener {

    private MainPanel parentPanel;
    private JButton performSurfBtn;
    private JRadioButton surfOriginalImg, surfSubtractedImg;
    private List<BufferedImage[]> surfResultList;

    public SurfProcessPanel(MainPanel parentPanel) {
        super(null);
        this.parentPanel = parentPanel;

        setSize(500, 60);

        // add perform surf button
        performSurfBtn = new JButton("PERFORM SURF");
        performSurfBtn.setBounds(224, 5, 270, 50);
        add(performSurfBtn);
        performSurfBtn.addActionListener(this);
        performSurfBtn.setEnabled(false);

        // add surf parameters: whether perform surf on original images or subtracted images
        surfOriginalImg = new JRadioButton("SURF on original frames");
        surfOriginalImg.setBounds(5, 5, 220, 25);
        add(surfOriginalImg);
        surfOriginalImg.setSelected(true);

        surfSubtractedImg = new JRadioButton("SURF on subtracted frames");
        surfSubtractedImg.setBounds(5, 30, 220, 25);
        add(surfSubtractedImg);

        ButtonGroup radioBtnGroup = new ButtonGroup();
        radioBtnGroup.add(surfOriginalImg);
        radioBtnGroup.add(surfSubtractedImg);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        surfResultList = new ArrayList<>();
        boolean surfOnSubtractedImg = surfSubtractedImg.isSelected();

        if (surfOnSubtractedImg) {
            if (parentPanel.getFrameNo() > 2) {
                parentPanel.subtractAndSurf();
            } else {
                MainPanel.setOutputMessage("To apply SURF on subtracted frames the number of frames must NOT be less than three.");
            }
        } else {
            for (int i = 0; i < parentPanel.getFrameNo() - 1; i++) {
                parentPanel.processSurf(i, i + 1);
            }
        }
        parentPanel.resetSurfProcessing();
    }

    public JButton getPerformSurfBtn() {
        return performSurfBtn;
    }

    public List<BufferedImage[]> getSurfResultList() {
        if (surfResultList == null) {
            surfResultList = new ArrayList<>();
        }

        return surfResultList;
    }
}
