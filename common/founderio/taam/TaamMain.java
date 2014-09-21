package founderio.taam;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.blocks.BlockOre;
import founderio.taam.blocks.BlockProductionLine;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.BlockSlidingDoor;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.blocks.multinet.ItemMultinetCable;
import founderio.taam.blocks.multinet.ItemMultinetMultitronix;
import founderio.taam.blocks.multinet.MultinetHandler;
import founderio.taam.blocks.multinet.MultinetPartFactory;
import founderio.taam.blocks.multinet.cables.OperatorRedstone;
import founderio.taam.items.ItemConveyorAppliance;
import founderio.taam.items.ItemDebugTool;
import founderio.taam.items.ItemIngot;
import founderio.taam.items.ItemPhotoCell;
import founderio.taam.items.ItemPlastic;
import founderio.taam.multinet.Multinet;


@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION, dependencies = "required-after:ForgeMultipart", guiFactory = Taam.GUI_FACTORY_CLASS)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;

	@SidedProxy(clientSide = "founderio.taam.TaamClientProxy", serverSide = "founderio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;

	public static MultinetPartFactory multinetMultipart;
	
	public static ItemMultinetCable itemMultinetCable;
	public static ItemMultinetMultitronix itemMultinetMultitronix;
	public static ItemDebugTool itemMultinetDebugger;
	public static ItemPhotoCell itemPhotoCell;
	public static ItemPlastic itemPlastic;
	public static ItemIngot itemIngot;
	public static ItemConveyorAppliance itemConveyorAppliance;
	
	public static CreativeTabs creativeTab;

	public static BlockSensor blockSensor;
	public static BlockProductionLine blockProductionLine;
	public static BlockSlidingDoor blockSlidingDoor;
	public static BlockOre blockOre;
	
	public static int sensor_placement_mode = 1;
	public static int sensor_delay = 30;
	
	

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add(Taam.MOD_AUTHOR1);
		meta.authorList.add(Taam.MOD_AUTHOR2);
		meta.description = Taam.MOD_DESCRIPTION;
		meta.logoFile = Taam.MOD_LOGO_PATH;
		meta.autogenerated = false;
		
		MinecraftForge.EVENT_BUS.register(new MultinetHandler());
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
				// TODO Auto-generated method stub
				return null;
			}
		};

		blockSensor = new BlockSensor();
		blockSensor.setBlockName(Taam.BLOCK_SENSOR);
		blockSensor.setCreativeTab(creativeTab);

		blockProductionLine = new BlockProductionLine();
		blockProductionLine.setBlockName(Taam.BLOCK_PRODUCTIONLINE);
		blockProductionLine.setCreativeTab(creativeTab);
		
		blockSlidingDoor = new BlockSlidingDoor();
		blockSlidingDoor.setBlockName(Taam.BLOCK_SLIDINGDOOR);
		blockSlidingDoor.setCreativeTab(creativeTab);
		
		blockOre = new BlockOre();
		blockOre.setBlockName(Taam.BLOCK_ORE);
		blockOre.setCreativeTab(creativeTab);
		
		itemMultinetCable = new ItemMultinetCable();
		itemMultinetCable.setUnlocalizedName(Taam.ITEM_MULTINET_CABLE);
		itemMultinetCable.setCreativeTab(creativeTab);
		
		itemMultinetDebugger = new ItemDebugTool();
		itemMultinetDebugger.setUnlocalizedName(Taam.ITEM_MULTINET_DEBUGGER);
		itemMultinetDebugger.setCreativeTab(creativeTab);
		
		itemMultinetMultitronix = new ItemMultinetMultitronix();
		itemMultinetMultitronix.setUnlocalizedName(Taam.ITEM_MULTINET_MULTITRONIX);
		itemMultinetMultitronix.setCreativeTab(creativeTab);
		
		itemPhotoCell = new ItemPhotoCell();
		itemPhotoCell.setUnlocalizedName(Taam.ITEM_PHOTOCELL);
		itemPhotoCell.setCreativeTab(creativeTab);
		
		itemPlastic = new ItemPlastic();
		itemPlastic.setUnlocalizedName(Taam.ITEM_PLASTIC);
		itemPlastic.setCreativeTab(creativeTab);
		
		itemIngot = new ItemIngot();
		itemIngot.setUnlocalizedName(Taam.ITEM_INGOT);
		itemIngot.setCreativeTab(creativeTab);
		
		itemConveyorAppliance = new ItemConveyorAppliance();
		itemConveyorAppliance.setUnlocalizedName(Taam.ITEM_CONVEYOR_APPLIANCE);
		itemConveyorAppliance.setCreativeTab(creativeTab);
		
		
		Multinet.registerOperator(new OperatorRedstone("redstone"));

		GameRegistry.registerItem(itemPhotoCell, Taam.ITEM_PHOTOCELL, Taam.MOD_ID);
		GameRegistry.registerItem(itemPlastic, Taam.ITEM_PLASTIC, Taam.MOD_ID);
		
		GameRegistry.registerItem(itemMultinetCable, Taam.ITEM_MULTINET_CABLE, Taam.MOD_ID);
		GameRegistry.registerItem(itemMultinetDebugger, Taam.ITEM_MULTINET_DEBUGGER, Taam.MOD_ID);
		GameRegistry.registerItem(itemMultinetMultitronix, Taam.ITEM_MULTINET_MULTITRONIX, Taam.MOD_ID);
		GameRegistry.registerItem(itemIngot, Taam.ITEM_INGOT, Taam.MOD_ID);
		GameRegistry.registerItem(itemConveyorAppliance, Taam.ITEM_CONVEYOR_APPLIANCE, Taam.MOD_ID);
		
		GameRegistry.registerBlock(blockSensor, ItemBlock.class, Taam.BLOCK_SENSOR);
		GameRegistry.registerBlock(blockProductionLine, null, Taam.BLOCK_PRODUCTIONLINE);
		GameRegistry.registerItem(new ItemMultiTexture(blockProductionLine, blockProductionLine, Taam.BLOCK_CONVEYOR_META), Taam.BLOCK_PRODUCTIONLINE, Taam.MOD_ID);
//		GameRegistry.registerBlock(blockSlidingDoor, ItemBlock.class, Taam.BLOCK_SLIDINGDOOR);
		GameRegistry.registerBlock(blockOre, null, Taam.BLOCK_ORE);
		GameRegistry.registerItem(new ItemMultiTexture(blockOre, blockOre, Taam.BLOCK_ORE_META), Taam.BLOCK_ORE, Taam.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		GameRegistry.registerTileEntity(TileEntityConveyor.class, Taam.TILEENTITY_CONVEYOR);
		GameRegistry.registerTileEntity(TileEntityConveyorHopper.class, Taam.TILEENTITY_CONVEYOR_HOPPER);
//		GameRegistry.registerTileEntity(TileEntitySlidingDoor.class, Taam.TILEENTITY_SLIDINGDOOR);
		
		GameRegistry.registerWorldGenerator(new OreGenerator(), 2);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		multinetMultipart = new MultinetPartFactory();
		proxy.registerRenderStuff();


		oreRegistration();
		TaamRecipes.addRecipes();
		TaamRecipes.addSmeltingRecipes();
		TaamRecipes.addOreRecipes();
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
	public static void oreRegistration(){
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOre, 1, 0));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOre, 1, 1));
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemIngot, 1, 0));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemIngot, 1, 1));
		OreDictionary.registerOre("materialPlastic", new ItemStack(itemPlastic));
	}
}

