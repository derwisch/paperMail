package com.github.derwisch.paperMail.configs;

import java.util.List;

import org.bukkit.Material;

public class CustomMailbox{
	
	private String key;
	private String url;
	private List<Material> recipe;
	private String permission;
	private String displayname;
	private List<String> lore;
	
	public CustomMailbox(){
	}

	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<Material> getRecipe() {
		return this.recipe;
	}
	
	public void setRecipe(List<Material> recipe){
		this.recipe = recipe;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getDisplayname() {
		return this.displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}


}