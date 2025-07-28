package me.miran.bedrockcracker.util;

import net.minecraft.client.MinecraftClient;

public class WorldHelper {

    public static Dimension getDimension() {
        if (MinecraftClient.getInstance().world == null) return null;

        String dimensionID = MinecraftClient.getInstance().world.getRegistryKey().getValue().toString();

        switch (dimensionID){
            case "minecraft:overworld" -> {
                return Dimension.OVERWORLD;
            }
            case "minecraft:the_nether" -> {
                return Dimension.NETHER;
            }
            case "minecraft:the_end" -> {
                return Dimension.END;
            }
            default -> {
                return null;
            }
        }
    }

    public enum Dimension {
        OVERWORLD,
        NETHER,
        END
    }


}
