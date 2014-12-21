package stoneharry.pvp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Score;

public class ScoreSystem {
	private ScoreSystem() {
		throw new AssertionError();
	}

	public static void zombiePointIncrement(String plr) {
		try {
			Score score = Main.objective.getScore(plr);
			score.setScore(score.getScore() + 1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void incrementTimerScore(String plr) {
		try {
			Score score = Main.objective.getScore(plr);
			score.setScore(score.getScore() + 3);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void incrementSurvivorScore(String plr) {
		try {
			Score score = Main.objective.getScore(plr);
			score.setScore(score.getScore() + 5);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void returnPersonalScore(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Your score is: "
				+ Main.objective.getScore(sender.getName()).getScore());
	}
}