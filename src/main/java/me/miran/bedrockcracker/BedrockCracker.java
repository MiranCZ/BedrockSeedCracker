package me.miran.bedrockcracker;

import me.miran.bedrockcracker.api.BedrockCrackerController;
import me.miran.bedrockcracker.api.settings.BedrockCrackerSettings;
import me.miran.bedrockcracker.api.DefaultBedrockCrackerController;
import me.miran.bedrockcracker.api.settings.CrackStartType;
import me.miran.bedrockcracker.command.CrackSeedCommand;
import me.miran.bedrockcracker.cracker.NetherCracker;
import me.miran.bedrockcracker.cracker.OverworldCracker;
import me.miran.bedrockcracker.util.BedrockCollector;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BedrockCracker implements ModInitializer {


    private static final String CHAT_PREFIX = "§8[§5§oBedrockCracker§8]§r ";

    private static final List<BedrockCrackerController> controllers = new ArrayList<>();

    private static final BedrockCrackerSettings settings = new BedrockCrackerSettings();


    @Override
    public void onInitialize() {
        loadControllersAndExecuteSetup();

        if (settings.crackStartType == CrackStartType.COMMAND) {
            ClientCommandRegistrationCallback.EVENT.register(new CrackSeedCommand());
        }

        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEndListener());
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> BedrockCollector.collectBedrock(chunk));
    }

    public static CrackStartType getCrackStartType() {
        return settings.crackStartType;
    }

    private void loadControllersAndExecuteSetup() {
        controllers.addAll(FabricLoader.getInstance().getEntrypoints("bedrockcracker", BedrockCrackerController.class));
        controllers.add(new DefaultBedrockCrackerController());

        controllers.sort(Comparator.comparingInt(BedrockCrackerController::getPriority));

        for (BedrockCrackerController controller : controllers) {
            controller.setup(settings);
        }
    }



    public static void sendChatMessage(String message) {
        sendChatMessage(Text.of(message));
    }

    public static void sendChatMessage(Text message) {
        if (MinecraftClient.getInstance().player == null) return;

        if (!settings.logProgress) return;

        MinecraftClient.getInstance().player.sendMessage(Text.literal(CHAT_PREFIX).append(message));
    }

    public static void crackWorldSeed() {
        if (!BedrockCollector.isCollected()) {
            sendChatMessage("§cYou dont have enough bedrock info!");
            sendChatMessage("§6Make sure you visited both the overworld and the nether");
            return;
        }

        sendChatMessage("§7Started cracking...");

        long startTime = System.currentTimeMillis();
        List<Long> structureSeeds = NetherCracker.crack();
        sendChatMessage("§7Search finished in "+((System.currentTimeMillis() - startTime)/1000) + " seconds");


        if (structureSeeds.isEmpty()) {
            sendChatMessage("§4SOMETHING WE WRONG :( no structure seed was found");
            return;
        } else if (structureSeeds.size() == 1) {
            sendChatMessage("§2Structure seed found: §a"+structureSeeds.get(0));
        } else if (structureSeeds.size() < 5) {
            sendChatMessage("§6Found " + structureSeeds.size() + " structure seeds... " + structureSeeds);
        } else {
            sendChatMessage("§cFound multiple structure seeds (" + structureSeeds.size() + ") " +
                    "guessing the world seed might take a bit longer");
        }

        sendChatMessage("§7Brute forcing world seed...");

        List<Long> worldSeeds = new ArrayList<>();
        for (long l : structureSeeds) {
            OverworldCracker.addOverworldSeedToList(l, worldSeeds);
        }

        if (worldSeeds.isEmpty()) {
            sendChatMessage("§cSOMETHING WE WRONG :( no world seed was found");
        } else if (worldSeeds.size() == 1) {

            for (BedrockCrackerController controller : controllers) {
                controller.seedCrackedEvent(worldSeeds.get(0));
            }

        } else {
            sendChatMessage("§4Found multiple (" + worldSeeds.size() + ") world seeds :(");

            if (worldSeeds.size() < 15) {
                sendChatMessage(worldSeeds.toString());
            }
        }
    }
}
