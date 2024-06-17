package com.keurigsweb.xpbooster.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonConfig {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(EXPBoost.class, new EXPBoost.EXPBoostSerializer())
            .registerTypeAdapter(GlobalBoost.class, new GlobalBoost.GlobalBoostSerializer())
//            .registerTypeAdapter(Boost.class, new Boost.BoostTypeAdapter())
            .setPrettyPrinting()
            .create();


    @Getter
    private final Map<String, EXPBoost> expBoosts;
    @Getter
    private final Map<Double, GlobalBoost> globalBoosts;
    private final File file;

    public JsonConfig(String name, XPBoostPlugin plugin) {
        file = new File(plugin.getDataFolder(), name);
        expBoosts = new HashMap<>();
        globalBoosts = new HashMap<>();
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
            JsonObject jsonObject = new JsonObject();

            JsonObject userSection = new JsonObject();
            for (Map.Entry<String, EXPBoost> entry : expBoosts.entrySet()) {
                userSection.add(entry.getKey(), gson.toJsonTree(entry.getValue()));
            }

            JsonObject globalSection = new JsonObject();
            for (Map.Entry<Double, GlobalBoost> entry : globalBoosts.entrySet()) {
                globalSection.add(String.valueOf(entry.getKey()), gson.toJsonTree(entry.getValue()));
            }

            jsonObject.add("user", userSection);
            jsonObject.add("global", globalSection);
            gson.toJson(jsonObject, writer);
        }
    }

    private void readFile() throws IOException {
        try (Reader reader = new FileReader(file)) {
            expBoosts.clear();

            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject != null) {
                JsonObject userSection = jsonObject.getAsJsonObject("user");

                if (userSection != null) {
                    for (Map.Entry<String, JsonElement> entry : userSection.entrySet()) {
                        String uuidString = entry.getKey();
                        JsonObject expBoostJson = entry.getValue().getAsJsonObject();
                        EXPBoost expBoost = gson.fromJson(expBoostJson, EXPBoost.class);
                        expBoost.setUuid(UUID.fromString(uuidString));
                        expBoosts.put(uuidString, expBoost);
                    }
                } else {
                    Chat.log("USER SECTION IS NULL");
                }

                JsonObject globalSection = jsonObject.getAsJsonObject("global");

                if (globalSection != null) {
                    for (Map.Entry<String, JsonElement> entry : globalSection.entrySet()) {
                        double key = Double.parseDouble(entry.getKey());
                        JsonObject globalBoostJson = entry.getValue().getAsJsonObject();
                        GlobalBoost globalBoost = gson.fromJson(globalBoostJson, GlobalBoost.class);
                        globalBoost.setMultiplier(Double.parseDouble(entry.getKey()));
                        globalBoosts.put(key, globalBoost);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}