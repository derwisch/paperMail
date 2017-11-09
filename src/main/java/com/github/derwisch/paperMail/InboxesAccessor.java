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
import com.github.derwisch.paperMail.inbox.Inbox;
import com.github.derwisch.paperMail.utils.Utils;

public class InboxesAccessor {
	
	
	
	public static Set<Inbox> Inboxes = new HashSet<Inbox>();
	
	public static void SaveAll() {
		for (InboxesAccessor inbox : Inboxes) {
			inbox.SaveInbox();
		}
	}
	
	public static InboxesAccessor GetInbox(UUID uuid) {
		for (InboxesAccessor inbox : Inboxes) {
			if (inbox.playerUUID.equals(uuid)) {
				return inbox;
			}
		}		
		return AddInbox(uuid);
	}
	
	public static InboxesAccessor GetInbox(OfflinePlayer player) {
		for (InboxesAccessor inbox : Inboxes) {
			if (inbox.playerUUID.equals(player.getUniqueId())) {
				return inbox;
			}
		}
		AddInbox(player.getUniqueId());
		return GetInbox(player.getUniqueId());
	}
	
	public static InboxesAccessor AddInbox(UUID uuid) {
		if (!Settings.InboxPlayers.contains(uuid)) {
			Settings.InboxPlayers.add(uuid);
		}
		InboxesAccessor inbox = new InboxesAccessor(uuid);
		Inboxes.add(inbox);
		return inbox;
	}
	
	public static void RemoveInbox(String playerUUID) {
		for (InboxesAccessor inbox : Inboxes) {
			if (inbox.playerUUID.equals(playerUUID)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public static InboxesAccessor getInboxfromLocation(Location loc){
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
	
	
	
	
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		if(inventory!=null){
			player.openInventory(inventory);
		}else{
			this.inventory = Bukkit.createInventory(player, 36, PaperMail.INBOX_GUI_TITLE);
			player.openInventory(inventory);
		}
	}
	
}
