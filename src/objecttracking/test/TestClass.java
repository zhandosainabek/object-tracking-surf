package objecttracking.test;

import java.util.ArrayList;
import java.util.HashMap;
import objecttracking.model.Displacement;
import org.opencv.core.Point;

/**
 *
 * @author Zhandos Ainabek
 */
public class TestClass {

    public static void main(String[] args) {
        HashMap<Displacement, ArrayList<Point>> clustersMap = new HashMap<>();

        ArrayList<Point> tmpList = new ArrayList<>();
        tmpList.add(new Point(209, 140));

        clustersMap.put(new Displacement(52, 49), tmpList);

        Displacement d = new Displacement(52, 49);
        System.out.println(clustersMap.containsKey(d));
    }
}
