package me.afoolslove.itemtocoin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.afoolslove.itemtocoin.config.Config;
import me.afoolslove.itemtocoin.network.SyncToCoinsPacket;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforgespi.language.IModFileInfo;

public class ItemToCoinCommand extends LiteralArgumentBuilder<CommandSourceStack> {
    public ItemToCoinCommand() {
        super("itemtocoin");
//        then(
//                LiteralArgumentBuilder.<CommandSourceStack>literal("sync")
//                        .requires(source -> source.hasPermission(4))
//                        .executes(context -> {
//                            if (context.getSource().source instanceof ServerPlayer) {
//                                SyncToCoinsPacket.sync();
//                                context.getSource().sendSystemMessage(Component.literal("synced."));
//                            }
//                            return Command.SINGLE_SUCCESS;
//                        })
//        );
        then(
                LiteralArgumentBuilder.<CommandSourceStack>literal("reload")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            Config.loadFile();
                            context.getSource().sendSystemMessage(Component.literal("reloaded."));
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
