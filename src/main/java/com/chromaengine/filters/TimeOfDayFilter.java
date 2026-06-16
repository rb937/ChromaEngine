package com.chromaengine.filters;

public class TimeOfDayFilter implements PixelFilter {

    private final double timeValue;

    public TimeOfDayFilter(double timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public void apply(int[] pixels, int width, int height) {

        double factor = timeValue / 100.0;

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];

            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            if (a == 0)
                continue;

            if (factor < 0) {
                double intensity = Math.abs(factor);

                r = (int) (r * (1.0 - (0.45 * intensity)));
                g = (int) (g * (1.0 - (0.35 * intensity)));
                b = Math.min(255, (int) (b + (30 * intensity)));

            } else if (factor > 0) {
                r = Math.min(255, (int) (r + (50 * factor)));
                g = Math.min(255, (int) (g + (40 * factor)));
                b = Math.min(255, (int) (b + (10 * factor)));
            }

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
