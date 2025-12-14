package com.justinm.togglepvp.event;

import com.justinm.togglepvp.TogglePVPMod;
import com.justinm.togglepvp.handler.PVPToggleHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.server.MinecraftServer;

@Mod.EventBusSubscriber(modid = "togglepvp", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PVPEventHandler {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();

        dispatcher.register(Commands.literal("pvp")
                .then(Commands.argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("on");
                            builder.suggest("off");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            String action = StringArgumentType.getString(context, "action");
                            ServerPlayer player = source.getPlayerOrException();

                            // Verificar si está en combate
                            if (PVPToggleHandler.isInCombat(player.getUUID())) {
                                long remaining = PVPToggleHandler.getCombatTimeRemaining(player.getUUID());
                                player.displayClientMessage(
                                        Component.literal("§cEstás en combate. Espera " + remaining + "s para cambiar PVP"),
                                        false
                                );
                                return 0;
                            }

                            if ("on".equalsIgnoreCase(action)) {
                                PVPToggleHandler.setPVPStatus(player.getUUID(), true);
                                player.displayClientMessage(
                                        Component.literal("§6PVP §ahabilitado§r").withStyle(style -> style),
                                        false
                                );
                                TogglePVPMod.LOGGER.info(player.getName().getString() + " ha habilitado PVP");
                                return 1;
                            } else if ("off".equalsIgnoreCase(action)) {
                                PVPToggleHandler.setPVPStatus(player.getUUID(), false);
                                player.displayClientMessage(
                                        Component.literal("§6PVP §cdeshabilitado§r").withStyle(style -> style),
                                        false
                                );
                                TogglePVPMod.LOGGER.info(player.getName().getString() + " ha deshabilitado PVP");
                                return 1;
                            } else {
                                player.displayClientMessage(
                                        Component.literal("§cUso: /pvp [on|off]"),
                                        false
                                );
                                return 0;
                            }
                        })
                )
                .executes(context -> {
                    context.getSource().sendFailure(Component.literal("§cDebes especificar 'on' u 'off'"));
                    return 0;
                })
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerAttack(LivingAttackEvent event) {
        // Solo procesamos si el que recibe daño es un jugador
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }

        // Obtenemos la fuente del daño
        DamageSource source = event.getSource();

        // Si el daño viene de otro jugador
        if (source.getEntity() instanceof ServerPlayer attacker) {
            // Si el atacante no tiene PVP activado, cancela el daño
            if (!PVPToggleHandler.isPVPEnabled(attacker.getUUID())) {
                event.setCanceled(true);
                attacker.displayClientMessage(
                        Component.literal("§cNo puedes atacar con PVP deshabilitado"),
                        true
                );
                return;
            }

            // Si la víctima no tiene PVP activado, cancela el daño
            if (!PVPToggleHandler.isPVPEnabled(victim.getUUID())) {
                event.setCanceled(true);
                attacker.displayClientMessage(
                        Component.literal("§c" + victim.getName().getString() + " tiene PVP deshabilitado"),
                        true
                );
                return;
            }

            // Solo si el ataque es válido (ambos tienen PVP ON), entran en combate
            PVPToggleHandler.enterCombat(attacker.getUUID());
            PVPToggleHandler.enterCombat(victim.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        // Marcar que el servidor está cerrando
        PVPToggleHandler.setServerShuttingDown(true);
        TogglePVPMod.LOGGER.info("Servidor cerrando - no se killará a jugadores en combate");
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // KILL INMEDIATO si el jugador se desconectó en combate y el servidor NO está cerrando
            if (PVPToggleHandler.isInCombat(player.getUUID()) && !PVPToggleHandler.isServerShuttingDown()) {
                player.kill();
                TogglePVPMod.LOGGER.info(player.getName().getString() + " fue killado por desconectarse en combate");
            }
            PVPToggleHandler.removePlayer(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Resetear el flag de servidor cerrando
            PVPToggleHandler.setServerShuttingDown(false);

            // Establecer PVP OFF por defecto al entrar al servidor
            PVPToggleHandler.setPVPStatus(player.getUUID(), false);
            player.displayClientMessage(
                    Component.literal("§6PVP deshabilitado por defecto. Usa §e/pvp on §6para activarlo"),
                    false
            );
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = event.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (PVPToggleHandler.isInCombat(player.getUUID())) {
                long timeRemaining = PVPToggleHandler.getCombatTimeRemaining(player.getUUID());
                player.displayClientMessage(
                        Component.literal("§eEn combate: §c" + timeRemaining + "s"),
                        true
                );
            }
        }
    }
}
