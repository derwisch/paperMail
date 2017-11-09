package com.github.derwisch.paperMail.inbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.configs.ConfigAccessor;
import com.github.derwisch.paperMail.utils.Utils;

public class Inbox{
	
	private UUID uuid;
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	private Mailboxes mailBoxes;
	private PlayerInboxChest chest;
	
	public Inbox(UUID uuid){
		this.uuid = uuid;
		this.configAccessor = new ConfigAccessor(PaperMail.instance, "players\\" + uuid + ".yml");
		this.playerConfig = configAccessor.getConfig();
		if(this.playerConfig==null){
			configAccessor.saveConfig();
			this.playerConfig = configAccessor.getConfig();
		}		
		this.mailBoxes = new Mailboxes(this.uuid, this.playerConfig);
		this.chest = new PlayerInboxChest(this.uuid, this.playerConfig);		
	}
	
	public FileConfiguration getPlayerConfig(){
		return this.playerConfig;
	}
	
	public ConfigAccessor getConfigAccessor(){
		return this.configAccessor;
	}
	
	public Mailboxes getMailBoxes(){
		return this.mailBoxes;
	}
	
	public PlayerInboxChest getInboxChest(){
		return this.chest;
	}
	
	public void saveInbox(){
		this.chest.saveStacks();
		this.mailBoxes.saveMailBoxes();
		configAccessor.saveConfig();
	}

	public boolean hasMailboxAtLocation(Location loc){
		for(MailboxObject mailBox : this.mailBoxes.getMailboxes()){
			if(Utils.areLocationsEqual(loc, mailBox.getLocation())){
				return true;
			}
		}
		return false;
	}
	
	public MailboxObject getMailboxAtLocation(Location loc){
		for(MailboxObject mailBox : this.mailBoxes.getMailboxes()){
			if(Utils.areLocationsEqual(loc, mailBox.getLocation())){
				return mailBox;
			}
		}
		return null;
	}
	
	public void addItem(ItemStack item){
		this.getInboxChest().addItemStack(item);
	}
	
	public void addItems(Collection<ItemStack> stacks){
		for(ItemStack stack : stacks){
			addItem(stack);
		}
	}
	
	public HashMap<Integer, Inventory> getInboxPages(){
		return this.getInboxChest().getPages();
	}
	
	public Inventory getPage(Integer pageNum){
		return this.getInboxChest().getPage(pageNum);
	}
	
	public List<ItemStack> getAllInboxStacks(){
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for(Inventory inv : this.getInboxPages().values()){
			for(ItemStack stack : inv){
				if(!stack.hasItemMeta()){
					stacks.add(stack);
				}else{
					ItemMeta meta = stack.getItemMeta();
					if(!meta.hasDisplayName() || meta == null){
						stacks.add(stack);
					}else{
						if(!meta.getDisplayName().contains(PlayerInboxChest.secretCode)){
							stacks.add(stack);
						}
					}
				}
			}
		}
		return stacks;
	}
	
}