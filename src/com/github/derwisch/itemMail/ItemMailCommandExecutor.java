package com.github.derwisch.itemMail;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMailCommandExecutor implements CommandExecutor {
    
	private ItemMail plugin;
 
	public ItemMailCommandExecutor(ItemMail plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("ItemMailCommandExecutor initialized");
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//System.out.println("Label: " + label);
		//for (String arg : args) {
		//	System.out.println("Argument: " + arg);
		//}
		
		if (cmd.getName().equalsIgnoreCase("itemmail")){
			if (!(sender instanceof Player) && args.length > 0) {
				sender.sendMessage("This command can only be run by a player.");
				return true;
			} else {
				if (args.length == 0) {
					sender.sendMessage("Current Version of ItemMail is " + ItemMail.instance.getDescription().getVersion());
					return true;
				}
				
				Player player = (Player) sender;
				
				
				if (!args[0].toLowerCase().equals("sendtext") && !args[0].toLowerCase().equals("createbox")) {
					player.sendMessage(ChatColor.DARK_RED + "invalid argument \"" + args[0] + "\"" + ChatColor.RESET);
					return true;
				}

				if (Settings.EnableTextMail && args[0].toLowerCase().equals("sendtext")) {
					if (args.length < 3) {
						if (args.length < 2) {
							player.sendMessage(ChatColor.DARK_RED + "Missing arguments for textmail!" + ChatColor.RESET);
							return true;
						}
						player.sendMessage(ChatColor.DARK_RED + "Missing text of textmail!" + ChatColor.RESET);
						return true;
					}
					
					Player recipient = ItemMail.server.getPlayer(args[1]);
					if (recipient == null) {
						recipient = ItemMail.server.getOfflinePlayer(args[1]).getPlayer();
						if (recipient == null) {
							player.sendMessage(ChatColor.DARK_RED + "Player not found" + ChatColor.RESET);
							return true;
						}
					}
					
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
					
					Inbox.GetInbox(recipient).AddItem(itemStack, player);

					System.out.println("Textmail sent to "  + recipient.getDisplayName());
					return true;
				}

				if (args[0].toLowerCase().equals("createbox")) {
					if (args.length < 2) {
						player.sendMessage(ChatColor.DARK_RED + "No player defined!" + ChatColor.RESET);
						return true;
					}
					
					Block block = player.getTargetBlock(null, 10);
					
					if (block != null && block.getType() == Material.CHEST) {

						Player recipient = ItemMail.server.getPlayer(args[1]);
						if (recipient == null) {
							recipient = ItemMail.server.getOfflinePlayer(args[1]).getPlayer();
							if (recipient == null) {
								player.sendMessage(ChatColor.DARK_RED + "Player not found" + ChatColor.RESET);
								return true;
							}
						}
						
						Chest chest = (Chest)block.getState();
						Inbox inbox = Inbox.GetInbox(recipient);
						
						inbox.inboxChest = chest;
						player.sendMessage(ChatColor.DARK_GREEN + "Inbox created!" + ChatColor.RESET);
						return true;
					} else {
						player.sendMessage(ChatColor.DARK_RED + "You must focus a chest" + ChatColor.RESET);
						return true;
					}
					
				}
			}
			System.out.println("End with unknown reason");
			return true;
		}
		System.out.println("Non matching command");
		return false;
	}
}