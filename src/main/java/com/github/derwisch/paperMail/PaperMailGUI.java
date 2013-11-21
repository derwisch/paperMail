package com.github.derwisch.paperMail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
//import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class PaperMailGUI {

	public static final String RECIPIENT_TITLE = ChatColor.RED + "Recipient" + ChatColor.RESET;
	public static final String SEND_BUTTON_ON_TITLE = ChatColor.WHITE + "Send" + ChatColor.RESET;
	public static final String CANCEL_BUTTON_TITLE = ChatColor.WHITE + "Cancel" + ChatColor.RESET;
	public static final String ENDERCHEST_BUTTON_TITLE = ChatColor.WHITE + "Open Enderchest" + ChatColor.RESET;
	
	private static ArrayList<PaperMailGUI> itemMailGUIs = new ArrayList<PaperMailGUI>();
	private static Map<String, PaperMailGUI> openGUIs = new HashMap<String, PaperMailGUI>();
	
	public Inventory Inventory;
	public Player Player;
	
	private ItemStack recipientMessage; 
	private ItemStack sendButtonEnabled;
	private ItemStack cancelButton; 
	private ItemStack enderChestButton;
	private boolean paperSent;
	
	public SendingGUIClickResult Result = SendingGUIClickResult.CANCEL;
	
	public static void RemoveGUI(String playerName) {
		openGUIs.put(playerName, null);
		openGUIs.remove(playerName);
	}
	
	public static PaperMailGUI GetOpenGUI(String playerName) {
		return openGUIs.get(playerName);
	}
	
	public PaperMailGUI(Player player) {
		this.paperSent = false;
		Player = player;
		Inventory = Bukkit.createInventory(player, Settings.MailWindowRows * 9, PaperMail.NEW_MAIL_GUI_TITLE);
		initializeButtons();
    	itemMailGUIs.add(this);
	}

	public PaperMailGUI(Player player, boolean paperSent) {
		this.paperSent = paperSent;
		Player = player;
		Inventory = Bukkit.createInventory(player, Settings.MailWindowRows * 9, PaperMail.NEW_MAIL_GUI_TITLE);
		initializeButtons();
    	itemMailGUIs.add(this);
	}
	
	private void initializeButtons() {
		recipientMessage = new ItemStack(Material.PAPER);
		sendButtonEnabled = new ItemStack(Material.WOOL);
		cancelButton = new ItemStack(Material.WOOL);
		enderChestButton = new ItemStack(Material.ENDER_CHEST);

    	sendButtonEnabled.setDurability((short)5);
    	cancelButton.setDurability((short)14);

    	ItemMeta recipientMessageMeta = recipientMessage.getItemMeta();
    	ItemMeta sendButtonEnabledMeta = sendButtonEnabled.getItemMeta();
    	ItemMeta cancelButtonMeta = cancelButton.getItemMeta();
    	ItemMeta enderChestButtonMeta = enderChestButton.getItemMeta();
    	
    	ArrayList<String> recipientMessageLore = new ArrayList<String>();
    	ArrayList<String> sendButtonDisabledLore = new ArrayList<String>();
    	ArrayList<String> enderChestButtonLore = new ArrayList<String>();

    	recipientMessageLore.add(ChatColor.GRAY + "Add a written book named" + ChatColor.RESET);
    	recipientMessageLore.add(ChatColor.GRAY + "like a player to define" + ChatColor.RESET);
    	recipientMessageLore.add(ChatColor.GRAY + "the recipient." + ChatColor.RESET);

    	sendButtonDisabledLore.add(ChatColor.GRAY + "State a recipient before" + ChatColor.RESET);
    	sendButtonDisabledLore.add(ChatColor.GRAY + "sending" + ChatColor.RESET);

    	enderChestButtonLore.add(ChatColor.GRAY + "Grants access to your" + ChatColor.RESET);
    	enderChestButtonLore.add(ChatColor.GRAY + "enderchest." + ChatColor.RESET);
    	enderChestButtonLore.add(ChatColor.GRAY + "You return to the mail after" + ChatColor.RESET);
    	enderChestButtonLore.add(ChatColor.GRAY + "closing the enderchest" + ChatColor.RESET);
    	
    	recipientMessageMeta.setDisplayName(RECIPIENT_TITLE);
    	recipientMessageMeta.setLore(recipientMessageLore);

    	sendButtonEnabledMeta.setDisplayName(SEND_BUTTON_ON_TITLE);

    	cancelButtonMeta.setDisplayName(CANCEL_BUTTON_TITLE);
    	
    	enderChestButtonMeta.setDisplayName(ENDERCHEST_BUTTON_TITLE);
    	enderChestButtonMeta.setLore(enderChestButtonLore);

    	recipientMessage.setItemMeta(recipientMessageMeta);
    	sendButtonEnabled.setItemMeta(sendButtonEnabledMeta);
    	cancelButton.setItemMeta(cancelButtonMeta);
    	enderChestButton.setItemMeta(enderChestButtonMeta);

    	Inventory.setItem(0, recipientMessage);
    	if (Settings.EnableEnderchest) {
    		Inventory.setItem(8, enderChestButton);
    	}
    	Inventory.setItem(((Settings.MailWindowRows - 1) * 9) - 1, sendButtonEnabled);
    	Inventory.setItem((Settings.MailWindowRows * 9) - 1, cancelButton);
	}
	
	public void Show() {
		if (Settings.EnableItemMail) {
			Player.openInventory(Inventory);
			openGUIs.put(Player.getDisplayName(), this);
		}
	}
	
	public void SetClosed() {
		RemoveGUI(Player.getDisplayName());
		itemMailGUIs.remove(this);
	}
		
	public void close() {
		Player.closeInventory();
		Result = SendingGUIClickResult.CANCEL;
	}
	
	public void SendContents() {		
		ArrayList<ItemStack> sendingContents = new ArrayList<ItemStack>();
		String playerName = "";
		
		for (int i = 0; i < Inventory.getSize(); i++) {
			ItemStack itemStack = Inventory.getItem(i);
			
			if (itemStack == null)
				continue;
			
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta.getDisplayName() != SEND_BUTTON_ON_TITLE && 
				itemMeta.getDisplayName() != CANCEL_BUTTON_TITLE && 
				itemMeta.getDisplayName() != ENDERCHEST_BUTTON_TITLE) {
				sendingContents.add(itemStack);
			}
			
			if (itemStack.getType() == Material.WRITTEN_BOOK && playerName == "") {
				BookMeta bookMeta = (BookMeta)itemMeta;
				playerName = bookMeta.getTitle();
			}
		}
		
		Inbox inbox = Inbox.GetInbox(playerName);
		inbox.AddItems(sendingContents, Player);
		
		if (paperSent) {
			ItemStack itemInHand = Player.getInventory().getItemInHand();
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			Player.setItemInHand(itemInHand);
		}
		
		Player.sendMessage(ChatColor.DARK_GREEN + "Message sent!" + ChatColor.RESET);
	}
	
	public static PaperMailGUI GetGUIfromPlayer(Player player) {
		for (PaperMailGUI gui : itemMailGUIs) {
			if (gui.Player == player)
				return gui;
		}
		return null;
	}
}
