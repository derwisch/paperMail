package com.github.derwisch.paperMail;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaperMailCommandExecutor implements CommandExecutor {
    
	private PaperMail plugin;
	private double Cost = Settings.Price;
 
	public PaperMailCommandExecutor(PaperMail plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("ItemMailCommandExecutor initialized");
	}
 
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("papermail")){
			if (!(sender instanceof Player) && args.length > 0) {
				sender.sendMessage("Current Version of ItemMail is " + PaperMail.instance.getDescription().getVersion());
				return true;
			} else {
				if (args.length == 0) {
					sender.sendMessage("Current Version of ItemMail is " + PaperMail.instance.getDescription().getVersion());
					return true;
				}
				
				Player player = (Player) sender;
				
				
				if (!args[0].toLowerCase().equals("sendtext") && !args[0].toLowerCase().equals("createbox")) {
					player.sendMessage(ChatColor.DARK_RED + "invalid argument \"" + args[0] + "\"" + ChatColor.RESET);
					return true;
				}

				if (Settings.EnableTextMail && args[0].toLowerCase().equals("sendtext") && player.hasPermission(Permissions.SEND_TEXT_PERM)) {
					if (args.length < 3) {
						if (args.length < 2) {
							player.sendMessage(ChatColor.DARK_RED + "Missing arguments for textmail!" + ChatColor.RESET);
							return true;
						}
						player.sendMessage(ChatColor.DARK_RED + "Missing text of textmail!" + ChatColor.RESET);
						return true;
					}
					if((Settings.EnableMailCosts == true) && (Settings.Price != 0)){
						if(PaperMailEconomy.hasMoney(Settings.Price, player) == true){
						ItemStack itemStack = new ItemStack(Material.PAPER);
						ItemMeta itemMeta = itemStack.getItemMeta();
					
						itemMeta.setDisplayName(ChatColor.WHITE + "Letter from " + player.getName() + ChatColor.RESET);
						ArrayList<String> lines = new ArrayList<String>();
					
						int count = 0;
						String currentLine = "";
					
						for (int i = 2; i < args.length; i++) {
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
					net.minecraft.server.v1_6_R3.ItemStack MineStack = CraftItemStack.asNMSCopy(itemStack);
					Inbox.GetInbox(args[1]).AddItem(MineStack, player);
                  
					player.sendMessage(ChatColor.DARK_GREEN + "Textmail sent to "  + args[1] + ChatColor.RESET);
                    PaperMailEconomy.takeMoney(Cost, player);
						}else{
	                    	player.sendMessage(ChatColor.RED + "Not Enough Money to send your mail!");
						}
                    }else if(Settings.EnableMailCosts == false){
							ItemStack itemStack = new ItemStack(Material.PAPER);
							ItemMeta itemMeta = itemStack.getItemMeta();
						
							itemMeta.setDisplayName(ChatColor.WHITE + "Letter from " + player.getName() + ChatColor.RESET);
							ArrayList<String> lines = new ArrayList<String>();
						
							int count = 0;
							String currentLine = "";
						
							for (int i = 2; i < args.length; i++) {
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
						net.minecraft.server.v1_6_R3.ItemStack MineStack = CraftItemStack.asNMSCopy(itemStack);
						Inbox.GetInbox(args[1]).AddItem(MineStack, player);
	                  
						player.sendMessage(ChatColor.DARK_GREEN + "Textmail sent to "  + args[1] + ChatColor.RESET);
					}
					return true;
				}

				if (args[0].toLowerCase().equals("createbox")) {
					Inbox inbox;
					
					if (args.length == 1 && (player.hasPermission(Permissions.CREATE_CHEST_SELF_PERM)  || player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM))) {
						inbox = Inbox.GetInbox(player.getName());
					} else if (args.length == 2 && player.hasPermission(Permissions.CREATE_CHEST_ALL_PERM)) {
						inbox = Inbox.GetInbox(args[1]);
					} else {
						player.sendMessage(ChatColor.DARK_RED + "Too many arguments!" + ChatColor.RESET);
						return true;
					}
					
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
			}
			return true;
		}
		return false;
	}
}