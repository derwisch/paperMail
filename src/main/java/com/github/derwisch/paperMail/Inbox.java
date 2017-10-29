package com.github.derwisch.paperMail;


import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.derwisch.paperMail.configs.ConfigAccessor;
import com.github.derwisch.paperMail.configs.Settings;

public class Inbox {
	
	public static ArrayList<Inbox> Inboxes = new ArrayList<Inbox>();
	
	public static void SaveAll() {
		for (Inbox inbox : Inboxes) {
			inbox.SaveInbox();
		}
	}
	
	public static Inbox GetInbox(UUID uuid) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(uuid)) {
				return inbox;
			}
		}
		AddInbox(uuid);
		return GetInbox(uuid);
	}
	
	public static void AddInbox(UUID uuid) {
		if (!Settings.InboxPlayers.contains(uuid)) {
			Settings.InboxPlayers.add(uuid);
		}
		Inbox inbox = new Inbox(uuid);
		Inboxes.add(inbox);
	}
	
	public static void RemoveInbox(String playerUUID) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(playerUUID)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public UUID playerUUID;
	public Inventory inventory;
	public Chest inboxChest;
	
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	
	public Inbox(UUID uuid) {
		this.playerUUID = uuid;
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + uuid + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		
		initMailBox();
		loadChest();
		loadItems();
	}
	
	private void initMailBox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		this.inventory = Bukkit.createInventory(player, 36, PaperMail.INBOX_GUI_TITLE);
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

		 configAccessor.saveConfig();
	}
	
	private void saveItems() {
		for (int i = 0; i < Settings.DefaultBoxRows * 9; i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack != null) {
				playerConfig.set("itemstack." + i, stack);
			}
		}
		
		configAccessor.saveConfig();
	}
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		
		player.openInventory(inventory);
	}
	
	public void SetChest(Chest newChest) {
		inboxChest = newChest;
		saveChest();
	}
	
	public void AddItem(ItemStack itemStack, Player sender) {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
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
