package com.github.derwisch.paperMail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import com.github.derwisch.paperMail.inbox.Inbox;

public class InboxesManager {
		
	private Set<Inbox> inboxes;
	private PaperMail plugin;
	
	public InboxesManager(PaperMail plugin){
		this.plugin = plugin;
		this.inboxes = initializeInboxes();
	}
	
	private Set<Inbox> initializeInboxes(){
		Set<Inbox> inboxes = new HashSet<Inbox>();
		List<String> players = new ArrayList<String>();
    	File dataFolder = new File(this.plugin.getDataFolder().toString()+"/players");
    	if(dataFolder.listFiles()!=null){
    		File[] files = dataFolder.listFiles();
        	for(File file : files){
            	players.add(file.getName().toString().replaceAll(".yml", ""));
            }    	
            for(String s : players){
            	UUID uuid = UUID.fromString(s);
            	if(uuid!=null){
            		inboxes.add(new Inbox(plugin, uuid));
            	}
            }
        }
    	return inboxes;
	}
	
	public Set<Inbox> getInboxes(){
		return this.inboxes;
	}
	
	public void addInbox(UUID uuid){
		if(!this.hasInbox(uuid)){
			this.inboxes.add(new Inbox(plugin, uuid));
		}
	}
	
	public boolean hasInbox(UUID uuid){
		for(Inbox inbox : this.inboxes){
			if(uuid == inbox.getUUID()){
				return true;
			}
		}
		return false;
	}
	
	public Inbox getInbox(UUID uuid){
		for(Inbox inbox : this.inboxes){
			if(uuid == inbox.getUUID()){
				return inbox;
			}
		}
		Inbox inbox = new Inbox(plugin, uuid);
		this.inboxes.add(inbox);
		return inbox;
	}
	
	public HashMap<Integer, Inventory> getPages(Inbox inbox) {
		return inbox.getInboxPages();
	}
	
	public Inventory getPageNum(Integer pageNum, Inbox inbox){
		return inbox.getPage(pageNum);
	}
	
	public boolean isMailbox(Block block){
		for(Inbox inbox : this.inboxes){
			if(inbox.hasMailboxAtLocation(block.getLocation())){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasMailboxAtBlock(UUID uuid, Block block){
		if(getInbox(uuid).hasMailboxAtLocation(block.getLocation())){
			return true;
		}		
		return false;
	}
	
	public void saveInboxes(){
		for(Inbox inbox : this.inboxes){
			inbox.saveInbox();
		}
	}
}
