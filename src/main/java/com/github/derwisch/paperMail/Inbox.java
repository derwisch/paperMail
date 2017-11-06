package com.github.derwisch.paperMail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.derwisch.paperMail.configs.ConfigAccessor;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.utils.Utils;

public class Inbox {
	
	public static Set<Inbox> Inboxes = new HashSet<Inbox>();
	
	public static HashMap<UUID, Set<Block>> mailBoxes = new HashMap<UUID, Set<Block>>();
	
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
		return AddInbox(uuid);
	}
	
	public static Inbox GetInbox(OfflinePlayer player) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(player.getUniqueId())) {
				return inbox;
			}
		}
		AddInbox(player.getUniqueId());
		return GetInbox(player.getUniqueId());
	}
	
	public static Inbox AddInbox(UUID uuid) {
		if (!Settings.InboxPlayers.contains(uuid)) {
			Settings.InboxPlayers.add(uuid);
		}
		Inbox inbox = new Inbox(uuid);
		Inboxes.add(inbox);
		return inbox;
	}
	
	public static void RemoveInbox(String playerUUID) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(playerUUID)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public static Inbox getInboxfromLocation(Location loc){
		if(hasInboxAtLocation(loc)){
			for(UUID id : mailBoxes.keySet()){
				Set<Block> mailBox = mailBoxes.get(id);
				for(Block b : mailBox){
					if(Utils.areLocationsEqual(loc, b.getLocation())){
						return GetInbox(id);
					}
				}
			}
		}	
		return null;
	}
	
	public static boolean hasInboxAtLocation(Location loc){
		for(Set<Block> mailBox : mailBoxes.values()){
			for(Block b : mailBox){
				if(Utils.areLocationsEqual(loc, b.getLocation())){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean hasInboxChestAtLocation(Player player, Location loc){
		if(hasInboxAtLocation(loc)){
			if(mailBoxes.containsKey(player.getUniqueId())){
				for(Block b : mailBoxes.get(player.getUniqueId())){
					if(Utils.areLocationsEqual(loc, b.getLocation())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	
	public UUID playerUUID;
	public Inventory inventory;
	public Set<Block> inboxChests;
	
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	
	public Inbox(UUID uuid) {
		this.playerUUID = uuid;
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + uuid + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		this.inboxChests = new HashSet<Block>();
		initMailBox();
		loadChest();
		loadItems();
		Inbox.Inboxes.add(this);
	}
	
	private void initMailBox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		this.inventory = Bukkit.createInventory(player, 36, PaperMail.INBOX_GUI_TITLE);
	}
	
	//change to for loop for ints
	private void loadChest() {
		ConfigurationSection chests = playerConfig.getConfigurationSection("chest");
		if(chests!=null){
			for(int i = 0; i < chests.getKeys(false).size(); i++){
				ConfigurationSection section = playerConfig.getConfigurationSection("chest" + "." + i);
				if(section!=null){
					String worldName = playerConfig.getString(section.getCurrentPath() + ".world");
					Bukkit.getLogger().info("[PaperMail] WorldSection: " + section.getCurrentPath().toString() + ".world");
					Bukkit.getLogger().info("[PaperMail] World: " + worldName);
					if(worldName!=null && worldName != ""){
						World world = Bukkit.getWorld(worldName);
						if(world!=null){
							int x = playerConfig.getInt(section.getCurrentPath() + ".x");
							int y = playerConfig.getInt(section.getCurrentPath() + ".y");
							int z = playerConfig.getInt(section.getCurrentPath() + ".z");
				            if(world.getBlockAt(x, y, z)!=null){
				            	Block block = world.getBlockAt(x, y, z);
				    			if(block != null){
				    				Bukkit.getLogger().info("[PaperMail]Checking for Right type --> " + block.getType().toString());
				    				if(block.getType() == Material.CHEST || block.getType() == Material.SKULL){
				   						inboxChests.add(block);
			    						Bukkit.getLogger().info("[PaperMail]Loading " + block.getType().toString() + " for " + playerUUID);
			    					}
			    				}
				    		}
						}				
					}
				}							 		
			}
		}else{
			Bukkit.getLogger().info("[PaperMail]: section = null");
		}
		if(inboxChests!=null){
			mailBoxes.put(playerUUID, inboxChests);
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
			String worldName = b.getLocation().getWorld().getName();
			int x = b.getLocation().getBlockX();
			int y = b.getLocation().getBlockY();
			int z = b.getLocation().getBlockZ();
			String type = b.getType().toString();
			playerConfig.set("chest." + i + ".type", type);
			playerConfig.set("chest." + i + ".world", worldName);
			playerConfig.set("chest." + i + ".x", x);
			playerConfig.set("chest." + i + ".y", y);
			playerConfig.set("chest." + i + ".z", z);
			i++;
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
				if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
					player.getInventory().addItem(itemStack);
					return;
				}
			}		
		}		
	}
	
	public void AddItems(Collection<ItemStack> items) {
		for (ItemStack itemStack : items) {
			AddItem(itemStack);
		}
		saveItems();
	}

	public void SaveInbox() {
		saveItems();
		saveChests();
	}
}
