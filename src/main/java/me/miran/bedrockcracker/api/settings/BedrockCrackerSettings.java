package me.miran.bedrockcracker.api.settings;

public class BedrockCrackerSettings {


    /**
     * how should the cracking process be started (either by command, or automatically)
     */
    public CrackStartType crackStartType = CrackStartType.COMMAND;

    /**
     * should the mod send chat messages
     */
    public Boolean logProgress = true;

    /**
     * whether gpu should be used for cracking if possible
     */
    public Boolean allowGpuUse = true;

}