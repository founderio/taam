package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.piping.IPipe;

/**
 * Debug Tool, currently used for debugging conveyors.
 *
 * @author founderio
 *
 */
public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {
		list.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.debugtool", new Object[0]));
		if (GuiScreen.isShiftKeyDown()) {
			String usage = I18n.format("lore.taam.debugtool.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				list.add(split[i]);
			}
		} else {
			list.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!Config.debug)
		{
			//TODO: Clarify!
			//if(!world.isRemote) {
			SoundEvent soundevent = SoundEvent.REGISTRY.getObject(new ResourceLocation("taam", "sip_ah"));
			worldIn.playSound(playerIn, pos, soundevent, SoundCategory.BLOCKS, 1f, 1f);
			//}
			return EnumActionResult.SUCCESS;
		}
		char remoteState = worldIn.isRemote ? 'C' : 'S';

		IBlockState state = worldIn.getBlockState(pos);

		String text = String.format(remoteState + " RS: %b Side: %s Weak: %d Strong: %d",
				state.canProvidePower(), facing.toString(), state.getWeakPower(worldIn, pos, facing), state.getStrongPower(worldIn, pos, facing));

		playerIn.addChatMessage(new TextComponentString(text));

		EnumFacing oppSide = facing.getOpposite();

		text = String.format(remoteState + " RS: %b Opposite Side: %s Weak: %d Strong: %d",
				state.canProvidePower(), oppSide.toString(), state.getWeakPower(worldIn, pos, oppSide), state.getStrongPower(worldIn, pos, oppSide));


		text = String.format(remoteState + " Indirectly Powered: %d",
				worldIn.isBlockIndirectlyGettingPowered(pos));

		playerIn.addChatMessage(new TextComponentString(text));

		boolean didSomething = false;

		TileEntity te = worldIn.getTileEntity(pos);


		if(te instanceof TileEntityConveyor) {
			didSomething = true;
			TileEntityConveyor tec = (TileEntityConveyor) te;
			tec.updateContainingBlockInfo();

			text = String.format(remoteState + " Conveyor facing %s. isEnd: %b isBegin: %b",
					tec.getFacingDirection().toString(), tec.isEnd, tec.isBegin);

			playerIn.addChatMessage(new TextComponentString(text));

		}

		if(te instanceof IConveyorApplianceHost) {
			//IConveyorApplianceHost host = (IConveyorApplianceHost)te;
		}

		if(te instanceof IPipe) {

			IPipe pipe = (IPipe)te;

			String content = "[";
			FluidStack[] fs = pipe.getFluids();
			for(FluidStack fluidContent : fs) {
				content += fluidContent.getLocalizedName() + " " + fluidContent.amount + ", ";
			}
			content += "]";

			text = String.format(remoteState + " %s Pipe pressure: %d suction: %d effective: %d Content: %s",
					pipe.getClass().getName(), pipe.getPressure(), pipe.getSuction(), pipe.getPressure() - pipe.getSuction(), content);

			playerIn.addChatMessage(new TextComponentString(text));
		}

		if(te instanceof IFluidHandler) {

			IFluidHandler fh = (IFluidHandler)te;

			FluidTankInfo[] ti = fh.getTankInfo(EnumFacing.UP);
			String content = "";
			if(ti.length > 0) {
				if(ti[0].fluid == null) {
					content = "Nothing 0/" + ti[0].capacity;
				} else {
					content = ti[0].fluid.getLocalizedName() + " " + ti[0].fluid.amount + "/" + ti[0].capacity;
				}
			}

			text = String.format(remoteState + " Content: %s",
					content);

			playerIn.addChatMessage(new TextComponentString(text));
		}
		if(didSomething) {
			return EnumActionResult.SUCCESS;
		} else if(!didSomething) {
			return EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

}
