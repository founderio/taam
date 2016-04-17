package net.teamio.taam.machines;

import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.block.BlockMultipart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IOccludingPart;
import mcmultipart.multipart.Multipart;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ITickable;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.teamio.taam.Taam;

public class MachineMultipart extends Multipart implements IOccludingPart, ITickable {
	private IMachine machine;
	private IMachineMetaInfo meta;

	public MachineMultipart() {
	}
	
	public MachineMultipart(IMachineMetaInfo meta) {
		this.meta = meta;
		this.machine = meta.createMachine();
	}

	@Override
	public String getType() {
		return meta.unlocalizedName();
	}
	
	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		machine.addCollisionBoxes(mask, list, collidingEntity);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		machine.addSelectionBoxes(list);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		machine.addOcclusionBoxes(list);
	}
	
	@Override
	public void onPartChanged(IMultipart part) {
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public void onNeighborBlockChange(Block block) {
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public void onNeighborTileChange(EnumFacing facing) {
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		
		return machine.getExtendedState(state, getWorld(), getPos()).withProperty(VARIANT, (Taam.MACHINE_META)meta);
	}
	
	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT;
	}

	public static final PropertyEnum<Taam.MACHINE_META> VARIANT = PropertyEnum.create("variant", Taam.MACHINE_META.class);
	
	@Override
	public BlockState createBlockState() {
		return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[] { VARIANT }, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
	}
	
	@Override
	public String getModelPath() {
		return machine.getModelPath();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		System.err.println("Reading nbt: " + machineID);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null) {
			this.meta = meta;
			machine = meta.createMachine();
			machine.readPropertiesFromNBT(tag);
		}
	}
	
	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		String machineID = buf.readStringFromBuffer(30);
		System.err.println("Reading buf: " + machineID);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null) {
			this.meta = meta;
			machine = meta.createMachine();
			machine.readUpdatePacket(buf);
		}
	}
	
	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		buf.writeString(meta.unlocalizedName());
		machine.writeUpdatePacket(buf);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("machine", meta.unlocalizedName());
		machine.writePropertiesToNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return machine.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return machine.getCapability(capability, facing);
	}
	
	/*
	 * ITickable implementation
	 */
	
	@Override
	public void update() {
		machine.update(getWorld(), getPos());
	}

}
