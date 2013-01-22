package com.github.derwisch.itemMail;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
 
public class ItemMailListener implements Listener {
	
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
		Inbox inbox = Inbox.GetInbox(event.getPlayer());
		if (inbox == null) {
			Inbox.AddInbox(event.getPlayer());
		}
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        ItemMeta inHandMeta = itemInHand.getItemMeta();
        if (itemInHand != null && itemInHand.getType() == Material.PAPER && inHandMeta.getDisplayName().equals((ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET))) {
        	new ItemMailGUI(player, true).Show();
        }
        
        Block clickedBlock = event.getClickedBlock(); 
        if(clickedBlock != null && clickedBlock.getState() instanceof Sign) {
            Sign s = (Sign)event.getClickedBlock().getState();
            if(s.getLine(1).toLowerCase().contains("[mailbox]")) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                	new ItemMailGUI(player).Show();
                } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                	Inbox inbox = Inbox.GetInbox(player);
                	inbox.openInbox();
                	event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Send(InventoryClickEvent event) {
    	if (event.getInventory().getName() != ItemMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	Inventory inventory = event.getInventory();
    	ItemMailGUI itemMailGUI = ItemMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	ItemStack recipientItem = inventory.getItem(0);
    	boolean writtenBookBoolean = (recipientItem != null && recipientItem.getType() == Material.WRITTEN_BOOK);
    	
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == ItemMailGUI.SEND_BUTTON_ON_TITLE) {
    		if (!writtenBookBoolean) {
    			((Player)inventory.getHolder()).sendMessage(ChatColor.RED + "No recipient defined" + ChatColor.RESET);
	    		event.setCancelled(true);
    		} else {
    			itemMailGUI.Result = SendingGUIClickResult.SEND;
	    		itemMailGUI.SendContents();
	    		((Player)inventory.getHolder()).closeInventory();
	    		event.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Cancel(InventoryClickEvent event) {
    	if (event.getInventory().getName() != ItemMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	Inventory inventory = event.getInventory();
    	ItemMailGUI itemMailGUI = ItemMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == ItemMailGUI.CANCEL_BUTTON_TITLE) {
    		itemMailGUI.Result = SendingGUIClickResult.CANCEL;
    		itemMailGUI.close();
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Enderchest(InventoryClickEvent event) {
    	if (event.getInventory().getName() != ItemMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == ItemMailGUI.ENDERCHEST_BUTTON_TITLE) {
    		Player player = (Player)event.getInventory().getHolder();
    		ItemMailGUI itemMailGUI = ItemMailGUI.GetGUIfromPlayer(player);
    		itemMailGUI.Result = SendingGUIClickResult.OPEN_ENDERCHEST;
    		event.setCancelled(true);
    		OpenInventory(player, player.getEnderChest());
    	}
    }

    private static Player player = null;
    private static Inventory inventory = null;    
    public static void OpenInventory(Player player, Inventory inventory) {
    	ItemMailListener.player = player;
    	ItemMailListener.inventory = inventory;
    	ItemMail.server.getScheduler().scheduleSyncDelayedTask(ItemMail.instance, new Runnable() {
    		@Override 
    	    public void run() {
    	    	openInventory(ItemMailListener.player, ItemMailListener.inventory);
    	    }
    	}, 0L);
    }
    
    private static void openInventory(Player player, Inventory inventory) {
		player.closeInventory();
    	player.openInventory(inventory);
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Recipient(InventoryClickEvent event) {
    	if (event.getInventory().getName() != ItemMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == ItemMailGUI.RECIPIENT_TITLE) {
    		if (cursorItem.getType() == Material.WRITTEN_BOOK) {
	    		event.setCurrentItem(cursorItem);
	    		event.setCursor(null);
	    		event.setCancelled(true);
    		} else {
        		event.setCancelled(true);
        	}
    	}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	Inventory inventory = event.getInventory();
    	
    	if (inventory.getName() == ItemMail.NEW_MAIL_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder()); 
    		ItemMailGUI gui = ItemMailGUI.GetGUIfromPlayer(player);
    		World world = player.getWorld();
    		Location playerLocation = player.getLocation();
    		if (gui.Result == SendingGUIClickResult.CANCEL) {
    			gui.SetClosed();
    			ItemStack[] inventoryContents = inventory.getContents();
    			if (inventoryContents != null) {
		    		for (ItemStack itemStack : inventoryContents) {
		    			if (itemStack == null)
		    				continue;
		    			
		    			ItemMeta itemMeta = itemStack.getItemMeta();
		    			if (itemMeta != null &&
		    					(itemMeta.getDisplayName() == ItemMailGUI.CANCEL_BUTTON_TITLE ||
		    					itemMeta.getDisplayName() == ItemMailGUI.ENDERCHEST_BUTTON_TITLE ||
		    					itemMeta.getDisplayName() == ItemMailGUI.RECIPIENT_TITLE ||
		    					itemMeta.getDisplayName() == ItemMailGUI.SEND_BUTTON_ON_TITLE)) {
		    				continue;
		    			}
		    			world.dropItemNaturally(playerLocation, itemStack);    			
		    		}
    			}
    		}
    		
    	}

    	if (inventory.getName() == ItemMail.INBOX_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder());
    		Inbox inbox = Inbox.GetInbox(player);
    		inbox.SaveInbox();
    	}
    	
    	
    	if (inventory.getType().toString() == "ENDER_CHEST") {
        	Player player = (Player)event.getPlayer();
        	ItemMailGUI gui = ItemMailGUI.GetOpenGUI(player);
        	if (gui != null) {
        		OpenInventory(player, gui.Inventory);
        		gui.Result = SendingGUIClickResult.CANCEL;
        	}
    		
    	}
    }
}