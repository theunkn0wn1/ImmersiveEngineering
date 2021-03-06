package blusunrize.immersiveengineering.common.blocks.wooden;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.INeighbourChangeTile;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.util.RotationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityTurntable extends TileEntityIEBase implements IDirectionalTile, INeighbourChangeTile, IHammerInteraction
{

	private EnumFacing facing = EnumFacing.UP;
	private boolean redstone = false;
	public boolean invert = false;

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		facing = EnumFacing.getFront(nbt.getInteger("facing"));
		redstone = nbt.getBoolean("redstone");
		invert = nbt.getBoolean("invert");
	}
	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		nbt.setInteger("facing", facing.ordinal());
		nbt.setBoolean("redstone", redstone);
		nbt.setBoolean("invert", invert);
	}

	@Override
	public void onNeighborBlockChange(BlockPos pos)
	{
		boolean r = this.worldObj.isBlockPowered(pos);
		if(r!=this.redstone)
		{
			this.redstone = r;
			if(this.redstone)
			{
				BlockPos target = pos.offset(facing);
				RotationUtil.rotateBlock(this.worldObj, target, invert?facing:facing.getOpposite());
			}
		}
	}

	@Override
	public EnumFacing getFacing()
	{
		return facing;
	}
	@Override
	public void setFacing(EnumFacing facing)
	{
		this.facing = facing;
	}
	@Override
	public int getFacingLimitation()
	{
		return 1;
	}
	@Override
	public boolean mirrorFacingOnPlacement(EntityLivingBase placer)
	{
		return placer.isSneaking();
	}
	@Override
	public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity)
	{
		return !entity.isSneaking();
	}
	@Override
	public boolean canRotate(EnumFacing axis)
	{
		return true;
	}

	@Override
	public boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking())
		{
			invert = !invert;
			markDirty();
			worldObj.addBlockEvent(getPos(), this.getBlockType(), 254, 0);
			return true;
		}
		return false;
	}
}