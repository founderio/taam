package net.teamio.taam;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.Taam.BLOCK_ORE_META;
import net.teamio.taam.conveyors.api.ChanceBasedRecipe;
import net.teamio.taam.conveyors.api.ChancedOutput;
import net.teamio.taam.conveyors.api.ProcessingRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		
		/*
		 * Crusher
		 */
		
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].dust) {
				continue;
			}
			if(values[meta].ore) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new ChanceBasedRecipe(new ItemStack(TaamMain.blockOre, 1, meta),
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, meta), 1.0f),
							new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
							new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
							));
			}
			if(values[meta].ingot) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new ChanceBasedRecipe(new ItemStack(TaamMain.itemIngot, 1, meta),
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, meta), 1.0f)
							));
			}
		}
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.stone),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.gravel), 0.15f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.cobblestone),
						new ChancedOutput(new ItemStack(Blocks.gravel), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.15f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.gravel),
						new ChancedOutput(new ItemStack(Blocks.sand), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.05f)
				)
		);
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.gold_block),
						new ChancedOutput(new ItemStack(Items.gold_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.iron_block),
						new ChancedOutput(new ItemStack(Items.iron_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.diamond_block),
						new ChancedOutput(new ItemStack(Items.diamond, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.emerald_block),
						new ChancedOutput(new ItemStack(Items.emerald, 9), 1.0f)
				)
		);
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.quartz_block),
						new ChancedOutput(new ItemStack(Items.quartz, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.quartz, 1), 0.25f)
				)
		);
		
		/*
		 * Grinder
		 */

		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER,
				new ChanceBasedRecipe(new ItemStack(Blocks.grass),
						new ChancedOutput(new ItemStack(Blocks.dirt), 1.0f),
						new ChancedOutput(new ItemStack(Items.wheat_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.pumpkin_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.melon_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Blocks.vine), 0.005f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new ChanceBasedRecipe(new ItemStack(Blocks.leaves), 
					new ChancedOutput(new ItemStack(Items.stick), 1.0f),
					new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.1f )
				));
		
		
	}
	public static void addSmeltingRecipes(){
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].ingot || !values[meta].ore) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
		// Resin -> Rubber Bar
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.resin.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()), 1);
	
	}

	public static void addOreRecipes(){
		
//		motion sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', "ingotIron",
				'R', Items.redstone));
//		sprayer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemConveyorAppliance, 1, 0),
				"NFN", "N N", "TCT",
				'N', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.nozzle.ordinal()),
				'C', "partBasicCircuit",
				'T', new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.pump.ordinal()),
				'F', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal())));
		
//		conveyor1 (Wood)
		//TODO recipe for conveyor1 (Wood)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal()),
				"rRP", "wSw", "WsW",
				'P', new ItemStack(Blocks.piston, 1, 0),
				'R', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				'w', new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				'S', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal()),
				'W', "plankWood",
				's', "stickWood",
				'r', "dustRedstone"
				));
//		conveyor2 (Aluminum)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				"MRM", "CSC", "ABA",
				'M', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				'R', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'S', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal()),
				'A', "ingotAluminum",
				'B', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal())));
//		conveyor3 (High Througput)
		//TODO recipe for conveyor1 (Wood)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor3.ordinal()),
				"YBY", "F F", "MCM",
				'M', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				'F', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				'Y', "dyeYellow",
				'B', "dyeBlack"
		));
//		conveyor hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper.ordinal()),
				Blocks.hopper, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal())));

		
//		chute
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockChute, 1, 0), 
				"InI","InI","nIn",
				'I', "ingotIron"));

//		wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "II*", "**I",
				'I', "ingotIron"));
		
//		photo cell
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemPart, 9, Taam.ITEM_PART_META.photocell.ordinal()),
				Blocks.daylight_detector));
//		magnetic coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				"CCC", "CIC", "CCC",
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.copper_wire.ordinal()),
				'I', "ingotIron"));
//		metal bearing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()), 
				"INI", "N*N", "INI",
				'I', "ingotIron",
				'N', "nuggetIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()), 
				"NIN", "I*I", "NIN",
				'I', "ingotIron",
				'N', "nuggetIron"));
//		support frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.support_frame.ordinal()),
				"*A*", "A*A", "AAA",
				'A', "ingotAluminum"));
//		support frame wood
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.support_frame_wood.ordinal()),
				"*S*", "S*S", "WWW",
				'W', "plankWood",
				'S', "stickWood"));
//		copper wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 12, Taam.ITEM_PART_META.copper_wire.ordinal()), 
				"CCC",
				'C', "ingotCopper"));
		
//		logistics chip
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.logistics_chip.ordinal()),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal())));
//		basic circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CCC", "RGR", "CCC",
				'C', "ingotCopper",
				'G', Items.gold_ingot,
				'R', Items.redstone));
//		advanced circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"RGR", "GCG", "RGR",
				'R', Items.redstone,
				'G', Items.gold_ingot,
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal())));
//		iron frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				"III", "I I", "I I",
				'I', "ingotIron"));
		
//		rubber band
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				"RRR", "R R", "RRR",
				'R', "materialRubber"));
//		tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal()),
				" I ", "I I", "III",
				'I', "ingotIron"));
//		nozzle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.nozzle.ordinal()),
				" I ", "I I", " I ",
				'I', "ingotIron"));
//		iron nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()),
				"ingotIron"));
//		wooden board
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"www", "s s",
				'w', "plankWood",
				's', "stickWood"));
//		aluminum plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminum"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminium"));
//		Conveyor Item Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.itembag.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeBlue",
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.iron_frame.ordinal())
				));
//		Conveyor Trash Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeGreen",
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.iron_frame.ordinal())
				));
//		Pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal()),
				"AAA","PMP", "AAA",
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'P', Blocks.piston,
				'A', "ingotAluminum"
				));
//		Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				"ACA", "CIC", "ACA",
				'C', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				'I', Items.iron_ingot,
				'A', "ingotAluminum"
				));
//		Shreader
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"PAP","MTB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'T', new ItemStack(TaamMain.blockProductionLineAttachable,1,Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"WAW","MCB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockChute,1),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));

//		Higt Speed Conveyor Hopper
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal()),
				"C C"," H ", " I ",
				'C', new ItemStack(TaamMain.blockProductionLine,1,Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				'H', Blocks.hopper,
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
//		Grinder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"PAP","MCB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockChute,1,0),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
	}

}
