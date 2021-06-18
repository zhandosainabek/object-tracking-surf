package objecttracking.model;

import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Zhandos Ainabek
 */
public class ClusterMatch {

    private Cluster prevCluster;
    private Cluster currentCluster;
    private double matchingScore;

    public ClusterMatch() {
    }

    public ClusterMatch(Cluster prevCluster, Cluster currentCluster) {
        this.prevCluster = prevCluster;
        this.currentCluster = currentCluster;
    }

    public ClusterMatch(Cluster prevCluster, Cluster currentCluster, double matchingScore) {
        this.prevCluster = prevCluster;
        this.currentCluster = currentCluster;
        this.matchingScore = matchingScore;
    }

    public Cluster getPrevCluster() {
        return prevCluster;
    }

    public Cluster getCurrentCluster() {
        return currentCluster;
    }

    public double matchClusters() {
        return Imgproc.matchShapes(prevCluster.getCountours(true), currentCluster.getCountours(true),
                Imgproc.CV_CONTOURS_MATCH_I1, 0);
    }

    public double matchClusters(Cluster clusterOne, Cluster ClusterTwo) {
        return Imgproc.matchShapes(clusterOne.getCountours(true), ClusterTwo.getCountours(true),
                Imgproc.CV_CONTOURS_MATCH_I1, 0);
    }

    /**
     * @return the matchingScore
     */
    public double getMatchingScore() {
        return matchingScore;
    }

    /**
     * @param matchingScore the matchingScore to set
     */
    public void setMatchingScore(double matchingScore) {
        this.matchingScore = matchingScore;
    }
}
