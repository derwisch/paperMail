package com.github.derwisch.paperMail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.recipes.WritingPaper;
import com.github.derwisch.paperMail.utils.UUIDUtils;

public class PaperMailCommandExecutor implements CommandExecutor {
    
	private PaperMail plugin;
 
	public PaperMailCommandExecutor(PaperMail plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("ItemMailCommandExecutor initialized");
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){		
		if (cmd.getName().equalsIgnoreCase("papermail")){
			if (!(sender instanceof Player) && args.length > 0) {
				sender.sendMessage("Current Version of PaperMail is " + plugin.getDescription().getVersion());
				return true;
			} else {
				Player player = (Player) sender;
				if (args.length == 0) {
					sender.sendMessage("Current Version of PaperMail is " + plugin.getDescription().getVersion());
					return true;
				}						
				if (!args[0].toLowerCase().equals("sendtext")) {
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
					
					if(UUIDUtils.getUUIDfromPlayerName(args[1])==null){
						player.sendMessage(ChatColor.DARK_RED + "You can't send mail to someone who doesn't exist!" + ChatColor.RESET);
						return true;
					}
					
					UUID uid = UUIDUtils.getUUIDfromPlayerName(args[1]);
					ItemStack letterpaper = new ItemStack(Material.PAPER, 1);
	        		ItemMeta letterMeta = letterpaper.getItemMeta();
	        		letterMeta.setDisplayName(args[1] + WritingPaper.secretCode);
	        		List<String> letterLore = new ArrayList<String>();
					int count = 0;
					String currentLine = "";				
					for (int i = 2; i < args.length; i++) {
						currentLine += args[i] + " ";
						count += args[i].length() + 1;
						if (++count >= 20) {
							count = 0;
							letterLore.add(ChatColor.translateAlternateColorCodes('&', ("&7&o" + currentLine + "&r")));
							currentLine = "";
						}
					}				
					if (currentLine != "") {
						letterLore.add(ChatColor.translateAlternateColorCodes('&', ("&7&o" + currentLine + "&r")));	
					}
					letterLore.add(ChatColor.translateAlternateColorCodes('&', (Settings.LetterSignature + player.getName())));
	        		letterMeta.setLore(letterLore);
	        		letterpaper.setItemMeta(letterMeta);
					this.plugin.getInboxesManager().getInbox(uid).addItem(letterpaper);
					player.sendMessage(ChatColor.DARK_GREEN + "Textmail sent to "  + args[1] + ChatColor.RESET);
					return true;
				}
			}
			return true;
		}
		return false;
	}
}