package schoperation.schopcraft.cap.vital;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import schoperation.schopcraft.CommonProxy;
import schoperation.schopcraft.cap.ghost.GhostProvider;
import schoperation.schopcraft.cap.ghost.IGhost;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.WetnessProvider;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.lib.ModItems;
import schoperation.schopcraft.packet.SummonInfoPacket;
import schoperation.schopcraft.util.SchopServerEffects;

import java.util.List;

/*
 * Where sanity is modified.
 */

public class SanityModifier {

	// The first variable is used for the timer at the end of onPlayerUpdate to
	// allow for a hallucination once per 20 ticks.
	// The other is for spawning "Them".
	private int lucidTimer = 0;
	private int spawnThemTimer = 0;

	public void onPlayerUpdate(Entity player)
	{
		// Capabilities
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);

		// Block position of player.
		BlockPos playerPos = player.getPosition();

		// ACTUAL position of player.
		double playerPosX = player.posX;
		double playerPosY = player.posY;
		double playerPosZ = player.posZ;

		// Lists of entities near the player.
		AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(7, 2, 7);
		AxisAlignedBB boundingBoxPlayers = player.getEntityBoundingBox().grow(4, 2, 4);
		List<EntityMob> nearbyMobs = player.world.getEntitiesWithinAABB(EntityMob.class, boundingBox);
		List<EntityAnimal> nearbyAnimals = player.world.getEntitiesWithinAABB(EntityAnimal.class, boundingBox);
		List<EntityPlayer> nearbyPlayers = player.world.getEntitiesWithinAABB(EntityPlayer.class, boundingBoxPlayers);
		List<EntityVillager> nearbyVillagers = player.world.getEntitiesWithinAABB(EntityVillager.class, boundingBoxPlayers);

		// Modifier from config
		float modifier = (float) SchopConfig.MECHANICS.sanityScale;

		// Being awake late at night is only for crazy people and college
		// students.
		if (!player.world.isDaytime() && playerPosY >= player.world.getSeaLevel())
		{
			stats.modifyStat(VitalStatType.SANITY, -0.0015f * modifier);
		}

		// Being in the nether or the end isn't too sane.
		if (player.dimension == -1 || player.dimension == 1)
		{

			stats.modifyStat(VitalStatType.SANITY, -0.004f * modifier);
		}

		// Constant drain in caves... because why not!
		if (playerPosY <= (player.world.getSeaLevel() - 15))
		{
			stats.modifyStat(VitalStatType.SANITY, -0.0015f * modifier);
		}

		// Being in the dark in general, is pretty spooky.
		if (player.world.getLight(playerPos, true) < 2 && player.dimension != -1 && player.dimension != 1)
		{
			stats.modifyStat(VitalStatType.SANITY, -0.08f * modifier);
		}

		else if (player.world.getLight(playerPos, true) < 4 && player.dimension != -1 && player.dimension != 1)
		{
			stats.modifyStat(VitalStatType.SANITY, -0.04f * modifier);
		}

		else if (player.world.getLight(playerPos, true) < 7 && player.dimension != -1 && player.dimension != 1 && (playerPosY <= player.world.getSeaLevel()))
		{
			stats.modifyStat(VitalStatType.SANITY, -0.02f * modifier);
		}

		// Being drenched for a long time won't do you good.
		if(wetness.getWetness() > 90.0f)
		{
			stats.modifyStat(VitalStatType.SANITY, -0.003f * modifier);
		}
		else if(wetness.getWetness() > 70.0f)
		{
			stats.modifyStat(VitalStatType.SANITY, -0.001f * modifier);
		}

		// Now iterate through each mob that appears on the list of nearby mobs.
		for (int numMobs = 0; numMobs < nearbyMobs.size(); numMobs++)
		{
			// Chosen mob
			EntityMob mob = (EntityMob) nearbyMobs.get(numMobs);

			// Now change sanity according to what it is.
			if(mob instanceof EntityEnderman)
			{
				stats.modifyStat(VitalStatType.SANITY, -0.005f * modifier);
			}
			else if(mob instanceof EntityEvoker || mob instanceof EntityIllusionIllager || mob instanceof EntitySpellcasterIllager || mob instanceof EntityVindicator)
			{
				stats.modifyStat(VitalStatType.SANITY, -0.004f * modifier);
			}
			else if(mob instanceof EntityWither)
			{
				stats.modifyStat(VitalStatType.SANITY, -0.05f * modifier);
			}
			else
			{
				stats.modifyStat(VitalStatType.SANITY, -0.003f * modifier);
			}
		}

		// Do the same for animals.
		for (int numAnimals = 0; numAnimals < nearbyAnimals.size(); numAnimals++)
		{
			// Chosen animal
			EntityAnimal animal = (EntityAnimal)nearbyAnimals.get(numAnimals);

			// Now change sanity according to what it is.
			if(animal instanceof EntityWolf || animal instanceof EntityOcelot || animal instanceof EntityParrot)
			{
				stats.modifyStat(VitalStatType.SANITY, 0.005f * modifier);
			}
			else if(animal instanceof EntitySheep)
			{
				stats.modifyStat(VitalStatType.SANITY, 0.003f * modifier);
			}
			else
			{
				stats.modifyStat(VitalStatType.SANITY, 0.002f * modifier);
			}
		}

		// And for players.
		for (int numPlayers = 0; numPlayers < nearbyPlayers.size(); numPlayers++)
		{

			// Chosen player
			EntityPlayerMP otherPlayer = (EntityPlayerMP) nearbyPlayers.get(numPlayers);

			// Ghost capability of other player.
			IGhost ghost = otherPlayer.getCapability(GhostProvider.GHOST_CAP, null);

			// Now change sanity, unless it's just the player themselves, or a
			// ghost.
			if(otherPlayer != player && !ghost.status())
			{
				stats.modifyStat(VitalStatType.SANITY, 0.003f * modifier);
			}
			else if(otherPlayer != player && ghost.status())
			{
				stats.modifyStat(VitalStatType.SANITY, -0.05f * modifier);
			}
		}

		// Villagers are nice as well.
		for (int numVillagers = 0; numVillagers < nearbyVillagers.size(); numVillagers++)
		{
			stats.modifyStat(VitalStatType.SANITY, 0.003f * modifier);
		}

		// ===========================================================================
		// The Side Effects of Insanity
		// ===========================================================================

		// Every 20 ticks (1 second) there is a chance for a hallucination to
		// appear; visual, audial, or both.
		// In this case, a hallucination is a client-only particle/sound. The
		// "things" (Maxwell refers to them as "Them") are a different area.
		// The more insane the player is, the bigger the chance is.
		if (lucidTimer < 20)
		{

			// Increment timer until it reaches 20.
			lucidTimer++;
		}
		else
		{

			// Reset timer
			lucidTimer = 0;

			// Increment THIS timer
			spawnThemTimer++;

			// There'll only be hallucinations for players with less than 70% of
			// their sanity.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.7))
			{

				// Determine if a hallucination should appear.
				double chanceOfHallucination = (double) (stats.getStat(VitalStatType.SANITY) / 100) + 0.3;
				double randomLucidNumber = Math.random();
				boolean shouldSpawnHallucination = chanceOfHallucination < randomLucidNumber;

				// So... should one appear?
				if (shouldSpawnHallucination)
				{

					// Now pick one... more random numbers!
					double pickAHallucination = Math.random();
					double randOffset = Math.random() * 6;
					int posOrNeg = (int) Math.round(Math.random());

					if (posOrNeg == 0)
					{
						randOffset = randOffset * -1;
					}

					// As of now... ten possibilities... all weighted equally.
					// These will be called on the client, so no one else can
					// see/hear them. Random positions nearby the player too.

					// Enderman noise + particles
					if (pickAHallucination >= 0 && pickAHallucination < 0.10)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"EndermanSound", "EndermanParticles", playerPosX + randOffset, playerPosY + 1,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Zombie sound
					else if (pickAHallucination >= 0.10 && pickAHallucination < 0.20)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"ZombieSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Ghast sound
					else if (pickAHallucination >= 0.20 && pickAHallucination < 0.30)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"GhastSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Explosion sound + particles
					else if (pickAHallucination >= 0.30 && pickAHallucination < 0.40)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"ExplosionSound", "ExplosionParticles", playerPosX + randOffset,
								playerPosY + randOffset, playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Stone sound
					else if (pickAHallucination >= 0.40 && pickAHallucination < 0.50)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"StoneBreakSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Mist in the air... tf???????
					else if (pickAHallucination >= 0.50 && pickAHallucination < 0.60)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"null", "CreepyMistParticles", playerPosX + randOffset, playerPosY + 1,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// A guardian appearing in your face. This one still scares
					// the crap out of me.
					else if (pickAHallucination >= 0.60 && pickAHallucination < 0.70)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"null", "GuardianParticles", playerPosX, playerPosY, playerPosZ);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Fire sounds + smoke particles
					else if (pickAHallucination >= 0.70 && pickAHallucination < 0.80)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"FireSound", "SmokeParticles", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// A�villager sound... are they lost?
					else if (pickAHallucination >= 0.80 && pickAHallucination < 0.90)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"VillagerSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}

					// Lava sound
					else if (pickAHallucination >= 0.90 && pickAHallucination <= 1.00)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
								"LavaSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}
				}
			}

			// There are other side effects of insanity other than
			// hallucinations.
			// Here, the player's view is wobbled/distorted
			// Some weird ambience is added to make insanity feel more insane.
			// And... weird. It's just the right word.
			// Also, They may come and attack you.

			// Make the screen of the insane player wobble.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.35))
			{

				SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
			}

			// Add some weird insanity ambiance.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.20))
			{

				// Random chance so it doesn't overlap with itself.
				double randInsanityAmbience = Math.random();

				if (randInsanityAmbience < 0.20)
				{

					IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
							"InsanityAmbienceSoundLoud", "null", playerPosX, playerPosY, playerPosZ);
					CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
				}
			}
			else if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
			{

				// Random chance so it doesn't overlap with itself.
				double randInsanityAmbience = Math.random();

				if (randInsanityAmbience < 0.20)
				{

					IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(),
							"InsanityAmbienceSound", "null", playerPosX, playerPosY, playerPosZ);
					CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) player);
				}
			}

			// Add and spawn "Them". As of now, it's just a bunch of invisible
			// endermen. They drop "Lucid Dream Essence."
			// They can be seen by all players, that's alright. They just like
			// to gather near black holes void of sanity.
			// If the player's sanity is really low, spawn a bunch of "Them" and
			// make "Them" attack the player.
			if ((stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.15)) && spawnThemTimer >= 15)
			{

				// Random numbers... gotta love random numbers.
				double randOffsetToSummonThem = Math.random() * 30;
				double posOrNeg = Math.round(Math.random());

				// Reset spawnThemTimer
				spawnThemTimer = 0;

				if (posOrNeg == 0)
				{
					randOffsetToSummonThem = randOffsetToSummonThem * -1;
				}

				// Instance of Them
				EntityEnderman them = new EntityEnderman(player.world);

				// Position Them
				them.setLocationAndAngles(playerPosX + randOffsetToSummonThem, playerPosY + 2,
						playerPosZ + randOffsetToSummonThem, 0.0f, 0);

				// Affect Them
				them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));

				// Aggroe Them
				them.setAttackTarget((EntityLivingBase) player);

				// Add to the "entity limit"... Them
				them.preventEntitySpawning = true;

				// Summon Them
				player.world.spawnEntity(them);
			}
			else if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
			{

				// Random numbers... YEE
				double randChanceToSummonThem = Math.random();
				double randOffsetToSummonThem = Math.random() * 30;
				double posOrNeg = Math.round(Math.random());

				if (posOrNeg == 0)
				{
					randOffsetToSummonThem = randOffsetToSummonThem * -1;
				}

				if (randChanceToSummonThem < 0.03)
				{

					// Instance of Them
					EntityEnderman them = new EntityEnderman(player.world);

					// Affect Them
					them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));

					// Position Them
					them.setLocationAndAngles(playerPosX + randOffsetToSummonThem, playerPosY + 2,
							playerPosZ + randOffsetToSummonThem, 0.0f, 0);

					// Add to the "entity limit"... Them
					them.preventEntitySpawning = true;

					// Summon Them
					player.world.spawnEntity(them);
				}
			}
		}
	}

	// This checks any consumed item by the player, and affects sanity
	// accordingly. Just vanilla items for now.
	public void onPlayerConsumeItem(EntityPlayer player, ItemStack item)
	{
		// Capability
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);

		// Number of items
		int amount = item.getCount();

		// If raw or bad food, drain sanity.
		if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.CHICKEN, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BEEF, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.RABBIT, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.MUTTON, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.PORKCHOP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.FISH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.ROTTEN_FLESH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.SPIDER_EYE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -15.0f);
		}

		// If cooked or good food, increase sanity.
		if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_CHICKEN, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_BEEF, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_RABBIT, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_MUTTON, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_PORKCHOP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_FISH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.PUMPKIN_PIE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 15.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKIE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.RABBIT_STEW, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 15.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.MUSHROOM_STEW, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BEETROOT_SOUP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 10.0f);
		}
	}

	// This checks if the player is sleeping.
	// It's mostly for servers, as not everyone may be asleep at the same time.
	// This method alone doesn't run for too long on singleplayer.
	public void onPlayerSleepInBed(EntityPlayer player)
	{
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);
		stats.modifyStat(VitalStatType.SANITY, 0.004f);
		// Induce some hunger.
		SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "hunger", 20, 4, false, false);
	}

	// At this point, the player has awoke from their sleep. This "sleep" could've been 1 second or 1 day.
	// Figure out if it is daytime (the sleep is successful). If so, grant extra sanity and drain extra hunger.
	public void onPlayerWakeUp(EntityPlayer player)
	{
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);
		
		// Is it daytime? If not, the player just clicked "Leave Bed" or something related to try to cheat the system (and might've succeeded).
		if(player.world.getWorldTime() % 24000 >= 0)
		{
			stats.modifyStat(VitalStatType.SANITY, 33F);
			// Make player hungry for breakfast (or something...).
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}

	// As we know, They will spawn near insane players. They should drop lucid dream essence when killed.
	public void onDropsDropped(Entity entityKilled, List<EntityItem> drops, int lootingLevel, DamageSource damageSource)
	{

		// Was this mob killed by a player? (and server-side).
		if (damageSource.getDamageType().equals("player") && !entityKilled.world.isRemote)
		{

			// Instance of player
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();

			// Capability
			VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);

			// Was the victim an enderman? or Them?
			if (entityKilled instanceof EntityEnderman)
			{

				// Now, was the player insane (or insane enough)?
				if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
				{
					drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
					
					double randChanceForAdditional = Math.random();
					if (randChanceForAdditional < 0.50)
					{

						drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
					}

					// A higher looting level on the weapon will give a chance
					// for more essence to drop.
					for (int i = 0; i < lootingLevel; i++)
					{

						double anotherOne = Math.random();
						if (anotherOne < 0.75)
						{

							drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
						}
					}
				}

				// The player regains sanity for killing one of their fears.
				stats.modifyStat(VitalStatType.SANITY, 15F);
			}
		}
	}
}