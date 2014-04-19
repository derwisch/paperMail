package com.github.derwisch.paperMail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;



//    minecraft internals
import net.minecraft.server.v1_7_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;



//    bukkit/craftbukkit imports
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Inbox {
	
	public static ArrayList<Inbox> Inboxes = new ArrayList<Inbox>();
	
	public static void SaveAll() throws IOException {
		for (Inbox inbox : Inboxes) {
			inbox.SaveInbox();
		}
	}
	
	public static Inbox GetInbox(String playerName) throws IOException, InvalidConfigurationException {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerName.equals(playerName)) {
				return inbox;
			}
		}
		//if player does not yet exist or have an inbox
		AddInbox(playerName);
		return GetInbox(playerName);
	}
	
	public static void AddInbox(String playerName) throws IOException, InvalidConfigurationException {
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
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	private File file;
	private File yamlfile;
	private File queueFile;
	public Inbox(String playerName) throws IOException, InvalidConfigurationException {
		this.playerName = playerName;
		String filename = playerName + ".txt";
		String yamlname = playerName + ".yml";
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + playerName + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		file = new File(PaperMail.instance.getDataFolder(), "players\\" + filename);
		yamlfile = new File(PaperMail.instance.getDataFolder(), "players\\" + yamlname);
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
	
	private void loadItems() throws IOException, InvalidConfigurationException {
		int i = 0;
		ItemStack oldstack = null;
		ItemStack stack = null;
		String itemString = null;
		String queueName = playerName + "Queue" + ".yml";
		boolean check = false;
		//Load Current stack save format
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		YamlConfiguration queueYaml = new Utf8YamlConfiguration();
		queueFile = new File(PaperMail.instance.getDataFolder(), "players\\" + queueName);
		if(!queueFile.exists())
			queueYaml.save(queueFile);
		yaml.load(yamlfile);
		queueYaml.load(queueFile);
		do {
			  
			  itemString = yaml.getString("newitemstack." + i);
			  if (itemString != null) {
		        stack = InventoryUtils.stringToItemStack(itemString);
		        inventory.addItem(stack);
		        check = InventoryUtils.inventoryCheck(inventory, stack);
		      }else if(InventoryUtils.inventoryCheck(inventory, stack) == true){
		    	  do {
		    		  itemString = queueYaml.getString("newitemstack." + i);
		    		  if (itemString != null) {
		  		        stack = InventoryUtils.stringToItemStack(itemString);
		    		  }
		    		  check = InventoryUtils.inventoryCheck(inventory, stack);
		    		  if (check == true){
		    			  inventory.addItem(stack);
		    		  }
		    	  }while((itemString != null) || (check != false));
		      }
			  
		      i++;
		    }while ((itemString != null) || (check != false));
		queueYaml.save(queueFile);
		i = 0;
		//Load old old stacks for conversion, set slots to empty after load
		do {
			oldstack = playerConfig.getItemStack("itemstack." + i);
			if (oldstack != null){
			if (InventoryUtils.inventoryCheck(inventory, oldstack) == true)
			{
				playerConfig.set("itemstack." + i, "");
				inventory.addItem(oldstack);
			}
			}
			i++;
		} while (oldstack != null);
		//Load old stacks for conversion, delete the username.txt if any found after load
		if(file.exists())
		{	
			try {
				c = NBTCompressedStreamTools.a(new FileInputStream(file));
				file.delete();
		} 	catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		NBTTagList list = c.getList("inventory", 10);
		CraftItemStack cis = null;
		for(int l = 0; l < list.size(); l++){
			  NBTTagCompound item = new NBTTagCompound();
			  item = list.get(l);
			  if(item != null)
			  {
				  int index = item.getInt("index");
				  net.minecraft.server.v1_7_R1.ItemStack is = net.minecraft.server.v1_7_R1.ItemStack.createStack(item); //net.minecraft.server item stack, not bukkit item stack
				  cis = CraftItemStack.asCraftMirror(is);
				  if (InventoryUtils.inventoryCheck(inventory, cis) == true)
				  {
					if(oldstack != cis){
						inventory.setItem(index,cis);
				  	 }
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
	
	//Save all items in the recipients or user's Papermail inbox inventory to yaml
	private void saveItems() throws IOException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		for (int i = 0; i < Settings.DefaultBoxRows * 9; i++) {
		      CraftItemStack stack = (CraftItemStack)this.inventory.getItem(i);
		      if (stack != null) {
		        String item = InventoryUtils.itemstackToString(stack);
		        yaml.set("newitemstack." + i, item);
		      }
		      if (stack == null)
		      {
		        String item = null;
		        yaml.set("newitemstack." + i, item);
		      }
		    }
		yaml.save(yamlfile);
	}
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerName);
		
		player.openInventory(inventory);
	}
	
	public void SetChest(Chest newChest) {
		inboxChest = newChest;
		saveChest();
	}
	
	public void AddItem(ItemStack itemStack, Player sender) throws IOException, InvalidConfigurationException {
		Player player = Bukkit.getServer().getPlayer(playerName);
		if (inboxChest != null) {
			if (inboxChest.getInventory().addItem(itemStack).keySet().toArray().length > 0) {
				if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
					if((player != null) && (itemStack != null))
					if (InventoryUtils.inventoryCheck(player.getInventory(), itemStack) == true)
					{
					player.getInventory().addItem(itemStack);
					}else{
						saveQueue(playerName, (CraftItemStack) itemStack);
					}
				}
			}
		} else {
			if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
				if((player != null) && (itemStack != null))
				{
				if (InventoryUtils.inventoryCheck(player.getInventory(), itemStack) == true)
					{
					player.getInventory().addItem(itemStack);
					}else{
						saveQueue(playerName, (CraftItemStack) itemStack);
					}
				}
			}
		}
		
		saveItems();
	}
	
	public void AddItems(Collection<ItemStack> items, Player sender) throws IOException, InvalidConfigurationException {
		for (ItemStack itemStack : items) {
			AddItem(itemStack, sender);
		}
	}
	//Loads a string from a yaml file with a section name(string) and index number(int)
	public static String loadStringFromYaml(File file,int index, String section) throws IOException, InvalidConfigurationException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		yaml.load(file);
		String item = yaml.getString(section + index);
		return item;
	}
	//Saves a string to a yaml file with a section name(string) and index number(int)
	public static void saveStringtoYaml(File file, int index, String str, String section) throws IOException, InvalidConfigurationException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		yaml.set(section + index, str);
		yaml.save(file);
	}

	public void SaveInbox() throws IOException {
		saveItems();
		saveChest();
	}
	
	public void saveQueue(String playerName, CraftItemStack itemStack) throws IOException, InvalidConfigurationException {
			String itemString = "";
			int n = 0;
			String yamlname = playerName + "Queue" + ".yml";
			queueFile = new File(PaperMail.instance.getDataFolder(), "players\\" + yamlname);
			YamlConfiguration yaml = new Utf8YamlConfiguration();
			String item = "";
			if(queueFile.exists()){
				yaml.load(queueFile);
				//get current placeholder number
				do {
					itemString = yaml.getString("newitemstack." + n);
			      if (itemString != null) {
			    	  yaml.set("newitemstack." + n, itemString);
			        n++;
			      }
			    }while ((itemString != null));
			}
			item = InventoryUtils.itemstackToString(itemStack);
				if (item != null) {
					yaml.set("newitemstack." + n, item);
				}
		yaml.save(queueFile);
	}
}
