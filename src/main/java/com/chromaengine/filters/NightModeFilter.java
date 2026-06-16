package com.chromaengine.filters;

public class NightModeFilter implements PixelFilter {

    @Override
    public void apply(int[] pixels, int width, int height) {

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            if (a == 0) {
                continue;
            }

            r = (int) (r * 0.65); // Reducing red and green for that night effect
            g = (int) (g * 0.7);

            b = Math.min(255, (int) (b * 0.85 + 15));

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        System.out.println("Night Mode filter applied successfully!");
    }
}