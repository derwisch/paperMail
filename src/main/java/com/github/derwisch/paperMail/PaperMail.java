package com.github.derwisch.paperMail;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.derwisch.paperMail.configs.CustomMailboxConfig;
import com.github.derwisch.paperMail.configs.Settings;
import com.github.derwisch.paperMail.configs.WritingPaperConfig;
import com.github.derwisch.paperMail.listeners.MailBoxListener;
import com.github.derwisch.paperMail.listeners.PaperMailListener;
import com.github.derwisch.paperMail.recipes.CustomMailboxes;
import com.github.derwisch.paperMail.recipes.WritingPaper;

import me.drkmatr1984.customevents.CustomEvents;

public class PaperMail extends JavaPlugin {
	  
	public static final String NEW_MAIL_GUI_TITLE = ChatColor.BLACK + "PaperMail: New Mail" + ChatColor.RESET;
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + "InboxesManager" + ChatColor.RESET;
	private static PaperMail plugin;
	public static Server server;
	public static Logger logger;
	
	private PaperMailListener listener;
	private MailBoxListener mailBoxListener;
	private FileConfiguration configuration;
	private CustomMailboxConfig mailboxConfig;
	private WritingPaperConfig writingpaperConfig;
	private CustomMailboxes customMailboxes;
	private InboxesManager inboxesManager;
	
    @Override
    public void onEnable() {
       	plugin = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.LoadConfiguration(configuration);
    	
    	PaperMailCommandExecutor commandExecutor = new PaperMailCommandExecutor(this); 
    	getCommand("papermail").setExecutor(commandExecutor);
    	CustomEvents customEvents = new CustomEvents(this, true);
    	customEvents.initializeLib();
    	inboxesManager = new InboxesManager(plugin);
    	this.listener = new PaperMailListener(this);
    	initializeRecipes();
        this.getServer().getPluginManager().registerEvents(listener, this);
        mailBoxListener = new MailBoxListener(this);
        this.getServer().getPluginManager().registerEvents(mailBoxListener, this);           
    	logger.info("Enabled PaperMail");
    }
    
	@Override
    public void onDisable() {
		inboxesManager.saveInboxes();
		Settings.SaveConfiguration(configuration);
		this.saveConfig();
    	getLogger().info("Disabled PaperMail");
    }
    
    private void initializeRecipes() {		
		mailboxConfig = new CustomMailboxConfig(plugin);
		mailboxConfig.initMailBoxConfig();
		writingpaperConfig = new WritingPaperConfig(plugin);
		writingpaperConfig.initWritingPaperConfig();
		new WritingPaper(plugin).registerWritingPaper();
		customMailboxes = new CustomMailboxes(plugin, mailboxConfig.getCustomMailboxes());
		customMailboxes.registerMailboxes();
    }
    
    public InboxesManager getInboxesManager(){
    	return this.inboxesManager;
    }
    
    public FileConfiguration getConfiguration(){
    	return this.configuration;
    }
}
