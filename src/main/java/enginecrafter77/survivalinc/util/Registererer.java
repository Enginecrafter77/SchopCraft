package enginecrafter77.survivalinc.util;

import enginecrafter77.survivalinc.ModBlocks;
import enginecrafter77.survivalinc.ModItems;
import enginecrafter77.survivalinc.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class Registererer {
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		for(ModBlocks mb : ModBlocks.values())
		{
			event.getRegistry().register(mb.get());
		}
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		for(ModItems mi : ModItems.values())
			event.getRegistry().register(mi.get());
		
		for(ModBlocks mb : ModBlocks.values())
		{
			Block block = mb.get();
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		//TODO inline
		event.getRegistry().registerAll(ModSounds.SOUNDS);
	}
}