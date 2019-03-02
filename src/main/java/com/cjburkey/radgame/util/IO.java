package com.cjburkey.radgame.util;

import com.cjburkey.radgame.ResourceLocation;
import java.io.*;
import java.util.stream.Collectors;

public final class IO {

    public static String readResource(String resource) {
        var stream = ClassLoader.getSystemResourceAsStream(resource);
        if (stream == null) {
            try {
                throw new FileNotFoundException("Resource not found: " + resource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ignored) {
        }
        return null;
    }

    public static String readResource(ResourceLocation resource) {
        return readResource(resource.getFullPath());
    }

}
