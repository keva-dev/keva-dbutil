package dev.keva.dbutil;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Converter {
    private Converter() {

    }

    private static final int REDIS_VERSION = 6;

    public static void fromKdbToRdb(File source, File destination) throws IOException {
        ChronicleMapBuilder<byte[], byte[]> mapBuilder = ChronicleMapBuilder.of(byte[].class, byte[].class)
            .name("keva-chronicle-map")
            .averageKey("SampleSampleSampleKey".getBytes())
            .averageValue("SampleSampleSampleSampleSampleSampleValue".getBytes())
            .entries(100);
        try (ChronicleMap<byte[], byte[]> map = mapBuilder.createOrRecoverPersistedTo(source)) {
            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            RDBOutputStream rdb = new RDBOutputStream(fileOutputStream);
            rdb.preamble(REDIS_VERSION);
            rdb.select(0);
            rdb.database(map);
            rdb.end();
        }
    }

    public static void fromRdbToKdb(File source, File destination) {
    }

}
