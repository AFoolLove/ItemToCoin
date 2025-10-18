package me.afoolslove.itemtocoin.network;

import me.afoolslove.itemtocoin.ItemToCoinMod;
import me.afoolslove.itemtocoin.ToCoin;
import me.afoolslove.itemtocoin.config.Config;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SyncToCoinsPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ItemToCoinMod.MODID, "sync_to_coins");
    public static final Type<SyncToCoinsPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncToCoinsPacket> STREAM_CODEC = CustomPacketPayload.codec(
            SyncToCoinsPacket::encode,
            SyncToCoinsPacket::decode
    );

    public final Map<ResourceLocation, ToCoin> toCoinMap;

    public SyncToCoinsPacket(Map<ResourceLocation, ToCoin> toCoinMap) {
        this.toCoinMap = new HashMap<>(toCoinMap);
    }

    public static SyncToCoinsPacket decode(RegistryFriendlyByteBuf buf) {
        return new SyncToCoinsPacket(Config.GSON.fromJson(buf.readUtf(), Config.TO_COIN_TYPE));
    }

    public static void encode(SyncToCoinsPacket packet, RegistryFriendlyByteBuf buf) {
        buf.writeUtf(Config.GSON.toJson(packet.toCoinMap));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncToCoinsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Config.load(packet.toCoinMap);
            Config.saveFile();
        });
    }

    public static void syncClient(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new SyncToCoinsPacket(Config.toCoinMap));
    }

    public static void sync() {
        PacketDistributor.sendToAllPlayers(new SyncToCoinsPacket(Config.toCoinMap));
    }
}
