#define MULTIPLY 25214903917L
#define MASK 281474976710655L

__kernel void crack(__global const long* lowBitsPointer, __global const long* tests, __global long* resultsAll, __global int* resultsIndex) {
    int id = get_global_id(0);

    const long lowBits = lowBitsPointer[0];

    long upperBitsQueue[13];
    int unknownBitsQueue[13];

    int queueIndex = 0;

    upperBitsQueue[0] = ((((long)id) << 6) | lowBits) << 12;
    unknownBitsQueue[0] = 12;

    while (queueIndex >= 0) {
        long upperBits = upperBitsQueue[queueIndex];
        int unknownBits = unknownBitsQueue[queueIndex];

        long lowerBitMask = (1L << unknownBits) - 1;
        long sub = lowerBitMask * MULTIPLY;
        long hashMask = MASK ^ lowerBitMask;

        queueIndex--;

        long compare = 225179981368524L - sub;

        // ultimate loop unrolling in action
        if ((((upperBits ^ (tests[0] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[1] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[2] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[3] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[4] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[5] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[6] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[7] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[8] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[9] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[10] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[11] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[12] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[13] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[14] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[15] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[16] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[17] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[18] & hashMask)) * MULTIPLY) & MASK) < compare) continue;
        if ((((upperBits ^ (tests[19] & hashMask)) * MULTIPLY) & MASK) < compare) continue;

        if (unknownBits == 0) {
            resultsAll[atomic_inc(resultsIndex)] = upperBits;
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
