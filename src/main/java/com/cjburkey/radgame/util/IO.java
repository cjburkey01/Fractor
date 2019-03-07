package com.cjburkey.radgame.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public final class IO {

    public static String readResource(final String resource) throws IOException {
        final var stream = ClassLoader.getSystemResourceAsStream(resource);
        if (stream == null) throw new FileNotFoundException("Resource not found: \"" + resource + "\"");
        return readStream(stream);
    }

    public static String readFile(final File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("File not found: \"" + file.getAbsolutePath() + "\"");
        return readStream(new FileInputStream(file));
    }

    public static String readStream(final InputStream stream) throws IOException {
        try (stream; final var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))) {
            return reader
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

}
