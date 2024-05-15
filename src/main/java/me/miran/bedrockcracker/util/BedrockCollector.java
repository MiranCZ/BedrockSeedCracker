package me.miran.bedrockcracker.util;

import me.miran.bedrockcracker.BedrockCracker;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.List;

public class BedrockCollector {

    private static final int NETHER_BEDROCK_SIZE = 128;
    private static final int OVERWORLD_BEDROCK_SIZE = 512;

    private static final HashSet<BlockPos> netherBedrockRoof = new HashSet<>();
    private static final HashSet<BlockPos> netherBedrockFloor = new HashSet<>();
    private static final HashSet<BlockPos> overworldBedrock = new HashSet<>();

    private static boolean netherBedrockCollected = false;
    private static boolean overworldBedrockCollected = false;

    public static void collectBedrock(WorldChunk chunk) {
        WorldHelper.Dimension dimension = WorldHelper.getDimension();

        if (dimension == null || dimension == WorldHelper.Dimension.END) return;


        if (dimension == WorldHelper.Dimension.NETHER) {
            if (netherBedrockFloor.size() < NETHER_BEDROCK_SIZE) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = chunk.getPos().getBlockPos(x, 4, z);
                        if (chunk.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                            netherBedrockFloor.add(pos);
                        }
                    }
                }
            }

            if (netherBedrockRoof.size() < NETHER_BEDROCK_SIZE) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = chunk.getPos().getBlockPos(x, 123, z);
                        if (chunk.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                            netherBedrockRoof.add(pos);
                        }
                    }
                }
            }

            if (netherBedrockFloor.size() >= NETHER_BEDROCK_SIZE || netherBedrockRoof.size() >= NETHER_BEDROCK_SIZE) {
                if (!netherBedrockCollected) {
                    netherBedrockCollected = true;
                    BedrockCracker.sendChatMessage("§2Bedrock from the nether collected! " +
                            "§7(floor: "+netherBedrockFloor.size()+", roof: "+netherBedrockRoof.size()+")");
                }
            }
        } else if (dimension == WorldHelper.Dimension.OVERWORLD) {
            if (overworldBedrock.size() > OVERWORLD_BEDROCK_SIZE) {
                if (!overworldBedrockCollected) {
                    overworldBedrockCollected = true;
                    BedrockCracker.sendChatMessage("§2Bedrock from overworld collected! §7("+overworldBedrock.size()+")");
                }
                return;
            }

            for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = chunk.getPos().getBlockPos(x, -60, z);

                        if (chunk.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                            overworldBedrock.add(pos);
                        }
                }
            }

        }

    }

    public static void reset() {
        netherBedrockFloor.clear();
        netherBedrockRoof.clear();
        overworldBedrock.clear();

        overworldBedrockCollected = false;
        netherBedrockCollected = false;
    }

    public static List<BlockPos> getNetherRoofBedrock() {
        return netherBedrockRoof.stream().toList();
    }

    public static List<BlockPos> getNetherFloorBedrock() {
        return netherBedrockFloor.stream().toList();
    }

    public static List<BlockPos> getOverworldBedrock() {
        return overworldBedrock.stream().toList();
    }

    public static boolean isCollected() {
        return netherBedrockFloor.size() >= NETHER_BEDROCK_SIZE &&
                netherBedrockRoof.size() >= NETHER_BEDROCK_SIZE &&
                overworldBedrock.size() >= OVERWORLD_BEDROCK_SIZE;
    }


}
