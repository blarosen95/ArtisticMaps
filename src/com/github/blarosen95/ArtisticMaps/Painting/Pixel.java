package com.github.blarosen95.ArtisticMaps.Painting;

public class Pixel {
    private final int x, y;
    private CanvasRenderer canvas;
    private byte color;

    public Pixel(CanvasRenderer canvas, int x, int y, byte color) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        canvas.addPixel(x, y, color);
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pixel)) return false;
        Pixel pixel = (Pixel) obj;
        return pixel.x == x && pixel.y == y && pixel.color == color;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
