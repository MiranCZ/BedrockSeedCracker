package me.miran.bedrockcracker.cracker.nether;

import java.util.List;

public class NetherCracker {


    private static boolean gpuAvailable;
    private static AbstractNetherCracker implementation;

    static {
        try {
            gpuAvailable = GPUNetherCracker.available();
        } catch (NoClassDefFoundError ignored) {
            gpuAvailable = false;
        }
        createImplementation();
    }


    public static List<Long> crack() {
        return implementation.crack();
    }

    public static boolean fallback() {
        if (gpuAvailable) {
            gpuAvailable = false;
            createImplementation();
            return true;
        }
        return false;
    }

    private static void createImplementation() {
        if (gpuAvailable) {
            implementation = new GPUNetherCracker();
        } else {
            implementation = new DefaultNetherCracker();
        }
    }

}
