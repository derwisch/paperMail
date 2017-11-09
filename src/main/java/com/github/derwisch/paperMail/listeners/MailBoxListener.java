package com.github.derwisch.paperMail.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import com.github.derwisch.paperMail.InboxesAccessor;
import com.github.derwisch.paperMail.PaperMailGUI;
import com.github.derwisch.paperMail.Permissions;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.recipes.CustomMailboxes;
import com.github.derwisch.paperMail.utils.BlockUtils;
import com.github.derwisch.paperMail.utils.UUIDUtils;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractCrouchLeftClickEvent;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractLeftClickEvent;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractRightClickEvent;
 
public class MailBoxListener implements Listener {
	
	@EventHandler
    public void onPlayerPlaceMailbox(BlockPlaceEvent event){
    	if(Settings.EnableCustomMailboxes){
    		if(event.getPlayer().hasPermission(Permissions.CREATE_CHEST_SELF_PERM)){
    			ItemStack mailbox = event.getItemInHand();
	    		if(mailbox!=null){
	    			if(CustomMailboxes.hasSecretCode(mailbox)){    				
	    				InboxesAccessor inbox = InboxesAccessor.GetInbox(event.getPlayer().getUniqueId());
		    			if(inbox!=null){
		    				inbox.addChest(event.getBlock());
		    				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "InboxesAccessor created!" + ChatColor.RESET);
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
	    				InboxesAccessor inbox = null;
		   				if(event.getLine(2).equals("") || event.getLine(2).isEmpty() || event.getLine(2)==null){
		   					event.setLine(2, event.getPlayer().getName());
		   					inbox = InboxesAccessor.GetInbox(player.getUniqueId());	    					
		   				}else{
		   					if(player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM)){
			   					if(UUIDUtils.getUUIDfromPlayerName(event.getLine(2)) != null){
			   						inbox = InboxesAccessor.GetInbox(UUIDUtils.getUUIDfromPlayerName(event.getLine(2)));
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
		   					inbox.addChest(BlockUtils.getAttachedBlock(event.getBlock()));
							player.sendMessage(ChatColor.DARK_GREEN + "InboxesAccessor created!" + ChatColor.RESET);
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
        	if(!InboxesAccessor.Inboxes.isEmpty() && InboxesAccessor.Inboxes!=null){
        		if(InboxesAccessor.hasInboxAtLocation(clickedBlock.getLocation())){ //Check if there are any inbox chests at all here
        			new PaperMailGUI(player).Show();
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
        	if(!InboxesAccessor.Inboxes.isEmpty() && InboxesAccessor.Inboxes!=null){
        		if(InboxesAccessor.hasInboxAtLocation(clickedBlock.getLocation())){ //Check if there are any inbox chests at all here
        			if(InboxesAccessor.hasInboxChestAtLocation(player, clickedBlock.getLocation())){ // Check if player has an inbox chest here
        				event.setCancelled(true);
        				InboxesAccessor.GetInbox(player).openInbox();
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
        	if(!InboxesAccessor.Inboxes.isEmpty() && InboxesAccessor.Inboxes!=null){
        		if(InboxesAccessor.hasInboxAtLocation(clickedBlock.getLocation())){ //Check if there are any inbox chests at all here
        			if(InboxesAccessor.hasInboxChestAtLocation(player, clickedBlock.getLocation())){ // Check if player has an inbox chest here
        				event.setCancelled(false);
        			}else{
        				event.setCancelled(true);
        			}      			
        		}
        	}     
        }
    }
}	