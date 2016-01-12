package net.teamio.taam.rendering;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.BlockMachines;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.ATileEntityAttachable;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAttachable;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class TaamRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

//	public final TechneModel modelSensor;
	public final ResourceLocation textureSensor;
	public final ResourceLocation textureSensorBlink;
	
	public final OBJModel modelConveyor;
	public final ResourceLocation textureConveyor;
	
//	public final WavefrontObject modelMachines;
	
	private RenderItem ri;
	private EntityItem ei;
	private float rot = 0;
	private float rot_sensor = 0;
	private double rotSin = 0;
	
	public TaamRenderer() {
//		ri = new RenderItem() {
//			@Override
//			public boolean shouldBob() {
//				return false;
//			}
//		};
		ri = Minecraft.getMinecraft().getRenderItem();
		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Items.apple));
		ei.rotationPitch = 0;
		ei.rotationYaw = 0;
		//ei.age = 0;
//		ri.setRenderManager(RenderManager.instance);
		
//		modelSensor = new TechneModel(new ResourceLocation(Taam.MOD_ID + ":models/sensor.tcn"));
		textureSensor = new ResourceLocation(Taam.MOD_ID + ":textures/models/sensor.png");
		textureSensorBlink = new ResourceLocation(Taam.MOD_ID + ":textures/models/sensor_blink.png");

		modelConveyor = null;//(OBJModel) OBJLoader.instance.loadModel(new ModelResourceLocation(Taam.MOD_ID + ":models/conveyor"));
		textureConveyor = new ResourceLocation(Taam.MOD_ID + ":textures/models/conveyor.png");

//		modelMachines = new CachingWavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/machines.obj"));

	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase == TickEvent.Phase.END) {
	    	rot++;
	    	rot_sensor++;
	    	if(rot_sensor > 360) {
	    		rot_sensor -= 360;
	    	}
			rotSin = Math.sin(Math.toRadians(rot*32));
	    }
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if(item.getItem() == TaamMain.itemConveyorAppliance) {
			return true;
		}
		Block block = Block.getBlockFromItem(item.getItem());
		if(block instanceof BlockSensor) {
			return true;
		} else if(block instanceof BlockProductionLine) {
			return true;
		} else if(block instanceof BlockProductionLineAttachable) {
			return true;
		} else if(block instanceof BlockMachines) {
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float x = 0;
		float y = 0;
		float z = 0;
		
		switch (type) {
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			x = -0.5f;
			y = -0.5f;
			z = -0.5f;
			break;
		case INVENTORY:
			x = 0.125f;
			z = 0.125f;
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			break;
		}
		
		if(item.getItem() == Item.getItemFromBlock(TaamMain.blockSensor)) {
			//TODO: Document meta components!
			int meta = item.getItemDamage() | 7;
			renderSensor(x, y, z, (meta & 7), false);
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockProductionLine)) {
			int meta = item.getItemDamage();
			switch(meta) {
			default:
			case 0:
			case 1:
			case 2:
				renderConveyor(null, x, y, z, meta);
				break;
			case 3:
				renderConveyorHopper(null, x, y, z, false);
				break;
			case 4:
				renderConveyorHopper(null, x, y, z, true);
				break;
			case 5:
				renderConveyorSieve(null, x, y, z);
				break;
			case 6:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Shredder);
				break;
			case 7:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Grinder);
				break;
			case 8:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Crusher);
				break;
			case 9:
				renderConveyorChute(null, x, y, z);
				break;
			}
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockProductionLineAttachable)) {
			int meta = item.getItemDamage() & 3;
			switch(meta) {
			default:
			case 0:
			case 1:
				renderItemBag(null, x, y, z, meta);
			}
		} else if(item.getItem() == TaamMain.itemConveyorAppliance) {
			int meta = item.getItemDamage();
			switch(meta) {
			default:
				break;
			case 0:
				GL11.glPushMatrix();
				/*
				 * Translate to coordinates
				 */
				GL11.glTranslated(x, y, z);
				
				Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);
				renderConveyorAppliance(Taam.APPLIANCE_SPRAYER);
				
				GL11.glPopMatrix();	
				break;
			case 1:
				//TODO: Render.
				break;
			}
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockMachines)) {
			int meta = item.getItemDamage();
			switch(meta) {
			case 0:
				renderChute(x, y, z);
				break;
			case 1:
				renderCreativeItemCache(x, y, z);
				break;
			}
		}
		
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tileEntity instanceof TileEntitySensor) {
			TileEntitySensor te = ((TileEntitySensor) tileEntity);
			int meta = tileEntity.getBlockMetadata();
			switch (tileEntity.getBlockMetadata() & 8) {
			case 0:
				renderSensor(x, y, z, (meta & 7), te.getRedstoneLevel() > 0);
				break;
			case 8:
				// TODO: renderMinect();
				break;
			}
		} else if (tileEntity instanceof TileEntityConveyor) {
			TileEntityConveyor tec = (TileEntityConveyor) tileEntity;
			renderConveyor(tec, x, y, z, tec.getSpeedLevel());
		} else if (tileEntity instanceof TileEntityConveyorHopper) {
			renderConveyorHopper((TileEntityConveyorHopper)tileEntity, x, y, z, false);
		} else if (tileEntity instanceof TileEntityConveyorProcessor) {
			renderConveyorProcessor((TileEntityConveyorProcessor) tileEntity, x, y, z, (byte)0);
		} else if (tileEntity instanceof TileEntityConveyorSieve) {
			renderConveyorSieve((TileEntityConveyorSieve) tileEntity, x, y, z);
		} else if(tileEntity instanceof TileEntityChute) {
			TileEntityChute teChute = (TileEntityChute) tileEntity;
			if(teChute.isConveyorVersion) {
				renderConveyorChute(teChute, x, y, z);
			} else {
				renderChute(x, y, z);
			}
		} else if(tileEntity instanceof TileEntityCreativeCache) {
			renderCreativeItemCache(x, y, z);
		} else if(tileEntity instanceof TileEntityConveyorItemBag || tileEntity instanceof TileEntityConveyorTrashCan) {
			int meta = tileEntity.getBlockMetadata();
			renderItemBag((ATileEntityAttachable)tileEntity, x, y, z, meta);
		}
		
		if(tileEntity instanceof IConveyorAwareTE) {
			renderConveyorItems((IConveyorAwareTE) tileEntity, x, y, z);
		}
	}

	private void renderChute(double x, double y, double z) {
		GL11.glPushMatrix();
		/*
		 * Translate to coordinates
		 */
		GL11.glTranslated(x, y, z);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);
		modelMachines.renderPart("Chute_chutemdl");
		
		GL11.glPopMatrix();
	}

	private void renderCreativeItemCache(double x, double y, double z) {
		GL11.glPushMatrix();
		/*
		 * Translate to coordinates
		 */
		GL11.glTranslated(x, y, z);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);
		modelMachines.renderPart("Creative_Cache_ccmdl");
		
		GL11.glPopMatrix();
	}
	
	private void conveyorPrepareRendering(IConveyorAwareTE tileEntity, double x, double y, double z, boolean isWood) {
		EnumFacing direction = conveyorGetDirection(tileEntity);
		
		GL11.glPushMatrix();
		/*
		 * Translate to coordinates
		 */
		GL11.glTranslated(x, y, z);
		
		/*
		 * Rotate if needed
		 */
		GL11.glTranslatef(0.5f, 0, 0.5f);
		
		if(direction == EnumFacing.WEST) {
			GL11.glRotatef(270, 0, 1, 0);
		} else if(direction == EnumFacing.NORTH) {
			GL11.glRotatef(180, 0, 1, 0);
		} else if(direction == EnumFacing.EAST) {
			GL11.glRotatef(90, 0, 1, 0);
		}

		GL11.glTranslated(-0.5, 0, -0.5);
		
		/*
		 * Bind Texture
		 */
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);

		/*
		 * Render Support Frame
		 */
		if(isWood) {
			modelConveyor.renderPart("Support_Wood_smdl_wood");
		} else {
			modelConveyor.renderPart("Support_Alu_smdl_alu");
		}
	}
	
	private void conveyorEndRendering() {
		GL11.glPopMatrix();
	}
	
	private EnumFacing conveyorGetDirection(IConveyorAwareTE tileEntity) {
		EnumFacing direction;
		if(tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}
	
	public void renderConveyorItems(IConveyorAwareTE tileEntity, double x, double y, double z) {
		EnumFacing direction = conveyorGetDirection(tileEntity);
		
		/*
		 * Rotate if needed
		 */
		float rotationDegrees = 0;
		if(direction == EnumFacing.WEST) {
			rotationDegrees = 270;
		} else if(direction == EnumFacing.NORTH) {
			rotationDegrees = 180;
		} else if(direction == EnumFacing.EAST) {
			rotationDegrees = 90;
		}
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		if(tileEntity != null) {

			if(tileEntity instanceof TileEntityConveyorProcessor) {
				TileEntityConveyorProcessor processor = (TileEntityConveyorProcessor) tileEntity;
				ItemStack processingStack = processor.getStackInSlot(0);
				if(processingStack != null) {
					GL11.glPushMatrix();
					Random rand = processor.getWorldObj().rand;
					GL11.glTranslatef(0.5f, -0.1f, 0.5f);
					
					/*
					 * Rotate if needed
					 */
					
					GL11.glRotatef(rotationDegrees, 0, 1, 0);

					if(!processor.isShutdown) {
						GL11.glTranslatef(
								0.015f * (1-rand.nextFloat()),
								0.025f * (1-rand.nextFloat()),
								0.015f * (1-rand.nextFloat()));
					}
					
					ei.setEntityItemStack(processingStack);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);
					RenderItem.renderInFrame = false;
					
					GL11.glPopMatrix();
				}
			}
			if(tileEntity.shouldRenderItemsDefault()) {
				float posY = 0.1f;
				if(tileEntity instanceof TileEntityConveyorSieve) {
					//TODO extract into separate method getItemRenderPosY() in IConveyorAwareTE
					if(((TileEntityConveyorSieve) tileEntity).isShutdown) {
						//posY = 0;
					} else {
						posY += (float)(rotSin*0.04);
					}
				}
				for(int slot = 0; slot < 9; slot++) {
					ItemWrapper wrapper = tileEntity.getSlot(slot);
					
					if(wrapper == null || wrapper.isEmpty()) {
						continue;
					}
					ItemStack itemStack = wrapper.itemStack;
					if(itemStack == null) {
						continue;
					}
					
					int movementProgress = tileEntity.getMovementProgress(slot);
					if(movementProgress < 0) {
						movementProgress = 0;
					}
					float speedsteps = tileEntity.getSpeedsteps();
					
					EnumFacing renderDirection = tileEntity.getNextSlot(slot);
					
					float posX = (float)ConveyorUtil.getItemPositionX(slot, movementProgress / speedsteps, renderDirection);
					
					
					float posZ = (float)ConveyorUtil.getItemPositionZ(slot, movementProgress / speedsteps, renderDirection);
					
					GL11.glPushMatrix();
					GL11.glTranslatef(posX, posY, posZ);
					
					//Don't rotate on the conveyors, as that makes transitions jumpy
					//GL11.glRotatef(rotationDegrees, 0, 1, 0);
					
					ei.setEntityItemStack(itemStack);
	
					// Used to be true, but blocks are too big that way..
					RenderItem.renderInFrame = false;
					ri.doRender(ei, 0, .5f, 0, 0, 0);
					//RenderItem.renderInFrame = false;
					
					GL11.glPopMatrix();
				}
			}
		}
		
		GL11.glPopMatrix();
	}
	
	public void renderConveyor(TileEntityConveyor tileEntity, double x, double y, double z, int meta) {
		
		boolean isWood;
		boolean isHighSpeed;
		
		boolean renderEnd = false;
		boolean renderBegin = false;
		boolean end = true;
		boolean begin = true;
		boolean renderAbove = false;
		boolean renderLeft = false;
		boolean renderRight = false;

		if(tileEntity == null) {
			isWood = meta == 0;
			isHighSpeed = meta == 2;
		} else {
			isWood = tileEntity.getSpeedLevel() == 0;
			isHighSpeed = tileEntity.getSpeedLevel() >= 2;

			end = tileEntity.isEnd;
			begin = tileEntity.isBegin;
			renderEnd = tileEntity.renderEnd;
			renderBegin = tileEntity.renderBegin;
			
			renderAbove = tileEntity.renderAbove;
			renderLeft = tileEntity.renderLeft;
			renderRight = tileEntity.renderRight;
		}
		
		conveyorPrepareRendering(tileEntity, x, y, z, isWood);
		
		if(!renderLeft && !renderRight) {
			if(isWood) {
				modelConveyor.renderPart("Conveyor_Direction_Marker_Wood_cdmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_Direction_Marker_Alu_cdmdl_alu");
			}
		}
		
		if(renderAbove) {
			if(isWood) {
				modelConveyor.renderPart("Support_Above_Wood_samdl_wood");
			} else {
				modelConveyor.renderPart("Support_Above_Alu_samdl_alu");
			}
		}
		
		if(end) {
			modelConveyor.renderPart("Conveyor_End_cemdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Framing_Wood_cemdl_wood");
				modelConveyor.renderPart("Conveyor_End_Walz_Wood_cwalzmdl_wood");
				modelConveyor.renderPart("Support_Caps_Wood_scmdl_wood");
				if(renderEnd)
					modelConveyor.renderPart("Conveyor_End_Cap_Wood_cecmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Framing_Alu_cemdl_alu");
				modelConveyor.renderPart("Conveyor_End_Walz_Alu_cwalzmdl_alu");
				modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
				if(renderEnd)
					modelConveyor.renderPart("Conveyor_End_Cap_Alu_cecmdl_alu");
			}
		} else {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Wood_csmdl_wood");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Wood_cwalzmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Alu_csmdl_alu");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Alu_cwalzmdl_alu");
			}
		}
		if(isHighSpeed) {
			modelConveyor.renderPart("Conveyor_High_Throughput_Framing_Alu_chtpmdl_alu");
		}
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);
		
		if(begin) {
			modelConveyor.renderPart("Conveyor_End_cemdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Framing_Wood_cemdl_wood");
				modelConveyor.renderPart("Conveyor_End_Walz_Wood_cwalzmdl_wood");
				modelConveyor.renderPart("Support_Caps_Wood_scmdl_wood");
				if(renderBegin)
					modelConveyor.renderPart("Conveyor_End_Cap_Wood_cecmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Framing_Alu_cemdl_alu");
				modelConveyor.renderPart("Conveyor_End_Walz_Alu_cwalzmdl_alu");
				modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
				if(renderBegin)
					modelConveyor.renderPart("Conveyor_End_Cap_Alu_cecmdl_alu");
			}
		} else {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Wood_csmdl_wood");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Wood_cwalzmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Alu_csmdl_alu");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Alu_cwalzmdl_alu");
			}
		}
		
		if(renderRight) {
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);

			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Cap_Wood_cecmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Cap_Alu_cecmdl_alu");
			}

			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);
		}
		if(renderLeft) {
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);

			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Cap_Wood_cecmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Cap_Alu_cecmdl_alu");
			}

			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);
		}
		
		if(tileEntity != null) {
			if(tileEntity.hasAppliance()) {
				renderConveyorAppliance(tileEntity.getApplianceType());
			}
		}
		
		GL11.glPopMatrix();
		
	}
	
	public void renderConveyorAppliance(String type) {
		if(Taam.APPLIANCE_SPRAYER.equals(type)) {
			modelConveyor.renderPart("Appliance_Sprayer_asmdl");
		}
	}
	
	public void renderConveyorHopper(TileEntityConveyorHopper tileEntity, double x, double y, double z, boolean forceHighSpeed) {
		boolean highSpeed = forceHighSpeed;
		if(tileEntity != null) {
			highSpeed = highSpeed || tileEntity.isHighSpeed();
		}
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		
		if(highSpeed) {
			modelConveyor.renderPart("Conveyor_Hopper_High_Speed_chmdl_hs");
		} else {
			modelConveyor.renderPart("Conveyor_Hopper_chmdl");
		}
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");

		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		conveyorEndRendering();
	}
	
	
	public void renderConveyorSieve(TileEntityConveyorSieve tileEntity, double x, double y, double z) {
		boolean spinning = true;
		if(tileEntity != null) {
			spinning = !tileEntity.isShutdown;
		}
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		modelConveyor.renderPart("Conveyor_Sieve_Chute_cscmdl");
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);
		
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");

		if(spinning) {
			double val = Math.sin(Math.toRadians(rot*32));
			GL11.glTranslated(0, 0.01 + val*0.04, 0);
		}
		
		modelConveyor.renderPart("Conveyor_Sieve_csvmdl");
		
		conveyorEndRendering();
	}
	
	public void renderConveyorProcessor(TileEntityConveyorProcessor tileEntity, double x, double y, double z, byte forceMode) {
		byte mode = forceMode;
		boolean spinning = true;
		if(tileEntity != null) {
			if(forceMode == 0) {
				mode = tileEntity.getMode();
			}
			spinning = !tileEntity.isShutdown;
		}
		
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		
		modelConveyor.renderPart("Conveyor_Processing_Chute_chutemdl");
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");

		renderConveyorProcessorWalz(mode, spinning);
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		renderConveyorProcessorWalz(mode, spinning);

		switch(mode) {
		case TileEntityConveyorProcessor.Crusher:
			modelConveyor.renderPart("Processor_Marker_Crusher_pmmdl_cru");
			break;
		case TileEntityConveyorProcessor.Grinder:
			modelConveyor.renderPart("Processor_Marker_Grinder_pmmdl_gri");
			break;
		case TileEntityConveyorProcessor.Shredder:
			modelConveyor.renderPart("Processor_Marker_Shredder_pmmdl_shr");
			break;
		}
		
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		conveyorEndRendering();
	}
	
	private void renderConveyorProcessorWalz(byte mode, boolean spinning) {
		GL11.glPushMatrix();
		if(spinning) {
			GL11.glTranslated(0.5, 0.25683, 0.61245);
			GL11.glRotatef(-rot*16, 1, 0, 0);
			GL11.glTranslated(-0.5, -0.25683, -0.61245);
		}
		modelConveyor.renderPart("Processor_Walzes");
		
		switch(mode) {
		case TileEntityConveyorProcessor.Crusher:
			modelConveyor.renderPart("BumpsCrusher");
			break;
		case TileEntityConveyorProcessor.Grinder:
			modelConveyor.renderPart("BumpsGrinder");
			break;
		case TileEntityConveyorProcessor.Shredder:
			modelConveyor.renderPart("BumpsShredder");
			break;
		}
		GL11.glPopMatrix();
	}
	
	private void renderConveyorChute(TileEntityChute tileEntity, double x, double y, double z) {
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		
		modelConveyor.renderPart("Conveyor_Chute_cchmdl");
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		conveyorEndRendering();
	}

	private void renderItemBag(ATileEntityAttachable tileEntity, double x, double y, double z, int meta) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		float fillPercent = 0;
		if(tileEntity instanceof TileEntityConveyorTrashCan) {
			fillPercent = ((TileEntityConveyorTrashCan) tileEntity).fillLevel;
			float maxFillPercent = Config.pl_trashcan_maxfill;
			fillPercent = fillPercent / maxFillPercent;
		} else if(tileEntity instanceof TileEntityConveyorItemBag) {
			fillPercent = ((TileEntityConveyorItemBag) tileEntity).fillPercent;
		}
		
		GL11.glTranslatef(0.5f, 0, 0.5f);
		
		int type = meta & 3;
		int rot = (meta & 12) >> 2;
		switch(rot) {
		default:
		case 0: // NORTH
			GL11.glRotatef(180, 0, 1, 0);
			break;
		case 1: // SOUTH
			break;
		case 2: // EAST
			GL11.glRotatef(90, 0, 1, 0);
			break;
		case 3: // WEST
			GL11.glRotatef(270, 0, 1, 0);
			break;
		}

		GL11.glTranslated(-0.5, 0, -0.5);
		/*
		 * Bind Texture
		 */
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);

		/*
		 * Render Support Frame
		 */
		switch(type) {
		default:
		case 0: // Bag
			modelConveyor.renderPart("Bag_bmdl");
			break;
		case 1: // TrashCan
			modelConveyor.renderPart("BagTrash_btmdl");
			break;
		}
		if(fillPercent > 0) {
			GL11.glTranslatef(0, fillPercent * 0.3f, 0);
			modelConveyor.renderPart("Bag_Filling_bfmdl");
		}
		
		GL11.glPopMatrix();
	}
	
	public void renderSensor(double x, double y, double z, int rotation, boolean fixBlink) {
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float) x + 0.5f, (float) y,
				(float) z + 0.5f);
		
		if((rot_sensor % 40) == 0 || fixBlink) {
			rot_sensor = 0;
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensorBlink);
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensor);
		}
		EnumFacing dir = EnumFacing.getOrientation(rotation).getOpposite();
		switch(dir) {
		case DOWN:
			break;
		case UP:
			GL11.glTranslatef(0f, 1f, 0f);
			GL11.glRotatef(180f, 1.0f, 0, 0);
			break;
		case NORTH:
			GL11.glTranslatef(0f, 0.5f, -0.5f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			break;
		case SOUTH:
			GL11.glTranslatef(0f, 0.5f, 0.5f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(180f, 0, 0, 1.0f);
			break;
		case WEST:
			GL11.glTranslatef(-0.5f, 0.5f, 0f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(-90f, 0, 0, 1.0f);
			break;
		case EAST:
			GL11.glTranslatef(0.5f, 0.5f, 0f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(90f, 0, 0, 1.0f);
			break;
		default:
			break;
		}
		// null means we are rendering for the inventory
		if(dir == null) {
			GL11.glScalef(0.125f, 0.125f, 0.125f);
			GL11.glRotatef(90f, 0, 1.0f, 0);
		} else {
			// 1/16th scale, as techne tends to be big..
			GL11.glScalef(0.0625f, 0.0625f, 0.0625f);
		}
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		modelSensor.renderPart("p1");
		modelSensor.renderPart("p2");
		modelSensor.renderPart("socket");
		
		if(dir != EnumFacing.DOWN && dir != EnumFacing.UP) {
			GL11.glRotatef(20f, 1.0f, 0, 0);
			GL11.glTranslatef(0f, 0f, 0.8f);
		}
		modelSensor.renderPart("device");

		GL11.glPopMatrix();
	}
}
