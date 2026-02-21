package com.chromaengine.filters;

public class TimeOfDayFilter implements PixelFilter {

    private final double timeValue; // Expected: 0 (Night) to 100 (Day)

    public TimeOfDayFilter(double timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public void apply(int[] pixels, int width, int height) {

        // Normalize the slider (0 to 100) into a math-friendly range (-1.0 to 1.0)
        // -1.0 is max night, 0.0 is neutral, 1.0 is max day.
        double factor = timeValue / 100.0;

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];

            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            if (a == 0)
                continue; // Skip transparent background

            if (factor < 0) {
                // NIGHT MODE (factor is between -1.0 and 0)
                // We use Math.abs(factor) to see "how much" night to apply
                double intensity = Math.abs(factor);

                // Crush red and green based on intensity, boost blue
                r = (int) (r * (1.0 - (0.45 * intensity)));
                g = (int) (g * (1.0 - (0.35 * intensity)));
                b = Math.min(255, (int) (b + (30 * intensity)));

            } else if (factor > 0) {
                // DAY MODE (factor is between 0 and 1.0)
                // Boost all channels to simulate harsh sunlight, heavily boosting red/yellow
                r = Math.min(255, (int) (r + (50 * factor)));
                g = Math.min(255, (int) (g + (40 * factor)));
                b = Math.min(255, (int) (b + (10 * factor)));
            }

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
