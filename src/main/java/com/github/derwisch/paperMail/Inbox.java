package com.github.derwisch.paperMail;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.derwisch.paperMail.configs.ConfigAccessor;
import com.github.derwisch.paperMail.configs.Settings;

public class Inbox {
	
	public static List<Inbox> Inboxes = new ArrayList<Inbox>();
	
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
	public Collection<Block> inboxChests;
	
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	
	public Inbox(UUID uuid) {
		this.playerUUID = uuid;
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + uuid + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		this.inboxChests = new ArrayList<Block>();
		initMailBox();
		loadChest();
		loadItems();
	}
	
	private void initMailBox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		this.inventory = Bukkit.createInventory(player, 36, PaperMail.INBOX_GUI_TITLE);
	}
	
	private void loadChest() {
		for(String s : playerConfig.getKeys(false)){
			if(s.contains("chest")){
				String worldName = playerConfig.getString(s + ".world");
				worldName = (worldName != null) ? worldName : "";
				World world = Bukkit.getWorld(worldName);
				int x = playerConfig.getInt(s + ".x");
				int y = playerConfig.getInt(s + ".y");
				int z = playerConfig.getInt(s + ".z");

				Block block = (world != null) ? world.getBlockAt(x, y, z) : null;
				if(block != null && (block.getType() == Material.CHEST || block.getState() instanceof Skull))
				inboxChests.add(block);
			}			
		}
		
	}
	
	private void loadItems() {
		int i = 0;
		ItemStack stack = null;
		do {
			stack = playerConfig.getItemStack("itemstack." + i);
			if (stack != null && stack.getType() != Material.AIR) {
				inventory.addItem(stack);
			}
			i++;
		} while (stack != null);
	}

	private void saveChests() {
		if (inboxChests == null || inboxChests.isEmpty()) {
			return;
		}
		int i = 0;
		for(Block b : inboxChests){
			i++;
			String worldName = b.getLocation().getWorld().getName();
			int x = b.getLocation().getBlockX();
			int y = b.getLocation().getBlockY();
			int z = b.getLocation().getBlockZ();
			playerConfig.set("chest" + i + ".world", worldName);
			playerConfig.set("chest" + i + ".x", x);
			playerConfig.set("chest" + i + ".y", y);
			playerConfig.set("chest" + i + ".z", z);
		}
		configAccessor.saveConfig();
	}
	
	private void saveItems() {
		for (int i = 0; i < Settings.DefaultBoxRows * 9; i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack != null && stack.getType()!=Material.AIR) {
				playerConfig.set("itemstack." + i, stack);
			}
		}
		
		configAccessor.saveConfig();
	}
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		
		player.openInventory(inventory);
	}
	
	public void addChest(Block newChest) {
		inboxChests.add(newChest);
		saveChests();
	}
	
	public void AddItem(ItemStack itemStack) {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		for(Block inboxChest : inboxChests){
			if(inboxChest!=null){
				if (inboxChest.getState() instanceof Chest) {
					if (((Chest)inboxChest).getInventory().addItem(itemStack).keySet().toArray().length > 0) {
						if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
							player.getInventory().addItem(itemStack);
							return;
						}
					}
				} else{
					if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
						player.getInventory().addItem(itemStack);
						return;
					}
				}
			}		
		}		
		saveItems();
	}
	
	public void AddItems(Collection<ItemStack> items) {
		for (ItemStack itemStack : items) {
			AddItem(itemStack);
		}
	}

	public void SaveInbox() {
		saveItems();
		saveChests();
	}
}
