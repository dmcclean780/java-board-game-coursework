package com.example.model.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;

public class LangManager {

    private static final String LANG_PATH = "/config/lang/";
    private static String currentLanguage;

    private static Map<String, String> translations = new HashMap<>();

    public static void load(ObjectMapper mapper, String langCode) {
        try (InputStream is = LangManager.class.getResourceAsStream(LANG_PATH + langCode + ".json")) {
            if (is == null) {
                throw new IllegalArgumentException("Missing language file: " + langCode);
            }
            translations = mapper.readValue(is, new TypeReference<Map<String, String>>() {});
            currentLanguage = langCode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load language file for " + langCode, e);
        }
    }

    public static String get(String key) {
        if (translations.isEmpty()) {
            throw new IllegalStateException("Language not loaded. Call LangManager.load() first.");
        }
        return translations.getOrDefault(key, "" + key + "_MISSING");
    }

    public static String setLanguage(String langCode) {
        load(new ObjectMapper(), langCode);
        return langCode;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static HashMap<String, String> getAvailableLanguages() {
        HashMap<String, String> languages = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            Path langDir = Paths.get(LangManager.class.getResource(LANG_PATH).toURI());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(langDir, "*.json")) {
                for (Path path : stream) {
                    try (InputStream is = Files.newInputStream(path)) {
                        JsonNode root = mapper.readTree(is);

                        // handle typo or correct key
                        String name = null;
                        if (root.has("language.name")) {
                            name = root.get("language.name").asText();
                        } else if (root.has("langauge.name")) {
                            name = root.get("langauge.name").asText();
                        }

                        if (name != null && !name.isBlank()) {
                            languages.put(path.getFileName().toString().replace(".json", ""), name);
                        } else {
                            System.err.println("Skipping language file without name: " + path);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to read " + path + ": " + e.getMessage());
                    }
                }
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load available languages from " + LANG_PATH, e);
        }

        return languages;
    }
}