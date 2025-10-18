package me.afoolslove.itemtocoin;

import com.mojang.logging.LogUtils;
import me.afoolslove.itemtocoin.config.Config;
import me.afoolslove.itemtocoin.registry.CommandRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import org.slf4j.Logger;

import java.util.Objects;

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

        ItemStack itemStack = event.getItemStack();

        ToCoin toCoin = Config.toCoinMap.get(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
        if (toCoin == null) {
            // 不支持转换为货币
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        CurrencyPlayerData.PlayerCurrency currency = null;
        for (CurrencyPlayerData.PlayerCurrency playerCurrency : EconomyAPI.getCurrencyPlayerData(serverPlayer).value) {
            if (Objects.equals(playerCurrency.currency.getName(), toCoin.type())) {
                currency = playerCurrency;
                break;
            }
        }

        if (currency == null) {
            MutableComponent msg = Component.translatable("itemtocoin.notype", toCoin.type());
            if (serverPlayer.hasPermissions(4)) {
                serverPlayer.sendSystemMessage(Component.literal(msg.getString()));
            } else {
                LOGGER.warn(msg.toString());
            }
            return;
        }

        int amount = toCoin.amount();
        if (itemStack.getCount() < amount) {
            // 数量不够转换
            MutableComponent msg = Component.translatable("itemtocoin.notenough", itemStack.getDisplayName(), amount);
            serverPlayer.sendSystemMessage(Component.literal(msg.getString()));
            return;
        }

        Holder<SoundEvent> sound = Config.SINGLE_SOUND;
        int count = 1;
        // 玩家是否潜行
        if (serverPlayer.isCrouching()) {
            sound = Config.MULTIPLE_SOUND;
            count = itemStack.getCount() / amount;
            itemStack.shrink(count * amount);
            if (itemStack.isEmpty()) {
                serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else {
            itemStack.shrink(amount);
        }
        int money = count * toCoin.rate();
        EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(serverPlayer, toCoin.type(), money);
        EconomyAPI.syncPlayer(serverPlayer);
        MutableComponent msg = Component.translatable("itemtocoin.tocoin", money, currency.currency.symbol.value, (long) currency.balance, currency.currency.symbol.value);
        serverPlayer.sendSystemMessage(Component.literal(msg.getString()));

        long rand = event.getLevel().getRandom().nextLong();
        serverPlayer.connection.send(new ClientboundSoundPacket(sound, SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1.0F, 1.0F, rand));
    }

//    @OnlyIn(Dist.DEDICATED_SERVER)
//    @SubscribeEvent
//    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            SyncToCoinsPacket.syncClient(serverPlayer);
//        }
//    }
}
