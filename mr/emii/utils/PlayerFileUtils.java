package mr.emii.utils;

import mr.emii.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerFileUtils {

    public static FileUtils getPlayerFile(Player player){
        FileUtils playerData = new FileUtils(player.getUniqueId().toString(), Main.getInstance().getDataFolder()+"/players");
        if(playerData.createFile()){
            List<String> kitsNames = new ArrayList<>();
            playerData.set("kits", kitsNames);
            playerData.set("currentKit", "");
            playerData.set("displayName", player.getDisplayName());
            playerData.set("level", 0);
            playerData.set("xp", 0);
            playerData.set("coins", 0);
            playerData.set("totalWins", 0);
            playerData.set("totalKills", 0);
            playerData.set("played", 0);
            playerData.set("deaths", 0);
        }
        return playerData;
    }

}
