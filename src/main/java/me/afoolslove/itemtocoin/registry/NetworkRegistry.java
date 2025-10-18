package me.afoolslove.itemtocoin.registry;

import me.afoolslove.itemtocoin.network.SyncToCoinsPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkRegistry {
    public static final String VERSION = "1.0";
    public static void register(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(SyncToCoinsPacket.TYPE, SyncToCoinsPacket.STREAM_CODEC, SyncToCoinsPacket::handle);
    }
}
