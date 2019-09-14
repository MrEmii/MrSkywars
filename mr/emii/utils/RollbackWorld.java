package mr.emii.utils;

import mr.emii.Main;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

public class RollbackWorld {
    public static void unloadMap(String mapname){
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(mapname), false)){
            Main.getInstance().getLogger().info("Successfully unloaded " + mapname);
        }else{
            Main.getInstance().getLogger().severe("COULD NOT UNLOAD " + mapname);
        }
    }
    public static void loadMap(String mapname){
        Bukkit.getServer().createWorld(new WorldCreator(mapname));
    }

    public static void rollback(String mapname){
        unloadMap(mapname);
        loadMap(mapname);
    }
}
