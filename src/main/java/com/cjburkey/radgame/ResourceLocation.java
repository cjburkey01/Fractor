package com.cjburkey.radgame;

import com.cjburkey.radgame.util.IO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class ResourceLocation {

    public final String domain;
    public final String path;
    public final String extension;

    public ResourceLocation(final String domain, String path, final String extension) {
        this.domain = domain.trim();
        path = path.trim().replace('\\', '/');
        while (path.startsWith("/")) path = path.substring(1);
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        this.path = path;
        this.extension = extension;
    }

    public ResourceLocation(final String domain, final String path) {
        this(domain, path, null);
    }

    public InputStream getStream() throws FileNotFoundException {
        var stream = ClassLoader.getSystemResourceAsStream(getFullPath());
        if (stream == null) throw new FileNotFoundException("Resource not found: \"" + getFullPath() + "\"");
        return stream;
    }

    public String readResource() throws IOException {
        return IO.readResource(getFullPath());
    }

    public String getFullPath() {
        if (extension != null) return String.format("assets/%s/%s.%s", domain, path, extension);
        return String.format("assets/%s/%s", domain, path);
    }

    @Override
    public String toString() {
        if (extension != null) return String.format("%s:%s.%s", domain, path, extension);
        return String.format("%s:%s", domain, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ResourceLocation) o;
        return domain.equals(that.domain) &&
                path.equals(that.path) &&
                extension.equals(that.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, path, extension);
    }

    public static ResourceLocation fromString(final String location, final boolean extension) {
        final var spl = location.split(Pattern.quote(":"));
        if (spl.length != 2) return null;
        final var domain = spl[0];
        final var pathExt = spl[1];
        if (extension) {
            final var extPer = pathExt.lastIndexOf('.');
            if (extPer >= 0) {
                return new ResourceLocation(domain, pathExt.substring(0, extPer), pathExt.substring(extPer + 1));
            }
        }
        return new ResourceLocation(domain, pathExt, null);
    }

}
