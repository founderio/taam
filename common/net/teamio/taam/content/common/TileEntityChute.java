package net.teamio.taam.content.common;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.ConveyorSlotsBase;
import net.teamio.taam.util.TaamUtil;

public class TileEntityChute extends BaseTileEntity implements IFluidHandler, IRotatable, ITickable {

	public boolean isConveyorVersion = false;
	private EnumFacing direction = EnumFacing.NORTH;
	private ConveyorSlotsBase conveyorSlots = new ConveyorSlotsBase() {
		
		@Override
		public int insertItemAt(ItemStack stack, int slot, boolean simulate) {
			IItemHandler target = getTargetItemHandler();

			ItemStack notAdded = ItemHandlerHelper.insertItemStacked(target, stack, simulate);
			int added = stack.stackSize;
			if(notAdded != null)
				added -= notAdded.stackSize;
			return added;
		}

		@Override
		public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
			return null;
		}
		
		@Override
		public double getInsertMaxY() {
			if(isConveyorVersion) {
				return 0.9;
			} else {
				return 1.3;
			}
		}

		@Override
		public double getInsertMinY() {
			if(isConveyorVersion) {
				return 0.3;
			} else {
				return 0.9;
			}
		}
	};

	public TileEntityChute(boolean isConveyorVersion) {
		this.isConveyorVersion = isConveyorVersion;
	}

	public TileEntityChute() {
		this(false);
	}

	@Override
	public String getName() {
		if (isConveyorVersion) {
			return "tile.taam.productionline.chute.name";
		} else {
			return "tile.taam.machines.chute.name";
		}
	}

	@Override
	public void update() {
		// Skip item insertion if there is a solid block / other chute above us
		if (isConveyorVersion || !worldObj.isSideSolid(pos.up(), EnumFacing.DOWN, false)) {
			ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isConveyorVersion", isConveyorVersion);
		if (isConveyorVersion) {
			tag.setInteger("direction", direction.ordinal());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		isConveyorVersion = tag.getBoolean("isConveyorVersion");
		if (isConveyorVersion) {
			direction = EnumFacing.getFront(tag.getInteger("direction"));
			if (direction.getAxis() == Axis.Y) {
				direction = EnumFacing.NORTH;
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) getTargetItemHandler();
		}
		return super.getCapability(capability, facing);
	}

	private TileEntity getTarget() {
		return worldObj.getTileEntity(pos.down());
	}
	
	private IItemHandler getDropItemHandler() {
		return new IItemHandler() {
			
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if(canDrop()) {
					if(!simulate && !worldObj.isRemote) {
						EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, stack);
						item.motionX = 0;
						item.motionY = 0;
						item.motionZ = 0;
						worldObj.spawnEntityInWorld(item);
					}
					return null;
				} else {
					return stack;
				}
			}
			
			@Override
			public ItemStack getStackInSlot(int slot) {
				return null;
			}
			
			@Override
			public int getSlots() {
				return 1;
			}
			
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return null;
			}
		};
	}

	private IItemHandler getTargetItemHandler() {
		TileEntity target = getTarget();
		if(target == null) {
			return getDropItemHandler();
		}
		IItemHandler handler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		if(handler == null) {
			return getDropItemHandler();
		} else {
			return handler;
		}
	}

	private IFluidHandler getTargetFluidHandler() {
		TileEntity target = getTarget();
		if(target instanceof IFluidHandler) {
			return (IFluidHandler) target;
		} else {
			return null;
		}
	}

	private boolean canDrop() {
		return TaamUtil.canDropIntoWorld(worldObj, pos.down());
	}

	/*
	 * IFluidHandler implementation
	 */

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from != EnumFacing.UP) {
			return 0;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.fill(from, resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(from != EnumFacing.UP) {
			return false;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.canFill(from, fluid);
		} else {
			return false;
		}
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(from != EnumFacing.UP) {
			return new FluidTankInfo[0];
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.getTankInfo(from);
		} else {
			return new FluidTankInfo[0];
		}
	}

	/*
	 * IRotatable Implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(isConveyorVersion) {
			this.direction = direction;
			if(!ArrayUtils.contains(EnumFacing.HORIZONTALS, direction)) {
				this.direction = EnumFacing.NORTH;
			}
			updateState(false, true, false);
		}
	}

}
