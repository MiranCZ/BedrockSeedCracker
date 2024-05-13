package me.miran.bedrockcracker.cracker;

import me.miran.bedrockcracker.util.BedrockCollector;
import me.miran.bedrockcracker.cracker.util.BedrockType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;

import java.util.List;

public class OverworldCracker {


    // brute forces the top bits of the world seed from the structure seed
    // it is mimicking the bedrock pattern spawning in overworld and checking against previously cached blocks
    public static void addOverworldSeedToList(long structureSeed, List<Long> resultList) {
        List<BlockPos> list = BedrockCollector.getOverworldBedrock();
        BedrockType bedrockType = BedrockType.FLOOR_OVERWORLD;

        seedLoop:
        for (long i = 0; i < (1<<16); i++) {
            long seed = i<<48 | structureSeed;

            Xoroshiro128PlusPlusRandom xoroshiro128PlusPlusRandom = new Xoroshiro128PlusPlusRandom(seed);
            RandomSplitter splitter = xoroshiro128PlusPlusRandom.nextSplitter();

            RandomSplitter bedrockSplitter = splitter.split(bedrockType.name).nextSplitter();

            for (BlockPos pos : list) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                double d = MathHelper.map(y, bedrockType.startY, bedrockType.endY, 1.0, 0.0);
                Random random = bedrockSplitter.split(x, y, z);

                if (((double)random.nextFloat()) >= d) continue seedLoop;
            }

            resultList.add(seed);
        }
    }

}
