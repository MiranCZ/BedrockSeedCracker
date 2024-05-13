package me.miran.bedrockcracker.cracker.util;

import net.minecraft.util.math.MathHelper;


/**
 * copy of Minecrafts CheckedRandom class (which is just the default javas Random class with some thread safety features I removed).
 * The only difference is that RandomSplitter has its seed exposed
 */
public class CheckedRandom {

    public static final long MULTIPLY = 25214903917L;
    public static final long ADD = 11L;
    public static final long MASK = 281474976710655L;

    private long seed;

    public CheckedRandom(long seed) {
        this.setSeed(seed);
    }


    public void setSeed(long seed) {
        this.seed = (seed ^ MULTIPLY) & MASK;
    }

    public RandomSplitter nextSplitter() {
        return new RandomSplitter(this.nextLong());
    }


    public int next(int bits) {
        long m = seed * MULTIPLY + ADD & MASK;
        seed = m;

        return (int)(m >> 48 - bits);
    }
    
    public long nextLong() {
        int i = this.next(32);
        int j = this.next(32);
        long l = (long)i << 32;
        return l + (long)j;
    }

    public static class RandomSplitter {
        public final long seed;

        public RandomSplitter(long seed) {
            this.seed = seed;
        }

        public CheckedRandom split(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new CheckedRandom(m);
        }


        public CheckedRandom split(String seed) {
            int i = seed.hashCode();

            return new CheckedRandom((long)i ^ this.seed);
        }
    }


}
