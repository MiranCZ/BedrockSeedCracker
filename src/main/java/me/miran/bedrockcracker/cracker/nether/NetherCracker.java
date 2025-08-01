package me.miran.bedrockcracker.cracker.nether;

import me.miran.bedrockcracker.BedrockCracker;

import java.util.List;

public class NetherCracker {


    private static boolean gpuAvailable;
    private static AbstractNetherCracker implementation;

    static {
        try {
            gpuAvailable = GPUNetherCracker.available();
        } catch (Throwable ignored) {
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
        // clear previous implementation if there was one
        if (implementation instanceof AutoCloseable closable) {
            try {
                closable.close();
            } catch (Exception e) {
                e.printStackTrace(); // TODO logger
            }
        }

        if (gpuAvailable && BedrockCracker.gpuAllowed()) {
            implementation = new GPUNetherCracker();
        } else {
            implementation = new DefaultNetherCracker();
        }
    }

}
