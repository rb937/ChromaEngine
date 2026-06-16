package com.chromaengine.filters;

public interface PixelFilter {
    void apply(int[] pixels, int width, int height);
}
