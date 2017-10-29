package com.github.derwisch.paperMail.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

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
}