package me.shzylo.mobmeat.events;

import me.shzylo.mobmeat.MobMeat;

import org.bukkit.Material;
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
 * TODO:
 * - Add Lore.
 */

public class SpiderEye implements Listener {

  MobMeat plugin;

	public SpiderEye(MobMeat instance) {
		this.plugin = instance;
		boolean enableCraft = false;

		if (plugin.getConfig().getBoolean("craft-spidereye", true) == true)
			enableCraft = true;

		if (enableCraft) {
			ShapedRecipe fermentedEye = new ShapedRecipe(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1))
					.shape("ee", "ee").setIngredient('e', Material.SPIDER_EYE);
			plugin.getServer().addRecipe(fermentedEye);
		}
	}

	@EventHandler
	public void eatSpiderEye(PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();

		boolean enabled = false;
		if (plugin.getConfig().getBoolean("new-eat-effect", true) == true)
			enabled = true;

		if (enabled) {
			if (p.getItemInHand().getType().equals(Material.SPIDER_EYE)) {
				if (p.getFoodLevel() < 20) {
					p.setFoodLevel(p.getFoodLevel() + 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 3));

					if (p.getHealth() < 20) {
						int health = p.getHealth();
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
