package founderio.taam.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.techne.TechneModel;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import founderio.taam.Taam;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.TileEntitySensor;

public class TaamRenderer extends TileEntitySpecialRenderer implements
IItemRenderer {

	public final TechneModel modelSensor;
	public final ResourceLocation textureSensor;
	public final ResourceLocation textureSensorBlink;
//	private RenderItem ri;
//	private EntityItem ei;
	private float rot = 0;
	
	public TaamRenderer() {
//		ri = new RenderItem() {
//			@Override
//			public boolean shouldBob() {
//				return false;
//			}
//		};
//		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Item.pickaxeDiamond));
//		ri.setRenderManager(RenderManager.instance);
		
		modelSensor = new TechneModel(new ResourceLocation(Taam.MOD_ID + ":models/sensor.tcn"));
		textureSensor = new ResourceLocation(Taam.MOD_ID
				+ ":textures/models/sensor.png");
		textureSensorBlink = new ResourceLocation(Taam.MOD_ID
				+ ":textures/models/sensor_blink.png");
	}
	
	public void tickEvent(TickEvent event) {
		if(event.type == Type.CLIENT && event.phase == Phase.END) {
			rot++;
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return Block.getBlockFromItem(item.getItem()) instanceof BlockSensor;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float offX = 0;
		float offY = 0;
		float offZ = 0;
		
		switch (type) {
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			offX = -0.5f;
			offY = -0.5f;
			offZ = -0.5f;
			break;
		case INVENTORY:
			offX = 0.125f;
			offZ = 0.125f;
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			break;
		}
		
		renderAt(item.getItemDamage() | 7, offX, offY, offZ, false);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float partialTickTime) {
		TileEntitySensor te = ((TileEntitySensor) tileentity);
		renderAt(tileentity.getBlockMetadata(), x, y, z, te.isPowering() > 0);
	}
	
	public void renderAt(int meta, double x, double y, double z, boolean fixBlink) {
		switch(meta & 8) {
		case 0:
			renderSensor(x, y, z, (meta & 7), fixBlink);
			break;
		case 8:
			//TODO: renderMinect();
			break;
		}
	}
	
	public void renderSensor(double x, double y, double z, int rotation, boolean fixBlink) {
		GL11.glPushMatrix();


		
		GL11.glTranslatef((float) x + 0.5f, (float) y,
				(float) z + 0.5f);
		
		
		if((rot % 40) == 0 || fixBlink) {
			rot = 0;
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensorBlink);
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensor);
		}
		ForgeDirection dir = ForgeDirection.getOrientation(rotation).getOpposite();
//System.out.println(rotation);
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
		case UNKNOWN:
			break;
		}
		if(dir == ForgeDirection.UNKNOWN) {
			GL11.glScalef(0.125f, 0.125f, 0.125f);
			GL11.glRotatef(90f, 0, 1.0f, 0);
			// 1/16th scale, as techne tends to be big..
		} else {
			GL11.glScalef(0.0625f, 0.0625f, 0.0625f);
		}
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		modelSensor.renderPart("p1");
		modelSensor.renderPart("p2");
		modelSensor.renderPart("socket");
		
		if(dir != ForgeDirection.DOWN && dir != ForgeDirection.UP) {
			GL11.glRotatef(20f, 1.0f, 0, 0);
			GL11.glTranslatef(0f, 0f, 0.8f);
		}
		modelSensor.renderPart("device");

		GL11.glPopMatrix();
	}
}
