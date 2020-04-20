package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class StatStorage implements IStorage<StatTracker> {
	
	public static final IStorage<StatTracker> instance = new StatStorage();
	
	private StatStorage() {}
	
	@Override
	public NBTBase writeNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		for(StatProvider provider : instance.getRegisteredProviders())
			compound.setFloat(provider.getStatID(), instance.getStat(provider));
		return compound;
	}

	@Override
	public void readNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			for(StatProvider provider : instance.getRegisteredProviders())
			{
				String id = provider.getStatID();
				if(compound.hasKey(id))
					instance.setStat(provider, compound.getFloat(id));
				else
					System.err.format("Error: Requested stat %s not defined in saved NBT!\n", id);
			}
		}
	}

}