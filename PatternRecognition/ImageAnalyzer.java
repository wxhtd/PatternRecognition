package PatternRecognition;

import java.util.HashMap;
import java.util.Vector;

class ImageAnalyzer {
    private Boolean debug = false;
    private int[][] image;    
    private int[][] imageBoundary;

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

    //Noise cancelling with Gaussian filter
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

    //Otsu's algorithm: find threshold which renders the minimum variance
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
        System.out.println("Threshold = " + threshold);
        return threshold;
    }

    //For each pixel, if larger than threshold, set to 1; otherwise, set to 0
    public int[][] GetBinaryArray(int[] histogram, int threshold) {
        int[][] result = new int[image.length][image[0].length];
        for (int i = 0; i < image.length; i++)
            for (int j = 0; j < image[0].length; j++)
                result[i][j] = image[i][j] > threshold ? 1 : 0;
        if (debug)
            displayImage(result);
        return result;
    }

    private String GetNode(int i, int j) {
        return i + "," + j;
    }

    //Find and label connected pixels
    //Naive solution: DFS from each pixel which value is 1
    //DFS won't work for real world images, will get StackOverFlow exception
    //Use DSU with optimized implementation instead
    public int[][] ConnectivityAnalysis(int[][] binaryImage) {
        int rows = binaryImage.length;
        int cols = binaryImage[0].length;
        int[][] labeledImage = new int[rows][cols];
        int label = 1;

        //DSU implemented with tree structure
        var dsu = new DSU_Enhance();
        //Initialize nodes
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (binaryImage[i][j] == 1) {
                    dsu.Add(GetNode(i, j));
                }
            }
        }
        //Union connected pixels
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (binaryImage[i][j] == 1) {
                    if (binaryImage[i - 1][j] == 1)
                        dsu.Union(GetNode(i, j), GetNode(i - 1, j));
                    if (binaryImage[i + 1][j] == 1)
                        dsu.Union(GetNode(i, j), GetNode(i + 1, j));
                    if (binaryImage[i][j - 1] == 1)
                        dsu.Union(GetNode(i, j), GetNode(i, j - 1));
                    if (binaryImage[i][j + 1] == 1)
                        dsu.Union(GetNode(i, j), GetNode(i, j + 1));
                }
            }
        }
        //For each connected object, label them
        for (var nodes : dsu.GetSets().values()) {
            for (var node : nodes) {
                var indice = node.split(",");
                int x = Integer.parseInt(indice[0]);
                int y = Integer.parseInt(indice[1]);
                labeledImage[x][y] = label;
            }
            label++;
        }
        if (debug)
            displayImage(labeledImage);
        return labeledImage;
    }

    //Detect square or circle
    public void ObjectDetection(int[][] connectedBinaryArray) {
        int circularCount = 0;
        int squareCount = 0;
        var areas = new HashMap<Integer, Integer>();

        //Calculate areas for each object
        for (int i = 0; i < connectedBinaryArray.length; i++)
            for (int j = 0; j < connectedBinaryArray[0].length; j++) {
                if (connectedBinaryArray[i][j] != 0) {
                    int label = connectedBinaryArray[i][j];
                    if (areas.containsKey(label))
                        areas.put(label, areas.get(label) + 1);
                    else
                        areas.put(label, 1);
                }
            }

        //Initiate 2-D array for object boundary output
        imageBoundary = new int[image.length][image[0].length];

        for (var label : areas.keySet()) {
            var p = CalculatePerimeter(connectedBinaryArray, label);
            var area = areas.get(label);
            System.out.println("Area = " + area + ", perimeter = " + p);

            double r = (4 * Math.PI * area) / (p * p);
            System.out.println("R = " + r);
            if (Math.abs(r - 1) <= 0.1) {
                if (debug)
                    System.out.println("The object is circular.");
                circularCount++;
            } 
            else {
                p += 4;// For sqaure/rectangle, the corner point should be counted twice
                System.out.println("Perimeter with offset for sqaure/rectangle = " + p);
                r = (4 * Math.PI * area) / (p * p);
                System.out.println("R with offset for sqaure/rectangle = " + r);
                if (Math.abs(r - Math.PI / 4) <= 0.1) {
                    if (debug)
                        System.out.println("The object is square.");
                    squareCount++;
                }
            }
        }
        System.out.println("Count of circles = " + circularCount);
        System.out.println("Count of squares = " + squareCount);
    }

    // Detect boundary by Tang Zhen-Jun's algorithm
    private int CalculatePerimeter(int[][] connectedBinaryArray, int label) {
        int[] pN = new int[] { 0, 0, 0, 0 };
        int[] oper = new int[] { 0, 0, 0, 0 };
        var boundary = new Vector<String>();
        Boolean isBoundary = false;
        for (int i = 1; i < connectedBinaryArray.length - 1; i++) {
            for (int j = 1; j < connectedBinaryArray[0].length - 1; j++) {
                isBoundary = false;
                if (connectedBinaryArray[i][j] == label) {
                    pN[0] = connectedBinaryArray[i + 1][j];
                    pN[1] = connectedBinaryArray[i][j - 1];
                    pN[2] = connectedBinaryArray[i - 1][j];
                    pN[3] = connectedBinaryArray[i][j + 1];
                    for (int k = 0; k < 4 && !isBoundary; k++) {
                        if (pN[k] == oper[k])
                            isBoundary = true;
                        if (isBoundary) {
                            boundary.add(i + "," + j);
                        }
                    }
                }
            }
        }

        // Print boundary
        if (debug) {
            int left = -1, right = -1, up = -1, bottom = -1;
            for (var key : boundary) {
                var indice = key.split(",");
                int x = Integer.parseInt(indice[0]);
                int y = Integer.parseInt(indice[1]);
                if (x > up)
                    up = x;
                if (bottom < 0 || x < bottom)
                    bottom = x;
                if (y > right)
                    right = y;
                if (left < 0 || y < left)
                    left = y;
                imageBoundary[x][y] = label;
            }
            for (int i = bottom; i >= 0 && i <= up; i++) {
                for (int j = left; j >= 0 && j <= right; j++) {
                    System.out.print(imageBoundary[i][j] + " ");
                }
                System.out.println();
            }
        }

        return boundary.size();
    }

    public void displayImage(int[][] image) {
        int rows = image.length;
        int cols = image[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }
}
