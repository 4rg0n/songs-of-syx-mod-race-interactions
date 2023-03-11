package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.*;

public class CompressionUtil {
    private final static Logger log = Loggers.getLogger(CompressionUtil.class);
    public final static Charset CHARSET = StandardCharsets.UTF_8;

    public static Optional<String> base64Encode(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                Base64.getEncoder()
                    .encodeToString(string.getBytes()));
        } catch (Exception e) {
            log.info("Could not encode to base64 string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }

    public static Optional<String> base64Decode(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(string);
            return Optional.of(new String(decodedBytes));
        } catch (Exception e) {
            log.info("Could not decode from base64 string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }

    public static Optional<String> compress(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
        try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream, compressor);
        ) {
            deflaterOutputStream.write(string.getBytes());
            deflaterOutputStream.close();
            return Optional.of(outputStream.toString());
        } catch (Exception e) {
            log.info("Could not compress string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }

    public static Optional<String> decompress(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        Inflater decompressor = new Inflater(true);

        try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(outputStream, decompressor);
        ) {
            inflaterOutputStream.write(string.getBytes());
            inflaterOutputStream.close();
            return Optional.of(outputStream.toString());
        } catch (Exception e) {
            log.info("Could not decompress string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }

    public static Optional<String> zip(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        try (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
        ) {
            gzip.write(string.getBytes());
            gzip.close();

            return Optional.of(out.toString(CHARSET.name()));
        } catch (IOException e) {
            log.info("Could not zip string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }

    public static Optional<String> unzip(String string) {
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        try (
            InputStream input = new ByteArrayInputStream(string.getBytes());
            GZIPInputStream unzippedStream = new GZIPInputStream(input);
            Reader reader = new InputStreamReader(unzippedStream, CHARSET);
            Writer writer = new StringWriter();
        ) {
            String unzippedString;
            char[] buffer = new char[10240];
            for (int length; (length = reader.read(buffer)) > 0;) {
                writer.write(buffer, 0, length);
            }

            unzippedString = writer.toString();
            return Optional.of(unzippedString);
        } catch (IOException e) {
            log.info("Could not unzip string: %s", e.getMessage());
            log.trace("STRING: %s", string, e);
            return Optional.empty();
        }
    }
}
