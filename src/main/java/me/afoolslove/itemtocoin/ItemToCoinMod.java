package me.afoolslove.itemtocoin;

import com.mojang.logging.LogUtils;
import me.afoolslove.itemtocoin.config.Config;
import me.afoolslove.itemtocoin.registry.CommandRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ItemToCoinMod.MODID)
public class ItemToCoinMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "itemtocoin";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ItemToCoinMod(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(CommandRegistry::register);
//        modEventBus.addListener(NetworkRegistry::register);
        Config.loadFile();
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!Config.SHOP_INSTALLED) {
            // 目标商店未安装
            return;
        }
        if (event.isCanceled()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer
                && event.getHand() == InteractionHand.MAIN_HAND)) {
            return;
        }

        if (event.getEntity().level().isClientSide()) {
            return;
        }

        Config.SHOP_TYPE.rightClickItem(event);
    }

//    @OnlyIn(Dist.DEDICATED_SERVER)
//    @SubscribeEvent
//    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            SyncToCoinsPacket.syncClient(serverPlayer);
//        }
//    }
}
