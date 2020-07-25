package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GhostEnergy implements StatProvider {
	private static final long serialVersionUID = -2088047893866334112L;
	
	public static final ResourceLocation identifier = new ResourceLocation(SurvivalInc.MOD_ID, "ghostenergy");
	public static final GhostEnergy instance = new GhostEnergy();
	
	public final FilteredEffectApplicator applicator;
	
	private GhostEnergy()
	{
		this.applicator = new FilteredEffectApplicator();
	}
	
	@Override
	public float updateValue(EntityPlayer target, float current)
	{
		return DefaultStats.capValue(this, this.applicator.apply(target, current));
	}

	@Override
	public ResourceLocation getStatID()
	{
		return GhostEnergy.identifier;
	}

	@Override
	public float getMaximum()
	{
		return 100F;
	}

	@Override
	public float getMinimum()
	{
		return 0F;
	}

	@Override
	public StatRecord createNewRecord()
	{
		return new SimpleStatRecord();
	}

	@Override
	public boolean isAcitve(EntityPlayer player)
	{
		Ghost ghost = player.getCapability(GhostProvider.target, null);
		return ghost.getStatus();
	}
}
