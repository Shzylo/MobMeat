package me.shzylo.mobmeat;

import me.shzylo.mobmeat.events.Bonemeal;
import me.shzylo.mobmeat.events.SpiderEye;
import me.shzylo.mobmeat.events.Sulphur;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/* MobMeat by Shzylo
 * PLUGIN TYPE:
 * - Fun
 * 
 * Plugin was made just for giggles :)
 */

public final class MobMeat extends JavaPlugin {
	private final String VERSION = "0.2";
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		registerEvents();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mobmeat")) {
			if (args.length == 0) {
				sender.sendMessage("===============[" + ChatColor.AQUA + "MobMeat" + ChatColor.WHITE + "]===============");
				sender.sendMessage(ChatColor.GOLD + "Plugin by: " + ChatColor.AQUA + "Shzylo");
				sender.sendMessage(ChatColor.GOLD + "Plugin version: " + ChatColor.AQUA + VERSION);
				sender.sendMessage("");
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "COMMANDS:");
				sender.sendMessage(ChatColor.GOLD + "/mm reload " + ChatColor.WHITE + "- Reloads MobMeat");
				sender.sendMessage(ChatColor.GOLD + "/mm help " + ChatColor.WHITE + "- How to use MobMeat");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "PERMISSIONS:");
				sender.sendMessage(ChatColor.GOLD + "mobmeat.reload " + ChatColor.WHITE + "- Allows you to reload MobMeat");
				sender.sendMessage(ChatColor.RED + "Default: Op");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "mobmeat.sulphur " + ChatColor.WHITE
						+ "- Allows you to use gunpowder as an explosive by right-clicking");
				sender.sendMessage(ChatColor.RED + "Default: True");
				return true;
			} else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					if (sender.hasPermission("mobmeat.reload")) {
						HandlerList.unregisterAll(this);
						this.reloadConfig();
						registerEvents();
						sender.sendMessage(ChatColor.AQUA + "MobMeat has been Reloaded!");
						return true;
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "No Permission!");
					}
				} else if (args[0].equalsIgnoreCase("help")) {
					if (args.length == 1) {
						sender.sendMessage("=============[" + ChatColor.AQUA + "MobMeat Help" + ChatColor.WHITE + "]=============");
						sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC
								+ "(NOTE: These are configurable, everything may not be enabled)");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.GREEN + "/mm" + ChatColor.WHITE + " help " + ChatColor.RED + "sulphur");
						sender.sendMessage(ChatColor.GREEN + "/mm" + ChatColor.WHITE + " help " + ChatColor.RED + "spidereye");
						sender.sendMessage(ChatColor.GREEN + "/mm" + ChatColor.WHITE + " help " + ChatColor.RED + "bonemeal");
						return true;
					}
					if (args[1].equalsIgnoreCase("sulphur") || args[1].equalsIgnoreCase("gunpowder")) {
						Sulphur sulphur = new Sulphur(this);
						sulphur.getInfo(sender);
						return true;

					} else if (args[1].equalsIgnoreCase("spidereye")) {
						SpiderEye spidereye = new SpiderEye(this);
						spidereye.getInfo(sender);
						return true;

					} else if (args[1].equalsIgnoreCase("bonemeal")) {
						Bonemeal bonemeal = new Bonemeal(this);
						bonemeal.getInfo(sender);
						return true;
					}
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "usage: /mm [reload | help]");
					return false;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "usage: /mm [reload | help]");
				return false;
			}
		}
		return false;
	}
	
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Sulphur(this), this);
		pm.registerEvents(new Bonemeal(this), this);
		pm.registerEvents(new SpiderEye(this), this);
	}
}
