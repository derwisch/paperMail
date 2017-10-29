package com.github.derwisch.paperMail.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.configs.CustomMailbox;
import com.github.derwisch.paperMail.utils.SkullUtils;

public class CustomMailboxes
{
	private PaperMail plugin;
	private List<CustomMailbox> customMailBoxes;
	
	public CustomMailboxes(PaperMail plugin, List<CustomMailbox> customMailBoxes){
		this.plugin = plugin;
		this.customMailBoxes = customMailBoxes;
	}
	
	public void registerMailboxes(){
		String secretCode = ChatColor.translateAlternateColorCodes('&', "&6&6&6");
		for(CustomMailbox c : this.customMailBoxes){
			ItemStack mailbox = SkullUtils.getCustomSkull(c.getUrl());
			ItemMeta mailBoxMeta = mailbox.getItemMeta();
			List<String> mailBoxLore = new ArrayList<String>();
			for(String s : c.getLore()){
				mailBoxLore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			mailBoxMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',c.getDisplayname()) + secretCode);
			mailBoxMeta.setLore(mailBoxLore);
			mailbox.setItemMeta(mailBoxMeta);
			NamespacedKey key = new NamespacedKey(this.plugin, c.getKey());		
			ShapedRecipe mailBoxRecipe = new ShapedRecipe(key, mailbox);
			mailBoxRecipe.shape("012", "345", "678");
			for(int i = 0; i < c.getRecipe().size(); i++){
				Material mat = c.getRecipe().get(i);
				if(mat!=null){
					mailBoxRecipe.setIngredient((((Integer)i).toString().charAt(0)), mat);
				}				
			}			
			Bukkit.addRecipe(mailBoxRecipe);
			Bukkit.getLogger().info(key + " Successfully Registered");
		}		
	}
}