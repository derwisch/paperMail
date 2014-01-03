package com.github.derwisch.paperMail;
import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

public class PaperMailQueue{
	
	public void saveQueue(File yamlfile, Inventory inventory) throws IOException {
			YamlConfiguration yaml = new Utf8YamlConfiguration();
			for (int i = 0; i < inventory.getSize(); i++) {
				CraftItemStack stack = (CraftItemStack)inventory.getItem(i);
				if (stack != null) {
					String item = InventoryUtils.itemstackToString(stack);
					yaml.set("newitemstack." + i, item);
				}
				if (stack == null)
				{
		        String item = null;
		        yaml.set("newitemstack." + i, item);
				}
		    }
		yaml.save(yamlfile);
	}
}