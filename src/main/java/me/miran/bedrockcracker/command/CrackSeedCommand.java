package me.miran.bedrockcracker.command;

import com.mojang.brigadier.CommandDispatcher;
import me.miran.bedrockcracker.BedrockCracker;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CrackSeedCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register((ClientCommandManager.literal("crackseed")).executes((context)-> run((ClientCommandSource) context.getSource())));

    }

    private int run(ClientCommandSource source) {
        new Thread(BedrockCracker::crackWorldSeed).start();

        return 0;
    }
}
