package com.github.derwisch.itemMail;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemMail extends JavaPlugin {
	  
	public static final String NEW_MAIL_GUI_TITLE = ChatColor.BLACK + "ItemMail: New Mail" + ChatColor.RESET;
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + "ItemMail: Inbox" + ChatColor.RESET;
	
	public static ItemMail instance;
	public static Server server;
	public static Logger logger;
	
	private ItemMailListener listener;
	private FileConfiguration configuration;
	
    @Override
    public void onEnable() {
    	instance = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.loadConfiguration(configuration);
    	
    	ItemMailCommandExecutor commandExecutor = new ItemMailCommandExecutor(this); 
    	getCommand("itemmail").setExecutor(commandExecutor);
    	
    	listener = new ItemMailListener();
        this.getServer().getPluginManager().registerEvents(listener, this);
        
        initializeRecipes();
        initializeInboxes();
        
    	logger.info("Enabled ItemMail");
    }
    
	@Override
    public void onDisable() {
		
		
		
    	getLogger().info("Disabled ItemMail");
    }
    
    private void initializeRecipes() {
		ItemStack letterPaper = new ItemStack(Material.PAPER);
		ItemMeta letterPaperMeta = letterPaper.getItemMeta();
		ArrayList<String> letterPaperLore = new ArrayList<String>();
		letterPaperMeta.setDisplayName(ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET);
		letterPaperLore.add(ChatColor.GRAY + "Used to send a letter" + ChatColor.RESET);
		letterPaperMeta.setLore(letterPaperLore);
    	letterPaper.setItemMeta(letterPaperMeta);
		
		ShapelessRecipe letterPaperRecipe = new ShapelessRecipe(letterPaper);
		letterPaperRecipe.addIngredient(Material.PAPER);
		letterPaperRecipe.addIngredient(Material.INK_SACK);
		letterPaperRecipe.addIngredient(Material.FEATHER);
		
		this.getServer().addRecipe(letterPaperRecipe);
    }

    private void initializeInboxes() {
		for (Player player : getServer().getOnlinePlayers()) {
			if (player == null) {
				continue;
			}
			Inbox.AddInbox(player);
		}
		for (OfflinePlayer offPlayer : getServer().getOfflinePlayers()) {
			
			Player player = offPlayer.getPlayer();
			
			if (player == null) {
				continue;
			}
			Inbox.AddInbox(player);
		}	
	}
    
    

}
