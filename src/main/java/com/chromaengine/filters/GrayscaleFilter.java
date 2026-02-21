package com.chromaengine.filters;

public class GrayscaleFilter implements PixelFilter {
    private double intensity; // 0 to 1.0 (how much to "fade" the color)

    public GrayscaleFilter(double intensity) {
        this.intensity = intensity / 100.0;
    }

    @Override
    public void apply(int[] pixels, int width, int height) {
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            // The industry standard for "Perceptual Brightness"
            int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

            int finalR = (int) (r * (1 - intensity) + gray * intensity);
            int finalG = (int) (g * (1 - intensity) + gray * intensity);
            int finalB = (int) (b * (1 - intensity) + gray * intensity);

            // CLAMPING: Prevents bitwise overflow glitches!
            r = Math.min(255, Math.max(0, finalR));
            g = Math.min(255, Math.max(0, finalG));
            b = Math.min(255, Math.max(0, finalB));

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}