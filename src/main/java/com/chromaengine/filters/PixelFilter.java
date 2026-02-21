package com.chromaengine.filters;

/**
 * The standard contract for any algorithm that manipulates ChromaEngine's raw
 * pixel memory.
 */
public interface PixelFilter {
    // We pass in the raw 1D array, plus width and height just in case
    // the algorithm needs spatial awareness (like an edge detector).
    void apply(int[] pixels, int width, int height);
}
