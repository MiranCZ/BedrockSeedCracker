package me.miran.bedrockcracker.cracker.nether;

import me.miran.bedrockcracker.util.BedrockCollector;
import me.miran.bedrockcracker.cracker.util.BedrockType;
import me.miran.bedrockcracker.cracker.util.CheckedRandom;
import me.miran.bedrockcracker.cracker.util.Helper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.*;

import static me.miran.bedrockcracker.cracker.util.CheckedRandom.*;

abstract class AbstractNetherCracker {


    public List<Long> crack() {
        List<Test> tests = loadTests();

        List<Test> mainTests = new ArrayList<>(tests);

        mainTests.removeIf((test) -> test.y < 64);
        mainTests = mainTests.subList(0,20);


        Test[] testArr = mainTests.toArray(new Test[0]);


        List<Long> results = getSeedCandidates(testArr);

        List<Long> structureSeeds = new ArrayList<>();

        for (long l : results) {
            structureSeeds.addAll(Helper.bedrockSeedToStructureSeed(l,"minecraft:bedrock_roof" ));
        }

        structureSeeds.removeIf((l) -> {
            for (Test test : tests) {
                long s = Helper.structureSeedToBedrockSeed(l, BedrockType.getFromY(test.y).name);
                CheckedRandom.RandomSplitter splitter = new CheckedRandom.RandomSplitter(s);

                if (!Helper.performCheck(test.x,test.y,test.z, splitter)) return true;
            }

            return false;
        });

        return structureSeeds;
    }

    protected abstract List<Long> getSeedCandidates(Test[] testArr);


    protected void runChecks(Test[] tests,long upperBits, int unknownBits, List<Long> resultList) {
        long lowerBitMask = (1L << unknownBits) - 1;
        long sub = lowerBitMask * MULTIPLY;
        long hashMask = MASK ^ lowerBitMask;

        for (Test test : tests) {
            if ((((upperBits ^ test.hashXor & hashMask) * MULTIPLY + test.offset) & MASK) < test.add - sub) return;
        }

        if (unknownBits == 0) {
            resultList.add(upperBits);
            return;
        }

        unknownBits--;

        long split = 1L << unknownBits;

        runChecks(tests,upperBits, unknownBits, resultList);
        runChecks(tests,upperBits | split, unknownBits, resultList);
    }


    protected long[] getBounds(int y) {
        double lowerBound = 0;
        double upperBound = 1;

        if (y > 5) {
            y -= 122;
            lowerBound = (5-y) /5d;
        } else  {
            upperBound = (5-y)/5d;
        }

        lowerBound *= MASK;
        upperBound *= MASK;

        return new long[]{(long) lowerBound, (long) upperBound};
    }

    protected List<Test> loadTests() {
        List<BlockPos> netherBedrock = new ArrayList<>();
        netherBedrock.addAll(BedrockCollector.getNetherFloorBedrock());
        netherBedrock.addAll(BedrockCollector.getNetherRoofBedrock());

        List<Test> tests = new ArrayList<>();

        for (BlockPos pos : netherBedrock) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            int lowerThan = Helper.getDesiredIntFromYHeight(y);

            tests.add(new Test(MathHelper.hashCode(x, y, z), x, y, z, lowerThan, getBounds(y)[0], getBounds(y)[1],
                    MathHelper.hashCode(x, y, z) ^ MULTIPLY, MASK - getBounds(y)[1], MASK - getBounds(y)[1] + getBounds(y)[0]));
        }

        return tests;
    }


    protected record Test(long hash,int x, int y, int z, int lowerThan, long lowerBound, long upperBound, long hashXor, long offset, long add){
    }


}
