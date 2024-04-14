package com.keurigsweb.xpbooster.base.data;

import com.google.gson.*;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.NumUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.UUID;


@Getter
@Setter
@SuperBuilder
public class EXPBoost {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected UUID uuid;
    protected double multiplier;
    protected long time;
    private String boosterType;

    public EXPBoost(UUID uuid, double multiplier, long time, String boosterType) {

        this.uuid = uuid;
        this.multiplier = multiplier;
        this.time = time;
        this.boosterType = boosterType;
    }

    public EXPBoost(UUID uuid, double multiplier, long time) {
        this(uuid, multiplier, time, null);
    }

    public static class EXPBoostSerializer implements JsonSerializer<EXPBoost>, JsonDeserializer<EXPBoost> {

        @Override
        public JsonElement serialize(EXPBoost src, Type typeOfSrc, JsonSerializationContext context) {
            return src.toJsonElement();
        }

        @Override
        public EXPBoost deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return gson.fromJson(json.getAsJsonObject(), EXPBoost.class);
        }
    }

    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        Calendar boostTime = Calendar.getInstance();
        boostTime.setTimeInMillis(time);

        if (time <= 0) {
            return false;
        }

        return boostTime.compareTo(now) < 0;
    }

    public String getRemainingTime() {
        if (time == 0) {
            return "PERMANENT";
        } else {
            return NumUtil.timeFormat(time, true);
        }
    }

    public JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("multiplier", multiplier);
        jsonObject.addProperty("time", time);
        return jsonObject;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}