package com.keurigsweb.xpbooster.base.data.booster.global;

import com.google.gson.*;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.util.NumUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Type;
import java.util.Calendar;


@Getter
@Setter
@SuperBuilder
public class GlobalBoost {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected String uuid;
    protected double multiplier;
    protected long time;

    public GlobalBoost(String uuid, double multiplier, long time) {
        this.uuid = uuid;
        this.multiplier = multiplier;
        this.time = time;
    }

    public static class GlobalBoostSerializer implements JsonSerializer<GlobalBoost>, JsonDeserializer<GlobalBoost> {

        @Override
        public JsonElement serialize(GlobalBoost src, Type typeOfSrc, JsonSerializationContext context) {
            return src.toJsonElement();
        }

        @Override
        public GlobalBoost deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return gson.fromJson(json.getAsJsonObject(), GlobalBoost.class);
        }
    }

    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        Calendar boostTime = Calendar.getInstance();
        boostTime.setTimeInMillis(time);

        if (time <= 0) {
            return false;
        }

        if (boostTime.compareTo(now) < 0) {
            XPBoostPlugin.getInstance().getDataConfig().getGlobalBoosts().remove(multiplier);
            XPBoostPlugin.getInstance().getDataConfig().save();
            return true;
        }

        return false;
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
        jsonObject.addProperty("uuid", uuid);
        jsonObject.addProperty("time", time);
        return jsonObject;
    }

    public void addTime(long time) {
        this.time += time - System.currentTimeMillis();
    }

    public void removeTime(long time) {
        this.time -= time - System.currentTimeMillis();
    }

    public void save() {
        XPBoostPlugin.getInstance().getDataConfig().save();
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
