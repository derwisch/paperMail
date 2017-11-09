package com.github.derwisch.paperMail.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils{
	public static List<Material> getMaterialListfromStringList(List<String> list){
		List<Material> matList = new ArrayList<Material>();
		for(String s : list){
			if(s=="" || s==null){
				Bukkit.getLogger().info("material name is null");
				continue;
			}
			matList.add(Material.valueOf(s.toUpperCase()));
		}
		return matList;
	}
	
	public static boolean areLocationsEqual(Location one, Location two){
		if(one.getWorld() == two.getWorld()){
			if(one.getBlockX() == two.getBlockX()){
				if(one.getBlockY() == two.getBlockY()){
					if(one.getBlockZ() == two.getBlockZ()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean inventoryCheck(Inventory inv, ItemStack is) {    
		ItemStack itemToAdd = is;
		int freeSpace = 0;
		for (ItemStack i : inv) {
			if (i == null) {
				freeSpace+=itemToAdd.getType().getMaxStackSize();
			} else if (i.getType() == itemToAdd.getType()) {
				freeSpace+=i.getType().getMaxStackSize() - i.getAmount();
			}
		}
		if (itemToAdd.getAmount() <= freeSpace) {
			return true;
			//Add the item, inventory has space
		} else {
			return false;
			//Don't add the item, Inventory is full
		}
	}
}