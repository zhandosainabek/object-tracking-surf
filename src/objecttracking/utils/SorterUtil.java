package objecttracking.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import objecttracking.model.Cluster;
import objecttracking.model.MovedPoint;
import org.opencv.core.Point;

/**
 *
 * @author Zhandos Ainabek
 */
public class SorterUtil {

    public static List<HashMap> sortClustersFromOrigin(List<Cluster> clustersList) {
        List<HashMap> clustersMapList = new ArrayList<>();

        for (Cluster cluster : clustersList) {
            HashMap<MovedPoint, Double> clusterMap = new HashMap<>();
            for (MovedPoint mp : cluster.getClusterPoints()) {
                double dist = PointUtil.distance(mp.getNewPoint().pt, new Point(0, 0));
                clusterMap.put(mp, dist);
            }
            clustersMapList.add(clusterMap);
        }

        clustersMapList = sortMapListByValues(clustersMapList);

        return clustersMapList;
    }

    private static List<HashMap> sortMapListByValues(List<HashMap> mapList) {
        List<HashMap> sortedMapList = new ArrayList<>();
        for (HashMap map : mapList) {
            List list = new LinkedList(map.entrySet());

            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) o1).getValue()).compareTo(((Map.Entry) o2).getValue());
                }
            });

            HashMap sortedMap = new LinkedHashMap();
            Iterator it = list.iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            sortedMapList.add(sortedMap);
        }

        return sortedMapList;
    }
}
