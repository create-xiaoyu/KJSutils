package com.xiaoyu.kjsutils;

import com.google.gson.*;
import de.keksuccino.fancymenu.customization.variables.Variable;
import de.keksuccino.fancymenu.customization.variables.VariableHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KJSutilsWrapper {
    private Path ValidateAndNormalizePath(String Path) {
        Path minecraftDir = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath();
        Path = Path.replace('\\', '/');
        return minecraftDir.resolve(Path).normalize().toAbsolutePath();
    }

    private String ResolveToJsonString(String pathStr) {
        try {
            Path baseDir = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath();
            Path filePath = baseDir.resolve(pathStr.replace('\\', '/')).normalize().toAbsolutePath();

            if (Files.exists(filePath)) {
                return Files.readString(filePath, StandardCharsets.UTF_8);
            }

            throw new IOException("File not found or invalid path: " + pathStr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON source: " + pathStr, e);
        }
    }

    private void WriteJsonToFile(String pathStr, String content) {
        try {
            Path baseDir = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath();
            Path filePath = baseDir.resolve(pathStr.replace('\\', '/')).normalize().toAbsolutePath();

            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write JSON to file: " + pathStr, e);
        }
    }

    public boolean Download(String URL, Path savePath, String FileName) {
        Path normalizedPath = ValidateAndNormalizePath(String.valueOf(savePath));
        if (Objects.equals(FileName, "null")) {
            FileName = URL.substring(URL.lastIndexOf('/') + 1);
            if (FileName.isEmpty()) {
                FileName = "HttpJS_Download_File";
            }
        }
        savePath = normalizedPath.resolve(FileName).normalize().toAbsolutePath();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URI(URL).toURL().openConnection();
            try (InputStream input = connection.getInputStream()) {
                Files.copy(input, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
            KJSutils.LOGGER.info("Download Success {} to {}, Code: {}", URL, savePath, connection.getResponseCode());
            return true;
        } catch (IOException | URISyntaxException e) {
            KJSutils.LOGGER.error("Download Error: {}", savePath, e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\['([^']+)']|\\.([A-Za-z0-9_]+)|\\[(\\d+)]|\\*|^([A-Za-z0-9_]+)");

    public String Analysis(String filePath, String jsonPath) {
        List<String> list = GetJsonValueByPath(ResolveToJsonString(filePath), jsonPath);
        if (list.isEmpty()) return "null";
        return list.getFirst();
    }

    public List<String> AnalysisAll(String filePath, String jsonPath) {
        return GetJsonValueByPath(ResolveToJsonString(filePath), jsonPath);
    }

    private List<String> GetJsonValueByPath(String jsonString, String jsonParsingPath) {
        try {
            JsonElement root = JsonParser.parseString(jsonString);
            JsonElement result = get(root, jsonParsingPath);
            return Flatten(result);
        } catch (Exception e) {
            KJSutils.LOGGER.error("Failed to parse JSON source: {}", jsonParsingPath, e);
            return List.of();
        }
    }

    private JsonElement get(JsonElement root, String path) {
        if (root == null || path == null || !path.startsWith("$"))
            throw new IllegalArgumentException("Invalid JSON path: " + path);

        if (path.startsWith("$$.")) {
            String rawKey = path.substring(3);
            if (!root.isJsonObject()) return JsonNull.INSTANCE;

            JsonObject obj = root.getAsJsonObject();
            return obj.has(rawKey) ? obj.get(rawKey) : JsonNull.INSTANCE;
        }

        path = path.substring(1);
        if (path.startsWith(".")) path = path.substring(1);
        List<Object> tokens = Tokenize(path);
        return Navigate(root, tokens);
    }

    private List<Object> Tokenize(String path) {
        List<Object> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(path);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            } else if (matcher.group(3) != null) {
                tokens.add(Integer.parseInt(matcher.group(3)));
            } else if (matcher.group(4) != null) {
                tokens.add(matcher.group(4));
            } else {
                tokens.add("*");
            }
        }
        return tokens;
    }

    private JsonElement Navigate(JsonElement current, List<Object> tokens) {
        for (Object token : tokens) {
            if (token.equals("*")) {
                JsonArray result = new JsonArray();
                if (current.isJsonObject()) {
                    for (var entry : current.getAsJsonObject().entrySet())
                        result.add(entry.getValue());
                } else if (current.isJsonArray()) {
                    for (JsonElement e : current.getAsJsonArray())
                        result.add(e);
                }
                current = result;
            } else if (token instanceof String key) {
                if (!current.isJsonObject()) return JsonNull.INSTANCE;
                JsonObject obj = current.getAsJsonObject();
                if (!obj.has(key)) return JsonNull.INSTANCE;
                current = obj.get(key);
            } else if (token instanceof Integer idx) {
                if (!current.isJsonArray()) return JsonNull.INSTANCE;
                JsonArray arr = current.getAsJsonArray();
                if (idx < 0 || idx >= arr.size()) return JsonNull.INSTANCE;
                current = arr.get(idx);
            }
        }
        return current;
    }

    private List<String> Flatten(JsonElement element) {
        List<String> list = new ArrayList<>();
        if (element == null || element.isJsonNull()) return list;

        if (element.isJsonPrimitive()) {
            list.add(element.getAsString());
        } else if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray())
                list.addAll(Flatten(e));
        } else if (element.isJsonObject()) {
            list.add(element.toString());
        }
        return list;
    }

    public void ModifyJsonValue(String filePath, String jsonPath, Object newValue) {
        try {
            String jsonContent = ResolveToJsonString(filePath);
            JsonElement root = JsonParser.parseString(jsonContent);

            JsonElement modifiedRoot = modifyJsonElement(root, jsonPath, newValue);

            String modifiedContent = new GsonBuilder().setPrettyPrinting().create().toJson(modifiedRoot);
            WriteJsonToFile(filePath, modifiedContent);

            KJSutils.LOGGER.info("Successfully modified {} at path {}", filePath, jsonPath);
        } catch (Exception e) {
            KJSutils.LOGGER.error("Failed to modify JSON: {}", filePath, e);
            throw new RuntimeException("Failed to modify JSON value", e);
        }
    }

    private JsonElement modifyJsonElement(JsonElement root, String path, Object newValue) {
        if (!path.startsWith("$.")) {
            throw new IllegalArgumentException("Only paths starting with '$.' are supported");
        }

        String relativePath = path.substring(2);
        String[] pathParts = relativePath.split("\\.");

        if (pathParts.length == 0) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }

        JsonObject currentObject;
        if (root.isJsonObject()) {
            currentObject = root.getAsJsonObject().deepCopy();
        } else {
            currentObject = new JsonObject();
        }

        JsonObject targetObject = currentObject;

        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if (!targetObject.has(part)) {
                targetObject.add(part, new JsonObject());
            }

            JsonElement nextElement = targetObject.get(part);
            if (!nextElement.isJsonObject()) {
                JsonObject newObject = new JsonObject();
                targetObject.add(part, newObject);
                nextElement = newObject;
            }

            targetObject = nextElement.getAsJsonObject();
        }

        String finalKey = pathParts[pathParts.length - 1];
        JsonElement newJsonValue = convertToJsonElement(newValue);
        targetObject.add(finalKey, newJsonValue);

        return currentObject;
    }

    private JsonElement convertToJsonElement(Object value) {
        switch (value) {
            case null -> {
                return JsonNull.INSTANCE;
            }
            case String s -> {
                return new JsonPrimitive(s);
            }
            case Number number -> {
                return new JsonPrimitive(number);
            }
            case Boolean b -> {
                return new JsonPrimitive(b);
            }
            case Character c -> {
                return new JsonPrimitive(c);
            }
            case JsonElement jsonElement -> {
                return jsonElement;
            }
            default -> {
                try {
                    return JsonParser.parseString(value.toString());
                } catch (JsonSyntaxException e) {
                    return new JsonPrimitive(value.toString());
                }
            }
        }
    }

    public void FMsetVariable(String variableName, String variableValue) {
        VariableHandler.setVariable(variableName, variableValue);
    }

    public void FMremoveVariable(String variableName) {
        VariableHandler.removeVariable(variableName);
    }

    public Variable FMgetVariable(String variableName) {
        return VariableHandler.getVariable(variableName);
    }

    public Boolean FMexistsVariable(String variableName) {
        return VariableHandler.variableExists(variableName);
    }

    public void FMinit() {
        VariableHandler.init();
    }

    public void FMclearAllVariables() {
        VariableHandler.clearVariables();
    }

    public String GetClientLanguage() {
        return Minecraft.getInstance().getLocale().getLanguage();
    }

    public String GetSystemLanguage() {
        Locale locale = Locale.getDefault();
        return locale.toLanguageTag();
    }
}
