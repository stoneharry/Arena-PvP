package stoneharry.pvp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin implements Listener {

	private HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
	private HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();

	private ConsoleCommandSender commandConsole = Bukkit.getServer()
			.getConsoleSender();
	public boolean gameRunning = false;
	public boolean gamePrep = true;

	private int[] homeCoords = { 0, 0, 0 };
	private int[] blueCoords = { 0, 0, 0 };
	private int[] redCoords = { 0, 0, 0 };
	private String homeName = "world";
	private String worldName = "arena";

	private ScoreboardManager manager = null;
	private Scoreboard board = null;
	private Team blueTeam = null;
	private Team redTeam = null;

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

	private void saveInventory(Player p) {
		if (p == null)
			return;
		String name = p.getName();
		inventories.put(name, p.getInventory().getContents().clone());
		armour.put(name, p.getInventory().getArmorContents());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setGameMode(GameMode.SURVIVAL);
	}

	private void teleportHome(Player p) {
		p.teleport(new Location(Bukkit.getWorld(homeName), homeCoords[0],
				homeCoords[1], homeCoords[2]));
	}

	private void teleportPlayerIn(Player p) {
		p.teleport(new Location(Bukkit.getWorld(worldName), blueCoords[0],
				blueCoords[1], blueCoords[2]));
	}

	private synchronized void startRound() {
		if (gameRunning)
			return;
		List<Player> players = getPlayers();
		int size = players.size();
		if (size > 1) {
			gameRunning = true;
			Collections.shuffle(players);
			gamePrep = true;
			boolean blue = false;
			for (Player p : players) {
				blue = !blue;
				if (blue) {
					p.teleport(new Location(Bukkit.getWorld(worldName),
							blueCoords[0], blueCoords[1], blueCoords[2]));
					p.sendMessage(ChatColor.AQUA + "[Server] " + ChatColor.RED
							+ "You have joined the blue team!");
					blueTeam.addPlayer(p);
				} else {
					p.teleport(new Location(Bukkit.getWorld(worldName),
							redCoords[0], redCoords[1], redCoords[2]));
					p.sendMessage(ChatColor.AQUA + "[Server] " + ChatColor.RED
							+ "You have joined the red team!");
					redTeam.addPlayer(p);
				}
				p.setScoreboard(board);
				p.setHealth(p.getMaxHealth());
				p.getInventory().clear();
				equipPlayer(p);
				p.sendMessage(ChatColor.AQUA + "[Server] " + ChatColor.RED
						+ "The game will begin in 10 seconds!");
			}
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(this, new Runnable() {
						@Override
						public void run() {
							gamePrep = false;
							for (Player p : getPlayers())
								p.sendMessage(ChatColor.AQUA + "[Server] "
										+ ChatColor.RED + "The game has begun!");
						}
					}, 20 * 10);
		} else {
			for (Player p : players)
				p.sendMessage(ChatColor.AQUA + "[Server] " + ChatColor.RED
						+ "There are not enough players to start this game.");
		}
	}

	private void equipPlayer(Player p) {
		PlayerInventory inventory = p.getInventory();
		inventory.setArmorContents(new ItemStack[] {
				new ItemStack(Material.DIAMOND_HELMET),
				new ItemStack(Material.DIAMOND_CHESTPLATE),
				new ItemStack(Material.DIAMOND_LEGGINGS),
				new ItemStack(Material.DIAMOND_BOOTS) });
		inventory.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
		inventory.addItem(new ItemStack(Material.BOW));
		inventory.addItem(new ItemStack(Material.ARROW, 20));
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
		homeName = getConfig().getString("homeWorld");
		homeCoords[0] = Integer.parseInt(getConfig().getString("homeX"));
		homeCoords[1] = Integer.parseInt(getConfig().getString("homeY"));
		homeCoords[2] = Integer.parseInt(getConfig().getString("homeZ"));
		blueCoords[0] = Integer.parseInt(getConfig().getString("blueX"));
		blueCoords[1] = Integer.parseInt(getConfig().getString("blueY"));
		blueCoords[2] = Integer.parseInt(getConfig().getString("blueZ"));
		redCoords[0] = Integer.parseInt(getConfig().getString("redX"));
		redCoords[1] = Integer.parseInt(getConfig().getString("redY"));
		redCoords[2] = Integer.parseInt(getConfig().getString("redZ"));
	}

	@Override
	public void onDisable() {
		try {
			for (Player p : getPlayers()) {
				teleportHome(p);
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
		// console = Logger.getLogger("Minecraft");
		commandConsole = Bukkit.getServer().getConsoleSender();
		getServer().getPluginManager().registerEvents(this, this);
		// Load the config
		LoadConfig();

		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		blueTeam = board.registerNewTeam("blueTeam");
		redTeam = board.registerNewTeam("redTeam");

		blueTeam.setPrefix(ChatColor.AQUA + "[Blue] ");
		redTeam.setPrefix(ChatColor.RED + "[Red] ");
		redTeam.setAllowFriendlyFire(false);
		blueTeam.setAllowFriendlyFire(false);
		Objective objective = board
				.registerNewObjective("showhealth", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("/ 20");

		commandConsole.sendMessage(ChatColor.AQUA + "########################");
		commandConsole.sendMessage(ChatColor.AQUA + "[StonedArena] "
				+ ChatColor.RED + " Enabled!");
		commandConsole.sendMessage(ChatColor.AQUA + "########################");

		Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new Runnable() {
					@Override
					public void run() {
						startRound();
					}
				}, 20 * 5, 5 * 20); // 20 ticks = 1 second
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!checkPlayer(event.getPlayer()))
			return;
		if (event.getPlayer() != null) {
			teleportHome(event.getPlayer());
			restoreInventory(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (checkPlayer(event.getPlayer())) {
			event.setQuitMessage(null);
			teleportHome(event.getPlayer());
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
		if (gamePrep)
			event.setCancelled(true);
	}

	@EventHandler
	public synchronized void onBlockBreak(BlockBreakEvent event) {
		if (!checkPlayer(event.getPlayer()))
			return;
		if (gamePrep)
			event.setCancelled(true);
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
			if (!(sender instanceof Player))
				return true;
			Player p = (Player) sender;
			if (checkPlayer(p)) {
				restoreInventory(p);
				teleportHome(p);
			} else {
				saveInventory(p);
				teleportPlayerIn(p);
			}
			return true;
		}
		return false;
	}
}