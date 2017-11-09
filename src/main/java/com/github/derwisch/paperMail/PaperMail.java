package com.github.derwisch.paperMail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + "InboxesAccessor" + ChatColor.RESET;
	public static PaperMail instance;
	public static Server server;
	public static Logger logger;
	
	private PaperMailListener listener;
	private MailBoxListener mailBoxListener;
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
    	CustomEvents customEvents = new CustomEvents(this, true);
    	customEvents.initializeLib();
    	this.listener = new PaperMailListener();
        this.getServer().getPluginManager().registerEvents(listener, this);
        mailBoxListener = new MailBoxListener();
        this.getServer().getPluginManager().registerEvents(mailBoxListener, this);
        
        initializeRecipes();
        initializeInboxes();
        
    	logger.info("Enabled PaperMail");
    }
    
	@Override
    public void onDisable() {
		InboxesAccessor.SaveAll();
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
    	List<String> players = new ArrayList<String>();
    	File dataFolder = new File(this.getDataFolder().toString()+"/players");
    	if(dataFolder.listFiles()!=null){
    		File[] files = dataFolder.listFiles();
        	for(File file : files){
            	players.add(file.getName().toString().replaceAll(".yml", ""));
            }    	
            for(String s : players){
            	if(!s.isEmpty() && s!=null){
            		InboxesAccessor.Inboxes.add(new InboxesAccessor(UUID.fromString(s)));
            	}
            }
        }
    }
    
    public FileConfiguration getConfiguration(){
    	return this.configuration;
    }
}
