package me.shzylo.mobmeat;

import me.shzylo.mobmeat.events.Bonemeal;
import me.shzylo.mobmeat.events.SpiderEye;
import me.shzylo.mobmeat.events.Sulphur;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/* MobMeat by Shzylo
 * PLUGIN TYPE:
 * - Fun
 * 
 * Plugin was made just for giggles :)
 */

public final class MobMeat extends JavaPlugin {

  @Override
	public void onEnable() {
		this.saveDefaultConfig();
		getLogger().info("MobMeat has loaded up.");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Sulphur(this), this);
		pm.registerEvents(new Bonemeal(this), this);
		pm.registerEvents(new SpiderEye(this), this);

		getServer().getPluginCommand("mobmeat").setPermissionMessage(
				"[" + ChatColor.AQUA + "MobMeat" + ChatColor.RESET + "]" + ChatColor.RED + " No Permission!");
	}

	@Override
	public void onDisable() {
		getLogger().info("MobMeat has shut down.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mobmeat")) {
			if (sender.hasPermission("mobmeat.reload")) {
				if (args.length == 0) {
					sender.sendMessage("===============[" + ChatColor.AQUA + "MobMeat" + ChatColor.WHITE
							+ "]===============");
					sender.sendMessage(ChatColor.GOLD + "Plugin by: " + ChatColor.AQUA + "Shzylo");
					sender.sendMessage(ChatColor.GOLD + "Plugin version: " + ChatColor.AQUA + "0.1");
					sender.sendMessage("");
					sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "COMMANDS:");
					sender.sendMessage(ChatColor.GOLD + "/mm reload " + ChatColor.WHITE + "- Reloads MobMeat");
					sender.sendMessage(ChatColor.GOLD + "/mm help " + ChatColor.WHITE + "- How to use MobMeat");
					sender.sendMessage("");
					sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "PERMISSIONS:");
					sender.sendMessage(ChatColor.GOLD + "mobmeat.reload " + ChatColor.WHITE
							+ "- Allows you to reload MobMeat");
					sender.sendMessage(ChatColor.RED + "Default: Op");
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GOLD + "mobmeat.sulphur " + ChatColor.WHITE
							+ "- Allows you to use gunpowder as an explosive by right-clicking");
					sender.sendMessage(ChatColor.RED + "Default: True");
					return true;
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						this.reloadConfig();
						sender.sendMessage(ChatColor.AQUA + "MobMeat has been Reloaded!");
						return true;
					} else if (args[0].equalsIgnoreCase("rl")) {
						this.reloadConfig();
						sender.sendMessage(ChatColor.AQUA + "MobMeat has been Reloaded!");
						return true;
					} else if (args[0].equalsIgnoreCase("help")) {
						sender.sendMessage("=============[" + ChatColor.AQUA + "MobMeat Help" + ChatColor.WHITE
								+ "]=============");
						sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC
								+ "(NOTE: These are configurable, everything may not be enabled)");
						sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "SULPHUR");
						sender.sendMessage("1. " + ChatColor.RED + "Right-click a block to create a mini explosion.");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "BONEMEAL");
						sender.sendMessage("1. " + ChatColor.RED + "Grow plants and trees in one click!");
						sender.sendMessage("2. " + ChatColor.RED + "Skeletons drop bonemeal.");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "SPIDEREYE");
						sender.sendMessage("1. " + ChatColor.RED
								+ "Craft spider eyes in a 2 by 2 square to make a fermented spider eye.");
						sender.sendMessage("2. "
								+ ChatColor.RED
								+ "Eating a spider eye takes away the poison effect, but a 10 second slow effect, and it adds one extra point of hunger.");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "usage: /mm <reload | help>");
						return false;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "usage: /mm <reload | help>");
					return false;
				}
			}
		}
		return false;
	}
}
