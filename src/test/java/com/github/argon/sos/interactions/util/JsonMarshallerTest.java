package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Loggers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import snake2d.util.file.JsonE;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.stream.Stream;

class JsonMarshallerTest {

    private final static JsonMarshaller jsonMarshaller = JsonMarshaller.getInstance();

    @BeforeAll
    static void beforeAll() {
        Loggers.setLevels(Level.FINER);
    }

    @Test
    void fromPojo() throws Exception {

        TestPojoFilled testPojoFilled = new TestPojoFilled();
        JsonE json = jsonMarshaller.fromPojo(testPojoFilled);


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