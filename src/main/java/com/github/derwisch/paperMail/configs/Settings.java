package com.github.derwisch.paperMail.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import com.github.derwisch.paperMail.utils.UUIDUtils;

public class Settings {

	// Config values
	public static boolean EnableTextMail = true;
	public static boolean EnableItemMail = true;
	public static boolean EnableEnderchest = true;
	public static boolean EnableCustomMailboxes = true;
	
	public static int MailWindowRows = 3;
	public static int DefaultBoxRows = 4;
	
	public static Material EnderChestRequirementID = Material.ENDER_PEARL;
	public static int EnderChestRequirementDV = 0;
	
	public static String LetterSignature = "&7&oSincerely, ";
	
	public static List<UUID> InboxPlayers = new ArrayList<UUID>();
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	DefaultBoxRows = Math.max(3, config.getInt("general.DefaultBoxRows"));
        	MailWindowRows = Math.max(3, config.getInt("general.MailWindowRows"));
        	
        	EnableTextMail = config.getBoolean("general.EnableTextMail");
        	EnableItemMail = config.getBoolean("general.EnableItemMail");
        	EnableEnderchest = config.getBoolean("general.EnableEnderchest");
        	EnableCustomMailboxes = config.getBoolean("general.EnableCustomMailboxes");
        	LetterSignature = config.getString("messages.LetterSignature");
        	if(!config.getStringList("inboxPlayers").isEmpty() && config.getStringList("inboxPlayers")!=null){
        		InboxPlayers = UUIDUtils.getUUIDListfromStringList(config.getStringList("inboxPlayers"));
        	}      	
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    public static void SaveConfiguration(Configuration config) {
    	config.set("general.DefaultBoxRows", DefaultBoxRows);
    	config.set("general.MailWindowRows", MailWindowRows);
    	
    	config.set("general.EnableTextMail", EnableTextMail);
    	config.set("general.EnableItemMail", EnableItemMail);
    	config.set("general.EnableEnderchest", EnableEnderchest);
    	config.set("general.EnableCustomMailboxes", EnableCustomMailboxes);
    	
    	config.set("messages.LetterSignature", LetterSignature);
    	
    	config.set("inboxPlayers", UUIDUtils.getStringListfromUUIDList(InboxPlayers));
    }
    
}
