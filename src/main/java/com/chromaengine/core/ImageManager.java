package com.chromaengine.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class ImageManager {

    private BufferedImage currentImage;
    private int[] displayPixelData;
    private int[] originalPixelData;
    private int[] trueOriginalPixelData;
    private int width;
    private int height;

    private Logger logger = (message) -> {
    };

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public boolean loadImage(String filePath) {
        try {
            File file = new File(filePath);
            BufferedImage rawImage = ImageIO.read(file);

            this.width = rawImage.getWidth();
            this.height = rawImage.getHeight();

            currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            currentImage.getGraphics().drawImage(rawImage, 0, 0, null);

            displayPixelData = ((DataBufferInt) currentImage.getRaster().getDataBuffer()).getData();

            originalPixelData = displayPixelData.clone();
            trueOriginalPixelData = displayPixelData.clone();

            System.out.println("Successfully loaded: " + width + "x" + height);
            System.out.println("Memory mapped " + originalPixelData.length + " pixels for high-speed processing.");

            logger.log("Info: Successfully loaded: " + width + "x" + height);
            logger.log("Memory mapped " + originalPixelData.length + " pixels for high-speed processing.");

            return true;

        } catch (IOException e) {
            System.err.println("Failed to load image from: " + filePath);
            logger.log("Failed to load image from: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveImage(String filePath, String format) {

        try {
            File outputFile = new File(filePath);
            ImageIO.write(currentImage, format, outputFile);

            System.out.println("Successfully exported image to: " + filePath);
            logger.log("Successfully exported image to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Failed to save image to: " + filePath);
            logger.log("Failed to save image to: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    public void resetToOriginal() {
        if (currentImage == null) {
            logger.log("Warning: Cannot reset, no image loaded.");
            return;
        }
        System.arraycopy(originalPixelData, 0, displayPixelData, 0, originalPixelData.length);
    }

    public void hardReset() {
        if (currentImage == null) {
            logger.log("Warning: Cannot hard reset, no image loaded.");
            return;
        }
        System.arraycopy(trueOriginalPixelData, 0, originalPixelData, 0, trueOriginalPixelData.length);
        System.arraycopy(trueOriginalPixelData, 0, displayPixelData, 0, trueOriginalPixelData.length);
    }

    public void unloadImage() {
        if (currentImage == null) {
            logger.log("Warning: No image to unload.");
            return;
        }
        currentImage = null;
        displayPixelData = null;
        originalPixelData = null;
        trueOriginalPixelData = null;
        width = 0;
        height = 0;
        logger.log("Image unloaded and memory released.");
    }

    public int[] getDisplayPixelData() {
        return displayPixelData;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
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