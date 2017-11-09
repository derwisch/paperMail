package com.github.derwisch.paperMail.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.InboxesAccessor;
import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.PaperMailGUI;
import com.github.derwisch.paperMail.SendingGUIClickResult;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.recipes.WritingPaper;
 
public class PaperMailListener implements Listener {
	
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
		InboxesAccessor inbox = InboxesAccessor.GetInbox(event.getPlayer().getUniqueId());
		if (inbox == null) {
			InboxesAccessor.AddInbox(event.getPlayer().getUniqueId());
		}
    }
    
    //U CAN SOMETIMES REMOVE BUTTONS(I NOTICED PAPER!)
    
    @EventHandler
    public void onInventoryClick_MailGUI_Send(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	if(event.getClick().isLeftClick()){
    		Inventory inventory = event.getInventory();
        	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
        	ItemStack currentItem = event.getCurrentItem();
        	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

        	ItemStack recipientItem = inventory.getItem(0);
        	boolean writtenBookBoolean = (recipientItem != null && WritingPaper.hasSecretCode(recipientItem) && recipientItem.getType() == Material.PAPER);
        	
        	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.SEND_BUTTON_ON_TITLE) {
        		if (writtenBookBoolean && itemMailGUI.hasUUIDfromBookTitle()) {
        			itemMailGUI.Result = SendingGUIClickResult.SEND;
    	    		itemMailGUI.SendContents();
    	    		itemMailGUI.SetClosed();    		
    	    		event.getWhoClicked().closeInventory();
    	    		event.setCancelled(true);    		
    	        	itemMailGUI = null;
    	        	PaperMailGUI.RemoveGUI(event.getWhoClicked().getUniqueId()); 			
        		} else {
        			event.getWhoClicked().sendMessage(ChatColor.RED + "No recipient defined" + ChatColor.RESET);
    	    		event.setCancelled(true);
        		}
        	}
    	}else{
    		event.setCancelled(true);
    	}
    	
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Cancel(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	if(event.getClick().isLeftClick()){
	    	Inventory inventory = event.getInventory();
	    	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
	    	ItemStack currentItem = event.getCurrentItem();
	    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
	
	    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.CANCEL_BUTTON_TITLE) {
	    		itemMailGUI.Result = SendingGUIClickResult.CANCEL;
	    		itemMailGUI.close();
	    		event.setCancelled(true);
	    	}
    	}else{
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Enderchest(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	if(event.getClick().isLeftClick()){
	    	ItemStack currentItem = event.getCurrentItem();
	    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
	    	
	    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.ENDERCHEST_BUTTON_TITLE) {
	    		Player player = (Player)event.getInventory().getHolder();
	    		PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer(player);
	    		itemMailGUI.Result = SendingGUIClickResult.OPEN_ENDERCHEST;
	    		event.setCancelled(true);
	    		OpenInventory(player, player.getEnderChest());
	    	}
    	}else{
    		event.setCancelled(true);
    	}
    }

    private static Player player = null;
    private static Inventory inventory = null;    
    public static void OpenInventory(Player player, Inventory inventory) {
    	PaperMailListener.player = player;
    	PaperMailListener.inventory = inventory;
    	PaperMail.server.getScheduler().scheduleSyncDelayedTask(PaperMail.instance, new Runnable() {
    		public void run() {
    	    	openInventory(PaperMailListener.player, PaperMailListener.inventory);
    	    }
    	}, 0L);
    }
    
    private static void openInventory(Player player, Inventory inventory) {
		player.closeInventory();
    	player.openInventory(inventory);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick_MailGUI_Recipient(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	if(event.getClick().isLeftClick()){
	    	ItemStack currentItem = event.getCurrentItem();
	    	ItemStack cursorItem = event.getCursor();
	    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
	
	    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE) {
	    		if (cursorItem.getType() == Material.PAPER) {
	    			if(WritingPaper.hasSecretCode(cursorItem)){
	    				event.setCurrentItem(cursorItem);
	    	    		event.setCursor(null);
	    	    		event.setCancelled(true);
	    			} 		
	    		} else {
	        		event.setCancelled(true);
	        	}
	    	}
    	}else{
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	Inventory inventory = event.getInventory();	
    	if (inventory.getName() == PaperMail.NEW_MAIL_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder()); 
    		PaperMailGUI gui = PaperMailGUI.GetGUIfromPlayer(player);
    		if(gui!=null){
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
    		    					(itemMeta.getDisplayName() == PaperMailGUI.CANCEL_BUTTON_TITLE ||
    		    					itemMeta.getDisplayName() == PaperMailGUI.ENDERCHEST_BUTTON_TITLE ||
    		    					itemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE ||
    		    					itemMeta.getDisplayName() == PaperMailGUI.SEND_BUTTON_ON_TITLE)) {
    		    				continue;
    		    			}
    		    			world.dropItemNaturally(playerLocation, itemStack);    			
    		    		}
        			}
        		}
    		}	
    	}

    	if (inventory.getName() == PaperMail.INBOX_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder());
    		InboxesAccessor inbox = InboxesAccessor.GetInbox(player.getUniqueId());
    		inbox.SaveInbox();
    	}
    	
    	
    	if (inventory.getType().toString() == "ENDER_CHEST") {
        	Player player = (Player)event.getPlayer();
        	PaperMailGUI gui = PaperMailGUI.GetOpenGUI(player.getUniqueId());
        	if (gui != null) {
        		OpenInventory(player, gui.Inventory);
        		gui.Result = SendingGUIClickResult.CANCEL;
        	}
    		
    	}
    }
    
    @EventHandler
    public void onPlayerWriteLetter(PlayerEditBookEvent event){
    	Player player = event.getPlayer();
    	ItemStack book = null;
    	EquipmentSlot slot = null;
    	if(player.getInventory().getItemInMainHand()!=null && player.getInventory().getItemInMainHand().getType().equals(Material.BOOK_AND_QUILL)){
    		book = player.getInventory().getItemInMainHand();
    		slot = EquipmentSlot.HAND;
    	}
    	if(slot == null && player.getInventory().getItemInOffHand()!=null && player.getInventory().getItemInOffHand().getType().equals(Material.BOOK_AND_QUILL)){
    		book = player.getInventory().getItemInOffHand();
    		slot = EquipmentSlot.OFF_HAND;
    	}
    	if(book==null)
    		return;
    	if(book.getType().equals(Material.BOOK_AND_QUILL)){
    		if(WritingPaper.hasSecretCode(book)){
        		BookMeta letterContents = event.getNewBookMeta();
        		ItemStack letterpaper = new ItemStack(Material.PAPER, 1);
        		ItemMeta letterMeta = letterpaper.getItemMeta();
        		letterMeta.setDisplayName(letterContents.getTitle() + WritingPaper.secretCode);
        		List<String> letterLore = new ArrayList<String>();
        		for(String s : letterContents.getPages()){
        			letterLore.add(ChatColor.translateAlternateColorCodes('&', ("&7&o" + s)));
        		}
        		letterLore.add(ChatColor.translateAlternateColorCodes('&', (Settings.LetterSignature + letterContents.getAuthor())));
        		letterMeta.setLore(letterLore);
        		letterpaper.setItemMeta(letterMeta);
        		if(player.getInventory().contains(book)){
        			int amount = book.getAmount() - 1;
        			if(slot==(EquipmentSlot.HAND)){
        				if(amount >= 1){
        					player.getInventory().getItemInMainHand().setAmount(amount);
        				}else{
        					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        				}      				
        			}
        			if(slot==(EquipmentSlot.OFF_HAND)){
        				if(amount >= 1){
        					player.getInventory().getItemInOffHand().setAmount(amount);
        				}else{
        					player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        				}      				
        			}
        			event.setCancelled(true);
        			player.getInventory().addItem(letterpaper);
        		}     				
        	}
    	}	
    }
}