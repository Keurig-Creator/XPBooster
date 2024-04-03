package com.keurig.xpbooster.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.EXPBoost;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonConfig {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(EXPBoost.class, new EXPBoost.EXPBoostSerializer())
            .setPrettyPrinting()
            .create();


    @Getter
    private final Map<UUID, EXPBoost> expBoosts;
    private final File file;

    public JsonConfig(String name, XPBoostPlugin plugin) {
        file = new File(plugin.getDataFolder(), name);
        expBoosts = new HashMap<>();
    }

    public void save() {
        try {
            writeFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            save();
        }

        try {
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(File path) throws IOException {
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(expBoosts, writer);
        }
    }

    private void readFile() throws IOException {
        try (Reader reader = new FileReader(file)) {
            expBoosts.clear();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String uuidString = entry.getKey();
                JsonObject expBoostJson = entry.getValue().getAsJsonObject();
                UUID uuid = UUID.fromString(uuidString);
                EXPBoost expBoost = gson.fromJson(expBoostJson, EXPBoost.class);
                expBoosts.put(uuid, expBoost);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}