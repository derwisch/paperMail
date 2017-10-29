package com.github.derwisch.paperMail.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.TileEntitySkull;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.UUID;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 *
 * @author xigsag, SBPrime
 */
public class SkullUtils {

    private static final Base64 base64 = new Base64();
    public final static String secretCode = ChatColor.translateAlternateColorCodes('&', "&6&6&6");

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        
        return head;
    }

    /**
     * Return a skull of a player.
     *
     * @param name player's name
     * @return itemstack
     */
	public static ItemStack getPlayerSkull(String name) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        if(UUIDUtils.getUUIDfromPlayerName(name)!=null){
        	meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUIDUtils.getUUIDfromPlayerName(name)));
        }else{
        	meta.setDisplayName("Steve");
        }        
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void setBlocktoSkull(String skinUrl, Block block) {
        block.setType(Material.SKULL);
        org.bukkit.block.Skull skullData = (org.bukkit.block.Skull) block.getState();
        skullData.setSkullType(SkullType.PLAYER);
      
        TileEntitySkull skullTile = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        skullTile.setGameProfile(getNonPlayerProfile(skinUrl));
        block.getState().update(true);
    }
    
    public static GameProfile getNonPlayerProfile(String skinURL) {
        GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
        return newSkinProfile;
    }
    
    public static boolean hasSecretCode(ItemStack stack){
    	if(stack!=null){
    		ItemMeta meta = stack.getItemMeta();
    		if(meta!=null){
    			if(meta.getDisplayName().contains(secretCode)){
    				return true;
    			}
    		}
    	}
    	return false;
    }
}
 