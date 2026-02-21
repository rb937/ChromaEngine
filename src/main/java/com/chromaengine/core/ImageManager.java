package com.chromaengine.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class ImageManager {

    private BufferedImage currentImage;
    private int[] displayPixelData; // The one we show on screen
    private int[] originalPixelData; // The untouched, pristine backup
    private int[] trueOriginalPixelData; // The ultimate untouched backup, for hard resets
    private int width;
    private int height;

    /**
     * Loads an image from the disk and maps its pixels directly to a 1D integer
     * array.
     */
    public boolean loadImage(String filePath) {
        try {
            // 1. Read the raw file
            File file = new File(filePath);
            BufferedImage rawImage = ImageIO.read(file);

            this.width = rawImage.getWidth();
            this.height = rawImage.getHeight();

            // 2. Force the image into a 32-bit ARGB format.
            // This ensures every pixel has an Alpha, Red, Green, and Blue channel.
            currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            currentImage.getGraphics().drawImage(rawImage, 0, 0, null);

            // 3. THE SECRET SAUCE: Direct Memory Access
            // Instead of using slow getRGB(x,y) loops, we extract the raw memory buffer.
            // 3. THE SECRET SAUCE: Direct Memory Access & Non-Destructive Cloning
            displayPixelData = ((DataBufferInt) currentImage.getRaster().getDataBuffer()).getData();

            // Instantly clone the array so we always have a clean, untouched backups
            originalPixelData = displayPixelData.clone();
            trueOriginalPixelData = displayPixelData.clone();

            System.out.println("Successfully loaded: " + width + "x" + height);
            System.out.println("Memory mapped " + originalPixelData.length + " pixels for high-speed processing.");

            return true;

        } catch (IOException e) {
            System.err.println("Failed to load image from: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the current BufferedImage to the hard drive.
     */
    public boolean saveImage(String filePath, String format) {
        if (currentImage == null) {
            System.err.println("ERROR: No image in memory to save.");
            return false;
        }

        try {
            File outputFile = new File(filePath);
            // ImageIO.write takes the image data, the format (e.g., "png"), and the
            // destination file
            ImageIO.write(currentImage, format, outputFile);

            System.out.println("Successfully exported image to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Failed to save image to: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Instantly overwrites the modified image with the pristine original data.
     * This is required before running any real-time slider updates.
     */
    public void resetToOriginal() {
        if (originalPixelData != null && displayPixelData != null) {
            // System.arraycopy is a low-level OS command. It copies millions of
            // array integers almost instantaneously, much faster than a for-loop.
            System.arraycopy(originalPixelData, 0, displayPixelData, 0, originalPixelData.length);
        }
    }

    public void hardReset() {
        if (trueOriginalPixelData != null) {
            // Overwrite BOTH the display and the working baseline with the absolute
            // original
            System.arraycopy(trueOriginalPixelData, 0, originalPixelData, 0, trueOriginalPixelData.length);
            System.arraycopy(trueOriginalPixelData, 0, displayPixelData, 0, trueOriginalPixelData.length);
        }
    }

    // (Make sure to rename getPixelData() to getDisplayPixelData() if you had it!)
    public int[] getDisplayPixelData() {
        return displayPixelData;
    }

    // --- Getters so our UI and Filters can access the data ---

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public int[] getPixelData() {
        return originalPixelData;
    }

    public int[] getOriginalPixelData() {
        return originalPixelData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}