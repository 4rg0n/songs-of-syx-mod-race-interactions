package com.github.argon.sos.interactions.util.json;

import com.github.argon.sos.interactions.FileService;
import com.github.argon.sos.interactions.game.json.Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class JsonTest {
    @Test
    void test() throws IOException {

        FileService fileService = FileService.getInstance();
        Path dataPath = fileService.getResourcePath("data/").orElseThrow(AssertionError::new);
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        List<String> brokenFiles = new ArrayList<>();


        try (Stream<Path> stream = Files.walk(dataPath)) {
            stream
                .filter(Files::isRegularFile)
                .filter(Objects::nonNull)
                .filter(path1 -> path1.toString().endsWith(".txt"))
                .forEach(path -> {
                    Path relative = dataPath.relativize(path);
                    System.out.println("Processing file " + relative);
                    String originalContent;

                    try {
                        originalContent = fileService.readResource("data/" + relative);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        Json json = new Json(originalContent);
                        String jsonString = json.toString();
                        successCount.addAndGet(1);
                    } catch (RuntimeException e) {
                        brokenFiles.add(relative.toString());
                    }
                    processedCount.addAndGet(1);
                });
        }

        System.out.printf("\nProcessed %d files. " +
                "\n%d successful and %d failed.",
            processedCount.get(), successCount.get(), processedCount.get() - successCount.get());

        System.out.println("Broken files:\n");
        brokenFiles.forEach(System.out::println);
    }
}