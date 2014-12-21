package stoneharry.pvp;

public class SortedBoard implements Comparable<SortedBoard> {
	public String player;
	public int score;

	public SortedBoard(String plr, int c) {
		player = plr;
		score = c;
	}

	@Override
	public int compareTo(SortedBoard o) {
		if (score > o.score)
			return -1;
		else if (score < o.score)
			return 1;
		return 0;
	}

}