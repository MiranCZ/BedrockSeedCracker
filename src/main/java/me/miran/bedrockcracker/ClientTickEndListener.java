package me.miran.bedrockcracker;

import me.miran.bedrockcracker.api.settings.CrackStartType;
import me.miran.bedrockcracker.util.BedrockCollector;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;


/**
 * handles cache clearing of BedrockCollector and cracking seeds if it's set to AUTO
 */
public class ClientTickEndListener implements ClientTickEvents.EndTick {

    private boolean needsChange = false;
    private boolean cracked = false;

    @Override
    public void onEndTick(MinecraftClient client) {
        World world = client.world;

        // if we are not in a world, and we were before -> clear the cache
        if (world == null) {
            if (needsChange) {
                BedrockCollector.reset();
                needsChange = false;
                cracked = false;
            }
        } else {
            needsChange = true;
        }

        if (BedrockCollector.isCollected() && !cracked && BedrockCracker.getCrackStartType() == CrackStartType.AUTO) {
            cracked = true;
            new Thread(BedrockCracker::crackWorldSeed).start();
        }
    }
}
