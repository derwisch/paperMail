package com.github.derwisch.paperMail.inbox;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MailboxObject{
	
	private UUID uuid;
	private Location location;
	private Material type;
	
	public MailboxObject(UUID uuid, Location location)
	{
		this.uuid = uuid;
		this.location = location;
		this.type = getBlock().getType();
	}
	
	public Material getType(){
		return this.type;
	}
	
	public MailboxType getMailboxType(){
		if(this.type == Material.SKULL || this.type == Material.SKULL_ITEM){
			return MailboxType.HEAD;
		}
		if(this.type == Material.CHEST){
			return MailboxType.CHEST;
		}
		return MailboxType.UNKNOWN;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public Location getLocation(){
		return this.location;
	}
	
	//Can be null
	public Block getBlock(){
		Block b = this.location.getBlock();
		return b;
	}
	
	public void loadMailboxes(){
		
	}
	
	public void saveMailboxes(){
		
	}
}