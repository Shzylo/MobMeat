package me.shzylo.mobmeat.events;

import me.shzylo.mobmeat.MobMeat;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/* =====
 * Sulphur Features:
 * - Explosive.
 * - Requires different amounts of sulphur,
 * depending on how strong the block is.
 * =====
 * TODO:
 * - Add Lore.
 * - Possibly have a custom explosion size?
 * - Not blow up minerals.
 */

public class Sulphur implements Listener {

  MobMeat plugin;

	public Sulphur(MobMeat instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void SulphurExplosive(final PlayerInteractEvent e) {
		Action act = e.getAction();
		final Player p = e.getPlayer();
		Block b = e.getClickedBlock();

		if (b != null) {
			Material hand = p.getItemInHand().getType();
			Material clicked = b.getType();
			World world = p.getWorld();

			boolean enabled = false;

			if (plugin.getConfig().getBoolean("sulphur-explosive") == true)
				enabled = true;

			if (enabled) {
				if (p.hasPermission("mobmeat.sulphur")) {
					if (hand.equals(Material.SULPHUR)) {
						if (act == Action.RIGHT_CLICK_BLOCK) {
							if (clicked.equals(Material.GRASS) || clicked.equals(Material.DIRT)
									|| clicked.equals(Material.SANDSTONE) || clicked.equals(Material.SAND)
									|| clicked.equals(Material.GRAVEL) || clicked.equals(Material.CLAY)
									|| clicked.equals(Material.NETHERRACK) || clicked.equals(Material.SOUL_SAND)
									|| clicked.equals(Material.GLASS)) {
								if (p.getGameMode() != GameMode.CREATIVE) {
									p.getInventory().removeItem(new ItemStack(Material.SULPHUR, 1));
								}
								world.createExplosion(b.getLocation(), 1f);
							}

							if (clicked.equals(Material.WOOD) || clicked.equals(Material.LOG)
									|| clicked.equals(Material.QUARTZ_ORE)) {
								if (p.getGameMode() != GameMode.CREATIVE) {
									if (p.getItemInHand().getAmount() >= 3) {
										p.getInventory().removeItem(new ItemStack(Material.SULPHUR, 3));
										world.createExplosion(b.getLocation(), 3f);
									} else {
										p.sendMessage(ChatColor.RED + "Requires 3 Sulphur to blow up " + ChatColor.AQUA
												+ clicked.toString());
									}
								} else {
									world.createExplosion(b.getLocation(), 3f);
								}
							}

							if (clicked.equals(Material.STONE) || clicked.equals(Material.COBBLESTONE)
									|| clicked.equals(Material.MOSSY_COBBLESTONE) || clicked.equals(Material.NETHER_BRICK)) {
								if (p.getGameMode() != GameMode.CREATIVE) {
									if (p.getItemInHand().getAmount() >= 4) {
										p.getInventory().removeItem(new ItemStack(Material.SULPHUR, 4));
										world.createExplosion(b.getLocation(), 4f);
									} else {
										p.sendMessage(ChatColor.RED + "Requires 4 Sulphur to blow up " + ChatColor.AQUA
												+ clicked.toString());
									}
								} else {
									world.createExplosion(b.getLocation(), 4f);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void removeDamage(EntityDamageEvent ede) {
		Entity entity = ede.getEntity();

		if (entity instanceof Player) {
			if (((HumanEntity) entity).getItemInHand().getType() == Material.SULPHUR) {
				if (Action.RIGHT_CLICK_BLOCK != null) {
					if (ede.getCause() == DamageCause.BLOCK_EXPLOSION) {
						ede.setCancelled(true);
					}
				}
			}
		}
	}
}
