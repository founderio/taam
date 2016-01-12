package net.teamio.taam.conveyors;

import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public abstract class ApplianceInventory implements IConveyorAppliance {
	public InventorySimple inventory;
	public FluidTank[] fluidTanks;
	
	public ApplianceInventory(int inventorySlots, int... tankCapacities) {
		inventory = new InventorySimple(inventorySlots, "Appliance Inventory");
		if(tankCapacities != null) {
			fluidTanks = new FluidTank[tankCapacities.length];
			for(int i = 0; i < tankCapacities.length; i++) {
				fluidTanks[i] = new FluidTank(tankCapacities[i]);
			}
		} else {
			fluidTanks = new FluidTank[0];
		}
	}
	
	protected abstract int getTankForSide(EnumFacing from);
	
	/*
	 * IFluidHandler
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource,
			boolean doDrain) {
		if(resource == null) {
			return null;
		}
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return null;
		}
		if(!resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}
	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		int tankID = getTankForSide(from);
		if(tankID == -1 || tankID >= fluidTanks.length) {
			return new FluidTankInfo[0];
		} else {
			return new FluidTankInfo[] { fluidTanks[tankID].getInfo() };
		}
	}
	
	protected IFluidTank getActualTank(int tankID) {
		if(tankID == -1 || tankID >= fluidTanks.length) {
			return null;
		} else {
			return fluidTanks[tankID];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		/*
		 * Write Tanks
		 */
		if(fluidTanks != null && fluidTanks.length != 0) {
			NBTTagList tanks = new NBTTagList();
			for(int i = 0; i < fluidTanks.length; i++) {
				NBTTagCompound tank = new NBTTagCompound();
				fluidTanks[i].writeToNBT(tank);
				tanks.appendTag(tank);
			}
			tag.setTag("tanks", tanks);
		}
		/*
		 * Write Items
		 */
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		/*
		 * Read Tanks
		 */
		NBTTagList tanks = tag.getTagList("tanks", NBT.TAG_COMPOUND);
		if(tanks != null) {
			int max = Math.min(tanks.tagCount(), fluidTanks.length);
			for(int i = 0; i < max; i++) {
				fluidTanks[i].readFromNBT(tanks.getCompoundTagAt(i));
			}
		}
		/*
		 * Read Items
		 */
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
	}
	/*
	 * IInventory
	 */

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return inventory.getStackInSlot(p_70301_1_);
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return inventory.decrStackSize(p_70298_1_, p_70298_2_);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return inventory.getStackInSlotOnClosing(p_70304_1_);
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		inventory.setInventorySlotContents(p_70299_1_, p_70299_2_);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public void markDirty() {
		inventory.markDirty();
	}
	
	@Override
	public int getField(int id) {
		return 0;
	}
	
	@Override
	public void setField(int id, int value) {
	}
	
	@Override
	public int getFieldCount() {
		return 0;
	}
	
	@Override
	public void clear() {
		inventory.clear();
	}
	
}
