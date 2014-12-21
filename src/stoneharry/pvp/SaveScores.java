package stoneharry.pvp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.scoreboard.Objective;

public class SaveScores implements Serializable {
	private static final long serialVersionUID = 658684995334112424L;

	private List<String> players;
	private List<Integer> scores;

	public SaveScores(Objective board) {
		List<String> players = new ArrayList<String>();
		List<Integer> scores = new ArrayList<Integer>();
		for (String plr : board.getScoreboard().getEntries()) {
			players.add(plr);
			scores.add(board.getScore(plr).getScore());
		}
		this.players = players;
		this.scores = scores;
	}

	public List<String> getPlayers() {
		return players;
	}

	public List<Integer> getScores() {
		return scores;
	}
}