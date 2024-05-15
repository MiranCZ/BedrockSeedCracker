This is basically a java port of [MisterX's "NetherBedrockCracker"](https://github.com/19MisterX98/Nether_Bedrock_Cracker) written in rust.
It has the addition of also being able to crack the world seed (even if the world's seed is set manually).


A relatively small fabric mod for finding Minecraft seed using bedrock patterns...

If you find any bugs I would be happy if you report them in the [issues](https://github.com/MiranCZ/BedrockSeedCracker/issues)

# How to use
You need to visit both the overworld and the nether to collect bedrock patterns there. The mod will tell you in chat once you collect enough bedrock.
It's not some crazy amount (512 pieces for the overworld and 128 floor and roof each for the nether), so even small render distances should work.
Then you just execute the `/crackseed` command and wait.

### How long does it take?
The time really depends on your machine. The program is more or less going through almost all structure seeds, so it can range from like 30 seconds to a couple of minutes.

# How does it work?
All bedrock patterns are in versions 1.18+ dependent on the world seed in some capacity.
Even though the world seed is a 64-bit number, the nether only uses 48 bottom bits of it along with using much more simple random number generator that is easier to crack.

So the mod first cracks the "structure seed" (48 bottom bits of the world seed) from the nether bedrock roof and floor.
Then it goes through all 2^16 combinations for the 16 bottom bits and checks it against the overworld bedrock hopefully resulting in getting the world seed.

