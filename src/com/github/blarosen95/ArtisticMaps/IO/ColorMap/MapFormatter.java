package com.github.blarosen95.ArtisticMaps.IO.ColorMap;

import java.io.IOException;

interface MapFormatter {
    byte[] generateBLOB(byte[] mapData) throws IOException;

    byte[] readBLOB(byte[] blobData);
}
