package com.justinm.togglepvp.command;

import com.justinm.togglepvp.handler.PVPToggleHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = "togglepvp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class PVPCommand {

    @SubscribeEvent
    public static void registerCommand(final FMLLoadCompleteEvent event) {
        // Commands are registered via CommandEvent
    }
}
