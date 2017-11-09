package com.github.derwisch.paperMail.inbox;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Mailboxes{
	
	private UUID uuid;
	private FileConfiguration playerConfig;
	private Set<MailboxObject> mailBoxes;
		
	public Mailboxes(UUID uuid, FileConfiguration playerConfig){
		this.uuid = uuid;
		this.playerConfig = playerConfig;
		this.mailBoxes = loadMailboxes();
	}
	
	private Set<MailboxObject> loadMailboxes(){
		Set<MailboxObject> mailBoxes = new HashSet<MailboxObject>();
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
				   						addMailbox(new MailboxObject(this.uuid, block.getLocation()));
			    						Bukkit.getLogger().info("[PaperMail]Loading " + block.getType().toString() + " for " + this.uuid.toString());
			    					}
			    				}
				    		}
						}				
					}
				}							 		
			}
		}
		return mailBoxes;
	}
	
	public void saveMailBoxes(){
		if (mailBoxes == null || mailBoxes.isEmpty()) {
			return;
		}
		int i = 0;
		for(MailboxObject mailBox : mailBoxes){
			Block b = mailBox.getBlock();
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
	}
	
	public void addMailbox(MailboxObject mailBox){
		this.mailBoxes.add(mailBox);
	}
	
	public void addMailboxes(Collection<MailboxObject> mailboxes){
		for(MailboxObject mailBox : mailboxes){
			addMailbox(mailBox);
		}
	}
	
	public Set<MailboxObject> getMailboxes(){
		return this.mailBoxes;
	}
}

	