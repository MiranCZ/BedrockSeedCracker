package me.miran.bedrockcracker.cracker.nether;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

class DefaultNetherCracker extends AbstractNetherCracker{

    @Override
    protected @NotNull List<Long> getSeedCandidates(Test[] testArr) {
        List<Long> results = new ArrayList<>();
        int threadCount = (int) (Runtime.getRuntime().availableProcessors()*0.75);
        threadCount = Math.max(1, threadCount);


        long limit = 1L<<36;
        long chunkSize = limit / threadCount;

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            long start = t * chunkSize;
            long end;

            if (t == threadCount - 1) { // Last thread handles remaining work
                end = limit;
            } else {
                end = (t + 1) * chunkSize;
            }

            new Thread(() -> {
                for (long i = start; i < end; i++) {
                    runChecks(testArr, i << 12, 12, results);
                }
                latch.countDown();
            }).start();

        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return results;
    }


}
