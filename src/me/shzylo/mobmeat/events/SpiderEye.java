package me.shzylo.mobmeat.events;

import me.shzylo.mobmeat.MobMeat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/* =====
 * SpiderEye Features:
 * - Craft in 2x2 square to make fermented.
 * - Creates slow effect when ate.
 * =====
 */

public class SpiderEye implements Listener {

	MobMeat plugin;

	private boolean newEffectEnabled, newCraftRecipe;

	public SpiderEye(MobMeat instance) {
		this.plugin = instance;

		newCraftRecipe = plugin.getConfig().getBoolean("craft-spidereye", true);
		newEffectEnabled = plugin.getConfig().getBoolean("new-eat-effect", true);

		if (newCraftRecipe) {
			ShapedRecipe fermentedEye = new ShapedRecipe(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1)).shape("ee", "ee")
					.setIngredient('e', Material.SPIDER_EYE);
			plugin.getServer().addRecipe(fermentedEye);
		}
	}

	public void getInfo(CommandSender s) {
		if (newEffectEnabled) {
			s.sendMessage("");
			s.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "SPIDEREYE");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Craft spider eyes in a 2 by 2 square to make a "
					+ "fermented spider eye.");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE
					+ "Eating a spider eye takes away the poison effect, but a 10 second slow effect and 5 second confusion, and "
					+ "it adds one extra point of hunger.");
		} else
			s.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "This Feature is currently off");
	}

	@EventHandler
	public void eatSpiderEye(PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();

		if (newEffectEnabled) {
			if (p.getItemInHand().getType().equals(Material.SPIDER_EYE)) {
				if (p.getFoodLevel() < 20) {
					p.setFoodLevel(p.getFoodLevel() + 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 3));
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 3));

					if (p.getHealth() < 20) {
						double health = p.getHealth();
						p.setHealth(health + 1);
					}

					plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

						@Override
						public void run() {
							p.removePotionEffect(PotionEffectType.POISON);
						}
					});

					if (p.hasPotionEffect(PotionEffectType.SLOW)) {
						p.removePotionEffect(PotionEffectType.SLOW);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 3));
					}
				}
			}
		}
	}
}
