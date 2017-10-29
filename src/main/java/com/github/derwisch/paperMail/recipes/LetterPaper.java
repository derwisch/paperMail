package com.github.derwisch.paperMail.recipes;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.configs.Settings;

public class LetterPaper
{
	private PaperMail plugin;
	
	public LetterPaper(PaperMail plugin){
		this.plugin = plugin;
	}
	
	public void registerLetterPaper(){
		final NamespacedKey key = new NamespacedKey(this.plugin, "LetterPaper");
		ItemStack letterPaper = new ItemStack(Settings.MailItemID);
		ItemMeta letterPaperMeta = letterPaper.getItemMeta();
		ArrayList<String> letterPaperLore = new ArrayList<String>();
		letterPaperMeta.setDisplayName(ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET);
		letterPaperLore.add(ChatColor.GRAY + "Used to send a letter" + ChatColor.RESET);
		letterPaperMeta.setLore(letterPaperLore);
    	letterPaper.setItemMeta(letterPaperMeta);
    	letterPaper.setDurability((short)Settings.MailItemDV);   	
		ShapelessRecipe letterPaperRecipe = new ShapelessRecipe(key, letterPaper);
		letterPaperRecipe.addIngredient(Material.PAPER);
		letterPaperRecipe.addIngredient(Material.INK_SACK);
		letterPaperRecipe.addIngredient(Material.FEATHER);
		
		this.plugin.getServer().addRecipe(letterPaperRecipe);
	}
}