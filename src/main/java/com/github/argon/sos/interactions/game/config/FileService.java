package com.github.argon.sos.interactions.game.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileService {
    @Getter(lazy = true)
    private final static FileService instance = new FileService();

    public Optional<Path> getResourcePath(String path) {
        URL url = getClass().getClassLoader().getResource(path);

        return Optional.ofNullable(url).map(url1 -> {
            try {
                return Paths.get(url1.toURI());
            } catch (URISyntaxException e) {
                return null;
            }
        });
    }

    public String readResource(Path path) throws IOException {
        return readResource(path.toString());
    }

    public String readResource(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            return readFromInputStream(inputStream);
        }
    }

    public String readFile(Path path) throws IOException {
        return String.join("\n", Files.readAllLines(path));
    }

    public void write(String content, Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Files.write(path, content.getBytes());
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
