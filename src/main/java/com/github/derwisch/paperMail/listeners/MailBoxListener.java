package com.github.derwisch.paperMail.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.github.derwisch.paperMail.InboxesManager;
import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.PaperMailGUI;
import com.github.derwisch.paperMail.Permissions;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.inbox.Inbox;
import com.github.derwisch.paperMail.inbox.MailboxObject;
import com.github.derwisch.paperMail.recipes.CustomMailboxes;
import com.github.derwisch.paperMail.utils.BlockUtils;
import com.github.derwisch.paperMail.utils.UUIDUtils;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractCrouchLeftClickEvent;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractLeftClickEvent;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractRightClickEvent;
 
public class MailBoxListener implements Listener {
	
	private PaperMail plugin;
	private InboxesManager inboxesManager;
	
	public MailBoxListener(PaperMail plugin){
		this.plugin = plugin;
		this.inboxesManager = plugin.getInboxesManager();
	}
	
	@EventHandler
    public void onPlayerPlaceMailbox(BlockPlaceEvent event){
    	if(Settings.EnableCustomMailboxes){
    		if(event.getPlayer().hasPermission(Permissions.CREATE_CHEST_SELF_PERM)){
    			ItemStack mailbox = event.getItemInHand();
	    		if(mailbox!=null){
	    			if(CustomMailboxes.hasSecretCode(mailbox)){    				
	    				Inbox inbox = inboxesManager.getInbox(event.getPlayer().getUniqueId());
		    			if(inbox!=null){
		    				inbox.getMailBoxes().addMailbox(new MailboxObject(event.getPlayer().getUniqueId(), event.getBlock().getLocation()));
		    				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "InboxesManager created!" + ChatColor.RESET);
		    			}
	            	}
	    		}
    		}else{
				event.getPlayer().sendMessage("No Permission to create MailboxObject(custom)");	
			}
    	}  	
    }
    
	@EventHandler
    public void playerCreateMailbox(SignChangeEvent event){
		if(event.getBlock().getState() instanceof Sign){
	    	if((BlockUtils.getAttachedBlock(event.getBlock())).getType().equals(Material.CHEST)){
	    		if(event.getLine(0).toLowerCase().contains("[mailbox]")){
	    			Player player = event.getPlayer();
	    			if(player.hasPermission(Permissions.CREATE_CHEST_SELF_PERM)  || player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM)){
	    				Inbox inbox = null;
		   				if(event.getLine(2).equals("") || event.getLine(2).isEmpty() || event.getLine(2)==null){
		   					event.setLine(2, event.getPlayer().getName());
		   					inbox = inboxesManager.getInbox(player.getUniqueId());	    					
		   				}else{
		   					if(player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM)){
			   					if(UUIDUtils.getUUIDfromPlayerName(event.getLine(2)) != null){
			   						inbox = inboxesManager.getInbox(UUIDUtils.getUUIDfromPlayerName(event.getLine(2)));
			   					}else{
			   						event.getBlock().breakNaturally();
			   						player.sendMessage(ChatColor.DARK_RED + "Can't create MailboxObject for that Player" + ChatColor.RESET);
			   						event.setCancelled(true);
			   						return;
			   					}
		   					}else{
		   						event.getBlock().breakNaturally();
			   					player.sendMessage("No Permission to create MailboxObject");
			   					event.setCancelled(true);
			   					return;
		   					}
		   				}	    				
		   				if(inbox!=null){
		   					inbox.getMailBoxes().addMailbox(new MailboxObject(player.getUniqueId(), BlockUtils.getAttachedBlock(event.getBlock()).getLocation()));
							player.sendMessage(ChatColor.DARK_GREEN + "InboxesManager created!" + ChatColor.RESET);
		   				}
	    			}else{
	    				player.sendMessage("No Permission to create MailboxObject");	
	    			}
	    		}
	    	} 		
	    }
	}   	
    
    
    @EventHandler
    public void onInboxLeftClick(PlayerInteractLeftClickEvent event) {
    	Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        
        if(clickedBlock != null) {
        	if(!inboxesManager.getInboxes().isEmpty() && inboxesManager.getInboxes()!=null){
        		if(inboxesManager.isMailbox(clickedBlock)){
        			new PaperMailGUI(plugin, player).Show();
        			event.setCancelled(true);
        		}
        	}        
        }
    }
    
    @EventHandler
    public void onInboxRightClick(PlayerInteractRightClickEvent event) {
    	Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock != null) {
        	if(!inboxesManager.getInboxes().isEmpty() && inboxesManager.getInboxes()!=null){
        		if(inboxesManager.isMailbox(clickedBlock)){ //Check if there are any inbox chests at all here
        			if(inboxesManager.hasMailboxAtBlock(player.getUniqueId(), clickedBlock)){ // Check if player has an inbox chest here
        				event.setCancelled(true);
        				openInbox(player.getUniqueId());
        			}else{
        				player.sendMessage("Not your inbox");
        				event.setCancelled(true);
        			}      			
        		}
        	}     
        }
    }
    
    @EventHandler
    public void onInboxCrouchLeftClick(PlayerInteractCrouchLeftClickEvent event){
    	Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock != null) {
        	if(!inboxesManager.getInboxes().isEmpty() && inboxesManager.getInboxes()!=null){
        		if(inboxesManager.isMailbox(clickedBlock)){ //Check if there are any inbox chests at all here
        			if(inboxesManager.hasMailboxAtBlock(player.getUniqueId(), clickedBlock)){ // Check if player has an inbox chest here
        				event.setCancelled(false);
        			}else{
        				event.setCancelled(true);
        			}      			
        		}
        	}     
        }
    }
    
    public void openInbox(UUID uuid) {
		Player player = Bukkit.getServer().getPlayer(uuid);
		Inventory inventory = inboxesManager.getInbox(uuid).getPage(1);
		if(inventory!=null){
			player.openInventory(inventory);
		}else{
			player.sendMessage("Page 1 is null in openInbox method in MailboxListener");
		}
	}
}	