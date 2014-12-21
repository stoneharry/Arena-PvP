package stoneharry.pvp;

import org.bukkit.Location;
import org.bukkit.Material;

public class ChangedBlock {
	public Material block;
	public Location location;

	public ChangedBlock(Material mat, Location loc) {
		block = mat;
		location = loc;
	}
}
