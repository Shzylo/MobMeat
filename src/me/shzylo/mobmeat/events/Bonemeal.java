package me.shzylo.mobmeat.events;

import java.util.Random;

import me.shzylo.mobmeat.MobMeat;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.Dispenser;
import org.bukkit.material.Tree;

/* =====
 * Bonemeal Features:
 * - Dropped by Skeleton
 * - 1-click to grow a plant / tree again!
 * =====
 */

public class Bonemeal implements Listener {

	MobMeat plugin;

	boolean enabledGrowth, skeletonDrops;

	public Bonemeal(MobMeat instance) {
		this.plugin = instance;
		enabledGrowth = plugin.getConfig().getBoolean("bonemeal-one-growth", true);
		skeletonDrops = plugin.getConfig().getBoolean("drop-bonemeal", true);
	}
	
	public void getInfo(CommandSender s) {
		if(enabledGrowth) {
			s.sendMessage("");
			s.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "BONEMEAL");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Grow plants and trees in one click again! (Along with Dispensers)");
			s.sendMessage("- " + ChatColor.LIGHT_PURPLE + "Skeletons drop bonemeal!");
		} else
			s.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "This Feature is currently off");
	}

	@EventHandler
	public void onSkeletonDeath(EntityDeathEvent event) {
		int dropRate = plugin.getConfig().getInt("bonemeal-drop-chance", 3);

		Entity skeleton = event.getEntity();
		boolean isSkeleton = skeleton instanceof Skeleton;
		World skeletonDeathWorld = skeleton.getWorld();
		Location skeletonLocation = skeleton.getLocation();

		if (skeletonDrops) {
			Random r = new Random();

			int drop = r.nextInt(dropRate);
			int maxDrop = 1 + r.nextInt(3);

			ItemStack dropBonemeal = new ItemStack(Material.INK_SACK, maxDrop, (short) 15);

			if (isSkeleton) {
				if (drop == 0)
					skeletonDeathWorld.dropItem(skeletonLocation, dropBonemeal);
			}
		}
	}

	/**
	 * Upcoming code by: tills13 ~ Thank you so much!
	 */

	@EventHandler
	public void onRightClickCrops(PlayerInteractEvent e) {
		ItemStack i = e.getItem();

		if (skeletonDrops) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (i != null) {
					byte bonemeal = e.getItem().getData().getData();
					if (bonemeal == 15) {
						boolean used = false;
						Block clickedBlock = e.getClickedBlock();
						if (clickedBlock.getType().equals(Material.CROPS) || clickedBlock.getType().equals(Material.POTATO)
								|| clickedBlock.getType().equals(Material.CARROT)) {
							growCrop(new Crops(clickedBlock.getTypeId(), clickedBlock.getData()), clickedBlock);
							used = true;
						} else if (e.getClickedBlock().getType().equals(Material.SAPLING)) {
							Tree tree = new Tree(clickedBlock.getTypeId(), clickedBlock.getData());
							TreeSpecies t = tree.getSpecies();

							TreeType type = null;
							if (t.equals(TreeSpecies.BIRCH)) {
								type = TreeType.BIRCH;
							} else if (t.equals(TreeSpecies.JUNGLE)) {
								used = generateJungleTree(clickedBlock);
							} else if (t.equals(TreeSpecies.REDWOOD)) {
								type = TreeType.REDWOOD;
							} else {
								if (Math.random() > 0.75) {
									type = TreeType.BIG_TREE;
								} else {
									type = TreeType.TREE;
								}
							}

							if (!t.equals(TreeSpecies.JUNGLE)) {
								clickedBlock.setType(Material.AIR);
								if (!clickedBlock.getWorld().generateTree(clickedBlock.getLocation(), type)) {
									clickedBlock.setType(Material.SAPLING);
								} else {
									used = true;
								}
							}
						}

						if (used == true) {
							if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
								if (e.getItem().getAmount() == 1) {
									e.getPlayer().setItemInHand(null);
								} else {
									e.getItem().setAmount(e.getItem().getAmount() - 1);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockDispenseEvent(BlockDispenseEvent e) {
		Block block = e.getBlock();
		Dispenser disp = (Dispenser) block.getState().getData();
		BlockFace face = disp.getFacing();
		Block relative = block.getRelative(face);
		ItemStack item = e.getItem();

		if (enabledGrowth) {

			if (item.getData().getData() == 15) {
				if (relative.getType().equals(Material.CROPS) || relative.getType().equals(Material.POTATO)
						|| relative.getType().equals(Material.CARROT)) {
					growCrop(new Crops(relative.getTypeId(), relative.getData()), relative);
				} else if (relative.getType().equals(Material.SAPLING)) {
					Tree tree = new Tree(relative.getTypeId(), relative.getData());
					TreeSpecies t = tree.getSpecies();

					TreeType type = null;
					if (t.equals(TreeSpecies.BIRCH)) {
						type = TreeType.BIRCH;
					} else if (t.equals(TreeSpecies.JUNGLE)) {
						generateJungleTree(relative);
					} else if (t.equals(TreeSpecies.REDWOOD)) {
						type = TreeType.REDWOOD;
					} else {
						if (Math.random() > 0.5) {
							type = TreeType.BIG_TREE;
						} else {
							type = TreeType.TREE;
						}
					}

					if (!t.equals(TreeSpecies.JUNGLE)) {
						relative.setType(Material.AIR);
						if (!relative.getWorld().generateTree(relative.getLocation(), type)) {
							relative.setType(Material.SAPLING);
						}
					}
				}
			}
		}
	}

	public boolean growCrop(Crops crop, Block block) {
		if (crop.getState() != CropState.RIPE) {
			crop.setState(CropState.RIPE);
			block.setData(crop.getData());
		}

		return false;
	}

	public boolean generateJungleTree(Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		World world = block.getWorld();
		boolean pass = false;

		Block block1, block2, block3;
		block1 = world.getBlockAt(x + 1, y, z);
		block2 = world.getBlockAt(x + 1, y, z + 1);
		block3 = world.getBlockAt(x, y, z + 1);

		if ((block1.getData() == block2.getData()) && (block2.getData() == block3.getData())
				&& (block1.getData() == block3.getData())) {
			if (block1.getData() == block.getData()) {
				pass = true;
			}
		}

		if (!pass) {
			block1 = world.getBlockAt(x, y, z + 1);
			block2 = world.getBlockAt(x - 1, y, z + 1);
			block3 = world.getBlockAt(x - 1, y, z);

			if ((block1.getData() == block2.getData()) && (block2.getData() == block3.getData())
					&& (block1.getData() == block3.getData())) {
				if (block1.getData() == block.getData()) {
					pass = true;
				}
			}
		}

		if (!pass) {
			block1 = world.getBlockAt(x - 1, y, z);
			block2 = world.getBlockAt(x - 1, y, z - 1);
			block3 = world.getBlockAt(x, y, z - 1);

			if ((block1.getData() == block2.getData()) && (block2.getData() == block3.getData())
					&& (block1.getData() == block3.getData())) {
				if (block1.getData() == block.getData()) {
					pass = true;
				}
			}
		}

		if (!pass) {
			block1 = world.getBlockAt(x, y, z - 1);
			block2 = world.getBlockAt(x + 1, y, z - 1);
			block3 = world.getBlockAt(x + 1, y, z);

			if ((block1.getData() == block2.getData()) && (block2.getData() == block3.getData())
					&& (block1.getData() == block3.getData())) {
				if (block1.getData() == block.getData()) {
					pass = true;
				}
			}
		}

		TreeType type;
		if (pass) {
			block1.setType(Material.AIR);
			block2.setType(Material.AIR);
			block3.setType(Material.AIR);
			block.setType(Material.AIR);
			type = TreeType.JUNGLE;
		} else {
			block.setType(Material.AIR);
			type = TreeType.SMALL_JUNGLE;
		}

		if (!block.getWorld().generateTree(block.getLocation(), type)) {
			block.setType(Material.SAPLING);
			if (pass) {
				block1.setType(Material.SAPLING);
				block2.setType(Material.SAPLING);
				block3.setType(Material.SAPLING);
			}
			return false;
		} else {
			return true;
		}
	}
}
