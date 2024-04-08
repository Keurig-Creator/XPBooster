package com.keurig.xpbooster.base.data;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.keurig.xpbooster.util.NumUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EXPBoost {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("uuid")
    private final UUID uuid;

    private double multiplier;
    private long date;
    private String type;

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        Calendar boostTime = Calendar.getInstance();
        boostTime.setTimeInMillis(date);

        if (date <= 0) {
            return false;
        }

        return boostTime.compareTo(now) < 0;
    }

    public String getRemainingTime() {
        if (date == 0) {
            return "PERMANENT";
        } else {
            return NumUtil.timeFormat(date, true);
        }
    }

    public JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("multiplier", multiplier);
        jsonObject.addProperty("date", date);
        return jsonObject;
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
}