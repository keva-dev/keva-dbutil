package dev.keva.dbutil;

import net.openhft.chronicle.map.ChronicleMap;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.CheckedOutputStream;

import static java.util.Objects.requireNonNull;

public class RDBOutputStream {

    private static final byte[] REDIS = "REDIS".getBytes(StandardCharsets.UTF_8);

    private static final int END_OF_STREAM = 0xFF;
    private static final int SELECT = 0xFE;

    private final CheckedOutputStream out;

    public RDBOutputStream(OutputStream out) {
        this.out = new CheckedOutputStream(requireNonNull(out), new CRC64());
    }

    public static byte[] toByteArray(long value) {
        byte[] b = new byte[Long.BYTES];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
        }
        return b;
    }

    public void preamble(int version) throws IOException {
        out.write(REDIS);
        out.write(version(version));
    }

    private byte[] version(int version) {
        StringBuilder sb = new StringBuilder(String.valueOf(version));
        for (int i = sb.length(); i < Integer.BYTES; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void select(int db) throws IOException {
        out.write(SELECT);
        length(db);
    }

    public void database(ChronicleMap<byte[], byte[]> db) throws IOException {
        for (Map.Entry<byte[], byte[]> entry : db.entrySet()) {
            value(entry.getKey(), entry.getValue());
        }
    }

    private void value(byte[] key, byte[] value) throws IOException {
        type();
        key(key);
        value(value);
    }

    private void type() throws IOException {
        out.write(0);
    }

    private void key(byte[] key) throws IOException {
        string(key);
    }

    private void value(byte[] value) throws IOException {
        string(value);
    }

    private void length(int length) throws IOException {
        if (length < 0x40) {
            // 1 byte: 00XXXXXX
            out.write(length);
        } else if (length < 0x4000) {
            // 2 bytes: 01XXXXXX XXXXXXXX
            int b1 = length >> 8;
            int b2 = length & 0xFF;
            out.write(0x40 | b1);
            out.write(b2);
        } else {
            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
            out.write(0x80);
            out.write(toByteArray(length));
        }
    }

    private void string(byte[] bytes) throws IOException {
        length(bytes.length);
        out.write(bytes);
    }

    public void end() throws IOException {
        out.write(END_OF_STREAM);
        out.write(toByteArray(out.getChecksum().getValue()));
        out.flush();
    }

}