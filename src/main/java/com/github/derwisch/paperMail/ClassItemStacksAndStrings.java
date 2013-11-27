package com.github.derwisch.paperMail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
 


import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
 


import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
 
public class ClassItemStacksAndStrings {
 
static public String itemstackToString(ItemStack is) {
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
 
        NBTTagCompound outputObject = new NBTTagCompound();
        CraftItemStack craft = getCraftVersion(is);
        if (craft != null) 
            CraftItemStack.asNMSCopy(craft).save(outputObject);
        NBTBase.a(outputObject, dataOutput);
        return new BigInteger(1, outputStream.toByteArray()).toString(32);
}
 

static public ItemStack stringToItemStack(String str) {
ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(str, 32).toByteArray());
NBTTagCompound item = (NBTTagCompound) NBTBase.a(new DataInputStream(inputStream));
net.minecraft.server.v1_6_R3.ItemStack stack = net.minecraft.server.v1_6_R3.ItemStack.createStack(item);
return  CraftItemStack.asCraftMirror(stack);
}
 
private static CraftItemStack getCraftVersion(ItemStack stack) {
        if (stack instanceof CraftItemStack)
            return (CraftItemStack) stack;
        else if (stack != null)
            return CraftItemStack.asCraftCopy(stack);
        else
            return null;
    }
}