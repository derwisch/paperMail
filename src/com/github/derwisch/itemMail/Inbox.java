package com.github.derwisch.itemMail;


import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Inbox {
	
	public static ArrayList<Inbox> Inboxes = new ArrayList<Inbox>();
	
	public static void SaveAll() {
		for (Inbox inbox : Inboxes) {
			inbox.SaveInbox();
			
		}
	}
	
	public static Inbox GetInbox(Player player) {
		for (Inbox inbox : Inboxes) {
			if (inbox.player.equals(player)) {
				return inbox;
			}
		}
		AddInbox(player);
		return GetInbox(player);
	}
	
	public static void AddInbox(Player player) {
		Inbox inbox = new Inbox(player);
		Inboxes.add(inbox);
	}
	
	public static void RemoveInbox(Player player) {
		for (Inbox inbox : Inboxes) {
			if (inbox.player.equals(player)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public Player player;
	public Inventory inventory;
	public Chest inboxChest;
	
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	
	public Inbox(Player player) {
		this.player = player;
		this.configAccessor = new ConfigAccessor(ItemMail.instance, "players\\" + player.getDisplayName() + ".yml");
		this.playerConfig = configAccessor.getConfig();
		this.inventory = Bukkit.createInventory(player, 36, ItemMail.INBOX_GUI_TITLE);
		configAccessor.saveConfig();
		
		loadChest();
		loadItems();
	}
	
	private void loadChest() {
		String worldName = playerConfig.getString("chest.world");
		worldName = (worldName != null) ? worldName : "";
		World world = Bukkit.getWorld(worldName);
		int x = playerConfig.getInt("chest.x");
		int y = playerConfig.getInt("chest.y");
		int z = playerConfig.getInt("chest.z");

		Block block = (world != null) ? world.getBlockAt(x, y, z) : null;
		
		inboxChest = (block != null && block.getType() == Material.CHEST) ? (Chest)block.getState() : null; 
	}

	private void saveChest() {
		if (inboxChest == null) {
			return;
		}
		
		String worldName = inboxChest.getLocation().getWorld().getName();
		int x = inboxChest.getLocation().getBlockX();
		int y = inboxChest.getLocation().getBlockY();
		int z = inboxChest.getLocation().getBlockZ();

		 playerConfig.set("chest.world", worldName);
		 playerConfig.set("chest.x", x);
		 playerConfig.set("chest.y", y);
		 playerConfig.set("chest.z", z);
	}
	
	private void loadItems() {
		int i = 0;
		ItemStack stack = null;
		do {
			stack = playerConfig.getItemStack("itemstack." + i);
			if (stack != null) {
				inventory.addItem(stack);
			}
			i++;
		} while (stack != null);
	}
	
	private void saveItems() {
		int i = 0;
		for (ItemStack stack : inventory.getContents()) {
			playerConfig.set("itemstack." + i, stack);
			i++;
		}
		
		configAccessor.saveConfig();
	}
	
	public void openInbox() {
		player.openInventory(inventory);
	}
	
	public void AddItem(ItemStack itemStack, Player sender) {
		if (inboxChest != null) {
			if (inboxChest.getInventory().addItem(itemStack).keySet().toArray().length > 0) {
				if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
					player.getInventory().addItem(itemStack);
				}
			}
		} else {
			if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
				player.getInventory().addItem(itemStack);
			}
		}
		
		saveItems();
	}
	
	public void AddItems(Collection<ItemStack> items, Player sender) {
		for (ItemStack itemStack : items) {
			AddItem(itemStack, sender);
		}
	}

	public void SaveInbox() {
		saveItems();
		saveChest();
	}
}
