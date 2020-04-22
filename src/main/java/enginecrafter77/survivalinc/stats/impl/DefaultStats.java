package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRecordEntry;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;

public enum DefaultStats implements StatProvider {
	HYDRATION(ModConfig.HYDRATION.scale, 0, 100),
	WETNESS(ModConfig.WETNESS.scale, 0, 100, 0F),
	SANITY(ModConfig.SANITY.scale, 0, 100);
	
	public final float scale;
	
	public final ModifierApplicator<EntityPlayer> modifiers;
	private float max, min, def;
	
	private DefaultStats(double scale, float min, float max, float def)
	{
		this.modifiers = new ModifierApplicator<EntityPlayer>();
		this.scale = (float)scale;
		this.min = min;
		this.max = max;
		this.def = def;
	}
	
	private DefaultStats(double scale, float min, float max)
	{
		this(scale, min, max, max * 0.75F);
	}
	
	@Override
	public float updateValue(EntityPlayer target, float current)
	{
		return modifiers.apply(target, current);
	}
	
	@Override
	public String getStatID()
	{
		return this.name().toLowerCase();
	}

	@Override
	public float getMaximum()
	{
		return this.max;
	}

	@Override
	public float getMinimum()
	{
		return this.min;
	}
	
	@Override
	public StatRecord createNewRecord()
	{
		return new StatRecordEntry(this.def);
	}

	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.CAP;
	}
}