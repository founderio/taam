package founderio.taam;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.BlockSlidingDoor;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.blocks.multinet.ItemMultinetCable;
import founderio.taam.blocks.multinet.MultinetHandler;
import founderio.taam.blocks.multinet.MultinetMultipart;

@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;

	@SidedProxy(clientSide = "founderio.taam.TaamClientProxy", serverSide = "founderio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;

	public static MultinetMultipart multinetMultipart;
	
	public static ItemMultinetCable itemMultinetCable;
	
	public static CreativeTabs creativeTab;

	public static BlockSensor blockSensor;
	
	public static BlockSlidingDoor blockSlidingDoor;
	
	private Configuration config;
	
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
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		Property spm = config.get(Configuration.CATEGORY_GENERAL, "sensor_placement_mode", 1, Taam.CFG_COMMENT_SENSOR_PLACEMENT_MODE);
		sensor_placement_mode = spm.getInt();

		if(sensor_placement_mode < 1 || sensor_placement_mode > 2) {
			sensor_placement_mode = 1;
			spm.set(sensor_placement_mode);
		}
		
		Property sd = config.get(Configuration.CATEGORY_GENERAL, "sensor_delay", 30, Taam.CFG_COMMENT_SENSOR_DELAY);
		sensor_delay = sd.getInt();

		if(sensor_delay < 10) {
			sensor_delay = 10;
			sd.set(sensor_delay);
		}
		
		creativeTab = new CreativeTabs(Taam.MOD_ID) {

			@Override
			public ItemStack getIconItemStack() {
				return new ItemStack(blockSensor);
			}
		};

		blockSensor = new BlockSensor(config.getBlock(Taam.BLOCK_SENSOR, 3030).getInt());
		blockSensor.setUnlocalizedName(Taam.BLOCK_SENSOR);
		blockSensor.setCreativeTab(creativeTab);
		
		blockSlidingDoor = new BlockSlidingDoor(config.getBlock(Taam.BLOCK_SLIDINGDOOR, 3031).getInt());
		blockSlidingDoor.setUnlocalizedName(Taam.BLOCK_SLIDINGDOOR);
		blockSlidingDoor.setCreativeTab(creativeTab);

		multinetMultipart = new MultinetMultipart();
		
		itemMultinetCable = new ItemMultinetCable(config.getItem(Taam.ITEM_MULTINET_CABLE, 3032).getInt());
		itemMultinetCable.setUnlocalizedName(Taam.ITEM_MULTINET_CABLE);
		itemMultinetCable.setCreativeTab(creativeTab);
		
		config.save();

		GameRegistry.registerBlock(blockSensor, ItemBlock.class, Taam.BLOCK_SENSOR, Taam.MOD_ID);
		//GameRegistry.registerBlock(blockSlidingDoor, ItemBlock.class, Taam.BLOCK_SLIDINGDOOR, Taam.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		//GameRegistry.registerTileEntity(TileEntitySlidingDoor.class, Taam.TILEENTITY_SLIDINGDOOR);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderStuff();
		MinecraftForge.EVENT_BUS.register(new MultinetHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}
