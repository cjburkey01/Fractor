package com.cjburkey.radgame;

import java.util.Objects;
import java.util.regex.Pattern;

public class ResourceLocation {

    public final String domain;
    public final String path;
    public final String extension;

    public ResourceLocation(String domain, String path, String extension) {
        this.domain = domain.trim();
        path = path.trim().replace('\\', '/');
        while (path.startsWith("/")) path = path.substring(1);
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        this.path = path;
        this.extension = extension;
    }

    public ResourceLocation(String domain, String path) {
        this(domain, path, null);
    }

    public String getFullPath() {
        if (extension != null) return String.format("assets/%s/%s.%s", domain, path, extension);
        return String.format("assets/%s/%s", domain, path);
    }

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

    public static ResourceLocation fromString(String location, boolean extension) {
        var spl = location.split(Pattern.quote(":"));
        if (spl.length != 2) return null;
        var domain = spl[0];
        var pathExt = spl[1];
        if (extension) {
            var extPer = pathExt.lastIndexOf('.');
            if (extPer >= 0) {
                return new ResourceLocation(domain, pathExt.substring(0, extPer), pathExt.substring(extPer + 1));
            }
        }
        return new ResourceLocation(domain, pathExt, null);
    }

}
