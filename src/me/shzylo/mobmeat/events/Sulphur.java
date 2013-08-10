package me.shzylo.mobmeat.events;

import java.util.ArrayList;

import me.shzylo.mobmeat.MobMeat;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/* =====
 * Sulphur Features:
 * - Explosive.
 * - Does not blow up minerals.
 * - Limited custom explosion size
 * - Deals damage to mobs (optional)
 * - Can set fire to mobs (optional)
 * =====
 */

public class Sulphur implements Listener {
	MobMeat plugin;

	int volume = 2, dmg, explosionSize, miningAmount, miningDelay, attackAmount;

	boolean enabled, damageAnimals, damageMonsters, damagePlayers, igniteAnimals, igniteMonsters, ignitePlayers;
	boolean usable = false;

	ArrayList<Player> cooldown = new ArrayList<Player>();

	public Sulphur(MobMeat instance) {
		this.plugin = instance;
		enabled = plugin.getConfig().getBoolean("sulphur-explosive", true);

		damageAnimals = plugin.getConfig().getBoolean("damage-animals", false);
		damageMonsters = plugin.getConfig().getBoolean("damage-monsters", true);
		damagePlayers = plugin.getConfig().getBoolean("damage-players", true);

		igniteAnimals = plugin.getConfig().getBoolean("ignite-animals", false);
		igniteMonsters = plugin.getConfig().getBoolean("ignite-monsters", false);
		ignitePlayers = plugin.getConfig().getBoolean("ignite-players", false);

		explosionSize = plugin.getConfig().getInt("explosion-size", 1);
		miningAmount = plugin.getConfig().getInt("mining-amount", 1);
		miningDelay = plugin.getConfig().getInt("mining-delay", 1);
		attackAmount = plugin.getConfig().getInt("attack-amount", 1);
		dmg = plugin.getConfig().getInt("sulphur-damage", 3);
	}

	public void getInfo(CommandSender s) {
		if (enabled) {
			s.sendMessage("");
			s.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "SULPHUR");

			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Right-click a block to create a mini explosion.");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Will not destroy minerals");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Gives you every block you destroy.");
			if (damageAnimals && !igniteAnimals)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to animals.");
			else if (damageAnimals && igniteAnimals)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to animals and sets them ablaze.");
			if (damageMonsters && !igniteMonsters)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to monsters.");
			else if (damageMonsters && igniteMonsters)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to monsters and sets them ablaze.");
			if (damagePlayers && !ignitePlayers)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to players");
			else if (damagePlayers && ignitePlayers)
				s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Deals damage to players and sets them ablaze.");
		} else
			s.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "This Feature is currently off");
	}

	/**
	 * Sulphur Blocks
	 */

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Action act = e.getAction();
		final Player p = e.getPlayer();
		Material hand = p.getItemInHand().getType();
		World w = p.getWorld();

		if (enabled) {
			if (hand.equals(Material.SULPHUR)) {
				if (act.equals(Action.RIGHT_CLICK_BLOCK)) {
					if (b != null) {
						Block bTarget = p.getTargetBlock(null, 5);
						Block bAbove = bTarget.getRelative(BlockFace.UP);
						Block bNorth = bTarget.getRelative(BlockFace.NORTH);
						Block bSouth = bTarget.getRelative(BlockFace.SOUTH);
						Block bEast = bTarget.getRelative(BlockFace.EAST);
						Block bWest = bTarget.getRelative(BlockFace.WEST);
						Block bDown = bTarget.getRelative(BlockFace.DOWN);

						Block bSEast = bTarget.getRelative(BlockFace.SOUTH_EAST);
						Block bSWest = bTarget.getRelative(BlockFace.SOUTH_WEST);
						Block bNEast = bTarget.getRelative(BlockFace.NORTH_EAST);
						Block bNWest = bTarget.getRelative(BlockFace.NORTH_WEST);

						Block bUpNorth = bTarget.getRelative(0, 1, 1);
						Block bUpSouth = bTarget.getRelative(0, 1, -1);
						Block bUpEast = bTarget.getRelative(-1, 1, 0);
						Block bUpWest = bTarget.getRelative(1, 1, 0);
						Block bUpNEast = bTarget.getRelative(1, 1, 1);
						Block bUpNWest = bTarget.getRelative(-1, 1, 1);
						Block bUpSEast = bTarget.getRelative(1, 1, -1);
						Block bUpSWest = bTarget.getRelative(-1, 1, -1);
						Block bDownNorth = bTarget.getRelative(0, -1, 1);
						Block bDownSouth = bTarget.getRelative(0, -1, -1);
						Block bDownEast = bTarget.getRelative(-1, -1, 0);
						Block bDownWest = bTarget.getRelative(1, -1, 0);
						Block bDownNEast = bTarget.getRelative(1, -1, 1);
						Block bDownNWest = bTarget.getRelative(-1, -1, 1);
						Block bDownSEast = bTarget.getRelative(1, -1, -1);
						Block bDownSWest = bTarget.getRelative(-1, -1, -1);

						if (p.hasPermission("mobmeat.sulphur")) {
							if (!p.getGameMode().equals(GameMode.CREATIVE)) {
								if (!this.cooldown.contains(p)) {
									if (p.getItemInHand().getAmount() >= miningAmount) {
										if (p.getItemInHand().getAmount() > miningAmount) {
											p.getItemInHand().setAmount(p.getItemInHand().getAmount() - miningAmount);
										} else if (p.getItemInHand().getAmount() == miningAmount) {
											p.setItemInHand(null);
										}
										usable = true;
										p.sendMessage(ChatColor.RED + "You are now on cooldown for " + ChatColor.GOLD + dmg + ChatColor.RED
												+ " seconds!");
										this.cooldown.add(p);
										plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
											public void run() {
												Sulphur.this.cooldown.remove(p);
												p.sendMessage(ChatColor.BLUE + "You can now use sulphur to mine again.");
											}
										}, 100L);
									}
								}
							} else
								usable = true;

							if (usable) {
								if (explosionSize == 0) {
									getBlock(e, bTarget, p, w);
								} else if (explosionSize > 0) {
									getBlock(e, bTarget, p, w);
									getBlock(e, bAbove, p, w);
									getBlock(e, bNorth, p, w);
									getBlock(e, bSouth, p, w);
									getBlock(e, bEast, p, w);
									getBlock(e, bWest, p, w);
									getBlock(e, bDown, p, w);

									if (explosionSize > 1) {
										getBlock(e, bSEast, p, w);
										getBlock(e, bSWest, p, w);
										getBlock(e, bNEast, p, w);
										getBlock(e, bNWest, p, w);

										if (explosionSize > 2) {
											getBlock(e, bUpNorth, p, w);
											getBlock(e, bUpSouth, p, w);
											getBlock(e, bUpEast, p, w);
											getBlock(e, bUpWest, p, w);
											getBlock(e, bUpNEast, p, w);
											getBlock(e, bUpNWest, p, w);
											getBlock(e, bUpSEast, p, w);
											getBlock(e, bUpSWest, p, w);
											getBlock(e, bDownNorth, p, w);
											getBlock(e, bDownSouth, p, w);
											getBlock(e, bDownEast, p, w);
											getBlock(e, bDownWest, p, w);
											getBlock(e, bDownNEast, p, w);
											getBlock(e, bDownNWest, p, w);
											getBlock(e, bDownSEast, p, w);
											getBlock(e, bDownSWest, p, w);
										}
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "--- ERROR ---");
									p.sendMessage(ChatColor.BLUE
											+ "There is an error with SULPHUR_EXLOSIVE. It appears that the explosion size "
											+ "is not a valid size. Please contact an administrator to fix this error.");
								}
							} else if (p.getItemInHand().getAmount() > miningAmount && this.cooldown.contains(p)) {
								p.sendMessage(ChatColor.RED + "Please wait your " + ChatColor.GOLD + dmg + ChatColor.RED + " seconds!");
							} else {
								p.sendMessage(ChatColor.RED + "You need " + ChatColor.GOLD + miningAmount + ChatColor.RED
										+ " sulphur to use the mining tool!");
							}
						}
					}
				}
			}
		}
		usable = false;
	}

	private void getBlock(PlayerInteractEvent e, Block b, Player p, World w) {
		if (!(b.getType().name().equals("COAL_ORE") || b.getType().name().equals("IRON_ORE")
				|| b.getType().name().equals("GOLD_ORE") || b.getType().name().equals("REDSTONE_ORE")
				|| b.getType().name().equals("GLOWING_REDSTONE_ORE") || b.getType().name().equals("DIAMOND_ORE")
				|| b.getType().name().equals("EMERALD_ORE") || b.getType().name().equals("QUARTZ_ORE")
				|| b.getType().name().equals("OBSIDIAN") || b.getType().name().equals("BEDROCK") || b.getType().name().equals("LAVA")
				|| b.getType().name().equals("STATIONARY_LAVA") || b.getType().name().equals("WATER")
				|| b.getType().name().equals("STATIONARY_WATER") || b.getType().name().equals("CHEST")
				|| b.getType().name().equals("TRAPPED_CHEST") || b.getType().name().equals("ENCHANTMENT_TABLE"))) {
			b.breakNaturally();
			playSoundAndEffect(b, w);
		}
	}

	private void playSoundAndEffect(Block b, World w) {
		w.playSound(b.getLocation(), Sound.EXPLODE, volume, 2);
		w.playEffect(b.getLocation(), Effect.SMOKE, 1);
	}

	/**
	 * Sulphur Mobs
	 */
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		if (enabled) {
			Player p = e.getPlayer();
			World w = p.getWorld();

			if (p.getItemInHand().getType().equals(Material.SULPHUR)) {
				if (damageAnimals) {
					if (e.getRightClicked() instanceof Cow) {
						Cow cow = (Cow) e.getRightClicked();
						onDamageEntity(cow, p, dmg, w);

					} else if (e.getRightClicked() instanceof Chicken) {
						Chicken chicken = (Chicken) e.getRightClicked();
						onDamageEntity(chicken, p, dmg, w);

					} else if (e.getRightClicked() instanceof Pig) {
						Pig pig = (Pig) e.getRightClicked();
						onDamageEntity(pig, p, dmg, w);

					} else if (e.getRightClicked() instanceof Sheep) {
						Sheep sheep = (Sheep) e.getRightClicked();
						onDamageEntity(sheep, p, dmg, w);

					} else if (e.getRightClicked() instanceof Horse) {
						Horse horse = (Horse) e.getRightClicked();
						onDamageEntity(horse, p, dmg, w);

					} else if (e.getRightClicked() instanceof Ocelot) {
						Ocelot ocelot = (Ocelot) e.getRightClicked();
						onDamageEntity(ocelot, p, dmg, w);

					} else if (e.getRightClicked() instanceof MushroomCow) {
						MushroomCow mooshroom = (MushroomCow) e.getRightClicked();
						onDamageEntity(mooshroom, p, dmg, w);

					} else if (e.getRightClicked() instanceof Wolf) {
						Wolf wolf = (Wolf) e.getRightClicked();
						onDamageEntity(wolf, p, dmg, w);

					} else if (e.getRightClicked() instanceof Squid) {
						Squid squid = (Squid) e.getRightClicked();
						onDamageEntity(squid, p, dmg, w);
					}

				}

				if (damageMonsters) {
					if (e.getRightClicked() instanceof Creeper) {
						Creeper creeper = (Creeper) e.getRightClicked();
						onDamageEntity(creeper, p, dmg, w);

					} else if (e.getRightClicked() instanceof Skeleton) {
						Skeleton skeleton = (Skeleton) e.getRightClicked();
						onDamageEntity(skeleton, p, dmg, w);

					} else if (e.getRightClicked() instanceof Spider) {
						Spider spider = (Spider) e.getRightClicked();
						onDamageEntity(spider, p, dmg, w);

					} else if (e.getRightClicked() instanceof CaveSpider) {
						CaveSpider cavespider = (CaveSpider) e.getRightClicked();
						onDamageEntity(cavespider, p, dmg, w);

					} else if (e.getRightClicked() instanceof Zombie) {
						Zombie zombie = (Zombie) e.getRightClicked();
						onDamageEntity(zombie, p, dmg, w);

					} else if (e.getRightClicked() instanceof Slime) {
						Slime slime = (Slime) e.getRightClicked();
						onDamageEntity(slime, p, dmg, w);

					} else if (e.getRightClicked() instanceof Ghast) {
						Ghast ghast = (Ghast) e.getRightClicked();
						onDamageEntity(ghast, p, dmg, w);

					} else if (e.getRightClicked() instanceof PigZombie) {
						PigZombie pigzombie = (PigZombie) e.getRightClicked();
						onDamageEntity(pigzombie, p, dmg, w);

					} else if (e.getRightClicked() instanceof Enderman) {
						Enderman enderman = (Enderman) e.getRightClicked();
						onDamageEntity(enderman, p, dmg, w);

					} else if (e.getRightClicked() instanceof Silverfish) {
						Silverfish silverfish = (Silverfish) e.getRightClicked();
						onDamageEntity(silverfish, p, dmg, w);

					} else if (e.getRightClicked() instanceof Blaze) {
						Blaze blaze = (Blaze) e.getRightClicked();
						onDamageEntity(blaze, p, dmg, w);

					} else if (e.getRightClicked() instanceof MagmaCube) {
						MagmaCube magmacube = (MagmaCube) e.getRightClicked();
						onDamageEntity(magmacube, p, dmg, w);

					} else if (e.getRightClicked() instanceof Bat) {
						Bat bat = (Bat) e.getRightClicked();
						onDamageEntity(bat, p, dmg, w);

					} else if (e.getRightClicked() instanceof Witch) {
						Witch witch = (Witch) e.getRightClicked();
						onDamageEntity(witch, p, dmg, w);
					}
				}

				if (damagePlayers) {
					if (e.getRightClicked() instanceof Player) {
							Player pl = (Player) e.getRightClicked();
							if(p.getWorld().getPVP() && pl.getWorld().getPVP()) {
							if (pl.getGameMode() != GameMode.CREATIVE)
								onDamageEntity(pl, p, dmg, w);
						}
					}
				}
			}
		}
	}

	private void onDamageEntity(LivingEntity e, Player p, int dmg, World w) {
		if (e instanceof LivingEntity) {
			((LivingEntity) e).damage(dmg, p);
			getSetFire(e);
			playSoundAndEffect(e, w);
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				if (p.getItemInHand().getAmount() > attackAmount) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - attackAmount);
				} else if (p.getItemInHand().getAmount() == attackAmount) {
					p.setItemInHand(null);
				}
			}
		}
	}

	private void getSetFire(LivingEntity e) {
		if (igniteAnimals) {
			if (e instanceof Cow || e instanceof Pig || e instanceof Chicken || e instanceof Sheep || e instanceof Horse
					|| e instanceof Ocelot || e instanceof MushroomCow || e instanceof Wolf || e instanceof Squid)
				((LivingEntity) e).setFireTicks(100);
		}
		if (igniteMonsters) {
			if (e instanceof Creeper || e instanceof Skeleton || e instanceof Spider || e instanceof CaveSpider
					|| e instanceof Zombie || e instanceof Slime || e instanceof Blaze || e instanceof Ghast || e instanceof PigZombie
					|| e instanceof MagmaCube || e instanceof Witch || e instanceof Bat || e instanceof Enderman
					|| e instanceof Silverfish)
				((LivingEntity) e).setFireTicks(100);
		}
		if (ignitePlayers) {
			if (e instanceof Player)
				((LivingEntity) e).setFireTicks(100);
		}
	}

	private void playSoundAndEffect(LivingEntity e, World w) {
		w.playSound(e.getLocation(), Sound.EXPLODE, volume, 2);
		w.playEffect(e.getLocation(), Effect.SMOKE, 1);
	}
}
