package com.chromaengine.filters;

public class ConvolutionFilter implements PixelFilter {
    private final double amount;

    public ConvolutionFilter(double amount) {
        this.amount = amount;
    }

    @Override
    public void apply(int[] pixels, int width, int height) {
        if (amount == 0)
            return;

        int[] source = pixels.clone();
        float[] kernel;
        int passes = 1;

        if (amount > 0) {
            kernel = new float[] {
                    0.111f, 0.111f, 0.111f,
                    0.111f, 0.111f, 0.111f,
                    0.111f, 0.111f, 0.111f
            };
            passes = (int) Math.max(1, amount / 15);
        } else {
            kernel = new float[] {
                    0f, -1f, 0f,
                    -1f, 5f, -1f,
                    0f, -1f, 0f
            };
        }

        for (int p = 0; p < passes; p++) {
            if (p > 0)
                source = pixels.clone();

            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    float r = 0, g = 0, b = 0;

                    int a = (source[y * width + x] >> 24) & 0xFF;

                    int kernelIndex = 0;
                    for (int ky = -1; ky <= 1; ky++) {
                        for (int kx = -1; kx <= 1; kx++) {
                            int pixel = source[(y + ky) * width + (x + kx)];
                            float kVal = kernel[kernelIndex++];

                            r += ((pixel >> 16) & 0xFF) * kVal;
                            g += ((pixel >> 8) & 0xFF) * kVal;
                            b += (pixel & 0xFF) * kVal;
                        }
                    }

                    int finalR = Math.min(255, Math.max(0, (int) r));
                    int finalG = Math.min(255, Math.max(0, (int) g));
                    int finalB = Math.min(255, Math.max(0, (int) b));

                    pixels[y * width + x] = (a << 24) | (finalR << 16) | (finalG << 8) | finalB;
                }
            }
        }
    }
}
