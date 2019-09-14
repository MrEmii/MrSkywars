package mr.emii.utils;

import mr.emii.enums.ActionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class WorldEdit {

    public static List<Location> circle(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++)
            for (int z = cz - r; z <= cz + r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
        return circleblocks;
    }


    public static void box(Entity ent, int sideLength, int height, boolean wantRoof, Material top, Material around) {
        Material fence = around;
        Material roof = top;
        Location entLoc = ent.getLocation();

        // Let's make sure our preconditions were met.
        if(sideLength < 3 || sideLength % 2 == 0) {
            throw new IllegalArgumentException("You must enter an odd number greater than 3 for the side length");
        }else if(height == 0) {
            throw new IllegalArgumentException("Height must be greater than 0.");
        }

        int delta = (sideLength / 2);
        Location corner1 = new Location(entLoc.getWorld(), entLoc.getBlockX() + delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() - delta);
        Location corner2 = new Location(entLoc.getWorld(), entLoc.getBlockX() - delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() + delta);
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for(int x = minX; x <= maxX; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    if((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y, z);
                        b.setType(fence);
                    }

                    if(y == height - 1 && wantRoof) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y + 1, z);
                        b.setType(roof);
                        Block bb = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() - 1, z);
                        bb.setType(roof);
                    }

                }
            }
        }
    }
    public static void box(Location entLoc, int sideLength, int height, boolean wantRoof, Material top, Material around) {
        Material fence = around;
        Material roof = top;

        // Let's make sure our preconditions were met.
        if(sideLength < 3 || sideLength % 2 == 0) {
            throw new IllegalArgumentException("You must enter an odd number greater than 3 for the side length");
        }else if(height == 0) {
            throw new IllegalArgumentException("Height must be greater than 0.");
        }

        int delta = (sideLength / 2);
        Location corner1 = new Location(entLoc.getWorld(), entLoc.getBlockX() + delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() - delta);
        Location corner2 = new Location(entLoc.getWorld(), entLoc.getBlockX() - delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() + delta);
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for(int x = minX; x <= maxX; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    if((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y, z);
                        b.setType(fence);
                    }

                    if(y == height - 1 && wantRoof) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y + 1, z);
                        b.setType(roof);
                        Block bb = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() - 1, z);
                        bb.setType(roof);
                    }

                }
            }
        }
    }


    public static CacheBlock makeBlockToCacheBlock(Block blockLocation, ActionType action){
        Block b = blockLocation;
        byte data = b.getData();
        CacheBlock cacheBlock = null;
        switch (action){
            case Break:
                cacheBlock = new CacheBlock(b, action);
                break;
            case Place:
                break;
            case Original:
                cacheBlock = new CacheBlock(b, action, b.getType(), data);
                break;
        }

        return cacheBlock;
    }

}
