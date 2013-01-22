package com.github.derwisch.paperMail;

import org.bukkit.configuration.Configuration;

public class Settings {

	// Config values
	public static boolean EnableTextMail = true;
	public static boolean EnableItemMail = true;
	public static boolean EnableEnderchest = true;

	public static int MailWindowRows = 3;
	public static int DefaultBoxRows = 4;
	
	public static int EnderChestRequirementID = 368;
	public static int EnderChestRequirementDV = 0;
	
	public static int MailItemID = 339;
	public static int MailItemDV = 1;
	public static String MailItemName = "Letter paper";
	
	// Messages
	public static String Inbox_Full_Send_Message = "The mailbox of {0} is full, the items have been sent back to you.";
	public static String Item_Missing_Message = "You are missing {0} to create a new letter.";
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	DefaultBoxRows = Math.max(3, config.getInt("general.DefaultBoxRows"));
        	MailWindowRows = Math.max(3, config.getInt("general.MailWindowRows"));
        	
        	EnableTextMail = config.getBoolean("general.EnableTextMail");
        	EnableItemMail = config.getBoolean("general.EnableItemMail");
        	EnableEnderchest = config.getBoolean("general.EnableEnderchest");

        	EnderChestRequirementID = config.getInt("general.EnderChestRequirementID");
        	EnderChestRequirementDV = config.getInt("general.EnderChestRequirementDV");
        	
        	MailItemID = config.getInt("general.MailItemID");
        	MailItemDV = config.getInt("general.MailItemDV");
        	MailItemName = config.getString("general.MailItemName");
        	

        	Inbox_Full_Send_Message = config.getString("messages.inbox-full-send");
        	Item_Missing_Message = config.getString("messages.ingredient-missing");
        	
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
    	
    	config.set("general.EnderChestRequirementID", EnderChestRequirementID);
    	config.set("general.EnderChestRequirementDV", EnderChestRequirementDV);
    	
    	config.set("general.MailItemID", MailItemID);
    	config.set("general.MailItemDV", MailItemDV);
    	config.set("general.MailItemName", MailItemName);
    	
    	config.set("messages.inbox-full-send", Inbox_Full_Send_Message);
    	config.set("messages.ingredient-missing", Item_Missing_Message);
    }
    
}
