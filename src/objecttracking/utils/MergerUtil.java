package objecttracking.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import objecttracking.model.Cluster;
import objecttracking.model.Displacement;
import objecttracking.model.MovedPoint;
import org.opencv.core.Point;

/**
 *
 * @author Zhandos Ainabek
 */
public class MergerUtil {

    // TODO Done, but still needs additional testing
    public static List<Cluster> mergeSimilarDispClusters(
            List<Cluster> clustersList, StringBuilder msgText) {
        List<Displacement> meanDisplacementList = new ArrayList<>();

//        msgText.append("\nMean displacement of clusters:    clustersList.size() = ")
//                .append(clustersList.size()).append("\n");


        for (Cluster cluster : clustersList) {
            Displacement meanDisp = cluster.getRoundedMeanDisplacement();
            meanDisplacementList.add(meanDisp);
//            msgText.append("    Average displacement: {").append(meanDisp).append("}\n");
        }
        if (meanDisplacementList.size() == clustersList.size()) {
            for (int i = 0; i < clustersList.size(); i++) {
                for (int j = i + 1; j < clustersList.size(); j++) {
                    double diffX = Math.abs(meanDisplacementList.get(i).getX() - meanDisplacementList.get(j).getX());
                    double diffY = Math.abs(meanDisplacementList.get(i).getY() - meanDisplacementList.get(j).getY());

                    if (diffX < 2 && diffY < 2) {
                        // merge clusters i and j
                        Cluster newCluster = new Cluster(clustersList.get(i).getClusterPoints());
                        newCluster.getClusterPoints().addAll(clustersList.get(j).getClusterPoints());
                        clustersList.remove(clustersList.get(j));
                        clustersList.remove(clustersList.get(i));
                        clustersList.add(newCluster);

                        if (clustersList.size() > 1) {
                            return mergeSimilarDispClusters(clustersList, msgText);
                        }
                        return clustersList;
                    }
                }
            }
        }
        return clustersList;
    }

    public static List<List<MovedPoint>> mergeSmallClusters(List<List<MovedPoint>> clusterList) {
        List<List<MovedPoint>> mergedClusterList = new ArrayList<>(clusterList);
        List<Point> centroids = new ArrayList<>();

        for (List<MovedPoint> cluster : clusterList) {
            if (cluster.size() < 3) {
                List<MovedPoint> mergedCluster = new ArrayList<>(cluster);

                // get centroids of all clusters
                for (List<MovedPoint> c : clusterList) {
                    double xSum = 0.0, ySum = 0.0;
                    for (MovedPoint mp : c) {
                        xSum += mp.getNewPoint().pt.x;
                        ySum += mp.getNewPoint().pt.y;
                    }
                    centroids.add(new Point(xSum / c.size(), ySum / c.size()));
                }

                // find the nearest cluster
                int minDistCentroidIdx = -1;
                double minDist = 400.0;
                double dist = 0.0;
                Point p = centroids.get(clusterList.indexOf(cluster));
                for (Point centroid : centroids) {
                    dist = Math.sqrt(Math.pow(p.x - centroid.x, 2) + Math.pow(p.y - centroid.y, 2));
                    if (dist > 0 && dist < minDist) {
                        minDist = dist;
                        minDistCentroidIdx = centroids.indexOf(centroid);
                    }
                }
                if (minDistCentroidIdx != -1) {
                    mergedCluster.addAll(clusterList.get(minDistCentroidIdx));
                    mergedClusterList.remove(clusterList.get(minDistCentroidIdx));
                    mergedClusterList.remove(cluster);
                    mergedClusterList.add(mergedCluster);

                    return mergeSmallClusters(mergedClusterList);
                }
            }
        }

        return mergedClusterList;
    }

    // TODO needs testing, improve performance
//    public static List<Cluster> mergeNearbyClusters(List<Cluster> clusterList) {
//        List<Point> centroids = new ArrayList<>();
//        HashMap clusterAvgDistanceMap = new HashMap<>();
//        List<Cluster> newClusterList = new ArrayList<>(clusterList);
//        for (Cluster cluster : newClusterList) {
//            clusterAvgDistanceMap.put(newClusterList.indexOf(cluster), cluster.getAvgDistanceBetweenPoints2());
//            centroids.add(cluster.getCentroid2());
//        }
//
//        Iterator it = clusterAvgDistanceMap.keySet().iterator();
////            int index = 0;
////        List<Cluster> newClusterList = new ArrayList<>(clusterList);
//        while (it.hasNext()) {
//            int clusterIdx = (int) it.next();
//            Cluster cluster = newClusterList.get(clusterIdx);
//            double avgDist = (double) clusterAvgDistanceMap.get(clusterIdx);
//            Point centroid = centroids.get(clusterIdx);
//
//            for (int i = clusterIdx + 1; i < centroids.size(); i++) {
//                double dist = PointUtil.distance(centroid, centroids.get(i));
//
//                if (dist < avgDist * 2.4 && dist > 0) {
//                    // merge clusters
//                    Cluster mergedCluster = new Cluster(cluster.getClusterPoints());
//                    mergedCluster.getClusterPoints().addAll(newClusterList.get(i).getClusterPoints());
//
//                    newClusterList.remove(newClusterList.get(i));
//                    newClusterList.remove(cluster);
//                    newClusterList.add(mergedCluster);
//
//                    if (newClusterList.size() > 1) {
//                        return mergeNearbyClusters(newClusterList);
//                    }
//                }
//            }
//        }
//
//        return newClusterList;
//    }
    public static List<Cluster> mergeNearbyClusters(List<Cluster> clusterList) {
        if (clusterList.size() > 1) {
            List<Point> centroids = new ArrayList<>();
            HashMap clusterAvgDistanceMap = new HashMap<>();

            for (Cluster cluster : clusterList) {
                clusterAvgDistanceMap.put(clusterList.indexOf(cluster), cluster.getAvgDistanceBetweenPoints2());
                centroids.add(cluster.getCentroid2());
            }

            Iterator it = clusterAvgDistanceMap.keySet().iterator();
            List<Cluster> newClusterList = new ArrayList<>(clusterList);
            while (it.hasNext()) {
                int clusterIdx = (int) it.next();
                Cluster cluster = clusterList.get(clusterIdx);

                double avgDist = (double) clusterAvgDistanceMap.get(clusterIdx);
                Point centroid = centroids.get(clusterIdx);

                for (int i = clusterIdx + 1; i < centroids.size(); i++) {
                    double dist = PointUtil.distance(centroid, centroids.get(i));

                    if (dist < avgDist * 3 && dist > 0) {
                        // merge clusters
                        List<MovedPoint> mergedCluster = new ArrayList<>(cluster.getClusterPoints());
                        mergedCluster.addAll(clusterList.get(i).getClusterPoints());
                        newClusterList.remove(clusterList.get(i));
                        newClusterList.remove(cluster);
                        newClusterList.add(new Cluster(mergedCluster));

                        return mergeNearbyClusters(newClusterList);
                    }
                }
            }

            return newClusterList;
        } else {
            return clusterList;
        }
    }
}
