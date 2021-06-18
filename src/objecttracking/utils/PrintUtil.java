package objecttracking.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objecttracking.model.Cluster;
import objecttracking.model.MovedPoint;

/**
 *
 * @author Zhandos Ainabek
 */
public class PrintUtil {

    public static void printMapList(List<HashMap> mapList, StringBuilder msgText) {
        for (HashMap<MovedPoint, Double> map : mapList) {
            System.out.println("Cluster map #" + (mapList.indexOf(map) + 1));
            msgText.append("Cluster map #").append((mapList.indexOf(map) + 1)).append(":\n");
            for (Map.Entry<MovedPoint, Double> entry : map.entrySet()) {
                System.out.println("    Point: " + entry.getKey().getNewPoint().pt + "; Distance from (0,0): " + entry.getValue());
                msgText.append("    Point: ").append(entry.getKey().getNewPoint().pt).append("; Distance from (0,0): ")
                        .append(entry.getValue()).append("\n");
            }
        }
        System.out.println();
        msgText.append("\n");
    }

    public static void printClusterList(List<Cluster> clustersList, StringBuilder msgText) {
        msgText.append("\nClusters number: ").append(clustersList.size()).append("\n");

        for (int i = 0; i < clustersList.size(); i++) {
            msgText.append("\nCluster # ").append(i + 1).append(":  ");
            Cluster cluster = clustersList.get(i);
            msgText.append("    Cluster size: ").append(cluster.getClusterPoints().size()).append("\n");
            for (MovedPoint mp : cluster.getClusterPoints()) {
                msgText.append("    Displacement: ").append(mp.getRoundedDisplacement());
                msgText.append("; Point").append(mp.getNewPoint().pt).append("; \n");
            }
        }
    }
}
