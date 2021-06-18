package objecttracking.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import objecttracking.utils.PointUtil;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Zhandos Ainabek
 */
public class Cluster {

    private List<MovedPoint> clusterPoints;
    private Scalar contourColor;
    private Scalar pointsColor;

    public Cluster() {
    }

    public Cluster(List<MovedPoint> cluster) {
        this.clusterPoints = cluster;
    }

    public void setClusterPoints(List<MovedPoint> cluster) {
        this.clusterPoints = cluster;
    }

    public List<MovedPoint> getClusterPoints() {
        if (clusterPoints == null) {
            clusterPoints = new ArrayList<>();
        }
        return clusterPoints;
    }

    public int getSize() {

        return getClusterPoints().size();
    }

    public Point getCentroid() {
        double xSum = 0.0, ySum = 0.0;

        for (MovedPoint mp : getClusterPoints()) {
            xSum += mp.getOldPoint().pt.x;
            ySum += mp.getOldPoint().pt.y;
        }

        return new Point(xSum / getClusterPoints().size(), ySum / getClusterPoints().size());
    }

    public Point getCentroid2() {
        double xSum = 0.0, ySum = 0.0;

        for (MovedPoint mp : getClusterPoints()) {
            xSum += mp.getNewPoint().pt.x;
            ySum += mp.getNewPoint().pt.y;
        }

        return new Point(xSum / getClusterPoints().size(), ySum / getClusterPoints().size());
    }

    public Displacement getMeanDisplacement() {
        double sumX = 0.0, sumY = 0.0;
        for (MovedPoint mp : getClusterPoints()) {
            sumX += mp.getDisplacementX();
            sumY += mp.getDisplacementY();
        }

        return new Displacement(sumX / getClusterPoints().size(), sumY / getClusterPoints().size());
    }

    public Displacement getRoundedMeanDisplacement() {
        double sumX = 0.0, sumY = 0.0;
        for (MovedPoint mp : getClusterPoints()) {
            sumX += mp.getRoundedDisplacement().getX();
            sumY += mp.getRoundedDisplacement().getY();
        }

        return new Displacement(sumX / getClusterPoints().size(), sumY / getClusterPoints().size());
    }

    public double getAvgDistanceBetweenPoints() {
        double sumDist = 0.0;
        int count = 0;
        for (int i = 0; i < getClusterPoints().size(); i++) {
            Point p1 = getClusterPoints().get(i).getOldPoint().pt;

            for (int j = i + 1; j < getClusterPoints().size(); j++) {
                Point p2 = getClusterPoints().get(j).getOldPoint().pt;
                sumDist += PointUtil.distance(p1, p2);
                count++;
            }
        }

        return sumDist / count;
    }

    public double getAvgDistanceBetweenPoints2() {
        double sumDist = 0.0;
        int count = 0;
        for (int i = 0; i < getClusterPoints().size(); i++) {
            Point p1 = getClusterPoints().get(i).getNewPoint().pt;

            for (int j = i + 1; j < getClusterPoints().size(); j++) {
                Point p2 = getClusterPoints().get(j).getNewPoint().pt;
                sumDist += PointUtil.distance(p1, p2);
                count++;
            }
        }

        return sumDist / count;
    }

    public MatOfPoint getCountours(boolean isQueryImg) {
        MatOfPoint p = new MatOfPoint();
        List<Point> pts = new ArrayList<>();
        for (MovedPoint mp : getClusterPoints()) {
            if (isQueryImg) {
                pts.add(mp.getOldPoint().pt);
            } else {
                pts.add(mp.getNewPoint().pt);
            }
        }
        p.fromList(pts);

        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(p, hull);

        // Convert MatOfInt to MatOfPoint for drawing convex hull
        List<Point> points = new ArrayList<>();
        // Loop over all points that need to be hulled in current contour
        for (int j = 0; j < hull.rows(); j++) {
            int index = (int) hull.get(j, 0)[0];
            points.add(new Point(p.get(index, 0)[0], p.get(index, 0)[1]));
        }

        // Convert Point arrays into MatOfPoint
        MatOfPoint mop = new MatOfPoint();
        mop.fromList(points);

        return mop;
    }

    /**
     * @return the contourColor
     */
    public Scalar getContourColor() {
        return contourColor;
    }

    /**
     * @param contourColor the contourColor to set
     */
    public void setContourColor(Scalar contourColor) {
        this.contourColor = contourColor;
    }

    /**
     * @return the pointsColor
     */
    public Scalar getPointsColor() {
        return pointsColor;
    }

    /**
     * @param pointsColor the pointsColor to set
     */
    public void setPointsColor(Scalar pointsColor) {
        this.pointsColor = pointsColor;
    }
}
