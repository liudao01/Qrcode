package com.kecson.util;

import com.google.zxing.LuminanceSource;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BufferedImageLuminanceSource extends LuminanceSource {
    private final BufferedImage image;
    private final int left;
    private final int top;

    public BufferedImageLuminanceSource(BufferedImage image) {
        this(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
        super(width, height);

        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        if ((left + width > sourceWidth) || (top + height > sourceHeight)) {
            throw new IllegalArgumentException(
                    "Crop rectangle does not fit within image data.");
        }

        for (int y = top; y < top + height; y++) {
            for (int x = left; x < left + width; x++) {
                if ((image.getRGB(x, y) & 0xFF000000) == 0) {
                    image.setRGB(x, y, -1);
                }
            }
        }

        this.image = new BufferedImage(sourceWidth, sourceHeight,
                                       10);
        this.image.getGraphics().drawImage(image, 0, 0, null);
        this.left = left;
        this.top = top;
    }

    public byte[] getRow(int y, byte[] row) {
        if ((y < 0) || (y >= getHeight())) {
            throw new IllegalArgumentException(
                    "Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if ((row == null) || (row.length < width)) {
            row = new byte[width];
        }
        image.getRaster().getDataElements(left, top + y, width, 1, row);
        return row;
    }

    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();
        int area = width * height;
        byte[] matrix = new byte[area];
        image.getRaster().getDataElements(left, top, width, height, matrix);
        return matrix;
    }

    public boolean isCropSupported() {
        return true;
    }

    public LuminanceSource crop(int left, int top, int width, int height) {
        return new BufferedImageLuminanceSource(image, this.left + left,
                                                this.top + top, width, height);
    }

    public boolean isRotateSupported() {
        return true;
    }

    public LuminanceSource rotateCounterClockwise() {
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform(0.0D, -1.0D, 1.0D,
                                                                                    0.0D, 0.0D, sourceWidth);
        BufferedImage rotatedImage = new BufferedImage(sourceHeight,
                                                       sourceWidth, 10);
        Graphics2D g = rotatedImage.createGraphics();
        g.drawImage(image, transform, null);
        g.dispose();
        int width = getWidth();
        return new BufferedImageLuminanceSource(rotatedImage, top,
                                                sourceWidth - (left + width), getHeight(), width);
    }
}



/* Location:           C:\Users\56870\Desktop\qr.jar

 * Qualified Name:     com.kecson.util.BufferedImageLuminanceSource

 * JD-Core Version:    0.7.0.1

 */