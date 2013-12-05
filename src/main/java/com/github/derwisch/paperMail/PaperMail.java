package com.github.derwisch.paperMail;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperMail extends JavaPlugin {
	  
	public static final String NEW_MAIL_GUI_TITLE = ChatColor.BLACK + "PaperMail: New Mail" + ChatColor.RESET;
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + "PaperMail: Inbox" + ChatColor.RESET;
	
	public static PaperMail instance;
	public static Server server;
	public static Logger logger;
	public static Economy economy = null;
	
	private PaperMailListener listener;
	private FileConfiguration configuration;
	
    @Override
    public void onEnable() {
    	instance = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.LoadConfiguration(configuration);
    	//Load Economy
    	if (setupEconomy().booleanValue())
    		System.out.println(this + " linked into " + economy.getName() + ", via Vault");
        if ((setupEconomy() == false) && (Settings.EnableMailCosts == true)) {
        	System.out.println(this + ": Vault economy not found, switching to gold ingots!");
        }
    	
    	PaperMailCommandExecutor commandExecutor = new PaperMailCommandExecutor(this); 
    	getCommand("papermail").setExecutor(commandExecutor);
    	
    	listener = new PaperMailListener();
        this.getServer().getPluginManager().registerEvents(listener, this);
        
        initializeRecipes();
        initializeInboxes();
        
    	logger.info("Enabled PaperMail");
    }
    
	@Override
    public void onDisable() {
		Inbox.SaveAll();
		Settings.SaveConfiguration(configuration);
		this.saveConfig();
    	getLogger().info("Disabled PaperMail");
    }
    
    private void initializeRecipes() {
		ItemStack letterPaper = new ItemStack(Settings.MailItemID);
		ItemMeta letterPaperMeta = letterPaper.getItemMeta();
		ArrayList<String> letterPaperLore = new ArrayList<String>();
		letterPaperMeta.setDisplayName(ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET);
		letterPaperLore.add(ChatColor.GRAY + "Used to send a letter" + ChatColor.RESET);
		letterPaperMeta.setLore(letterPaperLore);
    	letterPaper.setItemMeta(letterPaperMeta);
    	letterPaper.setDurability((short)Settings.MailItemDV);
		
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
			Inbox.AddInbox(player.getName());
		}
		for (OfflinePlayer offPlayer : getServer().getOfflinePlayers()) {
			
			Player player = offPlayer.getPlayer();
			
			if (player == null) {
				continue;
			}
			Inbox.AddInbox(player.getName());
		}
	}
    
    public static boolean isGoldIngot() {
        return economy == null;
     }

     
   public Boolean setupEconomy()
     {
       Plugin vault = getServer().getPluginManager().getPlugin("Vault");
      if (vault == null) {
        return Boolean.valueOf(false);
       }
     RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
     if (economyProvider != null) {
        economy = (Economy)economyProvider.getProvider();
       }

        return Boolean.valueOf(economy != null);
     }
}
