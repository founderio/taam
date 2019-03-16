package net.teamio.taam.machines;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;

import java.util.Collections;
import java.util.List;

public class MachineItemBlock extends ItemBlock {

	public MachineItemBlock(Block block) {
		super(block);
		setHasSubtypes(true);
	}


	@Override
	public int getMetadata(int damage) {
		return MathHelper.clamp(damage, 0, Taam.MACHINE_META.values().length - 1);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int meta = stack.getMetadata();
		String[] ttip = MachineTileEntity.getInfo(meta).getTooltip();
		if (ttip != null) {
			Collections.addAll(tooltip, ttip);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = MachineTileEntity.getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> items) {
		Taam.MACHINE_META[] values = Taam.MACHINE_META.values();
		for (int i = 0; i < values.length; i++) {
			items.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
	                            float hitX, float hitY, float hitZ, IBlockState newState) {

		boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if (!success) return false;

		MachineTileEntity te = (MachineTileEntity) world.getTileEntity(pos);

		if (!(te.machine instanceof IRotatable)) {
			return true;
		}

		((IRotatable) te.machine).setFacingDirection(MachineTileEntity.getDirection(player, pos));

		return true;
	}
}
