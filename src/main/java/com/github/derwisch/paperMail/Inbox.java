package com.github.derwisch.paperMail;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.server.v1_7_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagInt;
import net.minecraft.server.v1_7_R1.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
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
	
	public static Inbox GetInbox(String playerName) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerName.equals(playerName)) {
				return inbox;
			}
		}
		AddInbox(playerName);
		return GetInbox(playerName);
	}
	
	public static void AddInbox(String playerName) {
		if (!Settings.InboxPlayers.contains(playerName)) {
			Settings.InboxPlayers.add(playerName);
		}
		Inbox inbox = new Inbox(playerName);
		Inboxes.add(inbox);
	}
	
	public static void RemoveInbox(String playerName) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerName.equals(playerName)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public String playerName;
	public Inventory inventory;
	public Chest inboxChest;
	
	private NBTTagCompound c = new NBTTagCompound();
	private NBTTagList list = new NBTTagList();
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	private File file; 
	public Inbox(String playerName) {
		this.playerName = playerName;
		String filename = playerName + ".txt";
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + playerName + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		file = new File(PaperMail.instance.getDataFolder(), "players\\" + filename);
		if(file.exists())
		{
			try {
				c = NBTCompressedStreamTools.a(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list = c.getList("inventory", 10);
		
		c.set("inventory",list);
		try {
			NBTCompressedStreamTools.a(c, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		configAccessor.saveConfig();
		initMailBox();
		loadChest();
		loadItems();
	}
	
	private void initMailBox() {
		Player player = Bukkit.getServer().getPlayer(playerName);
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
		ItemStack oldstack = null;
		do {
			oldstack = playerConfig.getItemStack("itemstack." + i);
			if (oldstack != null){
				playerConfig.set("itemstack." + i, "");
				inventory.addItem(oldstack);
			}
			i++;
		} while (oldstack != null);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			c = NBTCompressedStreamTools.a(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NBTTagList list = c.getList("inventory", 10);
		CraftItemStack cis = null;
		for(int n = 0; n < list.size(); n++){
			  NBTTagCompound item = new NBTTagCompound();
			  item = list.get(n);
			  if(item != null){
			  int index = item.getInt("index");
			  net.minecraft.server.v1_7_R1.ItemStack is = net.minecraft.server.v1_7_R1.ItemStack.createStack(item); //net.minecraft.server item stack, not bukkit item stack
			  cis = CraftItemStack.asCraftMirror(is);
			  if(oldstack != cis){
			  inventory.setItem(index,cis);
			  }
			  }
			}
		
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
		for(int index = 0; index < inventory.getContents().length; index++){
			  ItemStack cis = inventory.getItem(index);
			  if((cis!=null)){
				  net.minecraft.server.v1_7_R1.ItemStack is = CraftItemStack.asNMSCopy(cis); //net.minecraft.server item stack, not bukkit!
			    NBTTagCompound itemCompound = new NBTTagCompound();
			    itemCompound = is.save(itemCompound);
			    itemCompound.set("index",new NBTTagInt(index));
			    list.add(itemCompound);
			  }
			  if(cis == null){
				  NBTTagCompound itemCompound = new NBTTagCompound();
				  itemCompound.set("index",new NBTTagInt(index));
				  list.add(itemCompound);
			  }
			}
		c.set("inventory",list);
		if(file.exists() == false){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			NBTCompressedStreamTools.a(c, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerName);
		
		player.openInventory(inventory);
	}
	
	public void SetChest(Chest newChest) {
		inboxChest = newChest;
		saveChest();
	}
	
	public void AddItem(ItemStack itemStack, Player sender) {
		Player player = Bukkit.getServer().getPlayer(playerName);
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
