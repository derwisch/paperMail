package com.github.derwisch.paperMail.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.utils.Utils;

public class WritingPaperConfig{
	
	private File writingPaperFile;
	private File dataFolder;
	private FileConfiguration writingPaper;
	
	private PaperMail plugin;
	
	public static String displayName = "&8Writing Paper";
	public static String permission = "papermail.craft.writingpaper";
	public static List<String> lore = new ArrayList<String>();
	public static List<Material> recipe = new ArrayList<Material>();
	
	public WritingPaperConfig(PaperMail plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder().toString() + "/recipes");
	}
		
	public void initWritingPaperConfig(){
		saveDefaultWritingPaperConfig();
		loadWritingPaperConfig();
	}
	
    ////////////////////////////////////////////////////////////
	public void saveDefaultWritingPaperConfig() {
		//pickup toggle writingPaper
		if(!(dataFolder.exists())){
			dataFolder.mkdir();
		}
	    if (writingPaperFile == null) {
	        writingPaperFile = new File(dataFolder, "writingPaperConfig.yml");
	    }
	    if (!writingPaperFile.exists()) {           
	        plugin.saveResource("recipes/writingPaperConfig.yml", false);
	    }    
    }
	  
	public void loadWritingPaperConfig(){
		//pickup toggle writingPaper
		writingPaper = YamlConfiguration.loadConfiguration(writingPaperFile);
		if(writingPaper!=null){
			displayName = writingPaper.getString("WritingPaper.displayname");
			permission = writingPaper.getString("WritingPaper.permission");
			lore = writingPaper.getStringList("WritingPaper.lore");
			if(!writingPaper.getStringList("WritingPaper.recipe").isEmpty() && writingPaper.getStringList("WritingPaper.recipe")!=null){
				recipe = Utils.getMaterialListfromStringList(writingPaper.getStringList("WritingPaper.recipe"));
			}
			if(recipe==null){
				recipe = new ArrayList<Material>();
			}
			if(recipe.size() > 9){
				recipe = recipe.subList(0, 8);
			}
			if(recipe.isEmpty()){
				recipe.add(Material.FEATHER);
				recipe.add(Material.INK_SACK);
				recipe.add(Material.PAPER);
			}		
		}		
	}
}