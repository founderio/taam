package founderio.taam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;

public class BlockProductionLine extends BaseBlock {
	
	public BlockProductionLine() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		this.setBlockTextureName(Taam.MOD_ID + ":tech_block");
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityConveyor();
		
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		
		super.setBlockBoundsBasedOnState(world, x, y, z);
	}
//	@Override
//	public boolean canProvidePower() {
//		return true;
//	}

//	@Override
//	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
//	
//	@Override
//	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x,
			int y, int z, int side) {
		return false;
	}
	
	@Override
	public void onPostBlockPlaced(World par1World, int par2, int par3,
			int par4, int par5) {
		updateBlocksAround(par1World, par2, par3, par4);
	}

//	@Override
//	public int onBlockPlaced(World par1World, int x, int y, int z,
//			int side, float hitx, float hity, float hitz, int meta) {
//		int metaPart = meta & 8;
//        int resultingRotation = side;
//        return metaPart | resultingRotation;
//	}
	
	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block block, int meta) {
		//TODO: Drop Items
		super.breakBlock(world, x, y, z, block, meta);
	}
		
}
