package objecttracking.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import objecttracking.gui.MainPanel;
import objecttracking.utils.ImageUtil;
import objecttracking.utils.MergerUtil;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Zhandos Ainabek
 */
public class SurfProcessor {

    private FeatureDetector detector;
    private MatOfKeyPoint queryImgKeyPoints, trainImgKeyPoints;
    private List<KeyPoint> moreQueryImgKeyPoints, moreTrainImgKeyPoints;
    private LinkedList<DMatch> goodMatches, moreGoodMatches;
    private StringBuilder msgText = new StringBuilder();

    private List<Cluster> prevFrameClusterList;

    public SurfProcessor() {
        prevFrameClusterList = new ArrayList<>();
    }

    public BufferedImage[] runSurfAlg(BufferedImage queryImg, BufferedImage trainImg,
            boolean blurAndSharpen, boolean redFilter, boolean greenFilter, boolean blueFilter,
            double ratioThreshold, int neighbourRadius, double minDispThreshold,
            double maxDispThreshold, double zValue) {

//        try {
//            // for testing: read image from file
//            queryImg = ImageIO.read(new File("/Users/zhandosainabek/Downloads/opencv_test_01.jpg"));
//            trainImg = ImageIO.read(new File("/Users/zhandosainabek/Downloads/opencv_test_02.jpg"));
//        } catch (IOException ex) {
//            Logger.getLogger(SurfProcessor.class.getName()).log(Level.SEVERE, null, ex);
//        }

        detector = FeatureDetector.create(FeatureDetector.SURF);
        moreGoodMatches = new LinkedList<>();
        queryImgKeyPoints = new MatOfKeyPoint();
        trainImgKeyPoints = new MatOfKeyPoint();

        ImageProcessor imgProcessor = new ImageProcessor();
        Clusterer clusterer = new Clusterer();
        List<DMatch> allMatches = new ArrayList<>();
        Mat queryImage = ImageUtil.convertBufferedImageToMat(queryImg);
        Mat trainImage = ImageUtil.convertBufferedImageToMat(trainImg);
        Mat processedQueryImg = new Mat(), processedTrainImg = new Mat();

        // apply filterings and SURF on filtered images
        if (blurAndSharpen) {
            // blur and sharpen images
            imgProcessor.blurAndSharpen(queryImage, processedQueryImg);
            imgProcessor.blurAndSharpen(trainImage, processedTrainImg);
        } else {
            processedQueryImg = queryImage;
            processedTrainImg = trainImage;
        }

        if (!redFilter && !greenFilter && !blueFilter) {
            allMatches = performSurf(processedQueryImg, processedTrainImg, ratioThreshold,
                    minDispThreshold, maxDispThreshold, false);
        } else {
            BufferedImage[] filteredImages = imgProcessor.getFilteredImages(
                    queryImg, trainImg, redFilter, greenFilter, blueFilter);
            Mat[] processedImgArr = new Mat[filteredImages.length];

            if (blurAndSharpen) {
                for (int i = 0; i < filteredImages.length; i++) {
                    if (filteredImages[i] != null) {
                        filteredImages[i] = imgProcessor.blurAndSharpen(filteredImages[i]);
                        processedImgArr[i] = ImageUtil.convertBufferedImageToMat(filteredImages[i]);
                    }
                }
            } else {
                for (int i = 0; i < filteredImages.length; i++) {
                    processedImgArr[i] = ImageUtil.convertBufferedImageToMat(filteredImages[i]);
                }
            }

            List<DMatch> tempDMatchList;
            if (redFilter) {
                tempDMatchList = performSurf(processedImgArr[0], processedImgArr[1],
                        ratioThreshold, minDispThreshold, maxDispThreshold, false);
                allMatches.addAll(tempDMatchList);
            }
            if (greenFilter) {
                int initQueryIdx = queryImgKeyPoints.toList().size();
                int initTrainIdx = trainImgKeyPoints.toList().size();
                tempDMatchList = performSurf(processedImgArr[2], processedImgArr[3],
                        ratioThreshold, minDispThreshold, maxDispThreshold, false);

                for (DMatch dm : tempDMatchList) {
                    dm.queryIdx = dm.queryIdx + initQueryIdx;
                    dm.trainIdx = dm.trainIdx + initTrainIdx;
                }
                allMatches.addAll(tempDMatchList);
            }
            if (blueFilter) {
                int initQueryIdx = queryImgKeyPoints.toList().size();
                int initTrainIdx = trainImgKeyPoints.toList().size();
                tempDMatchList = performSurf(processedImgArr[4], processedImgArr[5],
                        ratioThreshold, minDispThreshold, maxDispThreshold, false);

                for (DMatch dm : tempDMatchList) {
                    dm.queryIdx = dm.queryIdx + initQueryIdx;
                    dm.trainIdx = dm.trainIdx + initTrainIdx;
                }
                allMatches.addAll(tempDMatchList);
            }
        }

        // Cluster by displacement values
        List<Cluster> clustersList = clusterer.clusterByDisplacement(
                allMatches, queryImgKeyPoints, trainImgKeyPoints);

        // Merge clusters with very similar displacement
        MergerUtil.mergeSimilarDispClusters(clustersList, msgText);

        // OUTPUT ###############################################################
        msgText.append("\nClusters after merge: ").append(clustersList.size()).append("\n");

//        PrintUtil.printClusterList(clustersList, msgText);
        // #####################################################################
        // if neighbour radius is not -1, apply SURF on cropped images
        if (neighbourRadius > 0) {
            clustersList = cropAndApplySurf(processedQueryImg, processedTrainImg, clustersList, neighbourRadius);

            msgText.append("INITIAL GOOD MATCHES SIZE: ").append(allMatches.size());
            // add additionally found matching points
            allMatches.addAll(moreGoodMatches);
            msgText.append("\nEXTENDED MATCHES SIZE: ").append(allMatches.size());
        }

        if (zValue > 0) {
            // TODO needs additional testing: clustering by distance
            clustersList = clusterer.clusterByDistance(clustersList, zValue, msgText);
        }

        // set points color and contour color for each cluster
        for (Cluster cluster : clustersList) {
            Random randomGenerator = new Random();
            Scalar color = new Scalar(randomGenerator.nextInt(256),
                    randomGenerator.nextInt(256), randomGenerator.nextInt(256));
            cluster.setContourColor(color);
            cluster.setPointsColor(color);
        }

        // track clusters between consecutive frames
        // use prevFrameClusterList
        System.out.println("Frame Cluster List Centrois");
        for (Cluster cluster : prevFrameClusterList) {
            System.out.println("Previous Cluster centroids: " + cluster.getCentroid());
        }

        System.out.println("Matched clusters:");
        List<ClusterMatch> matchedClustersList = new ArrayList<>();
        for (Cluster cluster : clustersList) {
            if (cluster.getSize() > 2) {
                System.out.println("Current Cluster centroids: " + cluster.getCentroid());
                MatOfPoint currClusterContour = cluster.getCountours(true);
                for (Cluster prevCluster : prevFrameClusterList) {
                    if (prevCluster.getSize() > 2) {
                        MatOfPoint prevClusterContour = prevCluster.getCountours(true);
                        MatOfPoint2f contour = new MatOfPoint2f(prevClusterContour.toArray());
                        double res = Imgproc.pointPolygonTest(contour, cluster.getCentroid(), false);

                        if (res > 0) { // point (centroid) is inside the cluster
                            // set the same color for matched clusters via consecutive frames
                            cluster.setContourColor(prevCluster.getContourColor());
                            cluster.setPointsColor(prevCluster.getPointsColor());

                            // get match score
                            double score = Imgproc.matchShapes(currClusterContour, prevClusterContour, Imgproc.CV_CONTOURS_MATCH_I2, 0);

                            matchedClustersList.add(new ClusterMatch(prevCluster, cluster, score));

                            System.out.println("    Current cluster #" + clustersList.indexOf(cluster)
                                    + " with centroid " + cluster.getCentroid() + " matched with cluster #"
                                    + prevFrameClusterList.indexOf(prevCluster) + " in previous frame with centroid "
                                    + prevCluster.getCentroid() + "; Matching score is " + score);
                        }
                    }
                }
            }
        }

        // Remove duplicate matched clusters from matchedClustersList
        matchedClustersList = removeDuplicateClusterMatches(matchedClustersList);

        // Copy points from prevCluster adding the current mean displacement to currentCluster
        for (ClusterMatch matchedCluster : matchedClustersList) {
            Cluster currCluster = clustersList.get(clustersList.indexOf(matchedCluster.getCurrentCluster()));
            Cluster prevCluster = prevFrameClusterList.get(prevFrameClusterList.indexOf(matchedCluster.getPrevCluster()));

            List<MovedPoint> prevClusterPoints = prevCluster.getClusterPoints();
            Displacement meanDisplacement = currCluster.getMeanDisplacement();

            // add displacement to previous cluster points and add them to the current cluster
            for (MovedPoint mp : prevClusterPoints) {
                mp.getNewPoint().pt.x += meanDisplacement.getX();
                mp.getNewPoint().pt.y += meanDisplacement.getY();
                mp.getOldPoint().pt.x += meanDisplacement.getX();
                mp.getOldPoint().pt.y += meanDisplacement.getY();
            }
            currCluster.getClusterPoints().addAll(prevClusterPoints);
        }

        prevFrameClusterList = clustersList;

        // draw output results and get as an array of images
        BufferedImage[] resultImgArr = getOutputImgsArr(
                processedQueryImg, processedTrainImg, allMatches, clustersList);

        msgText.append("\nTotal number of interest points in the first frame: ").append(queryImgKeyPoints.toList().size()).append("\n");
        msgText.append("Total number of interest points in the second frame: ").append(trainImgKeyPoints.toList().size()).append("\n");
        msgText.append("Total number of matching keypoints: ").append(allMatches.size()).append("\n");

        // set output message to textarea in the panel
        MainPanel.setOutputMessage(msgText.toString());

        return resultImgArr;
    }

    private List<DMatch> performSurf(Mat queryImg, Mat trainImg, double ratioThreshold,
            double minMoveThreshold, double maxMoveThreshold, boolean isCropped) {

        goodMatches = new LinkedList<>();
        moreQueryImgKeyPoints = new ArrayList<>();
        moreTrainImgKeyPoints = new ArrayList<>();

        Mat queryGrayImg = ImageProcessor.getGrayImg(queryImg);
        Mat trainGrayImg = ImageProcessor.getGrayImg(trainImg);
        MatOfKeyPoint inQueryImgKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint inTrainImgKeyPoints = new MatOfKeyPoint();

        // find keypoints
        detector.detect(trainGrayImg, inTrainImgKeyPoints);
        detector.detect(queryGrayImg, inQueryImgKeyPoints);

        // Extract descriptors
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        Mat queryImgDescriptor = new Mat();
        Mat trainImgDescriptor = new Mat();
        extractor.compute(queryGrayImg, inQueryImgKeyPoints, queryImgDescriptor);
        extractor.compute(trainGrayImg, inTrainImgKeyPoints, trainImgDescriptor);

        // match keypoint descriptors
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        List<MatOfDMatch> matches = new ArrayList<>();
        if (!queryImgDescriptor.empty() && !trainImgDescriptor.empty() && trainImgDescriptor.size().height > 1.0) {
            matcher.knnMatch(queryImgDescriptor, trainImgDescriptor, matches, 2); // for each object keypoint takes 2 best matches
        }
        if (!matches.isEmpty()) {
            // find moved keypoints
            findGoodMatches(matches, ratioThreshold,
                    minMoveThreshold, maxMoveThreshold, inQueryImgKeyPoints, inTrainImgKeyPoints);
            // remove repeatedly matched keypoints, leaving one with the smalles distance
            removeDuplicates(goodMatches);
        }
        // if SURF applied to cropped images, add additional found keypoints
        if (isCropped) {
            for (DMatch dm : goodMatches) {
                moreQueryImgKeyPoints.add(inQueryImgKeyPoints.toList().get(dm.queryIdx));
                moreTrainImgKeyPoints.add(inTrainImgKeyPoints.toList().get(dm.trainIdx));
            }
        } else {
            List<KeyPoint> queryImgKeyPointList = new ArrayList<>(queryImgKeyPoints.toList());
            List<KeyPoint> trainImgKeyPointList = new ArrayList<>(trainImgKeyPoints.toList());
            queryImgKeyPointList.addAll(inQueryImgKeyPoints.toList());
            trainImgKeyPointList.addAll(inTrainImgKeyPoints.toList());
            queryImgKeyPoints.fromList(queryImgKeyPointList);
            trainImgKeyPoints.fromList(trainImgKeyPointList);
        }

        return goodMatches;
    }

    private LinkedList<DMatch> findGoodMatches(List<MatOfDMatch> allMatches,
            double ratioThreshold, double minMoveThreshold, double maxMoveThreshold,
            MatOfKeyPoint inQueryImgKeyPoints, MatOfKeyPoint inTrainImgKeyPoints) {

        for (MatOfDMatch matOfDMatch : allMatches) {
            List<DMatch> temp = matOfDMatch.toList();

            if (temp.size() > 1) {
                double distRatio = temp.get(1).distance / temp.get(0).distance;

                if (distRatio >= ratioThreshold) {
                    Point p1 = inQueryImgKeyPoints.toList().get(temp.get(0).queryIdx).pt;
                    Point p2 = inTrainImgKeyPoints.toList().get(temp.get(0).trainIdx).pt;

                    double xDiff = Math.abs(p1.x - p2.x);
                    double yDiff = Math.abs(p1.y - p2.y);

                    // check if keypoint position in the first image corresponds to
                    // keypoint position in the second image
                    if ((xDiff > minMoveThreshold && xDiff < maxMoveThreshold)
                            || (yDiff > minMoveThreshold && yDiff < maxMoveThreshold)) {
                        goodMatches.add(temp.get(0));
                    }
                }
            }
        }
        return goodMatches;
    }

    // TODO needs additional testing
    private LinkedList<DMatch> removeDuplicates(LinkedList<DMatch> goodMatches) {
        List<Integer> trainIdxs = new ArrayList<>();

        for (int i = 0; i < goodMatches.size(); i++) {
            int trainIdx = goodMatches.get(i).trainIdx;
            trainIdxs.add(trainIdx);
            if (trainIdxs.contains(trainIdx)) {
                int duplIndex = trainIdxs.indexOf(trainIdx);
                if (duplIndex != i) {
                    if (goodMatches.get(i).lessThan(goodMatches.get(duplIndex))) {
                        goodMatches.remove(duplIndex);
                        trainIdxs.remove(duplIndex);
                        i--;
                    } else {
                        goodMatches.remove(i);
                        trainIdxs.remove(i);
                        i--;
                    }
                }
            }
        }

        return goodMatches;
    }

    private List<Cluster> cropAndApplySurf(Mat queryImg, Mat trainImg,
            List<Cluster> clusterList, int radius) {

        List<KeyPoint> queryImgKeyPointList = new ArrayList<>(queryImgKeyPoints.toList());
        List<KeyPoint> trainImgKeyPointList = new ArrayList<>(trainImgKeyPoints.toList());
        int lastQueryIdx = queryImgKeyPointList.size();
        int lastTrainIdx = trainImgKeyPointList.size();

        List<Cluster> newClusterList = new ArrayList<>();

        for (Cluster cluster : clusterList) {
            Cluster newCluster = new Cluster();

            for (MovedPoint clusterPoint : cluster.getClusterPoints()) {
                newCluster.getClusterPoints().add(clusterPoint);
                Point keypoint1 = clusterPoint.getOldPoint().pt;
                Point keypoint2 = clusterPoint.getNewPoint().pt;

                double startX1 = (keypoint1.x - radius);
                double startY1 = (keypoint1.y - radius);
                double startX2 = (keypoint2.x - radius);
                double startY2 = (keypoint2.y - radius);

                if (startX1 < 0) {
                    startX1 = 0;
                }
                if (startY1 < 0) {
                    startY1 = 0;
                }
                if (startX2 < 0) {
                    startX2 = 0;
                }
                if (startY2 < 0) {
                    startY2 = 0;
                }

                // crop the query image with radius around the keypoint
                Size rectSize = new Size(radius * 2.0, radius * 2.0);
                Point startPoint1 = new Point(startX1, startY1);
                Point startPoint2 = new Point(startX2, startY2);

                if ((startPoint1.x + rectSize.width) > queryImg.size().width) {
                    rectSize.width = queryImg.size().width - startPoint1.x;
                }

                if ((startPoint1.y + rectSize.height) > queryImg.size().height) {
                    rectSize.height = queryImg.size().height - startPoint1.y;
                }

                Rect rect1 = new Rect(startPoint1, rectSize);
                Mat cropped1 = queryImg.submat(rect1);

                // crop the train image with radius around the keypoint
                if ((startPoint2.x + 2 * radius) > trainImg.size().width) {
                    rectSize.width = trainImg.size().width - startPoint2.x;
                }

                if ((startPoint2.y + 2 * radius) > trainImg.size().height) {
                    rectSize.height = trainImg.size().height - startPoint2.y;
                }

                Rect rect2 = new Rect(startPoint2, rectSize);
                Mat cropped2 = trainImg.submat(rect2);

                // apply SURF algorithm to cropped images
                performSurf(cropped1, cropped2, 1.5, 0.5, 3.0, true);
                List<DMatch> tempMatches = new ArrayList<>(goodMatches);

                for (int j = 0; j < tempMatches.size(); j++) {
                    moreGoodMatches.add(new DMatch(lastQueryIdx++, lastTrainIdx++, tempMatches.get(j).distance));
                    double x = moreQueryImgKeyPoints.get(j).pt.x + startX1;
                    double y = moreQueryImgKeyPoints.get(j).pt.y + startY1;
                    float size = moreQueryImgKeyPoints.get(j).size;
                    KeyPoint kp1 = new KeyPoint((float) x, (float) y, size);
                    queryImgKeyPointList.add(kp1);

                    x = moreTrainImgKeyPoints.get(j).pt.x + startX2;
                    y = moreTrainImgKeyPoints.get(j).pt.y + startY2;
                    size = moreTrainImgKeyPoints.get(j).size;
                    KeyPoint kp2 = new KeyPoint((float) x, (float) y, size);
                    trainImgKeyPointList.add(kp2);
                    newCluster.getClusterPoints().add(new MovedPoint(kp1, kp2));
                }
            }
            newClusterList.add(newCluster);
        }
        queryImgKeyPoints.fromList(queryImgKeyPointList);
        trainImgKeyPoints.fromList(trainImgKeyPointList);

        return newClusterList;
    }

    private Mat[] getLabeledPointsImg(Mat processedQueryImg, Mat processedTrainImg, List<Cluster> clustersList) {
        Mat[] outputImg = new Mat[2];
        outputImg[0] = new Mat();
        outputImg[1] = new Mat();
        List<KeyPoint> queryImgKeyPointList;
        List<KeyPoint> trainImgKeyPointList;

        for (Cluster cluster : clustersList) {
            queryImgKeyPointList = new ArrayList<>();
            trainImgKeyPointList = new ArrayList<>();
            for (MovedPoint mp : cluster.getClusterPoints()) {
                queryImgKeyPointList.add(mp.getOldPoint());
                trainImgKeyPointList.add(mp.getNewPoint());
            }
            MatOfKeyPoint queryImgPoints = new MatOfKeyPoint();
            MatOfKeyPoint trainImgPoints = new MatOfKeyPoint();

            queryImgPoints.fromList(queryImgKeyPointList);
            trainImgPoints.fromList(trainImgKeyPointList);

            Features2d.drawKeypoints(processedQueryImg, queryImgPoints, outputImg[0], cluster.getPointsColor(), 0); // 0 DEFAULT flag
            Features2d.drawKeypoints(processedTrainImg, trainImgPoints, outputImg[1], cluster.getPointsColor(), 0); // 0 DEFAULT flag
        }

        return outputImg;
    }

    private BufferedImage[] getOutputImgsArr(Mat processedQueryImg, Mat processedTrainImg,
            List<DMatch> allMatches, List<Cluster> clustersList) { // TODO change List<Cluster> to List<ClusterMatch>

        Mat originalImg = processedQueryImg;

        BufferedImage[] resultImgArr = new BufferedImage[7];
        MatOfDMatch gm = new MatOfDMatch();
        gm.fromList(allMatches);

        // draw matches with matching lines
        Mat imgMatches = new Mat();
        Features2d.drawMatches(processedQueryImg, queryImgKeyPoints,
                processedTrainImg, trainImgKeyPoints, gm, imgMatches);
        resultImgArr[0] = ImageUtil.convertMatToBufferedImage(imgMatches);

        // drawing all found points
        Mat keyPointsOut = new Mat();
        Features2d.drawKeypoints(processedQueryImg, queryImgKeyPoints, keyPointsOut);
        Highgui.imwrite("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/dataset_1(1).jpg", keyPointsOut);
        resultImgArr[1] = ImageUtil.convertMatToBufferedImage(keyPointsOut);
        Mat keyPointsOut1 = new Mat();
        Features2d.drawKeypoints(processedTrainImg, trainImgKeyPoints, keyPointsOut1);
        Highgui.imwrite("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/dataset_1(2).jpg", keyPointsOut1);
        resultImgArr[2] = ImageUtil.convertMatToBufferedImage(keyPointsOut1);

        // draw only good keypoints with random colour for each cluster
        Mat[] goodPoints = getLabeledPointsImg(processedQueryImg, processedTrainImg, clustersList); // TODO
        resultImgArr[3] = ImageUtil.convertMatToBufferedImage(goodPoints[0]);
        resultImgArr[4] = ImageUtil.convertMatToBufferedImage(goodPoints[1]);

        // for testing
        Highgui.imwrite("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/colour_filtered.jpg", goodPoints[0]);

        // LabelClusters
        List<KeyPoint> keypointList = new ArrayList<>(queryImgKeyPoints.toList());

        Mat out = new Mat();
        MatOfKeyPoint kpList = new MatOfKeyPoint();
        kpList.fromList(keypointList);
        Features2d.drawKeypoints(originalImg, kpList, out);

        // for testing
        Highgui.imwrite("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/new/result1.jpg", out);

        int inPointsCounter = 0, outPointsCounter = 0;
        for (KeyPoint kp : keypointList) {
            boolean isInside = false;
            for (Cluster c : clustersList) {
                MatOfPoint prevClusterContour = c.getCountours(true);
                MatOfPoint2f contour = new MatOfPoint2f(prevClusterContour.toArray());

                if (Imgproc.pointPolygonTest(contour, kp.pt, false) >= 0) {
                    isInside = true;
                }
            }
            if (!isInside) {
                outPointsCounter++;
            } else {
                inPointsCounter++;
            }
        }
        ClusterLabeler clusterLabeler = new ClusterLabeler();
        clusterLabeler.label(clustersList, out, processedTrainImg);
        Highgui.imwrite("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/new/Result.jpg", out);
        resultImgArr[5] = ImageUtil.convertMatToBufferedImage(out);
        resultImgArr[6] = ImageUtil.convertMatToBufferedImage(processedTrainImg);

        msgText.append("\nNumber of inside cluster points: ").append(inPointsCounter).append("\n");
        msgText.append("Number of outside cluster points: ").append(outPointsCounter).append("\n");

        // for evaluation
//        BufferedImage image1 = ImageUtil.convertMatToBufferedImage(originalImg);
//        BufferedImage resultImg = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());
//        for (int x = 0; x < image1.getWidth(); x++) {
//            for (int y = 0; y < image1.getHeight(); y++) {
//                Point pt = new Point(x, y);
//                boolean isIn = false;
//                for (Cluster c : clustersList) {
//                    MatOfPoint prevClusterContour = c.getCountours(true);
//                    MatOfPoint2f contour = new MatOfPoint2f(prevClusterContour.toArray());
//
//                    if (Imgproc.pointPolygonTest(contour, pt, false) >= 0) {
//                        isIn = true;
//                    }
//                }
//                if (isIn) {
//                    resultImg.setRGB(x, y, -1);
//                } else {
//                    resultImg.setRGB(x, y, 0);
//                }
//            }
//        }
//        try {
////            ImageIO.write(resultImg, "jpeg", new File("/Users/jamboproduction/Documents/UoM/Dissertation/Final Report/evaluation results/new/whileBlack1.jpeg"));
//            ImageIO.write(resultImg, "jpeg", new File("/Users/zhandosainabek/Downloads/opencv_test_01.jpg"));
//        } catch (IOException ex) {
//            Logger.getLogger(SurfProcessor.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return resultImgArr;
    }

    private List<ClusterMatch> removeDuplicateClusterMatches(List<ClusterMatch> matchedClustersList) {
        List<ClusterMatch> newMatchedClusterList = new ArrayList<>(matchedClustersList);
        for (int i = 0; i < matchedClustersList.size() - 1; i++) {
            ClusterMatch currentMatch = matchedClustersList.get(i);
            for (int j = i + 1; j < matchedClustersList.size(); j++) {
                ClusterMatch nextMatch = matchedClustersList.get(j);
                if (currentMatch.getCurrentCluster().equals(nextMatch.getCurrentCluster())
                        || currentMatch.getPrevCluster().equals(nextMatch.getPrevCluster())) {
                    if (currentMatch.getMatchingScore() < nextMatch.getMatchingScore()) {
                        newMatchedClusterList.remove(nextMatch);
                    } else {
                        newMatchedClusterList.remove(currentMatch);
                    }
                    return removeDuplicateClusterMatches(newMatchedClusterList);
                }
            }
        }

        return newMatchedClusterList;
    }
}
