package com.github.derwisch.paperMail.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.utils.Utils;

public class CustomMailboxConfig{
	
	private File mailboxFile;
	private File dataFolder;
	private FileConfiguration mailboxes;
	private List<CustomMailbox> customMailboxes = new ArrayList<CustomMailbox>();
	
	private PaperMail plugin;
	
	public CustomMailboxConfig(PaperMail plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder().toString() + "/recipes");
	}
		
	public void initMailBoxConfig(){
		saveDefaultMailBoxConfig();
		loadMailBoxConfig();
	}
	
    ////////////////////////////////////////////////////////////
	public void saveDefaultMailBoxConfig() {
		//pickup toggle mailboxes
		if(!(dataFolder.exists())){
			dataFolder.mkdir();
		}
	    if (mailboxFile == null) {
	        mailboxFile = new File(dataFolder, "mailBoxConfig.yml");
	    }
	    if (!mailboxFile.exists()) {           
	        plugin.saveResource("recipes/mailBoxConfig.yml", false);
	    }    
    }
	  
	public void loadMailBoxConfig(){
		//pickup toggle mailboxes
		List<String> ingredients = new ArrayList<String>();
		mailboxes = YamlConfiguration.loadConfiguration(mailboxFile);
		if(mailboxes!=null){
			for(String s : mailboxes.getKeys(false)){
				CustomMailbox mb = new CustomMailbox();
				mb.setKey(s);
				mb.setUrl(mailboxes.getString(s + ".headURL"));
				ingredients.add(mailboxes.getString(s + ".recipe.item1"));
				ingredients.add(mailboxes.getString(s + ".recipe.item2"));
				ingredients.add(mailboxes.getString(s + ".recipe.item3"));
				ingredients.add(mailboxes.getString(s + ".recipe.item4"));
				ingredients.add(mailboxes.getString(s + ".recipe.item5"));
				ingredients.add(mailboxes.getString(s + ".recipe.item6"));
				ingredients.add(mailboxes.getString(s + ".recipe.item7"));
				ingredients.add(mailboxes.getString(s + ".recipe.item8"));
				ingredients.add(mailboxes.getString(s + ".recipe.item9"));
				mb.setRecipe(Utils.getMaterialListfromStringList(ingredients));
				mb.setPermission(mailboxes.getString(s + ".permission"));
				mb.setDisplayname(mailboxes.getString(s + ".displayname"));
				mb.setLore(mailboxes.getStringList(s + ".lore"));
				customMailboxes.add(mb);
			}
		}		
	}
	  
	public List<CustomMailbox> getCustomMailboxes(){
		return this.customMailboxes;
	}
	
}