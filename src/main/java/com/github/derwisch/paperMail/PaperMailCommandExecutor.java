package com.github.derwisch.paperMail;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaperMailCommandExecutor implements CommandExecutor {
    
	private PaperMail plugin;
	private double Cost = Settings.Price;
 
	public PaperMailCommandExecutor(PaperMail plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("PaperMailCommandExecutor initialized");
	}
 
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("papermail")){
				if (args.length == 0 || args[0].toLowerCase().equals("help")) {
					helpMessage(sender);
					return true;
				}
				if (args.length > 0 && !args[0].toLowerCase().equals("help")){
					sender.sendMessage(ChatColor.DARK_RED  + "invalid argument \"" + ChatColor.BOLD + args[0] + ChatColor.RESET + ChatColor.RED + "\"" + ChatColor.RESET);
					sender.sendMessage(ChatColor.WHITE + "Commands have changed. If you have used PaperMail in the past,\nthey are the same without the papermail prefix. Otherwise please do\n/papermail or /papermail help" + ChatColor.RESET);
					return true;
				}
				
		}	
		if (cmd.getName().equalsIgnoreCase("sendtext")){
			if  ((sender instanceof Player) && (sender.hasPermission(Permissions.SEND_TEXT_PERM)))
			{		
			Player player = (Player) sender;
			if (args.length < 2) {
					if (args.length < 1) {
						player.sendMessage(ChatColor.DARK_RED + "Missing arguments for textmail!" + ChatColor.RESET);
						return true;
					}
					player.sendMessage(ChatColor.DARK_RED + "Missing text of textmail!" + ChatColor.RESET);
					return true;
				}
				//if player isn't cost exempt and costs is enabled and price is set, try to send textmail
				if((Settings.EnableMailCosts == true) && (Settings.Price != 0) && (!player.hasPermission(Permissions.COSTS_EXEMPT))){
					//check if player has the correct amount of currency
					if(PaperMailEconomy.hasMoney(Settings.Price, player) == true){
						sendText(player, args);
						PaperMailEconomy.takeMoney(Cost, player);
						return true;
					//if player doesn't have enough money don't send textmail
					}else{
	                    player.sendMessage(ChatColor.RED + "Not Enough Money to send your mail!");
	                    return true;
					}
                 }
				//if player is cost exempt or price is zero or mailcosts is off send textmail
				if((Settings.EnableMailCosts == false) || (player.hasPermission(Permissions.COSTS_EXEMPT) && (Settings.EnableMailCosts == true)) || ((Settings.EnableMailCosts == true) && (Settings.Price == 0) && (!player.hasPermission(Permissions.COSTS_EXEMPT)))){
					sendText(player, args);
					return true;
				}
				}else if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.DARK_PURPLE + "Console " + ChatColor.DARK_RED + "may not use paperMail user Commands other than help.");
		    return true;
		    }
		}	
				//create inbox chest
		if (cmd.getName().equalsIgnoreCase("createbox")) {
					if(sender instanceof Player){
					Inbox inbox = null;
					Player player = ((Player) sender);
					if (args.length == 0 && (player.hasPermission(Permissions.CREATE_CHEST_SELF_PERM)  || player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM))) {
						try {
							inbox = Inbox.GetInbox(player.getName());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (args.length == 1 && player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM)) {
						try {
							inbox = Inbox.GetInbox(args[0]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						player.sendMessage(ChatColor.DARK_RED + "Too many arguments!" + ChatColor.RESET);
						return true;
					}
					
					@SuppressWarnings("deprecation")
					Block block = player.getTargetBlock(null, 10);
					
					if (block != null && block.getType() == Material.CHEST) {

						
						Chest chest = (Chest)block.getState();
						
						inbox.SetChest(chest);
						
						player.sendMessage(ChatColor.DARK_GREEN + "Inbox created!" + ChatColor.RESET);
						return true;
					} else {
						player.sendMessage(ChatColor.DARK_RED + "You must focus a chest" + ChatColor.RESET);
						return true;
					}
					}
				}else if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.DARK_PURPLE + "Console " + ChatColor.DARK_RED + "may not use paperMail user Commands other than help.");
	                    return true;				
				}	
				return false;
	}
	
	public void helpMessage(CommandSender sender){
		sender.sendMessage(ChatColor.WHITE + "Current Version of PaperMail is " + PaperMail.instance.getDescription().getVersion() + ".\n" + ChatColor.BOLD + ChatColor.AQUA + ChatColor.UNDERLINE + "PAPERMAIL HELP" + ChatColor.RESET);
		sender.sendMessage("" + ChatColor.BOLD + ChatColor.RED + "Commands:" + ChatColor.RESET);
		sender.sendMessage(ChatColor.GOLD + "/sendtext <playername> <Composition of Text>" + ChatColor.RESET);
		sender.sendMessage(ChatColor.YELLOW + "playername must be exact if sending to an offline player" + ChatColor.RESET);
		sender.sendMessage(ChatColor.GOLD + "/createbox" + ChatColor.RESET);
		sender.sendMessage(ChatColor.YELLOW + "Creates extra storage for your mail items. Do this while looking at a chest." + ChatColor.RESET);
	}
	
	//Send the textmail
	public void sendText(Player player, String[] args){
		
		ItemStack itemStack = new ItemStack(Material.PAPER);
		ItemMeta itemMeta = itemStack.getItemMeta();
	
		itemMeta.setDisplayName(ChatColor.WHITE + "Letter from " + player.getName() + ChatColor.RESET);
		ArrayList<String> lines = new ArrayList<String>();
	
		int count = 0;
		String currentLine = "";
	
		for (int i = 1; i < args.length; i++) {
			currentLine += args[i] + " ";
			count += args[i].length() + 1;
			if (++count >= 20) {
				count = 0;
				lines.add(ChatColor.GRAY + currentLine + ChatColor.RESET);
				currentLine = "";
			}
	}
	
	if (currentLine != "") {
		lines.add(ChatColor.GRAY + currentLine + ChatColor.RESET);	
	}
	
	itemMeta.setLore(lines);
	itemStack.setItemMeta(itemMeta);
	String playerName = args[0];
	Player p = Bukkit.getPlayer(playerName);
	if (p != null) {
		playerName = p.getName();
	    } else {
	    OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
	    if (op != null) {
	    	playerName = op.getName();
	    	player.sendMessage(ChatColor.GREEN + "Player " + playerName + " is Offline. Dropping it in their mailbox!" + ChatColor.RESET);
	        } else {
	        	playerName = args[1];
	        	player.sendMessage(ChatColor.DARK_RED + "Player "  + playerName + " may not exist or doesn't have an Inbox yet. Creating Inbox for player " + playerName + ChatColor.RESET);
	        	player.sendMessage(ChatColor.DARK_RED + "Warning : If you misspelled, your items may be lost forever. Make sure you check the player's name before sending always." + ChatColor.RESET); 
	        }
	    }
	try {
		Inbox.GetInbox(playerName).AddItem(itemStack, player);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  
	player.sendMessage(ChatColor.DARK_GREEN + "Textmail sent to "  + playerName + "!" + ChatColor.RESET);
	}
}
