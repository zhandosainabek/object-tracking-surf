package objecttracking;

import objecttracking.gui.MainFrame;
import org.opencv.core.Core;

/**
 *
 * @author Zhandos Ainabek
 */
public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new MainFrame();
    }
}
