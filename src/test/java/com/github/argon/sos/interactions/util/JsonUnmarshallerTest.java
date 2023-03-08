package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.FileService;
import com.github.argon.sos.interactions.log.Loggers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import snake2d.util.file.Json;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

class JsonUnmarshallerTest {

    private final static JsonUnmarshaller jsonUnmarshaller = JsonUnmarshaller.getInstance();
    private final static JsonMarshaller jsonMarshaller = JsonMarshaller.getInstance();

    private final static FileService fileService = FileService.getInstance();

    @BeforeAll
    static void beforeAll() {
        Loggers.setLevels(Level.FINER);
    }

    @Test
    void toPojo() throws IOException {
//        TestPojo testPojoWrite = new TestPojo();
//        JsonE jsonE = jsonMarshaller.fromPojo(testPojoWrite);
//        Path tempFile = Files.createTempFile("test", "txt");
//        jsonE.save(tempFile);
//
//        Files.readAllLines(tempFile);
//
//        StringBuilder contentBuilder = new StringBuilder();
//
//        try (Stream<String> stream = Files.lines(tempFile, StandardCharsets.UTF_8)) {
//            stream.forEach(s -> contentBuilder.append(s).append("\n"));
//        }

//       System.out.println(contentBuilder);
//
        Path path = fileService.getResourcePath("json/TestPojo.txt").orElseThrow(AssertionError::new);
        Json json = new Json(path);

        TestPojoEmpty testPojoRead = jsonUnmarshaller.toPojo(json, TestPojoEmpty.class);

        return;
    }
}