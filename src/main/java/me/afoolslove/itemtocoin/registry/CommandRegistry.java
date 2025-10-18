package me.afoolslove.itemtocoin.registry;

import com.mojang.brigadier.CommandDispatcher;
import me.afoolslove.itemtocoin.commands.ItemToCoinCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandRegistry {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(new ItemToCoinCommand());
    }
}
