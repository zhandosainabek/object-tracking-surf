package objecttracking.model;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author Zhandos Ainabek
 */
public class ClusterLabeler {

    public ClusterLabeler() {
    }

    public void label(List<Cluster> clustersList, Mat queryImg, Mat trainImg) {
        List<MatOfPoint> queryHullMatList = new ArrayList<>();
        List<MatOfPoint> trainHullMatList = new ArrayList<>();

        // get contours for each cluster
        for (Cluster cluster : clustersList) {
            queryHullMatList.add(cluster.getCountours(true));
            trainHullMatList.add(cluster.getCountours(false));
        }

        // Find centroid for each convex hull
        findCentroid(queryHullMatList, queryImg);
        findCentroid(trainHullMatList, trainImg);

        // Draw contours + hull results
        for (int i = 0; i < clustersList.size(); i++) {
            if (queryHullMatList.get(i).rows() > 2) {
                Imgproc.drawContours(queryImg, queryHullMatList, i, clustersList.get(i).getContourColor(), 2);
            }

            if (trainHullMatList.get(i).rows() > 2) {
                Imgproc.drawContours(trainImg, trainHullMatList, i, clustersList.get(i).getContourColor(), 2);
            }
        }
    }

    private void findCentroid(List<MatOfPoint> hullList, Mat outImage) {
        for (MatOfPoint contour : hullList) {
            Moments moments = Imgproc.moments(contour);
            double area = moments.get_m00();
            double m10 = moments.get_m10();
            double m01 = moments.get_m01();
            if (area != 0) {
                float centerX = (float) (m10 / area);
                float centerY = (float) (m01 / area);

                Features2d.drawKeypoints(outImage, new MatOfKeyPoint(new KeyPoint(centerX, centerY, 5)), outImage, new Scalar(0), 0);
            }
        }
    }
}
