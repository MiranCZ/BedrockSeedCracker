package me.miran.bedrockcracker.api;

import me.miran.bedrockcracker.api.settings.BedrockCrackerSettings;

public interface BedrockCrackerController {


    /**
     * execute exactly once, has the chance to change settings of the BedrockCracker mod on its startup
     */
    default void setup(BedrockCrackerSettings settings) {
    }

    /**
     * called when the world seed is determined
     */
    void seedCrackedEvent(long worldSeed);

    /**
     * in case there is multiple mods using it, you can specify the priority for its execution
     * (bigger priority = executed later -> can overwrite previous changes)
     */
    default int getPriority() {
        return 100;
    }


}
