package net.teamio.taam.util;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.inv.InventoryUtils;

/**
 * Generic Utility Methods, used across multiple "themes".
 * @author oliver
 *
 */
public final class TaamUtil {
	private TaamUtil() {
		// Util class
	}

	public static boolean canDropIntoWorld(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isAir(state, world, pos) || block.getMaterial(state).isLiquid();
	}

	public static void breakBlockInWorld(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockInWorld(world, pos, blockState);
	}

	public static void breakBlockInWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		block.dropBlockAsItem(world, pos, blockState, 0);
		world.setBlockToAir(pos);
	}

	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockToInventory(player, world, pos, blockState);
	}

	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
		ItemStack toDrop = getItemStackFromWorld(world, pos, blockState);
		if(toDrop != null) {
			InventoryUtils.tryDropToInventory(player, toDrop, pos);
		}
		world.setBlockToAir(pos);
	}

	public static ItemStack getItemStackFromWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
        	return null;
        } else {
        	int damage = block.damageDropped(blockState);
        	return new ItemStack(block, 1, damage);
        }
	}

	public static boolean isShutdown(Random rand, int redstoneMode, boolean redstoneHigh) {
		boolean newShutdown = false;
		// Redstone. Other criteria?
		if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH && !redstoneHigh) {
			newShutdown = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW && redstoneHigh) {
			newShutdown = true;
		} else if(redstoneMode > 4 || redstoneMode < 0) {
			newShutdown = rand.nextBoolean();
		}
		return newShutdown;
	}

	/**
	 * Decides whether an attachable block can be placed somewhere.
	 * Checks for a solid side or a TileEntity implementing {@link IConveyorAwareTE}.
	 * @param world
	 * @param x The attachable block.
	 * @param y The attachable block.
	 * @param z The attachable block.
	 * @param dir The direction in which to check. Checks the block at the offset coordinates.
	 * @return
	 */
	public static boolean canAttach(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		if(world.isSideSolid(pos.offset(dir), dir.getOpposite(), false)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent instanceof IConveyorAwareTE;
	}

	/**
	 * Checks if actualInput is the same item as inputDefinition (respecting
	 * OreDictionary) or, if inputDefinition is null, if actualInput is
	 * registered with the ore dictionary matching inoutOreDictName.
	 *
	 * @param inputDefinition
	 * @param inputOreDictName
	 * @param actualInput
	 * @return
	 */
	public static boolean isInputMatching(ItemStack inputDefinition, String inputOreDictName, ItemStack actualInput) {
		if(actualInput == null) {
			return inputDefinition != null && inputOreDictName != null;
		} else {
			if(inputDefinition == null) {
				int[] oreIDs = OreDictionary.getOreIDs(actualInput);
				int myID = OreDictionary.getOreID(inputOreDictName);
				return ArrayUtils.contains(oreIDs, myID);
			} else {
				return inputDefinition.isItemEqual(actualInput) || OreDictionary.itemMatches(actualInput, inputDefinition, true);
			}
		}
	}


}
