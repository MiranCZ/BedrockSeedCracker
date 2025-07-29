package me.miran.bedrockcracker.cracker.nether;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.miran.bedrockcracker.cracker.util.CheckedRandom.*;

class GPUNetherCracker extends AbstractNetherCracker {


    static boolean available() {
        return KernelManager.instance().bestDevice().getType() == Device.TYPE.GPU;
    }

    @Override
    protected @NotNull List<Long> getSeedCandidates(Test[] testArr) {
        //FIXME 16384 is an arbitrary size
        long[] resultsAll = new long[16384];
        int[] resultIndex = new int[]{0};

        final long[][] tests = new long[20][1];
        for (int j = 0; j < testArr.length; j++) {
            Test test = testArr[j];
            tests[j] = new long[]{test.hashXor()};
        }

        int limit = 1 << 30;

        CrackerKernel kernel = new CrackerKernel(tests, resultsAll, resultIndex);
        Range range = Range.create(limit);

        int loopLimit = 1<<6;
        for (int i = 0; i < loopLimit; i++) {

            kernel.setLowBits(i);
            kernel.execute(range);

            System.out.println(i + "/"+ loopLimit);
        }
        kernel.dispose();


        System.out.println(Arrays.toString(resultIndex));
        System.out.println("----------------");

        List<Long> results = new ArrayList<>();
        for (int i = 0; i < resultIndex[0]; i++) {
            results.add(resultsAll[i]);
        }
        return results;
    }


    private static class CrackerKernel extends Kernel {
        private long lowBits = 0;
        private final long[][] tests;
        private final long[] resultsAll;
        private final int[] resultsIndex;

        public CrackerKernel(long[][] tests, long[] resultsAll, int[] resultsIndex) {
            this.tests = tests;
            this.resultsAll = resultsAll;
            this.resultsIndex = resultsIndex;
        }

        public void setLowBits(long value) {
            this.lowBits = value;
        }


        /**
         * <b>Warning:</b> This method assumes we are using bedrock floor and only the highest Y level of it.
         */
        @Override
        public void run() {
            int id = getGlobalId();

            long[] upperBitsQueue = new long[13];
            byte[] unknownBitsQueue = new byte[13];

            int queueIndex = 0;

            upperBitsQueue[0] = ((((long) id) << 6) | lowBits) << 12;
            unknownBitsQueue[0] = 12;

            while (queueIndex >= 0) {
                final long upperBits = upperBitsQueue[queueIndex];
                byte unknownBits = unknownBitsQueue[queueIndex];

                final long lowerBitMask = (1L << unknownBits) - 1;
                final long sub = lowerBitMask * MULTIPLY;
                final long hashMask = MASK ^ lowerBitMask;

                queueIndex--;

                long compare = 225179981368524L - sub;
                // ultimate loop unrolling in action
                if ((((upperBits ^ tests[0][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[1][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[2][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[3][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[4][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[5][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[6][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[7][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[8][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[9][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[10][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[11][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[12][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[13][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[14][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[15][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[16][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[17][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[18][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;
                if ((((upperBits ^ tests[19][0] & hashMask) * MULTIPLY) & MASK) < compare) continue;


                if (unknownBits == 0) {
                    resultsAll[resultsIndex[0]++] = upperBits;
                    continue;
                }

                unknownBits--;
                long split = 1L << unknownBits;

                queueIndex++;
                upperBitsQueue[queueIndex] = upperBits;
                unknownBitsQueue[queueIndex] = unknownBits;

                queueIndex++;
                upperBitsQueue[queueIndex] = upperBits | split;
                unknownBitsQueue[queueIndex] = unknownBits;
            }
        }
    }


}
