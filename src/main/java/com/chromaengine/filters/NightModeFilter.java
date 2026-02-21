package com.chromaengine.filters;

public class NightModeFilter implements PixelFilter {

    @Override
    public void apply(int[] pixels, int width, int height) {

        // Loop through the entire 1D memory array
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];

            // 1. EXTRACTION: Isolate the Alpha, Red, Green, and Blue channels
            // We shift the 32-bit integer to the right, then mask it with 0xFF (255)
            // to extract just that specific 8-bit color value.
            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            // If the pixel is fully transparent (like the background of a sprite), skip it!
            if (a == 0) {
                continue;
            }

            // 2. THE ALGORITHM: Apply the Dark / Ambient Blue aesthetic
            // We crush the reds and greens to simulate darkness, and boost the blues.
            r = (int) (r * 0.65); // Reduce red by 35%
            g = (int) (g * 0.7); // Reduce green by 30%

            // Boost blue, but use Math.min to ensure it never exceeds the maximum 255 value
            b = Math.min(255, (int) (b * 0.85 + 15));

            // 3. RECONSTRUCTION: Pack the channels back into a single 32-bit integer
            // We shift them back to the left and combine them using bitwise OR (|)
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        System.out.println("Night Mode filter applied successfully!");
    }
}