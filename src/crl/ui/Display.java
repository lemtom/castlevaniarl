package crl.ui;

import crl.game.Game;
import crl.monster.Monster;
import crl.npc.Hostage;
import crl.player.HiScore;
import crl.player.Player;
import crl.player.advancements.Advancement;

import java.io.File;
import java.util.List;
import java.util.Properties;

public abstract class Display {
	private static final String NEWLINES = " \n \n ";
	public static Display thus;

	public abstract int showTitleScreen();

	public abstract void showIntro(Player player);

	public abstract boolean showResumeScreen(Player player);

	public abstract void showEndgame(Player player);

	public abstract void showHiscores(HiScore[] scores);

	public abstract void showHelp();

	public abstract void showDraculaSequence();

	public abstract void showTimeChange(boolean day, boolean fog, boolean rain, boolean thunderstorm, boolean sunnyDay);

	public abstract int showSavedGames(File[] saveFiles);

	public abstract void showHostageRescue(Hostage h);

	public abstract Advancement showLevelUp(List<Advancement> soulIds);

	public abstract void showChat(String chatID, Game game);

	public abstract void showScreen(Object screenID);

	public abstract void showMap(String locationKey, String locationDescription);

	public abstract void showMonsterScreen(Monster who, Player player);

	public String getTimeChangeMessage(boolean day, boolean fog, boolean rain, boolean thunderstorm, boolean sunnyDay) {
		String baseMessage = day ? "THE MORNING SUN HAS VANQUISHED THE HORRIBLE NIGHT..."
				: "... WHAT A HORRIBLE NIGHT TO HAVE A CURSE";
		if (fog) {
			baseMessage += NEWLINES;
			baseMessage += "A HEAVY FOG ENGULFS THE PLACE";
		}
		if (rain) {
			baseMessage += NEWLINES;
			baseMessage += "RAIN STARTS POURING FROM THE DARK SKY";
		}
		if (thunderstorm) {
			baseMessage += NEWLINES;
			baseMessage += "A THUNDERSTORM BREAKS LOOSE THE SPIRITS OF DARK";
		}
		if (sunnyDay) {
			baseMessage += NEWLINES;
			baseMessage += "A GENTLE SUN SHINES IN THIS GRAY DAY";
		}

		return baseMessage;
	}

	protected static Properties keyBindings;

	public static void setKeyBindings(Properties keyBindings) {
		Display.keyBindings = keyBindings;
	}

	public static Properties getKeyBindings() {
		return keyBindings;
	}

}
