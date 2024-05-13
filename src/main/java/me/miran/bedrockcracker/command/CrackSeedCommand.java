package me.miran.bedrockcracker.command;

import com.mojang.brigadier.CommandDispatcher;
import me.miran.bedrockcracker.BedrockCracker;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CrackSeedCommand implements CommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register((CommandManager.literal("crackseed")).executes((context)-> run(context.getSource())));
    }

    private int run(ServerCommandSource source) {
        new Thread(BedrockCracker::crackWorldSeed).start();

        return 0;
    }
}