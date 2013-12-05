package com.github.derwisch.paperMail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.Configuration;

public class Settings {

	// Config values
	public static boolean EnableTextMail = true;
	public static boolean EnableItemMail = true;
	public static boolean EnableEnderchest = true;
	public static boolean EnableMailCosts = false;
	public static boolean PerItemCosts = false;

	public static int MailWindowRows = 3;
	public static int DefaultBoxRows = 4;
	
	public static int EnderChestRequirementID = 368;
	public static int EnderChestRequirementDV = 0;
	
	public static double Price = 0;
	public static double ItemCost = 0;
	public static int MailItemID = 339;
	public static int MailItemDV = 1;
	public static String MailItemName = "Letter paper";
	
	public static List<String> InboxPlayers = new ArrayList<String>();
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	DefaultBoxRows = Math.max(3, config.getInt("general.DefaultBoxRows"));
        	MailWindowRows = Math.max(3, config.getInt("general.MailWindowRows"));
        	
        	EnableTextMail = config.getBoolean("general.EnableTextMail");
        	EnableItemMail = config.getBoolean("general.EnableItemMail");
        	EnableEnderchest = config.getBoolean("general.EnableEnderchest");
        	EnableMailCosts = config.getBoolean("general.EnableMailCosts");
        	PerItemCosts = config.getBoolean("general.EnablePerItemCosts");
        	
        	Price = config.getDouble("general.CostToText");
        	ItemCost = config.getDouble("general.SendItemCost");

        	MailItemID = config.getInt("general.MailItemID");
        	MailItemDV = config.getInt("general.MailItemDV");
        	MailItemName = config.getString("general.MailItemName");
        	
        	InboxPlayers = config.getStringList("inboxPlayers");
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
    	config.set("general.EnableMailCosts", EnableMailCosts);
    	config.set("general.EnablePerItemCosts", PerItemCosts);
    	
    	config.set("general.CostToText", Price);
    	config.set("general.SendItemCost", ItemCost);
    	
    	config.set("general.MailItemID", MailItemID);
    	config.set("general.MailItemDV", MailItemDV);
    	config.set("general.MailItemName", MailItemName);
    	    	
    	config.set("inboxPlayers", InboxPlayers);
    }
    
}
