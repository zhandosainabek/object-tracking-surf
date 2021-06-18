package objecttracking.gui;

import objecttracking.model.VideoCapturer;
import org.opencv.highgui.VideoCapture;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * @author Zhandos Ainabek
 */
public class VideoSourcePanel extends JPanel implements ActionListener, ItemListener {

    private JButton chooseBtn;
    private JTextField filePathTextField;
    private JRadioButton webcamRadio, videoFileRadio;
    private VideoCapturer capt;

    public VideoSourcePanel(final VideoCapturer capt) {
        super(null);
        this.capt = capt;
        setBorder(new TitledBorder(null, "Video source settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setSize(696, 120);

        webcamRadio = new JRadioButton("Web Camera");
        webcamRadio.setBounds(6, 25, 150, 23);
        add(webcamRadio);
        webcamRadio.setSelected(true);
        webcamRadio.addActionListener(this);

        videoFileRadio = new JRadioButton("Video File");
        videoFileRadio.setBounds(160, 25, 150, 23);
        add(videoFileRadio);
        videoFileRadio.addActionListener(this);
        videoFileRadio.addItemListener(this);

        ButtonGroup videoSourceGroup = new ButtonGroup();
        videoSourceGroup.add(webcamRadio);
        videoSourceGroup.add(videoFileRadio);

        JLabel chooseLbl = new JLabel("Choose a video file:");
        chooseLbl.setBounds(6, 55, 333, 16);
        add(chooseLbl);

        filePathTextField = new JTextField();
        filePathTextField.setBounds(16, 77, 491, 30);
        add(filePathTextField);
        filePathTextField.setEditable(false);

        chooseBtn = new JButton("Choose video");
        chooseBtn.setBounds(519, 77, 170, 33);
        add(chooseBtn);
        chooseBtn.setEnabled(false);
        chooseBtn.addActionListener(this);
    }

    private void chooseVideo() {
        JFileChooser fileChooser = new JFileChooser("/Users/zhandosainabek/Downloads/");
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this.getParent());
        File[] files = fileChooser.getSelectedFiles();
        if (files.length == 1) {
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getPath();

                if (path != null) {
                    filePathTextField.setText(path);
                    this.capt.setVideoCapture(new VideoCapture(filePathTextField.getText()));
                }
            }
        } else if (files.length > 1) {
            this.capt.setFiles(files);
//            filePathTextField.setText(files[0].getAbsolutePath());
//            String filePaths = "";
//            VideoCapture cp = new VideoCapture();
//            cp.
//            for (File f : files) {
//                filePaths += f.getAbsolutePath();
//                Highgui.imread(f.getAbsolutePath());
//            }
//            // /Users/jamboproduction/Downloads/cvpr10_tud_stadtmitte/DaMultiview-seq7026.png/Users/jamboproduction/Downloads/cvpr10_tud_stadtmitte/DaMultiview-seq7027.png/Users/jamboproduction/Downloads/cvpr10_tud_stadtmitte/DaMultiview-seq7028.png/Users/jamboproduction/Downloads/cvpr10_tud_stadtmitte/DaMultiview-seq7029.png
//            filePathTextField.setText(filePaths);
//            this.capt.setVideoCapture(new VideoCapture("/Users/jamboproduction/Downloads/pedestrians/input/img_%02d.jpg"));

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(webcamRadio) || e.getSource().equals(videoFileRadio)) {
            if (webcamRadio.isSelected()) {
                this.capt.setVideoCapture(new VideoCapture(0));
                chooseBtn.setEnabled(false);
            } else if (videoFileRadio.isSelected()) {
                chooseBtn.setEnabled(true);
                if (!filePathTextField.getText().isEmpty()) {
                    this.capt.setVideoCapture(new VideoCapture(filePathTextField.getText()));
                }
            }
        } else if (e.getSource().equals(chooseBtn)) {
            chooseVideo();
        }
    }

    public JRadioButton getVideoFileRadio() {
        return videoFileRadio;
    }

    public JRadioButton getWebcamRadio() {
        return webcamRadio;
    }

    public JTextField getFilePathTextField() {
        return filePathTextField;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (videoFileRadio.isSelected()) {
            if (getFilePathTextField().getText().isEmpty())
                chooseVideo();
        }
    }
}
