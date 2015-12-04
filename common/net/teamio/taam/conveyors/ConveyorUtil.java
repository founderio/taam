package net.teamio.taam.conveyors;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.TaamUtil;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;

public class ConveyorUtil {
	
	private static boolean tryInsert(TileEntity tileEntity, EntityItem ei) {
		ItemStack entityItemStack = ei.getEntityItem();
		if(entityItemStack == null || entityItemStack.getItem() == null) {
			return false;
		}
		
		int previousStackSize = entityItemStack.stackSize;
		int added = 0;
		
		double relativeX = ei.posX - tileEntity.xCoord;
		double relativeY = ei.posY - tileEntity.yCoord;
		double relativeZ = ei.posZ - tileEntity.zCoord;
		
		if(tileEntity instanceof IConveyorAwareTE) {
			IConveyorAwareTE conveyorTE = (IConveyorAwareTE) tileEntity;
			
			int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

			if(slot >= 0 && slot < 9 && relativeY > conveyorTE.getInsertMinY() && relativeY < conveyorTE.getInsertMaxY()) {
				added = conveyorTE.insertItemAt(entityItemStack, slot);
			}
		} else if(tileEntity instanceof IInventory) {
			if(
				relativeX >= 0 && relativeX < 1 &&
				relativeY >= 0.9 && relativeY < 1.2 &&
				relativeZ >= 0 && relativeZ < 1
				) {
				IInventory inventory = (IInventory)tileEntity;
				InventoryRange range = new InventoryRange(inventory, ForgeDirection.UP.ordinal());
				added = previousStackSize - InventoryUtils.insertItem(range, entityItemStack, false);
			}
		}
		if(added == previousStackSize) {
			ei.setDead();
			return true;
		} else if(added > 0) {
			entityItemStack.stackSize = previousStackSize - added;
			ei.setEntityItemStack(entityItemStack);
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Tries to insert item entities from the world into an entity.
	 * Respects the conveyor system.
	 * 
	 * @param tileEntity
	 * @param world
	 * @param bounds
	 *            Optionally give an AABB Instance to speed up the search &
	 *            extend to unloaded chunks. Else only loaded entities are
	 *            respected.
	 * @param stopAtFirstMatch
	 *            Stop processing items after the first one was added?
	 *            
	 */
	public static boolean tryInsertItemsFromWorld(
			TileEntity tileEntity,
			World world,
			AxisAlignedBB bounds,
			boolean stopAtFirstMatch) {
		if(world.isRemote) {
			return false;
		}
		boolean didAdd = false;
		List<?> entities = world.loadedEntityList;
		
		//if Bounding Box is Supplied, use that.
		if(bounds != null) {
			entities = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		}
		for(int i = 0; i < entities.size(); i++) {
			Entity ent = (Entity)entities.get(i);
			
			if(ent instanceof EntityItem) {
				EntityItem ei = (EntityItem)ent;
				
				didAdd = tryInsert(tileEntity, ei) | didAdd;
				
				if(stopAtFirstMatch && didAdd) {
					break;
				}
			}
		}
		return didAdd;
	}
	
	public static int getNextSlot(int slot, ForgeDirection dir) {
		slot = getNextSlotUnwrapped(slot, dir);
		if(slot < 0) {
			slot += 9;
		} else if(slot > 8) {
			slot -= 9;
		}
		return slot;
	}

	public static int getNextSlotUnwrapped(int slot, ForgeDirection dir) {
		// X-Offset skips whole rows
		if(dir.offsetX != 0) {
			slot += dir.offsetX * 3;
		}
		// Z-Offset translates only regular,
		// but certain ones skip to the next row
		if(dir.offsetZ != 0) {
			int col = slot % 3;
			col += dir.offsetZ;
			if(col < 0) {
				slot -= 7;
			} else if(col > 2) {
				slot += 7;
			} else {
				slot += dir.offsetZ;
			}
		}
		return slot;
	}
	
	public static ForgeDirection getHighspeedTransition(int slot,
			ForgeDirection direction) {
		ForgeDirection transition = direction;
		switch(direction) {
		case NORTH:
			transition = highSpeedTransition[0][slot];
			break;
		case EAST:
			transition = highSpeedTransition[1][slot];
			break;
		case SOUTH:
			transition = highSpeedTransition[2][slot];
			break;
		case WEST:
			transition = highSpeedTransition[3][slot];
			break;
		default:
			transition = direction;
			break;
		}
		return transition;
	}
	
	public static int getSlot(ForgeDirection dir) {
		if(dir == ForgeDirection.DOWN || dir == ForgeDirection.UNKNOWN) {
			// Conveyors are only accessible from top/sides!
			return -1;
		} else if(dir == ForgeDirection.UP) {
			// Center
			return 4;
		} else {
			// From that center, we go one off
			return getNextSlot(4, dir);
		}
	}

	public static final double oneThird = 1/3.0;
	
	public static double getItemPositionX(int slot) {
		double x = Math.floor(slot / 3) + 0.5;
		return x * oneThird;
	}
	
	public static double getItemPositionX(int slot, double progress, ForgeDirection dir) {
		double x = getItemPositionX(slot);
		x += dir.offsetX * progress * oneThird;
		return x;
	}

	public static double getItemPositionZ(int slot) {
		double z = (slot % 3) + 0.5;
		return z * oneThird;
	}
	
	public static double getItemPositionZ(int slot, double progress, ForgeDirection dir) {
		double z = getItemPositionZ(slot);
		z += dir.offsetZ * progress * oneThird;
		return z;
	}
	
	public static int getSlotForRelativeCoordinates(double x, double z) {
		if(x > 1 || x < 0 || z > 1 || z < 0) {
			return -1;
		}
		int row = (int)Math.floor(x * 3f);
		int col = (int)Math.floor(z * 3f);
		return row * 3 + col;
	}
	
	private static final int[][] slotOrders;
	private static final ForgeDirection[][] highSpeedTransition;
	static {
		slotOrders = new int[2][];
		slotOrders[0] = new int[] {
				//North -Z to +Z (Processes line by line)
				//West -X to +X (Processes each line in "parallel")
				0, 1, 2,
				3, 4, 5,
				6, 7, 8
		};
		slotOrders[1] = new int[] {
				//South +Z to -Z (Processes line by line)
				//East +X to -X (Processes each line in "parallel")
				6, 7, 8,
				3, 4, 5,
				0, 1, 2
		};
		
		/*
		 * Mind Map:
		 *        NORTH
		 *      0	3	6
		 * WEST 1	4	7  EAST
		 *      2	5	8
		 *        SOUTH
		 */
		highSpeedTransition = new ForgeDirection[4][];
		highSpeedTransition[0] = new ForgeDirection[] {
				ForgeDirection.EAST, ForgeDirection.EAST, ForgeDirection.EAST,
				ForgeDirection.NORTH, ForgeDirection.NORTH, ForgeDirection.NORTH,
				ForgeDirection.WEST, ForgeDirection.WEST, ForgeDirection.WEST
		};
		highSpeedTransition[1] = new ForgeDirection[] {
				ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.NORTH,
				ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.NORTH,
				ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.NORTH
		};
		highSpeedTransition[2] = new ForgeDirection[] {
				ForgeDirection.EAST, ForgeDirection.EAST, ForgeDirection.EAST,
				ForgeDirection.SOUTH, ForgeDirection.SOUTH, ForgeDirection.SOUTH,
				ForgeDirection.WEST, ForgeDirection.WEST, ForgeDirection.WEST
		};
		highSpeedTransition[3] = new ForgeDirection[] {
				ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH,
				ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH,
				ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH
		};
	}
	
	
	/**
	 * Returns the ideal order to process slots on a conveyor. (Always from
	 * front to back, but lanes are not in a guaranteed order!)
	 * 
	 * @param dir
	 * @return
	 */
	public static int[] getSlotOrderForDirection(ForgeDirection dir) {
		switch (dir) {
		default:
		case NORTH:
			return slotOrders[0];
		case SOUTH:
			return slotOrders[1];
		case WEST:
			return slotOrders[0];
		case EAST:
			return slotOrders[1];
		}
	}
	
	
	/**
	 * Drops the installed appliance and its content, if available.
	 * @param applianceHost
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return true if an appliance was there and did drop.
	 */
	public static boolean dropAppliance(IConveyorApplianceHost applianceHost, EntityPlayer player, World world, int x, int y, int z) {
		String type = applianceHost.getApplianceType();
		if(type == null) {
			return false;
		}
		IConveyorApplianceFactory factory = ApplianceRegistry.getFactory(type);
		if(factory == null) {
			return false;
		}
		Vector3 location = new Vector3(x, y, z);
		/*
		 * Drop appliance
		 */
		IConveyorAppliance appliance = applianceHost.getAppliance();
		if(appliance == null) {
			return false;
		}
		ItemStack stack = appliance.getItemStack();
		//TODO: Make ItemStack retain certain data? (Tanks... Energy...)
		if(stack != null) {
			if(player != null) {
				TaamUtil.tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		/*
		 * Drop appliance content
		 */
		for(int i = 0; i < appliance.getSizeInventory(); i++) {
			stack = appliance.getStackInSlot(i);
			if(stack == null) {
				continue;
			}
			if(player != null) {
				TaamUtil.tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		return true;
	}

	/**
	 * Drops the item in the passed slot, exactly where it is rendered now.
	 * @param slot The slot to be dropped.
	 */
	public static void dropItem(World world, IConveyorAwareTE tileEntity, int slot, boolean withVelocity) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		// System.out.println("Dropping slot " + slot + " >>" + slotObject.itemStack);
		
		if(!world.isRemote) {
			float speedsteps = tileEntity.getSpeedsteps();
			ForgeDirection direction = tileEntity.getMovementDirection();
			
			double posX = tileEntity.posX() + getItemPositionX(slot, slotObject.movementProgress / speedsteps, direction);
			double posY = tileEntity.posY() + 0.5f;
			double posZ = tileEntity.posZ() + getItemPositionZ(slot, slotObject.movementProgress / speedsteps, direction);
			
			if(slotObject.itemStack != null) {
				EntityItem item = new EntityItem(world, posX, posY, posZ, slotObject.itemStack);
				if(withVelocity) {
					float speed = (Byte.MAX_VALUE - speedsteps) * 0.0019f;
					item.motionX = direction.offsetX * speed;
					item.motionY = direction.offsetY * speed;
					item.motionZ = direction.offsetZ * speed;
				} else {
					item.motionX = 0; 
			        item.motionY = 0; 
			        item.motionZ = 0; 
				}
				world.spawnEntityInWorld(item);
			}
		}
		
		slotObject.itemStack = null;
	}

	public static boolean transferSlot(IConveyorAwareTE tileEntity, int slot, IConveyorAwareTE nextBlock, int nextSlot) {
		// System.out.println("Transfer external " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		
		int transferred = nextBlock.insertItemAt(slotObject.itemStack.copy(), nextSlot);
		if(transferred > 0) {
			slotObject.itemStack.stackSize -= transferred;
			if(slotObject.itemStack.stackSize <= 0) {
				slotObject.itemStack = null;
	
				// Reset processing state, so next item starts "fresh"
				slotObject.processing = 0;
				
				// Stack moved completely
				return true;
			}
		}
		// Stack not moved at all, or has backlog
		return false;
	}

	public static boolean transferSlot(IConveyorAwareTE tileEntity, int slot, int nextSlot) {
		// System.out.println("Transfer internal " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		ItemWrapper nextSlotObject = tileEntity.getSlot(nextSlot);
		if(nextSlotObject.itemStack == null) {
			nextSlotObject.itemStack = slotObject.itemStack;
			
			slotObject.itemStack = null;
	
			// Reset processing state, so next item starts "fresh"
			slotObject.processing = 0;
			
			// Stack moved completely
			return true;
		}
		return false;
	}

	public static int insertItemAt(IConveyorAwareTE tileEntity, ItemStack item, int slot, boolean simulate) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		if(slotObject.itemStack == null) {
			if(!simulate) {
				slotObject.itemStack = item.copy();
				slotObject.unblock();
				slotObject.resetMovement();
			}
			return item.stackSize;
		} else if(slotObject.itemStack.isItemEqual(item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.stackSize;
			if(availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.stackSize);
				if(!simulate) {
					slotObject.itemStack.stackSize += availableSpace;
				}
				return availableSpace;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static boolean defaultTransition(World world, IConveyorAwareTE tileEntity, int[] slotOrder) {
		IConveyorApplianceHost applianceHost = null;
		if(tileEntity instanceof IConveyorApplianceHost) {
			applianceHost = (IConveyorApplianceHost)tileEntity;
		}
		boolean needsUpdate = false;
		for(int index = 0; index < slotOrder.length; index++) {
		
			int slot = slotOrder[index];
			
			ItemWrapper wrapper = tileEntity.getSlot(slot);
			
			if(wrapper.isEmpty()) {
				continue;
			}
			
			if(applianceHost != null) {
				IConveyorAppliance appliance = applianceHost.getAppliance();
				if(appliance != null) {
					// System.out.println("Process");
					appliance.processItem(applianceHost, slot, wrapper);
				}
			}
			
			ForgeDirection direction = tileEntity.getMovementDirection();
			byte speedsteps = tileEntity.getSpeedsteps();
			
			boolean slotWrapped = false;
			boolean nextSlotFree = false;
			boolean nextSlotMovable = false;
			int nextSlotProgress = 0;
			boolean wrappedIsSameDirection = true;
			
			IConveyorAwareTE nextBlock = null;
			
			ForgeDirection nextSlotDir = tileEntity.getNextSlot(slot);
			int nextSlot = getNextSlotUnwrapped(slot, nextSlotDir);
			
			if(nextSlot < 0) {
				nextSlot += 9;
				slotWrapped = true;
			} else if(nextSlot > 8) {
				nextSlot -= 9;
				slotWrapped = true;
			}
			
			// Slot wrapped to next block
			if(slotWrapped) {
				// Next block, potentially a conveyor-aware block.
				int nextBlockX = tileEntity.posX() + direction.offsetX;
				int nextBlockY = tileEntity.posY() + direction.offsetY;
				int nextBlockZ = tileEntity.posZ() + direction.offsetZ;
				
				TileEntity te = world.getTileEntity(nextBlockX, nextBlockY, nextBlockZ);
				
				if(te instanceof IConveyorAwareTE) {
					nextBlock = (IConveyorAwareTE) te;
					
					nextSlotFree = nextBlock.getSlot(nextSlot).isEmpty();
					wrappedIsSameDirection = nextBlock.getMovementDirection() == direction;
					nextSlotMovable = nextBlock.canSlotMove(nextSlot) && wrappedIsSameDirection;
					nextSlotProgress = nextBlock.getMovementProgress(nextSlot);
					byte nextSpeedSteps = nextBlock.getSpeedsteps();
					if(nextSpeedSteps != speedsteps) {
						if(nextSpeedSteps == 0) {
							nextSlotProgress = 0;
						} else {
							nextSlotProgress = Math.round((nextSlotProgress / (float)nextSpeedSteps) * speedsteps);
						}
					}
					
				} else {
					// Drop it
					nextSlotFree = true;
					nextSlotMovable = true;
				}
			} else {
				ItemWrapper nextWrapper = tileEntity.getSlot(nextSlot);
				nextSlotFree = nextWrapper.itemStack == null;
				nextSlotMovable = !nextWrapper.isBlocked();
				nextSlotProgress = nextWrapper.movementProgress;
			}
			
			// check next slot.
			if(!wrapper.isBlocked() && (nextSlotFree || nextSlotMovable)) {
				if(wrapper.movementProgress == speedsteps && nextSlotFree) {
					if(slotWrapped && (nextBlock == null || !nextBlock.isSlotAvailable(nextSlot))) {
						// No next block, drop it.
						dropItem(world, tileEntity, slot, true);
					} else {
						boolean completeTransfer;
						if(slotWrapped) {
							completeTransfer = transferSlot(tileEntity, slot, nextBlock, nextSlot);
						} else {
							completeTransfer = transferSlot(tileEntity,slot, nextSlot);
						}
						if(!completeTransfer) {
							// We still have some items pending here..
							nextSlotFree = false;
							nextSlotMovable = false;
						}
					}
					needsUpdate = true;
				}
			}
			if(nextSlotFree || (nextSlotMovable && wrappedIsSameDirection && wrapper.movementProgress < nextSlotProgress)) {
				wrapper.movementProgress++;
				if(wrapper.movementProgress > speedsteps) {
					wrapper.movementProgress = 0;
				}
			}
		}
		return needsUpdate;
	}

	public static void defaultPlayerInteraction(EntityPlayer player, IConveyorAwareTE tileEntity, float hitX, float hitZ) {
		int clickedSlot = getSlotForRelativeCoordinates(hitX, hitZ);
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if(playerStack == null) {
			// Take from Conveyor
			ItemWrapper wrapper = tileEntity.getSlot(clickedSlot);
			if(!wrapper.isEmpty()) {
				player.inventory.setInventorySlotContents(playerSlot, wrapper.itemStack);
				wrapper.itemStack = null;
			}
		} else {
			// Put on conveyor
			int inserted = tileEntity.insertItemAt(playerStack, clickedSlot);
			if(inserted == playerStack.stackSize) {
				player.inventory.setInventorySlotContents(playerSlot, null);
			} else {
				playerStack.stackSize -= inserted;
				player.inventory.setInventorySlotContents(playerSlot, playerStack);
			}
		}
	}
}
