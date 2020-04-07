package enginecrafter77.survivalinc.util;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.SchopConfig;
import enginecrafter77.survivalinc.season.Season;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class SchopWorldData extends WorldSavedData {

	// Identifier
	private static final String ID = "survivalinc";

	// Stuff to save
	// Season data
	// 1 = winter, 2 = spring, 3 = summer, 4 = autumn
	public int season = 0;
	public int daysIntoSeason = 0;

	// Put anymore data here whenever necessary

	// Constructors
	public SchopWorldData()
	{

		super(ID);
	}

	public SchopWorldData(String id)
	{

		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{

		season = nbt.getInteger("season");
		daysIntoSeason = nbt.getInteger("daysIntoSeason");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{

		compound.setInteger("season", season);
		compound.setInteger("daysIntoSeason", daysIntoSeason);

		return compound;
	}

	// Easy loading (Do SchopWorldData data = SchopWorldData.load(world);)
	public static SchopWorldData load(World world)
	{

		SchopWorldData data = (SchopWorldData) world.getMapStorage().getOrLoadData(SchopWorldData.class, ID);

		// Does it not exist?
		if (data == null)
		{
			SurvivalInc.logger.warn("No world data found for survivalinc. Creating new file.");

			data = new SchopWorldData();

			// Predetermine some values if necessary

			// Seasons
			// Determine starting season and daysIntoSeason
			double springOrFall = Math.random();

			if (springOrFall < 0.50)
			{

				data.season = 2;
			}

			else
			{

				data.season = 4;
			}

			if (data.season == 2)
			{

				data.daysIntoSeason = (SchopConfig.SEASONS.springLength / 2) - 1;
			}

			else
			{

				data.daysIntoSeason = 0;
			}

			data.markDirty();
			world.getMapStorage().setData(ID, data);
		}

		return data;
	}

	// Conversion methods for seasons
	public static int seasonToInt(Season season)
	{

		switch (season)
		{

		case WINTER:
			return 1;
		case SPRING:
			return 2;
		case SUMMER:
			return 3;
		case AUTUMN:
			return 4;
		default:
			return 0;
		}
	}

	public static Season intToSeason(int seasonInt)
	{

		switch (seasonInt)
		{

		case 1:
			return Season.WINTER;
		case 2:
			return Season.SPRING;
		case 3:
			return Season.SUMMER;
		case 4:
			return Season.AUTUMN;
		default:
			return null;
		}
	}

	public Season getSeasonFromData()
	{

		switch (this.season)
		{

		case 1:
			return Season.WINTER;
		case 2:
			return Season.SPRING;
		case 3:
			return Season.SUMMER;
		case 4:
			return Season.AUTUMN;
		default:
			return null;

		}
	}
}