package PatternRecognition;

import java.util.HashSet;
import java.util.Vector;

class ImageAnalyzer {
    private int[][] image;
    private HashSet<String> dfsCache = new HashSet<String>();
    private Boolean debug = false;

    public int[][] GetImage() {
        return this.image.clone();
    }

    public ImageAnalyzer(int[][] inputImage) {
        image = inputImage;
    }

    public ImageAnalyzer(int[][] inputImage, Boolean debug) {
        image = inputImage;
        this.debug = debug;
    }

    public int[] GetHistogram() {
        int[] histogram = new int[256]; // Assuming 8-bit grayscale image
        for (int[] row : image) {
            for (int pixel : row) {
                histogram[pixel]++;
            }
        }
        if (debug) {
            System.out.println("gray-level       #-of-pixels");
            for (int i = 0; i < histogram.length; i++) {
                if (!(histogram[i] == 0)) {
                    System.out.printf("    %d                  %d\n", i, histogram[i]);
                }
            }
        }
        return histogram;
    }

    public void ApplyGaussianFilter() {
        int rows = image.length;
        int cols = image[0].length;
        int[][] result = new int[rows][cols];
        double[][] kernel = {
                { 1.0 / 16, 1.0 / 8, 1.0 / 16 },
                { 1.0 / 8, 1.0 / 4, 1.0 / 8 },
                { 1.0 / 16, 1.0 / 8, 1.0 / 16 }
        };
        for (int i = 0; i < rows; i++) {
            result[i][0] = image[i][0];
            result[i][cols - 1] = image[i][cols - 1];
        }
        for (int i = 0; i < cols; i++) {
            result[0][i] = image[0][i];
            result[rows - 1][i] = image[rows - 1][i];
        }
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                double sum = 0.0;
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        sum += kernel[x + 1][y + 1] * image[i + x][j + y];
                    }
                }
                result[i][j] = (int) Math.round(sum);
            }
        }
        image = result;
    }

    public int GetOtsuThreshold(int[] histogram) {
        // Total number of pixels
        int total = 0;
        for (int i : histogram) {
            total += i;
        }

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histogram[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histogram[t]; // Weight Background
            if (wB == 0)
                continue;
            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;
            sumB += (float) (t * histogram[t]);
            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground
            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }
        return threshold;
    }

    public int[][] GetBinaryArray(int[] histogram, int threshold) {
        int[][] result = new int[image.length][image[0].length];
        for (int i = 0; i < image.length; i++)
            for (int j = 0; j < image[0].length; j++)
                result[i][j] = image[i][j] > threshold ? 1 : 0;

        return result;
    }

    public int[][] ConnectivityAnalysis(int[][] binaryImage) {
        int rows = binaryImage.length;
        int cols = binaryImage[0].length;
        int[][] labeledImage = new int[rows][cols];
        int label = 1;
        dfsCache.clear();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (binaryImage[i][j] == 1 && labeledImage[i][j] == 0) {
                    DFS(binaryImage, labeledImage, label, i, j);
                    label++;
                }
            }
        }
        return labeledImage;
    }

    private void DFS(int[][] binaries, int[][] labeledImage, int label, int i, int j) {
        if (dfsCache.contains(i + "," + j))
            return;
        labeledImage[i][j] = label;
        dfsCache.add(i + "," + j);
        if (i > 0 && binaries[i - 1][j] == 1 && labeledImage[i - 1][j] != label)
            DFS(binaries, labeledImage, label, i - 1, j);
        if (i < binaries.length - 1 && binaries[i + 1][j] == 1 && labeledImage[i + 1][j] != label)
            DFS(binaries, labeledImage, label, i + 1, j);
        if (j > 0 && binaries[i][j - 1] == 1 && labeledImage[i][j - 1] != label)
            DFS(binaries, labeledImage, label, i, j - 1);
        if (j < binaries[0].length - 1 && binaries[i][j + 1] == 1 && labeledImage[i][j + 1] != label)
            DFS(binaries, labeledImage, label, i, j + 1);
    }

    public void ObjectDetection(int[][] connectedBinaryArray) {
        int circularCount = 0;
        int squareCount = 0;
        dfsCache = new HashSet<String>();
        var detectedLabels = new Vector<Integer>();
        for (int i = 0; i < connectedBinaryArray.length; i++)
            for (int j = 0; j < connectedBinaryArray[0].length; j++) {
                if (connectedBinaryArray[i][j] != 0 && !detectedLabels.contains(connectedBinaryArray[i][j])) {
                    if (debug) System.out.println("Object found");
                    int label = connectedBinaryArray[i][j];
                    dfsCache.clear();
                    int area = CalculateArea(connectedBinaryArray, label, i, j);
                    int perimeter = CalculatePerimeter(connectedBinaryArray, label, i, j);
                    double r = (4 * Math.PI * area) / (perimeter * perimeter);
                    if (Math.abs(r - 1) <= 0.1) {
                        if (debug) System.out.println("The object is circular.");
                        circularCount++;
                    } else if (Math.abs(r * 4 - Math.PI) <= 0.1) {
                        if (debug) System.out.println("The object is square.");
                        squareCount++;
                    }
                    detectedLabels.add(connectedBinaryArray[i][j]);
                }
            }
        System.out.println("Count of circles = " + circularCount);
        System.out.println("Count of squares = " + squareCount);
    }

    private int CalculatePerimeter(int[][] connectedBinaryArray, int label, int i, int j) {
        int[] pN = new int[] { 0, 0, 0, 0 };
        int[] oper = new int[] { 0, 0, 0, 0 };
        var boundary = new Vector<String>();
        Boolean isBoundary = false;
        for (int l = 1; l < connectedBinaryArray.length - 1; l++) {
            for (int m = 1; m < connectedBinaryArray[0].length - 1; m++) {
                isBoundary = false;
                if (connectedBinaryArray[l][m] == label) {
                    pN[0] = connectedBinaryArray[l + 1][m];
                    pN[1] = connectedBinaryArray[l][m - 1];
                    pN[2] = connectedBinaryArray[l - 1][m];
                    pN[3] = connectedBinaryArray[l][m + 1];
                    for (int k = 0; k < 4 && !isBoundary; k++) {
                        if (pN[k] == oper[k])
                            isBoundary = true;
                        if (isBoundary) {
                            boundary.add(l + "," + m);
                        }
                    }
                }
            }
        }
        return boundary.size();
    }

    private int CalculateArea(int[][] connectedBinaryArray, int label, int i, int j) {
        if (i < 0 || i >= connectedBinaryArray.length || j < 0 || j >= connectedBinaryArray[0].length)
            return 0;
        if (dfsCache.contains(i + "," + j) || connectedBinaryArray[i][j] != label)
            return 0;

        int sum = 1;
        dfsCache.add(i + "," + j);
        sum += CalculateArea(connectedBinaryArray, label, i - 1, j);
        sum += CalculateArea(connectedBinaryArray, label, i + 1, j);
        sum += CalculateArea(connectedBinaryArray, label, i, j - 1);
        sum += CalculateArea(connectedBinaryArray, label, i, j + 1);
        return sum;
    }
}