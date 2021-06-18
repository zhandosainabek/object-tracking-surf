package objecttracking.model;

/**
 *
 * @author Zhandos Ainabek
 */
public class Displacement {

    private double x;
    private double y;

    public Displacement(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Displacement() {
        this(0, 0);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        final Displacement other = (Displacement) obj;
        return (other.getX() == x && other.getY() == y);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}
