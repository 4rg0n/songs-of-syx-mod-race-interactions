package com.github.argon.sos.interactions.util;

import org.junit.jupiter.api.Test;
import snake2d.util.file.JsonE;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class JsonConverterUtilTest {

    @Test
    void fromPojo() throws Exception {
        TestPojo testPojo = new TestPojo();
        JsonE json = JsonConverterUtil.fromPojo(testPojo);


        Path tempFile = Files.createTempFile("test", "txt");
        json.save(tempFile);

        Files.readAllLines(tempFile);

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(tempFile, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        System.out.println(contentBuilder);

        return;
    }
}