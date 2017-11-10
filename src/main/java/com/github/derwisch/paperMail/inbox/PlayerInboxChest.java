package com.github.derwisch.paperMail.inbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.paperMail.PaperMail;
import com.github.derwisch.paperMail.utils.SkullUtils;
import com.github.derwisch.paperMail.utils.UUIDUtils;
import com.github.derwisch.paperMail.utils.Utils;

public class PlayerInboxChest{
	
	private UUID uuid;
	private HashMap<Integer, Inventory> pages;
	private FileConfiguration playerConfig;
	public static final String secretCode = ChatColor.translateAlternateColorCodes('&', "&k&r");

	public PlayerInboxChest(UUID uuid, FileConfiguration playerConfig){
		this.uuid = uuid;
		this.playerConfig = playerConfig;
		this.pages = createInventoryList(loadStacks());
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public Inventory getPage(int pageNum){
		return this.pages.get(pageNum);
	}
	
	public HashMap<Integer, Inventory> getPages(){
		return this.pages;
	}
	
	//fix these methods so they add the stack to the last inventory in the HashMap,
	//and create a new Inventory and add it to that if the inventory is full 
	public void addItemStack(ItemStack stack){
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(stack);
		this.pages = createInventoryList(stacks);	
	}
	
	public void addItemStacks(Collection<ItemStack> stacks){
		this.pages = createInventoryList((List<ItemStack>) stacks);
	}
	
	//fix this monkey shit
	private HashMap<Integer, Inventory> createInventoryList(List<ItemStack> stacks){
		HashMap<Integer, Inventory> pages;
		if(this.pages!=null){
			pages = this.pages;
		}else{
			pages = new HashMap<Integer, Inventory>();
		}
		String title = UUIDUtils.getPlayerNamefromUUID(uuid) + "'s " + PaperMail.INBOX_GUI_TITLE;
		int j = 0;
		int pageNum = 1;
		if(!pages.isEmpty()){
			pageNum = pages.size();
		}		
		do
		{
			Inventory tempInv = Bukkit.createInventory(null, 36, "Temp");
			if(!pages.isEmpty()){
				tempInv = pages.get(pages.size());
			}
			ItemStack item = null;
			do{
				try{
					item = stacks.get(j);
				}catch(IndexOutOfBoundsException e){
					
				}
				if(stacks!=null && item!=null)
					if(Utils.inventoryCheck(tempInv, item)){
						tempInv.addItem(item);
						j++;
					}				
			}while(item!=null && Utils.inventoryCheck(tempInv, item) && tempInv!=null);			
			Inventory inventory = Bukkit.createInventory(null, 54, title);
			if(tempInv!=null){
				for(ItemStack item1 : tempInv){
					if(item1!=null){
						inventory.addItem(item1);
					}				
				}
			}
			pages.put(pageNum,inventory);
			pageNum++;
		}while(j < stacks.size());
		
		int i = 1;
		for(Inventory inventory : pages.values()){
			if(i > 1){
				inventory.setItem(36, getBackButton(i - 1));
			}
			inventory.setItem(40, getPageNumberButton(i));
			if(i < pages.size()){
				inventory.setItem(45, getForwardButton(i + 1));
			}
			i++;
		}
		return pages;
	}
	
	private ItemStack getBackButton(int pageNum){
		ItemStack backButton = SkullUtils.getCustomSkull("http://textures.minecraft.net/texture/86971dd881dbaf4fd6bcaa93614493c612f869641ed59d1c9363a3666a5fa6");
		backButton.setAmount(1);
		if(backButton.hasItemMeta()){
			ItemMeta backMeta = backButton.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.translateAlternateColorCodes('&', "Back to page " + (pageNum - 1)) + secretCode);
			backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack"));
			backMeta.setLore(lore);
			backButton.setItemMeta(backMeta);
		}
		return backButton;
	}
	
	private ItemStack getPageNumberButton(int pageNum){
		ItemStack pageNumber = new ItemStack(Material.WOOL,1,(byte)8);
		if(pageNum < 65){
			pageNumber.setAmount(pageNum);
		}
		if(pageNumber.hasItemMeta()){
			ItemMeta pageMeta = pageNumber.getItemMeta();
			List<String> lore = new ArrayList<String>();
			pageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&ePage&6 " + pageNum) + secretCode);
			pageMeta.setLore(lore);
			pageNumber.setItemMeta(pageMeta);
		}
		return pageNumber;
	}
	
	private ItemStack getForwardButton(int pageNum){
		ItemStack forwardButton = SkullUtils.getCustomSkull("http://textures.minecraft.net/texture/f32ca66056b72863e98f7f32bd7d94c7a0d796af691c9ac3a9136331352288f9");
		forwardButton.setAmount(1);
		if(forwardButton.hasItemMeta()){
			ItemMeta forwardMeta = forwardButton.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.translateAlternateColorCodes('&', "Forward to page " + (pageNum - 1)) + secretCode);
			forwardMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aForward"));
			forwardMeta.setLore(lore);
			forwardButton.setItemMeta(forwardMeta);
		}
		return forwardButton;
	}
	
	private List<ItemStack> loadStacks(){
		int i = 1;
		ItemStack stack = null;
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		do {
			stack = playerConfig.getItemStack("itemstack." + i);
			if (stack != null && stack.getType() != Material.AIR) {
				if(!stack.hasItemMeta()){
					stacks.add(stack);
				}else{
					ItemMeta meta = stack.getItemMeta();
					if(!meta.hasDisplayName() || meta == null){
						stacks.add(stack);
					}else{
						if(!meta.getDisplayName().contains(secretCode)){
							stacks.add(stack);
						}
					}
				}
				
			}
			i++;
		} while (stack != null);
		return stacks;
	}
	
	public void saveStacks(){
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for(Inventory inv : this.getPages().values()){
			for(ItemStack stack : inv){
				if(!stack.hasItemMeta()){
					stacks.add(stack);
				}else{
					ItemMeta meta = stack.getItemMeta();
					if(!meta.hasDisplayName() || meta == null){
						stacks.add(stack);
					}else{
						if(!meta.getDisplayName().contains(secretCode)){
							stacks.add(stack);
						}
					}
				}
			}
		}
		int i = 1;
		for (ItemStack stack : stacks) {
			if (stack != null && stack.getType()!=Material.AIR) {
				playerConfig.set("itemstack." + i, stack);
				i++;
			}
		}
	}
}