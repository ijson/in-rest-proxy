package com.ijson.rest.proxy.config;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 17/6/27.
 */

public class ConfigFactory {

    private Logger log = LoggerFactory.getLogger(ConfigFactory.class);

    public static ExtMap<String, Object> getConfig(String name) throws IOException {
        ConfigFactory factory = new ConfigFactory();
        ExtMap<String, Object> extMap = new ExtMap<>();
        Path path = factory.scanConfigPath();
        List<String> value = Files.readLines(new File(path.toFile().getPath() + File.separator + name), Charsets.UTF_8);
        value.stream().forEach(extension -> setMap(extMap, extension));
        return extMap;
    }

    static String getConfigToString(String name) throws IOException {
        ConfigFactory factory = new ConfigFactory();
        Path path = factory.scanConfigPath();
        List<String> value = Files.readLines(new File(path.toFile().getPath() + File.separator + name), Charsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        value.forEach(sb::append);
        return sb.toString();
    }


    private static void setMap(ExtMap<String, Object> extMap, String extension) {
        if (Strings.isNullOrEmpty(extension)) return;
        Map<String, String> map = Splitter.on(",").trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(extension);
        map.forEach((k, v) -> {
            if (k.contains("#")) return;
            extMap.put(k.trim(), v.trim());
        });
    }


    private Path scanConfigPath() {
        Path basePath = scanProperty();
        if (basePath != null) {
            return basePath;
        }
        //查找若干文件以便找到classes根目录
        String files = "autoconf,log4j.properties,logback.xml,application.properties";
        for (String i : Splitter.on(',').split(files)) {
            String s = scanResource(i);
            if (s != null) {
                basePath = new File(s).toPath().getParent().resolve("autoconf");
                File root = basePath.toFile();
                if (root.exists() || root.mkdir()) {
                    return basePath;
                }
            }
        }
        return new File(System.getProperty("java.io.tmpdir")).toPath();
    }

    private Path scanProperty() {
        String localCachePath = System.getProperty("config.path");
        if (!Strings.isNullOrEmpty(localCachePath)) {
            File f = new File(localCachePath);
            f.mkdirs();
            return f.toPath();
        }
        return null;
    }

    private String scanResource(String resource) {
        try {
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources(resource);
            while (ps.hasMoreElements()) {
                URL url = ps.nextElement();
                String s = url.toString();
                if (s.startsWith("file:/")) {
                    String os = System.getProperty("os.name");
                    if (os != null && os.toLowerCase().contains("windows")) {
                        return s.substring(6);
                    } else {
                        return s.substring(5);
                    }
                }
            }
        } catch (IOException e) {
            log.error("cannot find {} under classpath", resource, e);
        }
        return null;
    }
}
