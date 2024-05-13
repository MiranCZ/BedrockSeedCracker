package me.miran.bedrockcracker.cracker.util;


import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

import static me.miran.bedrockcracker.cracker.util.CheckedRandom.MASK;

public class Helper {

    public static boolean performCheck(int x, int y, int z, CheckedRandom.RandomSplitter splitter) {
        BedrockType type;
        try {
            type = BedrockType.getFromY(y);
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        return performCheck(x,y,z,splitter,type);
    }

    public static boolean performCheck(int x, int y, int z, CheckedRandom.RandomSplitter splitter, BedrockType bedrockType) {
        int desiredInt = getDesiredIntFromYHeight(y);

        int value = splitter.split(x, y, z).next(24);

        if (bedrockType.invert)  {
            return value >= desiredInt;
        }

        return value < desiredInt;
    }


    public static int getDesiredIntFromYHeight(int y) {
        BedrockType bedrockType = BedrockType.getFromY(y);

        if (bedrockType.invert) {
            return (int) ((1- MathHelper.map(y,bedrockType.endY-1,bedrockType.startY-1,1,0))*(1<<24));
        }

        return (int) (MathHelper.map(y,bedrockType.startY,bedrockType.endY,1,0)*(1<<24));
    }


    // TODO do this without creating new objects
    public static long structureSeedToBedrockSeed(long structureSeed, String ruleName) {
        checkValidBedrockRule(ruleName);

        CheckedRandom.RandomSplitter random = new CheckedRandom(structureSeed).nextSplitter();
        CheckedRandom rand = random.split(ruleName);
        CheckedRandom.RandomSplitter bedrockRand = rand.nextSplitter();

        return bedrockRand.seed;
    }

    public static List<Long> bedrockSeedToStructureSeed(long bedrockSeed, String ruleName) {
        checkValidBedrockRule(ruleName);

        bedrockSeed &= MASK;

        List<Long> result = new ArrayList<>();

        for (long l : getSeedFromMaskedNextLong(bedrockSeed)) {
            l ^= ruleName.hashCode();

            result.addAll(getSeedFromMaskedNextLong(l));
        }

        return result;
    }

    private static void checkValidBedrockRule(String ruleName) {
        if (!ruleName.equals("minecraft:bedrock_roof") && !ruleName.equals("minecraft:bedrock_floor")) {
            throw new IllegalArgumentException("not a valid rule! {" + ruleName + "}");
        }
    }

    // logic used from: https://docs.rs/next_long_reverser/0.1.0/src/next_long_reverser/lib.rs.html#36-73
    public static List<Long> getSeedFromMaskedNextLong(long structureSeed) {
        List<Long> res = new ArrayList<>(2);
        structureSeed = structureSeed & 0xffff_ffff_ffffL;
        long lowerBits = (structureSeed & 0xffff_ffffL);
        long upperBits = ((structureSeed) >> 32);

        // Did the lower bits affect the upper bits
        if ((lowerBits & 0x8000_0000L) != 0) {
            upperBits += 1; // restoring the initial value of the upper bits
        }

        // The algorithm is meant to have bits_of_danger = 0, but this runs into overflow issues.
        // By using a different small value, we introduce small numerical error which probably cannot break things
        // while keeping everything in range of a long and avoiding nasty BigDecimal/BigInteger overhead
        int bitsOfDanger = 1;

        long lowMin = (lowerBits << 16 - bitsOfDanger);
        long lowMax = (((lowerBits + 1) << 16 - bitsOfDanger) - 1);
        long upperMin = (((upperBits << 16) - 107048004364969L) >> bitsOfDanger);

        // hardcoded matrix multiplication again
        long m1lv = floorDiv(lowMax * -33441L + upperMin * 17549L, 1L << 31 - bitsOfDanger) + 1; // I cancelled out a common factor of 2 in this line
        long m2lv = floorDiv(lowMin * 46603L + upperMin * 39761L, 1L << 32 - bitsOfDanger) + 1;

        // with a lot more effort you can make these loops check 2 things and not 4 but I'm not sure it would even be much faster
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                long seed = (-39761L * (m1lv + i)) + 35098L * (m2lv + j);
                if (((46603L * (m1lv + i)) + 66882L * (m2lv + j) + 107048004364969L) >> 16 == upperBits) {
                    if ((seed) >> 16 == lowerBits) {
                        res.add(((seed * 254681119335897L + 120305458776662L) & 0xffff_ffff_ffffL) ^ CheckedRandom.MULTIPLY); // pull back 2 LCG calls
                    }
                }
            }
        }

        return res;
    }

    private static long floorDiv(long a, long b) {
        return (a - (a < 0 ? b - 1 : 0)) / b;
    }
}
