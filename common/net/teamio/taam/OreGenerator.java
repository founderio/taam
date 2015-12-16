package net.teamio.taam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	public static class GenerationInfo {
		public WorldGenMinable gen;
		public int generateAbove;
		public int generateBelow;
		public int maxDepositCount;
		public GenerationInfo(WorldGenMinable gen, int generateAbove,
				int generateBelow, int maxDepositCount) {
			this.gen = gen;
			this.generateAbove = generateAbove;
			this.generateBelow = generateBelow;
			this.maxDepositCount = maxDepositCount;
		}
	}
	
	List<GenerationInfo> gens;
	
	public OreGenerator() {
		gens = new ArrayList<GenerationInfo>();
		// Copper Ore
		if(Config.genOre[0])
			gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.copper.ordinal(), Config.oreSize[0], Blocks.stone), Config.oreAbove[0], Config.oreBelow[0], Config.oreDepositCount[0]));
		// Tin Ore
		if(Config.genOre[1])
			gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.tin.ordinal(), Config.oreSize[1], Blocks.stone), Config.oreAbove[1], Config.oreBelow[1], Config.oreDepositCount[1]));
		// Native Aluminum
		if(Config.genOre[2])
			gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.aluminum.ordinal(), Config.oreSize[2], Blocks.stone), Config.oreAbove[2], Config.oreBelow[2], Config.oreDepositCount[2]));
		// Bauxite
		if(Config.genOre[3]){
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.bauxite.ordinal(), Config.oreSize[3], Blocks.stone), Config.oreAbove[3], Config.oreBelow[3], Config.oreDepositCount[3]));
//		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.bauxite.ordinal(), 35, Blocks.dirt), 0, 128, 5));
		}
		// Kaolinite
		if(Config.genOre[4]){
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.kaolinite.ordinal(), Config.oreSize[4], Blocks.stone), Config.oreAbove[4], Config.oreBelow[4], Config.oreDepositCount[4]));
//		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, Taam.BLOCK_ORE_META.kaolinite.ordinal(), 35, Blocks.dirt), 0, 128, 2));
		}
	}
		
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		default:
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {
	}

	private void generateNether(World world, Random random, int i, int j) {
	}

	private void generateSurface(World world, Random random, int i, int j) {
		for(GenerationInfo gen : gens) {
			for (int k = 0; k < gen.maxDepositCount; k++) {
				int firstBlockXCoord = i + random.nextInt(16);
				int firstBlockYCoord = gen.generateAbove + random.nextInt(gen.generateBelow - gen.generateAbove);
				int firstBlockZCoord = j + random.nextInt(16);
				gen.gen.generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
			}
		}
	}

}