package com.github.derwisch.paperMail;


import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.utils.BlockUtils;
import com.github.derwisch.paperMail.utils.SkullUtils;
 
public class PaperMailListener implements Listener {
	
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
		Inbox inbox = Inbox.GetInbox(event.getPlayer().getUniqueId());
		if (inbox == null) {
			Inbox.AddInbox(event.getPlayer().getUniqueId());
		}
    }
    
    
    //Create Inbox by placing sign on chest(and turn into skull mailbox if option is enabled)
    @EventHandler
    public void onPlayerPlaceMailbox(BlockPlaceEvent event){
    	ItemStack mailbox = event.getItemInHand();
    	if(SkullUtils.hasSecretCode(mailbox)){
    		//record block location to save for inbox location, then on click event check if the block location
    		//is inside of the player's inbox list
    	}
    }
    
	@EventHandler
    public void playerCreateMailbox(SignChangeEvent event){
    	if(event.getBlock().getState() instanceof Sign){
    		Sign sign = (Sign) event.getBlock().getState();
    		if((BlockUtils.getAttachedBlock(event.getBlock())).getType().equals(Material.CHEST)){
    			if(event.getLine(0).toLowerCase().contains("[mailbox]")){
    				//if not Using custom mailbox skulls
    				sign.setLine(2, event.getPlayer().getName());
    				
                    //if(use custom skull items)
    				Block b = BlockUtils.getAttachedBlock(event.getBlock());
    				SkullUtils.setBlocktoSkull("http://textures.minecraft.net/texture/c2bccb5240885ca64e424a0c168a78c676b8c847d187f6fbf6027a1fe86ee", b);
    				//create enum for different skull types and a switch method that takes
    				//the block and sets it based on the enum
    				event.getBlock().setType(Material.AIR);
    				//not gonna work, gonna have to use protocollib, but probably worth it in the end
    			}
    		} 		
    	}
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
    	ItemStack itemInMainHand = null;
    	ItemStack itemInOffHand = null;
    	ItemMeta inMainHandMeta = null;;
        ItemMeta inOffHandMeta = null;;
        Player player = event.getPlayer();
        if(player.getEquipment().getItemInMainHand()!=null){
        	itemInMainHand = player.getEquipment().getItemInMainHand();
        	if(itemInMainHand.hasItemMeta())
        		inMainHandMeta = itemInMainHand.getItemMeta();
        }
        if(player.getEquipment().getItemInOffHand()!=null){
        	itemInOffHand = player.getEquipment().getItemInOffHand();
        	if(itemInOffHand.hasItemMeta())
        		inOffHandMeta = itemInOffHand.getItemMeta();
        }     
        if (inMainHandMeta != null){
        	if(itemInMainHand.getType() == Settings.MailItemID && itemInMainHand.getDurability() == Settings.MailItemDV && inMainHandMeta.getDisplayName().equals((ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET)) && player.hasPermission(Permissions.SEND_ITEM_PERM))
        		new PaperMailGUI(player, true).Show();
        }
        if (inOffHandMeta != null){
        	if(itemInOffHand.getType() == Settings.MailItemID && itemInMainHand.getDurability() == Settings.MailItemDV && inMainHandMeta.getDisplayName().equals((ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET)) && player.hasPermission(Permissions.SEND_ITEM_PERM))
        		new PaperMailGUI(player, true).Show();
        }
        
        Block clickedBlock = event.getClickedBlock(); 
        if(clickedBlock != null && clickedBlock.getState() instanceof Sign) {
            Sign s = (Sign)event.getClickedBlock().getState();
            if(s.getLine(0).toLowerCase().contains("[mailbox]")) {
                if (event.getHand().equals(EquipmentSlot.HAND)) {
                	new PaperMailGUI(player).Show();
                } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                	Inbox inbox = Inbox.GetInbox(player.getUniqueId());
                	inbox.openInbox();
                	event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Send(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	Inventory inventory = event.getInventory();
    	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	ItemStack recipientItem = inventory.getItem(0);
    	boolean writtenBookBoolean = (recipientItem != null && recipientItem.getType() == Material.WRITTEN_BOOK);
    	
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.SEND_BUTTON_ON_TITLE) {
    		if (!writtenBookBoolean && !itemMailGUI.hasUUIDfromBookTitle()) {
    			((Player)inventory.getHolder()).sendMessage(ChatColor.RED + "No recipient defined" + ChatColor.RESET);
	    		event.setCancelled(true);
    		} else {
    			itemMailGUI.Result = SendingGUIClickResult.SEND;
	    		itemMailGUI.SendContents();
	    		itemMailGUI.SetClosed();
	    		((Player)inventory.getHolder()).closeInventory();
	    		event.setCancelled(true);
	    		
	        	itemMailGUI = null;
	        	PaperMailGUI.RemoveGUI(((Player)inventory.getHolder()).getUniqueId());
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Cancel(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	Inventory inventory = event.getInventory();
    	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.CANCEL_BUTTON_TITLE) {
    		itemMailGUI.Result = SendingGUIClickResult.CANCEL;
    		itemMailGUI.close();
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Enderchest(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.ENDERCHEST_BUTTON_TITLE) {
    		Player player = (Player)event.getInventory().getHolder();
    		PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer(player);
    		itemMailGUI.Result = SendingGUIClickResult.OPEN_ENDERCHEST;
    		event.setCancelled(true);
    		OpenInventory(player, player.getEnderChest());
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
    
    @EventHandler
    public void onInventoryClick_MailGUI_Recipient(InventoryClickEvent event) {
    	if (event.getInventory().getName() != PaperMail.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE) {
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
    	
    	if (inventory.getName() == PaperMail.NEW_MAIL_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder()); 
    		PaperMailGUI gui = PaperMailGUI.GetGUIfromPlayer(player);
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

    	if (inventory.getName() == PaperMail.INBOX_GUI_TITLE) {
    		Player player = ((Player)inventory.getHolder());
    		Inbox inbox = Inbox.GetInbox(player.getUniqueId());
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
}