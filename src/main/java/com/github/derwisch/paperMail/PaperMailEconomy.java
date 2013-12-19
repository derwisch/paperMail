package com.github.derwisch.paperMail;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PaperMailEconomy{
	public static boolean hasMoney = true;
	public static String BANK_NOTE_NAME;
	
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
  //take money from the player
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
  //check if player has correct amount of currency, return true if they do, false if they don't
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
  
//Converts an amount of a player's money into a custom Bank Note Item
  public static ItemStack getBankNote(double amount, Player player){
		ItemStack bankNote = new ItemStack(Material.PAPER); 
		BANK_NOTE_NAME = PaperMailGUI.BANK_NOTE_DISPLAY + ChatColor.RED + "(" + ChatColor.GREEN + "$" + ChatColor.GOLD + amount + ChatColor.RED +  ")" + ChatColor.RESET;
		ItemMeta bankNoteMeta = bankNote.getItemMeta();
		ArrayList<String> bankNoteLore = new ArrayList<String>();
		bankNoteLore.add(ChatColor.GRAY + "Right Click this Bank");
		bankNoteLore.add(ChatColor.GRAY + "Note to deposit the sum");
		bankNoteLore.add(ChatColor.GRAY + "into your Account");
		bankNoteLore.add(ChatColor.GREEN + "$" + ChatColor.GOLD + amount + ChatColor.RESET);
		bankNoteMeta.setDisplayName(BANK_NOTE_NAME);
		bankNoteMeta.setLore(bankNoteLore);
		bankNote.setItemMeta(bankNoteMeta);
		PaperMailEconomy.takeMoney(amount, player);
		return bankNote;
	}
  
  public static void cashBankNote(Player player, double amount){
	  String playerName = player.getName();
	  PaperMail.economy.depositPlayer(playerName, amount);
  }
}
