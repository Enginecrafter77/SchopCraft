package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;

public class GuiRenderBar extends Gui {
	
	// resource locations of bars.
	private final ResourceLocation tempBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final int textureWidth = 100, textureHeight = 11, barWidth = 80;
	
	// stats for rendering
	private static float wetness = 0.00f;
	private static float maxWetness = 100.00f;
	private static float thirst = 100.00f;
	private static float maxThirst = 100.00f;
	private static float sanity = 100.00f;
	private static float maxSanity = 100.00f;
	
	// these methods are to get the correct stats of the player.
	public static void getServerThirst(float newThirst, float newMaxThirst) {
		
		thirst = newThirst;
		maxThirst = newMaxThirst;
	}
	
	public static void getServerSanity(float newSanity, float newMaxSanity) {
		
		sanity = newSanity;
		maxSanity = newMaxSanity;
	}
	
	public static void getServerWetness(float newWetness, float newMaxWetness) {
		
		wetness = newWetness;
		maxWetness = newMaxWetness;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
			
			// instance of Minecraft. All of this crap is client-side (well of course)
			Minecraft mc = Minecraft.getMinecraft();
			
			// get current screen resolution
			ScaledResolution scaled = new ScaledResolution(mc);
			int screenWidth = scaled.getScaledWidth();
			int screenHeight = scaled.getScaledHeight();
			
			// determine width of WETNESS bar.
			double oneWetnessUnit = (double) barWidth / maxWetness; // default 0.8
			int currentWidthWetness = (int) (oneWetnessUnit * wetness);
			String textWetness = Float.toString(Math.round(wetness)) + "%";
			
			// determine width of THIRST bar.
			double oneThirstUnit = (double) barWidth / maxThirst; // default 0.8
			int currentWidthThirst = (int) (oneThirstUnit * thirst);
			String textThirst = Float.toString(Math.round(thirst)) + "%";
			
			// determine width of SANITY bar.
			double oneSanityUnit = (double) barWidth / maxSanity; // default 0.8
			int currentWidthSanity = (int) (oneSanityUnit * sanity);
			String textSanity = Float.toString(Math.round(sanity)) + "%";
			
			// this is temporary bullcrap to test the bars. they work.
			float oneUnit = (float) (barWidth / mc.player.getMaxHealth());
			int currentWidth = (int) (oneUnit * mc.player.getHealth());
			int playerHealth = (int) mc.player.getHealth();
			String text = Integer.toString(playerHealth) + "%";
			
			// only show bars if the f3 debug screen isn't showing.
			if (!mc.gameSettings.showDebugInfo) {
				
				// top rect is bar, bottom rect is outline/icon
				// TEMPERATURE
				mc.renderEngine.bindTexture(tempBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-277, 19, 14, currentWidth, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-280, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, text, screenWidth-textureWidth-16, screenHeight-277, Integer.parseInt("FFFFFF", 16));
				
				// THIRST
				mc.renderEngine.bindTexture(thirstBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-257, 19, 14, currentWidthThirst, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-260, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, textThirst, screenWidth-textureWidth-16, screenHeight-257, Integer.parseInt("FFFFFF", 16));
				
				// SANITY
				mc.renderEngine.bindTexture(sanityBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-237, 19, 14, currentWidthSanity, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-240, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, textSanity, screenWidth-textureWidth-16, screenHeight-237, Integer.parseInt("FFFFFF", 16));
				
				// WETNESS
				// only show wetness if there is wetness. This is in place so wetness isn't confused with thirst.
				if (wetness > 0) {
					
					mc.renderEngine.bindTexture(wetnessBar);
					drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-217, 19, 14, currentWidthWetness, textureHeight);
					drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-220, 0, 0, textureWidth, textureHeight);
					drawCenteredString(mc.fontRenderer, textWetness, screenWidth-textureWidth-16, screenHeight-217, Integer.parseInt("FFFFFF", 16));
				}
			}
		}
	}
}