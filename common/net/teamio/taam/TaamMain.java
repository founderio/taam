package net.teamio.taam;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.content.ItemWithMetadata;
import net.teamio.taam.content.common.BlockMachines;
import net.teamio.taam.content.common.BlockOre;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.BlockSlidingDoor;
import net.teamio.taam.content.common.FluidDye;
import net.teamio.taam.content.common.ItemDebugTool;
import net.teamio.taam.content.common.ItemDust;
import net.teamio.taam.content.common.ItemIngot;
import net.teamio.taam.content.common.ItemWrench;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAttachable;
import net.teamio.taam.content.conveyors.ItemAttachable;
import net.teamio.taam.content.conveyors.ItemConveyorAppliance;
import net.teamio.taam.content.conveyors.ItemProductionLine;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ApplianceRegistry;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.gui.GuiHandler;


@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION, guiFactory = Taam.GUI_FACTORY_CLASS)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;

	@SidedProxy(clientSide = "net.teamio.taam.TaamClientProxy", serverSide = "net.teamio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;

	public static SimpleNetworkWrapper network;
	
	public static ItemDebugTool itemDebugTool;
	public static ItemWrench itemWrench;
	public static ItemWithMetadata itemTool;
	public static ItemWithMetadata itemMaterial;
	public static ItemWithMetadata itemPart;
	public static ItemIngot itemIngot;
	public static ItemDust itemDust;
	public static ItemConveyorAppliance itemConveyorAppliance;
	
	public static CreativeTabs creativeTab;

	public static BlockSensor blockSensor;
	public static BlockMachines blockMachines;
	public static BlockProductionLine blockProductionLine;
	public static BlockProductionLineAttachable blockProductionLineAttachable;
	public static BlockSlidingDoor blockSlidingDoor;
	public static BlockOre blockOre;
	
	public static FluidDye[] fluidsDye;
	
	public static DamageSource ds_processed = new DamageSource("taam.processed").setDamageBypassesArmor();
	public static DamageSource ds_shredded = new DamageSource("taam.shredded").setDamageBypassesArmor();
	public static DamageSource ds_ground = new DamageSource("taam.ground").setDamageBypassesArmor();
	public static DamageSource ds_crushed = new DamageSource("taam.crushed").setDamageBypassesArmor();
	public static DamageSource ds_reconfigured = new DamageSource("taam.reconfigured").setDamageIsAbsolute();
	
	public TaamMain() {
        DepLoader.load();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add(Taam.MOD_AUTHOR1);
		meta.authorList.add(Taam.MOD_AUTHOR2);
		meta.description = Taam.MOD_DESCRIPTION;
		meta.logoFile = Taam.MOD_LOGO_PATH;
		meta.autogenerated = false;
		
		MinecraftForge.EVENT_BUS.register(new TaamCraftingHandler());
		FMLCommonHandler.instance().bus().register(new Config());
		
		Config.init(event.getSuggestedConfigurationFile());
		creativeTab = new CreativeTabs(Taam.MOD_ID) {

			@Override
			@SideOnly(Side.CLIENT)
			public ItemStack getIconItemStack() {
				return new ItemStack(blockSensor);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {
				return null;
			}
		};

		blockSensor = new BlockSensor();
		blockSensor.setBlockName(Taam.BLOCK_SENSOR);
		blockSensor.setCreativeTab(creativeTab);

		blockMachines = new BlockMachines();
		blockMachines.setBlockName(Taam.BLOCK_MACHINES);
		blockMachines.setCreativeTab(creativeTab);
		
		blockProductionLine = new BlockProductionLine();
		blockProductionLine.setBlockName(Taam.BLOCK_PRODUCTIONLINE);
		blockProductionLine.setCreativeTab(creativeTab);
		
		blockProductionLineAttachable = new BlockProductionLineAttachable();
		blockProductionLineAttachable.setBlockName(Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE);
		blockProductionLineAttachable.setCreativeTab(creativeTab);
		
		blockSlidingDoor = new BlockSlidingDoor();
		blockSlidingDoor.setBlockName(Taam.BLOCK_SLIDINGDOOR);
		blockSlidingDoor.setCreativeTab(creativeTab);
		
		blockOre = new BlockOre();
		blockOre.setBlockName(Taam.BLOCK_ORE);
		blockOre.setCreativeTab(creativeTab);
		
		itemDebugTool = new ItemDebugTool();
		itemDebugTool.setUnlocalizedName(Taam.ITEM_DEBUG_TOOL);
		itemDebugTool.setCreativeTab(creativeTab);
		
		itemWrench = new ItemWrench();
		itemWrench.setUnlocalizedName(Taam.ITEM_WRENCH);
		itemWrench.setCreativeTab(creativeTab);
		
		itemTool = new ItemWithMetadata("tool", Taam.ITEM_TOOL_META.values());
		itemTool.setUnlocalizedName(Taam.ITEM_TOOL);
		itemTool.setCreativeTab(creativeTab);
		
		itemMaterial = new ItemWithMetadata("material", Taam.ITEM_MATERIAL_META.values());
		itemMaterial.setUnlocalizedName(Taam.ITEM_MATERIAL);
		itemMaterial.setCreativeTab(creativeTab);
		
		itemPart = new ItemWithMetadata("part", Taam.ITEM_PART_META.values());
		itemPart.setUnlocalizedName(Taam.ITEM_PART);
		itemPart.setCreativeTab(creativeTab);
		
		itemIngot = new ItemIngot("ingot", Taam.BLOCK_ORE_META.values());
		itemIngot.setUnlocalizedName(Taam.ITEM_INGOT);
		itemIngot.setCreativeTab(creativeTab);
		
		itemDust = new ItemDust("dust",Taam.BLOCK_ORE_META.values());
		itemDust.setUnlocalizedName(Taam.ITEM_DUST);
		itemDust.setCreativeTab(creativeTab);
		
		itemConveyorAppliance = new ItemConveyorAppliance();
		itemConveyorAppliance.setUnlocalizedName(Taam.ITEM_CONVEYOR_APPLIANCE);
		itemConveyorAppliance.setCreativeTab(creativeTab);
		
		GameRegistry.registerItem(itemMaterial, Taam.ITEM_MATERIAL, Taam.MOD_ID);
		GameRegistry.registerItem(itemPart, Taam.ITEM_PART, Taam.MOD_ID);
		GameRegistry.registerItem(itemIngot, Taam.ITEM_INGOT, Taam.MOD_ID);
		GameRegistry.registerItem(itemDust, Taam.ITEM_DUST,Taam.MOD_ID);
		GameRegistry.registerItem(itemTool, Taam.ITEM_TOOL,Taam.MOD_ID);

		GameRegistry.registerItem(itemDebugTool, Taam.ITEM_DEBUG_TOOL, Taam.MOD_ID);
		GameRegistry.registerItem(itemWrench, Taam.ITEM_WRENCH, Taam.MOD_ID);

		GameRegistry.registerItem(itemConveyorAppliance, Taam.ITEM_CONVEYOR_APPLIANCE, Taam.MOD_ID);
				
		GameRegistry.registerBlock(blockSensor, ItemBlock.class, Taam.BLOCK_SENSOR);
		
		GameRegistry.registerBlock(blockMachines, null, Taam.BLOCK_MACHINES);
		GameRegistry.registerBlock(blockProductionLine, null, Taam.BLOCK_PRODUCTIONLINE);
		GameRegistry.registerBlock(blockProductionLineAttachable, null, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE);
		GameRegistry.registerBlock(blockOre, null, Taam.BLOCK_ORE);

		GameRegistry.registerItem(new ItemMultiTexture(blockMachines, blockMachines, Taam.BLOCK_MACHINES_META.valuesAsString()), Taam.BLOCK_MACHINES, Taam.MOD_ID);
		GameRegistry.registerItem(new ItemProductionLine(blockProductionLine, blockProductionLine, Taam.BLOCK_PRODUCTIONLINE_META.valuesAsString()), Taam.BLOCK_PRODUCTIONLINE, Taam.MOD_ID);
		GameRegistry.registerItem(new ItemAttachable(blockProductionLineAttachable, blockProductionLineAttachable, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.valuesAsString()), Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE, Taam.MOD_ID);
//		GameRegistry.registerBlock(blockSlidingDoor, ItemBlock.class, Taam.BLOCK_SLIDINGDOOR);
		GameRegistry.registerItem(new ItemMultiTexture(blockOre, blockOre, Taam.BLOCK_ORE_META.valuesAsString()), Taam.BLOCK_ORE, Taam.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		GameRegistry.registerTileEntity(TileEntityChute.class, Taam.TILEENTITY_CHUTE);
		GameRegistry.registerTileEntity(TileEntityCreativeCache.class, Taam.TILEENTITY_CREATIVECACHE);
		GameRegistry.registerTileEntity(TileEntityConveyor.class, Taam.TILEENTITY_CONVEYOR);
		GameRegistry.registerTileEntity(TileEntityConveyorHopper.class, Taam.TILEENTITY_CONVEYOR_HOPPER);
		GameRegistry.registerTileEntity(TileEntityConveyorProcessor.class, Taam.TILEENTITY_CONVEYOR_PROCESSOR);
		GameRegistry.registerTileEntity(TileEntityConveyorItemBag.class, Taam.TILEENTITY_CONVEYOR_ITEMBAG);
		GameRegistry.registerTileEntity(TileEntityConveyorTrashCan.class, Taam.TILEENTITY_CONVEYOR_TRASHCAN);
		GameRegistry.registerTileEntity(TileEntityConveyorSieve.class, Taam.TILEENTITY_CONVEYOR_SIEVE);
//		GameRegistry.registerTileEntity(TileEntitySlidingDoor.class, Taam.TILEENTITY_SLIDINGDOOR);
		
		GameRegistry.registerWorldGenerator(new OreGenerator(), 2);
		
		ApplianceRegistry.registerFactory(Taam.APPLIANCE_SPRAYER, new ApplianceSprayer.Factory());
		
		Enum<?>[] fluidsDyeValues = Taam.FLUID_DYE_META.values();
		fluidsDye = new FluidDye[fluidsDyeValues.length];
		for(int i = 0; i < fluidsDyeValues.length; i++) {
			fluidsDye[i] = new FluidDye(Taam.FLUID_DYE + fluidsDyeValues[i].name());
			FluidRegistry.registerFluid(fluidsDye[i]);
		}
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Taam.CHANNEL_NAME);
		proxy.registerPackets(network);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		proxy.registerRenderStuff();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		oreRegistration();
		TaamRecipes.addRecipes();
		TaamRecipes.addSmeltingRecipes();
		TaamRecipes.addOreRecipes();
				
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public static void oreRegistration(){
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("oreAluminum", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));
		
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("ingotAluminum", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));
		
		OreDictionary.registerOre("nuggetIron", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()));
		
		OreDictionary.registerOre("materialPlastic", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()));
		OreDictionary.registerOre("materialRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("itemRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("materialGraphite", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.graphite.ordinal()));
		OreDictionary.registerOre("materialSiliconWafer", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.silicon_wafer.ordinal()));
		
		OreDictionary.registerOre("partPhotocell", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.photocell.ordinal()));
		OreDictionary.registerOre("partMotor", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()));
		OreDictionary.registerOre("partBasicCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()));
		OreDictionary.registerOre("partAdvancedCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()));
	}
}

