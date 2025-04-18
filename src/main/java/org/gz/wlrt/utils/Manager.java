package org.gz.wlrt.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.gz.wlrt.Wlrt;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;

public class Manager {
    private static final HashMap<GlobalBlockPos, GlobalBlockPos> TO_SOURCE_MAP = new HashMap<>();
    private static final HashMap<GlobalBlockPos, HashSet<GlobalBlockPos>> TO_OUTPUT_MAP = new HashMap<>();
    private static @Nullable GlobalBlockPos LOOKING_AT = null;
    private static final Gson GSON = new Gson();


    public static void setLookingAt(GlobalBlockPos pos) {
        LOOKING_AT = pos;
    }

    public static GlobalBlockPos getLookingAt() {
        return LOOKING_AT;
    }

    public static void add(GlobalBlockPos output, GlobalBlockPos source) {
        TO_SOURCE_MAP.put(output, source);
        TO_OUTPUT_MAP.computeIfAbsent(source, k -> new HashSet<>()).add(output);
    }

    public static void removeBySource(GlobalBlockPos source) {
        var output = TO_OUTPUT_MAP.get(source);
        output.forEach(TO_SOURCE_MAP::remove);
        TO_OUTPUT_MAP.remove(source);
    }

    public static void removeByOutput(GlobalBlockPos output) {
        var source = TO_SOURCE_MAP.get(output);
        TO_OUTPUT_MAP.get(source).remove(output);
        if (TO_OUTPUT_MAP.get(source).isEmpty()) {
            TO_OUTPUT_MAP.remove(source);
        }
        TO_SOURCE_MAP.remove(output);
    }


    public static void clear() {
        TO_SOURCE_MAP.clear();
        TO_OUTPUT_MAP.clear();
    }

    public static GlobalBlockPos getSource(GlobalBlockPos output) {
        return TO_SOURCE_MAP.get(output);
    }

    public static HashSet<GlobalBlockPos> getOutputs(GlobalBlockPos source) {
        return TO_OUTPUT_MAP.get(source);
    }


    public static void load(Path root) {
        Path path = root.resolve("wlrt").resolve("wlrt.json");
        if (!Files.exists(path)) {
            return;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            JsonObject obj = GSON.fromJson(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
            obj.asMap().forEach((output, source) -> {
                GlobalBlockPos outputPos = GlobalBlockPos.from(output);
                GlobalBlockPos sourcePos = GlobalBlockPos.from(source.getAsString());
                add(outputPos, sourcePos);
            });
        } catch (IOException e) {
            Wlrt.LOGGER.error("Failed to load file: {}", e.getMessage());
        }
    }

    private static void createParentDirectory(Path path) {
        Path parent = path.getParent();

        try {
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            Wlrt.LOGGER.error("Failed to create parent directory for save file: " + e.getMessage());
        }
    }

    public static void save(Path root) {
        Path mapPath = root.resolve("wlrt").resolve("wlrt.json");
        Wlrt.LOGGER.info("Saving WLRT data to {}", mapPath);
        createParentDirectory(mapPath);
        try (OutputStream outputStream = Files.newOutputStream(mapPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            JsonObject mapObject = new JsonObject();
            TO_SOURCE_MAP.forEach((to, source) -> {
                mapObject.add(to.toString(), new JsonPrimitive(source.toString()));
            });
            String json = GSON.toJson(mapObject);
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Wlrt.LOGGER.error("Failed to save file: {}", e.getMessage());
        }
    }

    public static boolean isLinkedOutput(GlobalBlockPos output) {
        return getSource(output) != null;
    }

    public static boolean isLinkedSource(GlobalBlockPos pos) {
        return getOutputs(pos) != null;
    }
}
