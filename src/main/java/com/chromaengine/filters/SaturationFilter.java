package com.chromaengine.filters;

public class SaturationFilter implements PixelFilter {
    private final double satFactor;
    private final double vibFactor;

    public SaturationFilter(double saturation, double vibrance) {
        this.satFactor = 1.0 + (saturation / 100.0);
        this.vibFactor = vibrance / 100.0;
    }

    @Override
    public void apply(int[] pixels, int width, int height) {
        if (satFactor == 1.0 && vibFactor == 0.0)
            return;

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xFF;
            if (a == 0)
                continue;

            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

            int max = Math.max(r, Math.max(g, b));
            int min = Math.min(r, Math.min(g, b));
            double currentSaturation = (max == 0) ? 0 : (max - min) / (double) max;

            double dynamicVibrance = vibFactor * (1.0 - currentSaturation);

            double totalFactor = satFactor + dynamicVibrance;

            totalFactor = Math.max(0, totalFactor);

            int finalR = (int) (gray + ((r - gray) * totalFactor));
            int finalG = (int) (gray + ((g - gray) * totalFactor));
            int finalB = (int) (gray + ((b - gray) * totalFactor));

            r = Math.min(255, Math.max(0, finalR));
            g = Math.min(255, Math.max(0, finalG));
            b = Math.min(255, Math.max(0, finalB));

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
