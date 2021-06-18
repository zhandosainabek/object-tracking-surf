package objecttracking.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import objecttracking.utils.ImageUtil;

/**
 *
 * @author Zhandos Ainabek
 */
public class ImageSubtractionProcessPanel extends JPanel implements ActionListener {

    private MainPanel parentPanel;
    private JButton performImgSubtractionBtn;
    private JComboBox chooseImg1Combo, chooseImg2Combo;

    public ImageSubtractionProcessPanel(MainPanel parentPanel, final int frameNo) {
        super(null);
        this.parentPanel = parentPanel;

        setSize(385, 120);

        performImgSubtractionBtn = new JButton("Subtract images");
        performImgSubtractionBtn.setBounds(2, 5, 200, 40);
        add(performImgSubtractionBtn);
        performImgSubtractionBtn.addActionListener(this);
        performImgSubtractionBtn.setEnabled(false);

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setBounds(207, 0, 60, 20);
        add(fromLbl);

        Integer[] choices = new Integer[frameNo];
        for (int i = 0; i < frameNo; i++) {
            choices[i] = (i + 1);
        }

        chooseImg1Combo = new JComboBox(choices);
        chooseImg1Combo.setBounds(198, 18, 66, 28);
        add(chooseImg1Combo);

        chooseImg2Combo = new JComboBox(choices);
        chooseImg2Combo.setBounds(254, 18, 66, 28);
        add(chooseImg2Combo);
        chooseImg2Combo.setSelectedIndex(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(performImgSubtractionBtn)) {
            int from = (int) getChooseImg1Combo().getSelectedItem();
            int imgSel = (int) getChooseImg2Combo().getSelectedItem();
            BufferedImage resImg = parentPanel.subtractImages(from-1, imgSel-1, false);
            ImageUtil.setImgToImagePanel(parentPanel.getSubtractResultPanel(), resImg);
        }
    }

    /**
     * @return the chooseImg1Combo
     */
    public JComboBox getChooseImg1Combo() {
        return chooseImg1Combo;
    }

    /**
     * @return the chooseImg2Combo
     */
    public JComboBox getChooseImg2Combo() {
        return chooseImg2Combo;
    }

    public JButton getPerformImgSubtractionBtn() {
        return performImgSubtractionBtn;
    }
}
