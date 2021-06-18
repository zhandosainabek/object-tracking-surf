package objecttracking.utils;

import org.opencv.core.Point;

/**
 *
 * @author Zhandos Ainabek
 */
public class PointUtil {

    public static double distance(Point p1, Point p2) {
        double dist = -1;

        if (p1 != null && p2 != null)
            dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));

        return dist;
    }
}
