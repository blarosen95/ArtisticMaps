package com.github.blarosen95.ArtisticMaps.IO.ColorMap;

import com.github.blarosen95.ArtisticMaps.IO.Database.Map;

import java.io.IOException;

public class f32x32 implements MapFormatter {
    private static byte[] foldMap(byte[] mapData) {
        byte[] foldedData = new byte[Map.Size.STANDARD.value];
        for (int x = 0; x < 128; x += 4) {
            for (int y = 0; y < 128; y += 4) {
                foldedData[(x / 4) + ((y / 4) * 32)] = mapData[x + (y * 128)];
            }
        }
        return foldedData;
    }

    private static byte[] unfoldMap(byte[] mapData) {
        byte[] unfoldedData = new byte[Map.Size.MAX.value];
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                int ix = x * 4;
                int iy = y * 4;
                for (int px = 0; px < 4; px++) {
                    for (int py = 0; py < 4; py++) {
                        unfoldedData[(px + ix) + ((py + iy) * 128)] = mapData[x + (y * 32)];
                    }
                }
            }
        }
        return unfoldedData;
    }

    @Override
    public byte[] generateBLOB(byte[] mapData) throws IOException {
        byte[] compressedData;
        if (mapData.length == Map.Size.STANDARD.value) {
            compressedData = Compressor.compress(mapData);
        } else if (mapData.length == Map.Size.MAX.value) {
            compressedData = Compressor.compress(foldMap(mapData));
        } else {
            throw new IOException("Invalid MapData!");
        }
        return compressedData;
    }

    @Override
    public byte[] readBLOB(byte[] blobData) {
        byte[] decompressedData = Compressor.decompress(blobData);
        return unfoldMap(decompressedData);
    }
}