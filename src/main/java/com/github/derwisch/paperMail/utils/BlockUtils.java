package com.github.derwisch.paperMail.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

public class BlockUtils
{	
	public static Block getAttachedBlock(Block b) {
        MaterialData m = b.getState().getData();
        BlockFace face = BlockFace.DOWN;
        if (m instanceof Attachable) {
            face = ((Attachable) m).getAttachedFace();
        }
        return b.getRelative(face);
    }
}