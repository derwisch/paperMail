package com.github.derwisch.paperMail.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.configs.WritingPaperConfig;

public class WritingPaper{
	
	private PaperMail plugin;
	public final static String secretCode = ChatColor.translateAlternateColorCodes('&', "&7&7&7&r");
	
	public WritingPaper(PaperMail plugin){
		this.plugin = plugin;
	}
	
	public void registerWritingPaper(){
		final NamespacedKey key = new NamespacedKey(this.plugin, "WritingPaper");
		ItemStack writingPaper = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta writingPaperMeta = writingPaper.getItemMeta();
		List<String> writingPaperLore = new ArrayList<String>();
		for(String s : WritingPaperConfig.lore){
			writingPaperLore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		writingPaperMeta.setDisplayName((ChatColor.translateAlternateColorCodes('&', WritingPaperConfig.displayName)) + secretCode);
		writingPaperMeta.setLore(writingPaperLore);
    	writingPaper.setItemMeta(writingPaperMeta);
    	writingPaper.setDurability((short)0);   	
		ShapelessRecipe writingPaperRecipe = new ShapelessRecipe(key, writingPaper);
		for(Material m : WritingPaperConfig.recipe){
			writingPaperRecipe.addIngredient(m);
		}
		this.plugin.getServer().addRecipe(writingPaperRecipe);
	}
	
	public static boolean hasSecretCode(ItemStack stack){
    	if(stack!=null && stack.hasItemMeta()){
    		ItemMeta meta = stack.getItemMeta();
    		if(meta!=null){
    			if(meta.hasDisplayName()){
    				if(meta.getDisplayName().contains(secretCode)){
        				return true;
        			}
    			}   			
    		}
    	}
    	return false;
    }
	
	public static String stripSecretCode(String code){
		String result = code;
		if(code.contains(secretCode)){
			result = code.replace(secretCode, "");
		}
		return result;
	}
}
