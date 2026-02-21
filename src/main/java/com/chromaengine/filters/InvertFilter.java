package com.chromaengine.filters;

public class InvertFilter implements PixelFilter {
    @Override
    public void apply(int[] pixels, int width, int height) {
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xFF;
            if (a == 0)
                continue;

            // Bitwise NOT or simple subtraction works: 255 - current
            int r = 255 - ((argb >> 16) & 0xFF);
            int g = 255 - ((argb >> 8) & 0xFF);
            int b = 255 - (argb & 0xFF);

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}