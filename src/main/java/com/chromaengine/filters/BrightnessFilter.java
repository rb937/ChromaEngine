package com.chromaengine.filters;

public class BrightnessFilter implements PixelFilter {
    private final int offset;

    public BrightnessFilter(double amount) {
        this.offset = (int) amount; // -100 to 100
    }

    @Override
    public void apply(int[] pixels, int width, int height) {
        if (offset == 0)
            return; // Don't waste CPU if slider is at 0

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xFF;
            if (a == 0)
                continue;

            int r = Math.min(255, Math.max(0, ((argb >> 16) & 0xFF) + offset));
            int g = Math.min(255, Math.max(0, ((argb >> 8) & 0xFF) + offset));
            int b = Math.min(255, Math.max(0, (argb & 0xFF) + offset));

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
