package PatternRecognition;

public class ImageAnalyzerTest {
    
    public static void displayImage(int[][] image) {
        int rows = image.length;
        int cols = image[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] inputImage = {
                { 1, 3, 5, 7, 9, 3, 4, 4, 5, 6,1, 3, 5, 7, 9, 3, 4, 4, 5, 6 ,1, 3, 5, 7, 9, 3, 4, 4, 5, 6  },
                { 1, 33, 35, 7, 9, 3, 4, 4, 5, 6,1, 33, 35, 7, 9, 3, 4, 4, 5, 6,1, 33, 35, 7, 9, 3, 4, 4, 5, 6 },
                { 1, 25, 5, 7, 9, 3, 4, 4, 5, 6,1, 25, 5, 7, 9, 3, 4, 4, 5, 6,1, 25, 5, 7, 9, 3, 4, 4, 5, 6 },
                { 1, 3, 5, 20, 25, 24, 33, 25, 26, 24,21, 23, 5, 20, 25, 24, 33, 5, 6, 4,1, 3, 5, 20, 25, 24, 33, 5, 6, 4 },
                { 1, 3, 5, 22, 35, 24, 32, 25, 26, 24,21, 23, 5, 22, 35, 24, 32, 5, 6, 4, 1, 3, 5, 22, 35, 24, 32, 5, 6, 4 },
                { 1, 3, 5, 20, 28, 34, 23, 25, 26, 24,21, 23, 5, 20, 28, 34, 23, 5, 6, 4,1, 3, 5, 20, 28, 34, 23, 5, 6, 4 },
                { 1, 3, 5, 21, 25, 27, 23, 25, 26, 24,21, 23, 5, 21, 25, 27, 23, 5, 6, 4, 1, 3, 5, 21, 25, 27, 23, 5, 6, 4},
                { 1, 3, 27, 7, 9, 3, 4, 4, 25, 26 , 1, 3, 27, 7, 9, 3, 4, 4, 5, 6 , 1, 3, 27, 7, 9, 3, 4, 4, 5, 6 },
                { 1, 3, 5, 7, 9, 3, 4, 4, 5, 6 ,1, 3, 5, 7, 9, 3, 4, 4, 5, 6 ,1, 3, 5, 7, 9, 3, 4, 4, 5, 6 },
                { 1, 3, 5, 7, 9, 3, 4, 4, 25, 6,1, 3, 5, 7, 9, 3, 4, 4, 25, 6,1, 3, 5, 7, 9, 3, 4, 4, 25, 6 }
        };
var test = new ImageProcessor("test.jpg");
test.ConvertGrayScaleToImage(inputImage, "testtest.jpg");
        // var imageAnalyzer = new ImageAnalyzer(inputImage);
        // System.out.println("Input image (with noise):");
        // displayImage(imageAnalyzer.GetImage());

        // //Test without noise cancelling
        // System.out.println("Histogram:");
        // var histogram = imageAnalyzer.GetHistogram();
        // var threshold = imageAnalyzer.GetOtsuThreshold(histogram);
        // System.out.println("Threshold: " + threshold);

        // var binaries = imageAnalyzer.GetBinaryArray(histogram, threshold);
        // System.out.println("Binary:");
        // displayImage(binaries);

        // System.out.println("Connectivity:");
        // var connectedImage = imageAnalyzer.ConnectivityAnalysis(binaries);
        // displayImage(connectedImage);
        // imageAnalyzer.ObjectDetection(connectedImage);

        // //Test noise cancelling
        // imageAnalyzer.ApplyGaussianFilter();
        // System.out.println("\nOutput image (cleaned image):");
        // displayImage(imageAnalyzer.GetImage());

        // System.out.println("Histogram:");
        // histogram = imageAnalyzer.GetHistogram();
        // threshold = imageAnalyzer.GetOtsuThreshold(histogram);
        // System.out.println("Threshold: " + threshold);

        // binaries = imageAnalyzer.GetBinaryArray(histogram, threshold);
        // System.out.println("Binary:");
        // displayImage(binaries);
        // connectedImage = imageAnalyzer.ConnectivityAnalysis(binaries);
        // System.out.println("Connectivity:");
        // displayImage(connectedImage);
        // imageAnalyzer.ObjectDetection(connectedImage);
    }
}
