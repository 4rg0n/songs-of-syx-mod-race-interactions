package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Loggers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import snake2d.util.file.JsonE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

class JsonConverterTest {

    private final static JsonConverter jsonConverter = JsonConverter.getInstance();

    @BeforeAll
    static void beforeAll() {
        Loggers.setLevels(Level.FINER);
    }

    @Test
    void fromPojo() throws Exception {

        TestPojo testPojo = new TestPojo();
        JsonE json = jsonConverter.fromPojo(testPojo);


        Path tempFile = Files.createTempFile("test", "txt");
        json.save(tempFile);

        Files.readAllLines(tempFile);

        StringBuilder contentBuilder = new StringBuilder();

//        try (Stream<String> stream = Files.lines(tempFile, StandardCharsets.UTF_8)) {
//            stream.forEach(s -> contentBuilder.append(s).append("\n"));
//        }
//
//        System.out.println(contentBuilder);

        return;
    }
}