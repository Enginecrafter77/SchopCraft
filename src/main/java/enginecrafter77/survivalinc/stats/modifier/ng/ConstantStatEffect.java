package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConstantStatEffect implements StatEffect {

	public final Operation operation;
	public final float argument;
	
	public ConstantStatEffect(Operation operation, float argument)
	{
		this.operation = operation;
		this.argument = argument;
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		return this.operation.apply(current, this.argument);
	}

	@Override
	public Side sideOnly()
	{
		return null;
	}
	
	public enum Operation implements BiFunction<Float, Float, Float> {
		OFFSET((Float current, Float mod) -> current + mod),
		SCALE((Float current, Float mod) -> current * mod);
		
		private final BiFunction<Float, Float, Float> function;
		
		private Operation(BiFunction<Float, Float, Float> function)
		{
			this.function = function;
		}

		@Override
		public Float apply(Float current, Float mod)
		{
			return function.apply(current, mod);
		}
	}
}
