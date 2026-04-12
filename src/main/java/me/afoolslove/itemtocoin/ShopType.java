package me.afoolslove.itemtocoin;

import com.viscriptshop.util.ViScriptShopServerUtil;
import me.afoolslove.itemtocoin.config.Config;
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
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;

import java.util.Objects;
import java.util.function.Consumer;

public enum ShopType {
    SDM("sdm", event -> {
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
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
                ItemToCoinMod.LOGGER.warn(msg.toString());
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
    }),
    VSS("vss", event -> {
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
        ItemStack itemStack = event.getItemStack();

        ToCoin toCoin = Config.toCoinMap.get(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
        if (toCoin == null) {
            // 不支持转换为货币
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

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
        ViScriptShopServerUtil.addMoney(serverPlayer, money);
        MutableComponent msg = Component.translatable("itemtocoin.tocoin", money, "", (long) ViScriptShopServerUtil.getMoney(serverPlayer), "");
        serverPlayer.sendSystemMessage(Component.literal(msg.getString()));

        long rand = event.getLevel().getRandom().nextLong();
        serverPlayer.connection.send(new ClientboundSoundPacket(sound, SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1.0F, 1.0F, rand));
    }),
    ;
    private final String type;
    private final Consumer<PlayerInteractEvent.RightClickItem> onRightClickItem;

    ShopType(String type, Consumer<PlayerInteractEvent.RightClickItem> onRightClickItem) {
        this.type = type;
        this.onRightClickItem = onRightClickItem;
    }

    public String getType() {
        return type;
    }

    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        onRightClickItem.accept(event);
    }

    public static ShopType getByType(String type) {
        if (type == null) {
            return SDM;
        }
        for (ShopType value : values()) {
            if (value.getType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return SDM;
    }
}
