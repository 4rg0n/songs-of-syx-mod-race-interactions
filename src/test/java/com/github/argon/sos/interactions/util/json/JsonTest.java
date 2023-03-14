package com.github.argon.sos.interactions.util.json;

import com.github.argon.sos.interactions.FileService;
import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.JsonMapper;
import com.github.argon.sos.interactions.game.json.JsonWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class JsonTest {

    private final FileService fileService = FileService.getInstance();
    @Test
    @Disabled // for internal testing only =)
    void parse_gameJsonFiles() throws IOException {
        Path dataPath = fileService.getResourcePath("data/").orElseThrow(AssertionError::new);
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger successParseCount = new AtomicInteger(0);
        AtomicInteger successJsonEProduceCount = new AtomicInteger(0);
        Map<String, Integer>brokenParseFiles = new HashMap<>();
        Map<String, Integer> brokenJsonEProduceFiles = new HashMap<>();

        try (Stream<Path> stream = Files.walk(dataPath)) {
            stream.filter(Files::isRegularFile)
                .filter(Objects::nonNull)
                .filter(path1 -> path1.toString().endsWith(".txt"))
                .forEach(path -> {
                    processedCount.addAndGet(1);
                    Path relative = dataPath.relativize(path);
                    System.out.println("Processing file " + relative);
                    String originalContent;

                    try {
                        originalContent = fileService.readResource("data/" + relative);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Json json;
                    try {
                        json = new Json(originalContent);
                    } catch (RuntimeException e) {
                        brokenParseFiles.put(relative.toString(), 0);
                        return;
                    }

                    String jsonENew;
                    try {
                        jsonENew = new Json(json.getRoot(), JsonWriter.getJsonE()).toString();
                    } catch (RuntimeException e) {
                        brokenParseFiles.put(relative.toString(), 0);
                        return;
                    }

                    try {
                        assertEqualsContent(originalContent, jsonENew);
                        successParseCount.addAndGet(1);
                    } catch (RuntimeException | AssertionFailedError e) {
                        brokenParseFiles.put(relative.toString(), originalContent.length() - jsonENew.length());
                    }

                    String mappedJsonE;
                    try {
                        mappedJsonE = JsonMapper.mapJsonE(json.getRoot()).toString();
                    } catch (RuntimeException | AssertionFailedError e) {
                        brokenJsonEProduceFiles.put(relative.toString(), 0);
                        return;
                    }

                    try {
                        assertEqualsContent(originalContent, mappedJsonE);
                        successJsonEProduceCount.addAndGet(1);
                    } catch (RuntimeException | AssertionFailedError e) {
                        brokenJsonEProduceFiles.put(relative.toString(), originalContent.length() - mappedJsonE.length());
                    }
                });
        }

        System.out.printf("\nProcessed %d files.\n\nPARSING: %d successful and %d failed.\nPRODUCING: %d successful and %d failed.\n",
            processedCount.get(), successParseCount.get(), processedCount.get() - successParseCount.get(),
            successJsonEProduceCount.get(), processedCount.get() - successJsonEProduceCount.get());

        System.out.println("\nPARSING failed files:\n");
        brokenParseFiles.forEach((path, lengthDifference) -> System.out.println(path + "; diff: " + lengthDifference));

        System.out.println("\nPRODUCING failed files:\n");
        brokenJsonEProduceFiles.forEach((path, lengthDifference) -> System.out.println(path + "; diff: " + lengthDifference));
    }

    @Test
    void parse_JsonEAndProduceJsonE() throws IOException {
        String jsonString = fileService.readResource("json/JsonE.txt");
        Json json = new Json(jsonString, JsonWriter.getJsonE());

        String parsedJsonString = json.toString();

        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
    }

    @Test
    void parse_JsonEAndProduceJson() throws IOException {
        String jsonEString = fileService.readResource("json/JsonE.txt");
        String jsonString = fileService.readResource("json/Json.txt");
        Json json = new Json(jsonEString, new JsonWriter(true, true, false, true));

        String parsedJsonString = json.toString();
        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
    }

    private void assertEqualsWithoutWhitespace(String actual, String expected) {
        Assertions.assertThat(actual.replaceAll("\\s+",""))
            .isEqualTo(expected.replaceAll("\\s+",""));
    }

    private void assertEqualsContent(String actual, String expected) {
        actual = actual.replaceAll("\\s+","");
        expected = expected.replaceAll("\\s+","");

        char[] actualChars = actual.toCharArray();
        Arrays.sort(actualChars);
        actual = new String(actualChars);

        char[] expectedChars = expected.toCharArray();
        Arrays.sort(expectedChars);
        expected = new String(expectedChars);

        Assertions.assertThat(actual)
            .isEqualTo(expected);
    }
}