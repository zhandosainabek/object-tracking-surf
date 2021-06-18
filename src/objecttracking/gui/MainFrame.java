package objecttracking.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Zhandos Ainabek
 */
public class MainFrame extends JFrame {

    private MainPanel mainPanel;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem imgSubtraction;
    private JMenuItem surf;
    private JMenuItem exit;
    private JMenuItem documentation;
    private MyThread thread = null;

    public MainFrame() {
        super("SURF example");
        init();
        setVisible(true);
        thread = new MyThread();
        thread.start();
    }

    private void init() {
        mainPanel = new MainPanel();
        addMenuBar();
        setContentPane(mainPanel);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addMenuBar() {
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        imgSubtraction = new MenuItemConfigured("Image Subtraction");
        surf = new MenuItemConfigured("SURF Detector");
        exit = new MenuItemConfigured("Exit");
        documentation = new MenuItemConfigured("Documentation");

        fileMenu.add(imgSubtraction);
        fileMenu.add(surf);
        fileMenu.add(exit);
        helpMenu.add(documentation);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    class MyThread extends Thread {

        private volatile boolean running = true;

        @Override
        public void run() {
            running = true;
            while (running) {
                mainPanel.updatePanelImg();
            }
        }

        public void terminate() {
            running = false;
        }

        public void resumeVideo() {
            running = true;
        }
    }

    class MenuItemConfigured extends JMenuItem implements ActionListener {

        public MenuItemConfigured(String text) {
            super(text);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == imgSubtraction) {
                mainPanel.showImageSubtractionPanel();
            } else if (e.getSource() == surf) {
                mainPanel.showSurfPanel();
            } else if (e.getSource() == exit) {
                int selection = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Are you sure to close the program?", "Confirmation Dialog",
                        JOptionPane.YES_NO_OPTION);
                if (selection == 0) {
                    thread.terminate();
                    MainFrame.this.dispose();
                }
            } else if (e.getSource() == documentation) {
                // TODO
            }
        }
    }

}
