package mr.emii.utils;

import mr.emii.enums.ActionType;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CacheBlock {

    private Block block;
    private ActionType action;
    private Material materialType;
    private byte blockData;

    public CacheBlock(Block block, ActionType action) {
        this.block = block;
        this.action = action;
    }

    public CacheBlock(Block block, ActionType action, Material materialType) {
        this.block = block;
        this.action = action;
        this.materialType = materialType;
    }

    public CacheBlock(Block block, ActionType action, Material materialType, byte blockData) {
        this.block = block;
        this.action = action;
        this.materialType = materialType;
        this.blockData = blockData;
    }

    public byte getBlockData() {
        return blockData;
    }

    public Material getMaterialType() {
        return materialType;
    }

    public Block getBlock() {
        return block;
    }

    public ActionType getAction() {
        return action;
    }
}
