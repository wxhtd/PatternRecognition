package PatternRecognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    private int[][] grayScalePixels;
    private Boolean debug = false;

    public ImageProcessor(String imageFilePath) {
        System.out.println("Start processing image");
        grayScalePixels = ConvertImageToGrayScaleArray(imageFilePath);
        System.out.println("Image processed");
    }

    public ImageProcessor(String imageFilePath, Boolean debug) {
        System.out.println("Start processing image");
        System.out.println("Debug mode");
        this.debug = debug;
        grayScalePixels = ConvertImageToGrayScaleArray(imageFilePath);
        System.out.println("Image processed");
    }

    public void displayImage() {
        int rows = grayScalePixels.length;
        int cols = grayScalePixels[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grayScalePixels[i][j] + " ");
            }
            System.out.println();
        }
    }


    public void PatternCongnition() {
        System.out.println("Start Pattern Congnition");
        var analyzer = new ImageAnalyzer(grayScalePixels,debug);
        analyzer.ApplyGaussianFilter();
        var histogram = analyzer.GetHistogram();
        var threshold = analyzer.GetOtsuThreshold(histogram);
        var binaries = analyzer.GetBinaryArray(histogram, threshold);
        var connectedActivities = analyzer.ConnectivityAnalysis(binaries);
        analyzer.ObjectDetection(connectedActivities);
        System.out.println("Pattern Congnition completed");
    }

    public int[][] ConvertImageToGrayScaleArray(String imageFilePath) {
        try {
            var file = new File(imageFilePath);
            var image = ImageIO.read(file);

            int width = image.getWidth();
            int height = image.getHeight();

            int[][] pixels = new int[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(i, j);
                    // Convert RGB to grayscale using a simple formula
                    int grayscaleValue = (int) (0.299 * ((rgb >> 16) & 0xFF) +
                            0.587 * ((rgb >> 8) & 0xFF) +
                            0.114 * (rgb & 0xFF));
                    pixels[i][j] = grayscaleValue;
                }
            }

            if (debug) {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        System.out.print(pixels[i][j] + " ");
                    }
                    System.out.println(); // Move to the next line after each row
                }
            }
            return pixels;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[0][0];
    }

    // This is a test function, generate image from a 2D grayscale array
    public void ConvertGrayScaleToImage(int[][] grayscaleArray, String outputPath) {
        // Get the width and height of the grayscale array
        int rows = grayscaleArray.length;
        int cols = grayscaleArray[0].length;

        // Create a BufferedImage with TYPE_BYTE_GRAY
        BufferedImage image = new BufferedImage(rows, cols, BufferedImage.TYPE_BYTE_GRAY);

        // Set the pixel values based on the grayscale array
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int grayValue = grayscaleArray[i][j];
                // Ensure that the grayscale value is in the valid range [0, 255]
                grayValue = Math.max(0, Math.min(255, grayValue));
                // Set the pixel value
                image.getRaster().setSample(i, j, 0, grayValue);
            }
        }

        // Save the BufferedImage to a file
        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
