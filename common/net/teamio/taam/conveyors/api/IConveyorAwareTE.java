package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.conveyors.ItemWrapper;

public interface IConveyorAwareTE {
	
	boolean canSlotMove(int slot);
	boolean isSlotAvailable(int slot);
	int getMovementProgress(int slot);
	byte getSpeedsteps();
	
	BlockPos getPos();
	
	/**
	 * 
	 * @param item
	 * @param slot
	 * @return The actual amount of items added
	 */
	int insertItemAt(ItemStack item, int slot);
	EnumFacing getMovementDirection();
	ItemWrapper getSlot(int slot);
	EnumFacing getNextSlot(int slot);
	/**
	 * Used to skip default item rendering on select machines,
	 * e.g. the processors.
	 * @return
	 */
	boolean shouldRenderItemsDefault();
	/**
	 * The minimum relative Y coord for items to be inserted
	 * @return
	 */
	double getInsertMaxY();
	/**
	 * The maximum relative Y coord for items to be inserted
	 * @return
	 */
	double getInsertMinY();
	
}
