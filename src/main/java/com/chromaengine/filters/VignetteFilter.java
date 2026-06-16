package com.chromaengine.filters;

public class VignetteFilter implements PixelFilter {
    private final double intensity; // -100 to 100

    public VignetteFilter(double intensity) {
        this.intensity = intensity / 100.0; // Normalized to -1.0 to 1.0
    }

    @Override
    public void apply(int[] pixels, int width, int height) {
        if (intensity == 0)
            return;

        double centerX = width / 2.0;
        double centerY = height / 2.0;

        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * width + x;
                int argb = pixels[i];
                int a = (argb >> 24) & 0xFF;
                if (a == 0)
                    continue;

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                double distX = x - centerX;
                double distY = y - centerY;
                double distance = Math.sqrt(distX * distX + distY * distY);

                double factor = (distance / maxDistance) * Math.abs(intensity);

                if (intensity > 0) { // Black vig
                    r = (int) (r * (1.0 - factor));
                    g = (int) (g * (1.0 - factor));
                    b = (int) (b * (1.0 - factor));
                } else { // White vig
                    r = (int) (r + ((255 - r) * factor));
                    g = (int) (g + ((255 - g) * factor));
                    b = (int) (b + ((255 - b) * factor));
                }

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }
}