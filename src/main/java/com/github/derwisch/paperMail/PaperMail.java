package com.github.derwisch.paperMail;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.derwisch.paperMail.configs.CustomMailboxConfig;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.configs.WritingPaperConfig;
import com.github.derwisch.paperMail.listeners.PaperMailListener;
import com.github.derwisch.paperMail.recipes.CustomMailboxes;
import com.github.derwisch.paperMail.recipes.WritingPaper;

public class PaperMail extends JavaPlugin {
	  
	public static final String NEW_MAIL_GUI_TITLE = ChatColor.BLACK + "PaperMail: New Mail" + ChatColor.RESET;
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + "PaperMail: Inbox" + ChatColor.RESET;
	public static PaperMail instance;
	public static Server server;
	public static Logger logger;
	
	private PaperMailListener listener;
	private FileConfiguration configuration;
	private CustomMailboxConfig mailboxConfig;
	private WritingPaperConfig writingpaperConfig;
	private CustomMailboxes customMailboxes;
	
    @Override
    public void onEnable() {
       	instance = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.LoadConfiguration(configuration);
    	
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
		mailboxConfig = new CustomMailboxConfig(instance);
		mailboxConfig.initMailBoxConfig();
		writingpaperConfig = new WritingPaperConfig(instance);
		writingpaperConfig.initWritingPaperConfig();
		new WritingPaper(instance).registerWritingPaper();
		customMailboxes = new CustomMailboxes(instance, mailboxConfig.getCustomMailboxes());
		customMailboxes.registerMailboxes();
    }

    private void initializeInboxes() {
		for (Player player : getServer().getOnlinePlayers()) {
			if (player == null) {
				continue;
			}
			Inbox.AddInbox(player.getUniqueId());
		}
		for (OfflinePlayer offPlayer : getServer().getOfflinePlayers()) {
			if(offPlayer.hasPlayedBefore()){
				Player player = offPlayer.getPlayer();
				
				if (player == null) {
					continue;
				}
				Inbox.AddInbox(player.getUniqueId());
			}			
		}
	}
    
    public FileConfiguration getConfiguration(){
    	return this.configuration;
    }
}
