package com.github.argon.sos.interactions.util.json;

import com.github.argon.sos.interactions.FileService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class HashMapConverterTest {

    @Test
    void parse() throws IOException {

        FileService fileService = FileService.getInstance();
        Path path = fileService.getResourcePath("data/").orElseThrow(AssertionError::new);
        Map<String, Throwable> brokenJacksonJsons = new HashMap<>();
        Map<String, Throwable> brokenGsonJsons = new HashMap<>();
        AtomicInteger processedFilesCount = new AtomicInteger(0);
        AtomicInteger jacksonSuccessCount = new AtomicInteger(0);
        AtomicInteger gsonSuccessCount = new AtomicInteger(0);


        try (Stream<Path> stream = Files.walk(path)) {
            stream
                .filter(Files::isRegularFile)
                .filter(Objects::nonNull)
                .filter(path1 -> path1.toString().endsWith(".txt"))
                .forEach(path1 -> {
                    processedFilesCount.addAndGet(1);
                    Path relative = path.relativize(path1);
                    System.out.println("Processing file " + relative);
                    String originalContent;

                    try {
                        originalContent = fileService.readResource("data/" + relative);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    HashMapConverter hashMapConverter = new HashMapConverter();
                    String normalizedContent = hashMapConverter.normalizeGameJsonRegex(originalContent);

                    try {
                        hashMapConverter.normalizeGameJsonParser(originalContent, relative.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }


//                    try {
//                        hashMapConverter.parseJackson(content);
//                        jacksonSuccessCount.addAndGet(1);
//                    } catch (Exception e) {
//                        brokenJacksonJsons.put(relative.toString(), e);
//                    }
//
//                    try {
//                        hashMapConverter.parseGson(content);
//                        gsonSuccessCount.addAndGet(1);
//                    } catch (Exception e) {
//                        brokenGsonJsons.put(relative.toString(), e);
//                    }
                });
        }

        System.out.printf("\nProcessed %d files. " +
                "\nJACKSON: %d successful and %d failed." +
                "\nGSON: %d successful and %d failed\n\n",
            processedFilesCount.get(), jacksonSuccessCount.get(), brokenJacksonJsons.size(),
            gsonSuccessCount.get(), brokenGsonJsons.size());
    }
}