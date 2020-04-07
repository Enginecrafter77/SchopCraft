package enginecrafter77.survivalinc.stats;

import java.io.Serializable;

import net.minecraft.entity.player.EntityPlayer;

//TODO documentation
public interface StatProvider extends Serializable {
	public float updateValue(EntityPlayer target, float current);
	public String getStatID();
	
	public float getMaximum();
	public float getMinimum();
	
	public float getDefault();
	
	public OverflowHandler getOverflowHandler();
}