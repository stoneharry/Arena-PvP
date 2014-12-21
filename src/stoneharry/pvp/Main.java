package stoneharry.pvp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public String worldName = "";

	private HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
	private HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();

	private ConsoleCommandSender commandConsole = Bukkit.getServer()
			.getConsoleSender();
	private Logger console = null;

	private boolean checkPlayer(Player p) {
		if (p != null && p.getWorld().getName().equals(worldName))
			return true;
		return false;
	}

	private void restoreInventory(Player p) {
		if (p == null)
			return;
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		String name = p.getName();
		ItemStack[] items = inventories.get(name);
		ItemStack[] arm = armour.get(name);
		if (items != null) {
			for (ItemStack item : items) {
				if (item != null)
					p.getInventory().addItem(item);
			}
			inventories.remove(name);
		}
		if (arm != null) {
			p.getInventory().setArmorContents(arm);
			armour.remove(name);
		}
		p.setDisplayName(name);
		p.setPlayerListName(name);
	}

	private void saveInventory(Player p, boolean teleport) {
		if (p == null)
			return;
		String name = p.getName();
		inventories.put(name, p.getInventory().getContents().clone());
		armour.put(name, p.getInventory().getArmorContents());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setGameMode(GameMode.SURVIVAL);
	}

	public List<Player> getPlayers() {
		Collection<? extends Player> plrs = Bukkit.getOnlinePlayers();
		List<Player> returnVal = new LinkedList<Player>();
		for (Player p : plrs) {
			if (p.getWorld().getName().equals(worldName))
				returnVal.add(p);
		}
		return returnVal;
	}

	private void LoadConfig() {
		// The following method will not overwrite an existing file.
		saveDefaultConfig();
		worldName = getConfig().getString("WorldName");
	}

	@Override
	public void onDisable() {
		try {
			for (Player p : getPlayers()) {
				// TODO
				// p.teleport(new Location(Bukkit.getWorld(homeWorldName),
				// homeWorldCoords[0], homeWorldCoords[1],
				// homeWorldCoords[2]));
				restoreInventory(p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		commandConsole.sendMessage(ChatColor.AQUA + "########################");
		commandConsole.sendMessage(ChatColor.AQUA + "[StonedArena] "
				+ ChatColor.RED + " Disabled!");
		commandConsole.sendMessage(ChatColor.AQUA + "########################");
	}

	@Override
	public void onEnable() {
		console = Logger.getLogger("Minecraft");
		commandConsole = Bukkit.getServer().getConsoleSender();
		getServer().getPluginManager().registerEvents(this, this);
		// Load the config
		LoadConfig();
		commandConsole.sendMessage(ChatColor.AQUA + "########################");
		commandConsole.sendMessage(ChatColor.AQUA + "[StonedArena] "
				+ ChatColor.RED + " Enabled!");
		commandConsole.sendMessage(ChatColor.AQUA + "########################");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!checkPlayer(event.getPlayer()))
			return;
		if (event.getPlayer() != null) {
			// TODO
			// event.getPlayer().teleport(
			// new Location(Bukkit.getWorld(homeWorldName),
			// homeWorldCoords[0], homeWorldCoords[1],
			// homeWorldCoords[2]));
			restoreInventory(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (checkPlayer(event.getPlayer())) {
			event.setQuitMessage(null);
			// TODO
			// event.getPlayer().teleport(
			// new Location(Bukkit.getWorld(homeWorldName),
			// homeWorldCoords[0], homeWorldCoords[1],
			// homeWorldCoords[2]));
			restoreInventory(event.getPlayer());
		}
	}

	// Prevent players getting hungry
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player) {
			if (checkPlayer((Player) e))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public synchronized void onBlockPlace(BlockPlaceEvent event) {
		if (!checkPlayer(event.getPlayer()))
			return;

	}

	@EventHandler
	public synchronized void onBlockBreak(BlockBreakEvent event) {
		if (!checkPlayer(event.getPlayer()))
			return;
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		// Try to stop weather
		if (event.getWorld().getName().equals(worldName)) {
			getServer().getWorld(worldName).setWeatherDuration(1);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommandPre(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (checkPlayer(p)) {
			if (!p.isOp()) {
				String message = event.getMessage();
				if (message.equals("/arena")) {
					event.setCancelled(false);
				} else {
					event.setCancelled(true);
					p.sendMessage(ChatColor.RED
							+ "To leave this game mode use: /arena");
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arena")) {
			return true;
		}
		return false;
	}
}