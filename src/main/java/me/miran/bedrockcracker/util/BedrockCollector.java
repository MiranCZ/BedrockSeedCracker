package me.miran.bedrockcracker.util;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.List;

public class BedrockCollector {

    private static final HashSet<BlockPos> netherBedrockRoof = new HashSet<>();
    private static final HashSet<BlockPos> netherBedrockFloor = new HashSet<>();
    private static final HashSet<BlockPos> overworldBedrock = new HashSet<>();

    private static final int NETHER_BEDROCK_SIZE = 64;
    private static final int OVERWORLD_BEDROCK_SIZE = 512;


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
        } else if (dimension == WorldHelper.Dimension.OVERWORLD) {
            if (overworldBedrock.size() > OVERWORLD_BEDROCK_SIZE) return;

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

    public static void clear() {
        netherBedrockFloor.clear();
        netherBedrockRoof.clear();
        overworldBedrock.clear();
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
