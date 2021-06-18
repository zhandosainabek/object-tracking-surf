package objecttracking.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import objecttracking.utils.MergerUtil;
import objecttracking.utils.PointUtil;
import objecttracking.utils.SorterUtil;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;

/**
 *
 * @author Zhandos Ainabek
 */
public class Clusterer {

    public Clusterer() {
    }

    // TODO
    public List<Cluster> clusterByDistance(List<Cluster> clustersList,
                                            double zValue, StringBuilder msgText) {

        // sorting Points in clusters by distance from the initial point(0, 0)
        List<HashMap> sortedMapList = SorterUtil.sortClustersFromOrigin(clustersList);

//        msgText.append("\nSORTED MAP: \n");
//        PrintUtil.printMapList(sortedMapList, msgText);

        List<Cluster> sortedClusterList = new ArrayList<>();
        for (HashMap clusterMap : sortedMapList) {
            List<MovedPoint> clusterPoints = new ArrayList<>(clusterMap.keySet());
            sortedClusterList.add(new Cluster(clusterPoints));
        }

        List<Cluster> newClusterList = new ArrayList<>();

        for (int i = 0; i < sortedClusterList.size(); i++) {
            List<Cluster> tmpList = sortedClusterList.subList(i, i + 1);
            tmpList = clusterByDistRecursion(tmpList, zValue, msgText);

            // TODO reassign all the points to clusters
            reassignClusterPoints(tmpList);

            // TODO merge clusters which have less than 3 keypoints to the nearest cluster
//            tmpList = MergerUtil.mergeSmallClusters(tmpList);

            // TODO merge clusters centroids of which lies within the average distance
            // of neighbouring cluster
            tmpList = MergerUtil.mergeNearbyClusters(tmpList);

            newClusterList.addAll(tmpList);
        }

//        PrintUtil.printClusterList(newClusterList, msgText);

        return newClusterList;
    }

    // TODO z value is not correctly calculated for the first two points
    private List<Cluster> clusterByDistRecursion(List<Cluster> clusterList,
                                            double zValue, StringBuilder msgText) {

        // TODO
        if (clusterList.size() > 1)
            reassignClusterPoints(clusterList);

        List<Cluster> newClusterList = new ArrayList<>(clusterList);

        for (Cluster cluster : clusterList) {
//            msgText.append("Cluster map #").append((clusterList.indexOf(cluster) + 1)).append(":\n");
            double distMidpoint, distMSum = 0.0, xSum = 0.0, ySum = 0.0, midX = 0.0, midY = 0.0, z = 0.0;
            List<Double> distList = new ArrayList<>();
//            System.out.println("Cluster #" + clusterList.indexOf(cluster));
            for (int i = 0; i < cluster.getClusterPoints().size(); i++) {
                MovedPoint mp = cluster.getClusterPoints().get(i);
                int ptIdx = cluster.getClusterPoints().indexOf(mp);

                xSum += mp.getNewPoint().pt.x;
                ySum += mp.getNewPoint().pt.y;
//                if (ptIdx == 0) {
//                    midX = mp.getNewPoint().pt.x;
//                    midY = mp.getNewPoint().pt.y;
//                }

                distMidpoint = PointUtil.distance(mp.getNewPoint().pt, new Point(midX, midY));
//                distList.add(distMidpoint);
                midX = xSum / (double) (ptIdx + 1);
                midY = ySum / (double) (ptIdx + 1);


                if (ptIdx > 0) {
                    distList.add(distMidpoint);
                    distMSum += distMidpoint;
                    double mean = distMSum / (double) (ptIdx + 1);

                    double sdSum = 0.0;
                    for (double d : distList) {
                        sdSum += Math.pow((d - mean), 2.0);
                    }
                    double sd = Math.sqrt(sdSum / (double) (distList.size() - 1));

                    if (sd != 0.0) {
                        z = Math.abs(distMidpoint - mean) / sd;
                    }

//                    System.out.print("KeyPoint" + ptIdx + ": " + mp.getNewPoint().pt
//                            + "; D(P" + ptIdx + ", M) = " + distMidpoint
//                            + "; New midpoint: (" + midX + ", " + midY + "); ");
//                    System.out.println("Mean value: " + mean + "; Std deviation: " + sd + "; Z value: " + z);
//                    msgText.append("KeyPoint").append(ptIdx).append(": ").append(mp.getNewPoint().pt)
//                            .append("; New midpoint: (")
//                            .append(midX).append(", ").append(midY).append("); ").append("D(P").append(ptIdx)
//                            .append(", M) = ").append(Math.round(distMidpoint));
//
//                    msgText.append("; Mean value: ").append(mean).append("; Std deviation: ")
//                            .append(sd).append("; Z value: ").append(z).append("; D/SD = ")
//                            .append(distMidpoint / sd).append(";\n");
                    System.out.println("Mean value: " + mean + "; Std deviation: " + sd + "; Z value: " + z);
                }
                if (z > zValue) {
                    Cluster tmpList = new Cluster();
                    tmpList.getClusterPoints().add(mp);
                    newClusterList.get(clusterList.indexOf(cluster)).getClusterPoints().remove(mp);
                    newClusterList.add(tmpList);
                    System.err.println("Merged!!!!!");
                    // TODO reassign points
                    return clusterByDistRecursion(newClusterList, zValue, msgText);
                }

            }
            System.out.println("");
        }

        return newClusterList;
    }

    private List<Cluster> reassignClusterPoints(List<Cluster> clusterList) {
        List<Point> centroids = new ArrayList<>();
        // TODO find centroid for each cluster
        for (Cluster cluster : clusterList)
            centroids.add(cluster.getCentroid2());

        // TODO reassign points to clusters
        for (Cluster cluster : clusterList) {
            for (int i = 0; i < cluster.getClusterPoints().size(); i++) {
                MovedPoint mp = cluster.getClusterPoints().get(i);
                Point p = mp.getNewPoint().pt;
                int minDistCentroidIdx = -1;
                double minDist = 400.0;

                for (Point centroid : centroids) {
                    double dist = PointUtil.distance(p, centroid);
                    if (dist < minDist) {
                        minDist = dist;
                        minDistCentroidIdx = centroids.indexOf(centroid);
                    }
                }
                if (minDistCentroidIdx != -1 && minDistCentroidIdx != clusterList.indexOf(cluster)) {
                    clusterList.get(minDistCentroidIdx).getClusterPoints().add(mp);
                    cluster.getClusterPoints().remove(mp);
                    i--;
                }
            }
        }

        return clusterList;
    }

    public List<Cluster> clusterByDisplacement(List<DMatch> allMatches,
            MatOfKeyPoint queryImgKeyPoints, MatOfKeyPoint trainImgKeyPoints) {
        List<Cluster> clustersList = new ArrayList<>();

        for (DMatch dm : allMatches) {
            KeyPoint p1 = queryImgKeyPoints.toList().get(dm.queryIdx);
            KeyPoint p2 = trainImgKeyPoints.toList().get(dm.trainIdx);

            MovedPoint mp = new MovedPoint(p1, p2);

            // OUTPUT ###############################################################
            System.out.println("Point1: " + mp.getOldPoint() + "; Point2: " + mp.getNewPoint()
                    + "; Displacement: {" + Math.round(mp.getNewPoint().pt.x - mp.getOldPoint().pt.x) + ", "
                    + Math.round(mp.getNewPoint().pt.y - mp.getOldPoint().pt.y) + "}");
            // #####################################################################

            int clusterIndex = isNewCluster(mp, clustersList);
            if (clusterIndex == -1) {
                Cluster newCluster = new Cluster();
                newCluster.getClusterPoints().add(mp);
                clustersList.add(newCluster);
            } else {
                clustersList.get(clusterIndex).getClusterPoints().add(mp);
            }
        }

        return clustersList;
    }

    private int isNewCluster(MovedPoint mp, List<Cluster> clustersList) {
        Displacement d = mp.getRoundedDisplacement();
        double x = d.getX();
        double y = d.getY();

        for (int i = 0; i < clustersList.size(); i++) {
            Cluster currentCluster = clustersList.get(i);
            Displacement meanDisp = currentCluster.getRoundedMeanDisplacement();

            if (((x - 1) == meanDisp.getX() || x == meanDisp.getX() || (x + 1) == meanDisp.getX())
                    && ((y - 1) == meanDisp.getY() || y == meanDisp.getY() || (y + 1) == meanDisp.getY())) {

                return i;
            }
        }
        return -1;
    }
}
