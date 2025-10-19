package me.afoolslove.itemtocoin;

import com.google.gson.*;

import java.lang.reflect.Type;

public record ToCoin(int amount, String type, int rate) {

    public static class Serializer implements JsonDeserializer<ToCoin> {

        @Override
        public ToCoin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int amount = 1;
            String type = "";
            int rate = 1;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("amount")) {
                    amount = jsonObject.get("amount").getAsInt();
                }
                if (jsonObject.has("type")) {
                    type = jsonObject.get("type").getAsString();
                }
                if (jsonObject.has("rate")) {
                    rate = jsonObject.get("rate").getAsInt();
                }
            }
            return new ToCoin(amount, type, rate);
        }
    }
}
