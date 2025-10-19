package me.afoolslove.itemtocoin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.afoolslove.itemtocoin.ItemToCoinMod;
import me.afoolslove.itemtocoin.ToCoin;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(ToCoin.class, new ToCoin.Serializer())
            .setPrettyPrinting()
            .create();
    public static final TypeToken<Map<ResourceLocation, ToCoin>> TO_COIN_TYPE = new TypeToken<>() {
    };

    public static final ResourceLocation SINGLE_SOUND_LOCATION = ResourceLocation.fromNamespaceAndPath(ItemToCoinMod.MODID, "sound");
    public static final ResourceLocation MULTIPLE_SOUND_LOCATION = ResourceLocation.fromNamespaceAndPath(ItemToCoinMod.MODID, "sounds");

    public static Holder<SoundEvent> SINGLE_SOUND;
    public static Holder<SoundEvent> MULTIPLE_SOUND;


    public static Map<ResourceLocation, ToCoin> toCoinMap;

    public static void load(Map<ResourceLocation, ToCoin> toCoinMap) {
        Config.toCoinMap = new HashMap<>(toCoinMap);

        ToCoin singleSound = Config.toCoinMap.remove(SINGLE_SOUND_LOCATION);
        if (singleSound == null) {
            SINGLE_SOUND = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse("minecraft:entity.experience_orb.pickup")));
        } else {
            SINGLE_SOUND = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(singleSound.type())));
        }

        ToCoin multipleSound = Config.toCoinMap.remove(MULTIPLE_SOUND_LOCATION);
        if (multipleSound == null) {
            MULTIPLE_SOUND = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse("minecraft:entity.player.levelup")));
        } else {
            MULTIPLE_SOUND = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(multipleSound.type())));
        }
    }

    public static void loadFile() {
        try {
            Path itemToCoinPath = FMLPaths.CONFIGDIR.get().resolve(ItemToCoinMod.MODID);
            if (!Files.exists(itemToCoinPath)) {
                Files.createDirectory(itemToCoinPath);
            }
            Path toCoinsPath = itemToCoinPath.resolve("toCoins.json");
            if (!Files.exists(toCoinsPath)) {
                Files.writeString(toCoinsPath, """
                        {
                          "itemtocoin:sound": {
                            "type": "minecraft:entity.experience_orb.pickup"
                          },
                          "itemtocoin:sounds": {
                            "type": "minecraft:entity.player.levelup"
                          },
                          "minecraft:stone": {
                            "amount": 1,
                            "type": "itc",
                            "rate": 1
                          }
                        }\
                        """);
            }

            load(GSON.fromJson(Files.newBufferedReader(toCoinsPath), TO_COIN_TYPE));
        } catch (IOException e) {
            Config.toCoinMap = new HashMap<>();
            ItemToCoinMod.LOGGER.error("loading config failed.", e);
        }
    }

    public static void saveFile() {
        saveFile(toCoinMap);
    }

    public static void saveFile(Map<ResourceLocation, ToCoin> toCoinMap) {
        try {
            Path itemToCoinPath = FMLPaths.CONFIGDIR.get().resolve(ItemToCoinMod.MODID);
            if (!Files.exists(itemToCoinPath)) {
                Files.createDirectory(itemToCoinPath);
            }
            Path toCoinsPath = itemToCoinPath.resolve("toCoins.json");
            Files.writeString(toCoinsPath, GSON.toJson(toCoinMap));
        } catch (IOException e) {
            ItemToCoinMod.LOGGER.error("saving config failed.", e);
        }
    }
}
