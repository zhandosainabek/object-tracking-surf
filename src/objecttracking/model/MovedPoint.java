package objecttracking.model;

import org.opencv.features2d.KeyPoint;

/**
 *
 * @author Zhandos Ainabek
 */
public class MovedPoint {

    private KeyPoint oldPoint;
    private KeyPoint newPoint;

    public MovedPoint(KeyPoint oldPoint, KeyPoint newPoint) {
        this.oldPoint = oldPoint;
        this.newPoint = newPoint;
    }

    public KeyPoint getNewPoint() {
        return newPoint;
    }

    public void setNewPoint(KeyPoint newPoint) {
        this.newPoint = newPoint;
    }

    public KeyPoint getOldPoint() {
        return oldPoint;
    }

    public void setOldPoint(KeyPoint oldPoint) {
        this.oldPoint = oldPoint;
    }

    public double getDisplacementX() {
        return (newPoint.pt.x - oldPoint.pt.x);
    }

    public double getDisplacementY() {
        return (newPoint.pt.y - oldPoint.pt.y);
    }

    public Displacement getRoundedDisplacement() {
        return new Displacement(((int) Math.round((Math.round(getDisplacementX()) + 100.0) / 2.0)),
                (int) Math.round((Math.round(getDisplacementY()) + 100.0) / 2.0));
    }
}
