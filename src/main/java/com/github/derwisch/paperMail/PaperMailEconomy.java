package com.github.derwisch.paperMail;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;


public class PaperMailEconomy{
	public static boolean hasMoney = true;
	
  public static int goldCounter(Player player){    /*Count the total number of Gold Ingots in Inventory if using Gold Ingot System*/
	  int gold = 0;
	  for (ItemStack i : player.getInventory()) {
		  if ((i != null) && (i.getType() == Material.GOLD_INGOT)){
				int counter = 0;
				counter = i.getAmount();
				gold = gold + counter;
			}
			}
	  return gold;
		}
  
  public static void takeGold(int Price, Player player){    /*Take Gold Ingots from Inventory if using Gold Ingot System*/
	  int change;
	  int goldLeft = Price;
	  for (ItemStack i : player.getInventory().getContents()){
		  if ((i != null) && (i.getType() == Material.GOLD_INGOT)){
			  if (i.getAmount() >= goldLeft){
				  change = i.getAmount() - goldLeft;
				  if(change == 0)
				  {
					  player.getInventory().removeItem(i);
					  player.updateInventory();
					  return;
				  }
				  if(change != 0){
					  i.setAmount(change);
					  return;
				  }
			  }
			  if(i.getAmount() < goldLeft){
				goldLeft = goldLeft - i.getAmount();
				player.getInventory().removeItem(i);
				player.updateInventory();
			  	}
			  }
			  }
	  }
  
  public static void takeMoney(Double price, Player player){
    if (!(PaperMail.isGoldIngot())) {
				PaperMail.economy.withdrawPlayer(player.getName(), price.doubleValue());
				player.sendMessage(ChatColor.GREEN + "%price% removed from Wallet!".replace("%price%", new StringBuilder().append(ChatColor.WHITE).append(price.toString()).toString()));   
    }
   else {
       int goldPrice = (int)Math.ceil(price.doubleValue());  
       takeGold(goldPrice, player);
       StringBuilder sb = new StringBuilder();
       sb.append("");
       sb.append(goldPrice);
       player.sendMessage(ChatColor.GREEN + "%price% Gold Ingots removed from Inventory!".replace("%price%", sb.append(ChatColor.WHITE)).toString());    
      }
    }
  public static boolean hasMoney(Double price, Player player){
	  if ((!(PaperMail.isGoldIngot())) && (Settings.EnableMailCosts != false) && (price != 0)) {
		  if (PaperMail.economy.getBalance(player.getName()) < price.doubleValue()) {
		  		hasMoney = false;
		  		return false;
				 }
	  }else if((PaperMail.isGoldIngot()) && (Settings.EnableMailCosts != false) && (price != 0)){
		  int goldAmount = goldCounter(player);
		  int goldPrice = (int)Math.ceil(price.doubleValue());
		  if (goldAmount < goldPrice) {
		         hasMoney = false;
		         return false;
		  }
		  
	  }
	  hasMoney = true;
	  return true;
  }
  
 public static double ItemCost(InventoryClickEvent event){ 
	int NumItems = 0;
	double perCost = 0;
	perCost = Settings.ItemCost;
	Inventory Inventory = event.getInventory();
	for (int i = 0; i < Inventory.getSize(); i++) {
		ItemStack itemStack = Inventory.getItem(i);
		
		if (itemStack == null)
			continue;
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.getDisplayName() != PaperMailGUI.SEND_BUTTON_ON_TITLE && 
			itemMeta.getDisplayName() != PaperMailGUI.CANCEL_BUTTON_TITLE && 
			itemMeta.getDisplayName() != PaperMailGUI.ENDERCHEST_BUTTON_TITLE) {
				if((Settings.PerItemCosts) == true)
					NumItems = NumItems +1;
		}
	}
		if ((Settings.EnableMailCosts == true) && (Settings.PerItemCosts == true) && (perCost != 0))
		{
				NumItems = NumItems - 1;
				perCost = Settings.ItemCost * NumItems;
		}
	return perCost;
 }
}



