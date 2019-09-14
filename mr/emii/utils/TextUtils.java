package mr.emii.utils;

import mr.emii.Main;
import mr.emii.game.Game;
import mr.emii.models.KitModel;
import mr.emii.models.PlayerModel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TextUtils {

    public static List<KitModel> ListStringToKits(List<String> kitString) {
        List<KitModel> kitList = new ArrayList<>();

        kitString.stream().forEach(kit -> {
            KitModel m_kit = (KitModel) Main.getInstance().getKitsManager().getByString(kit);
            kitList.add(m_kit);
        });

        return kitList;
    }

    public static String LocationToString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }

    public static List<ItemStack> ListStringToListItemStack(ConfigurationSection map) {
        List<ItemStack> itemStackList = new ArrayList<>();
        map.getValues(false).forEach((a, b) -> {
            ItemStack m_item = ItemStack.deserialize(map.getConfigurationSection(a).getValues(false));
            itemStackList.add(m_item);
        });
        return itemStackList;
    }

    public static Location StringToLocation(String string) {
        if (!string.isEmpty()) {
            String[] splitString = string.split(":");

            World world = Main.getInstance().getServer().getWorld(splitString[0]);

            double x = Double.parseDouble(splitString[1]);
            double y = Double.parseDouble(splitString[2]);
            double z = Double.parseDouble(splitString[3]);
            float yaw = Float.parseFloat(splitString[4]);
            float pitch = Float.parseFloat(splitString[5]);

            Location newLocation = new Location(world, x, y, z);

            newLocation.setYaw(yaw);
            newLocation.setPitch(pitch);

            return newLocation;
        } else {
            return null;
        }

    }

    public static List<Location> ListStringToListLocation(List<String> stringList) {
        List<Location> locationList = new ArrayList<>();
        if (stringList.size() > 0) {
            stringList.stream().forEach(locs -> {
                locationList.add(StringToLocation(locs));
            });
        }

        return locationList;
    }



    public static         HashMap<Integer, String> replaceGame(List<String> data, Game game, PlayerModel player) {
        Calendar calendar = Calendar.getInstance();
        String date = Main.getInstance().messages.getString("formats.date").replace("{day}", String.valueOf(calendar.getTime().getDay())).replace("{month}", String.valueOf(calendar.getTime().getMonth())).replace("{year}", String.valueOf(calendar.getTime().getYear()));
        String spawns = Main.getInstance().messages.getString("formats.spawns").replace("{players}", String.valueOf(game.getInGame().size())).replace("{maxplayers}", String.valueOf(game.getMaxPlayers()));

        HashMap<Integer, String>scoreData = new HashMap<>();
        String blanks = ChatColor.RESET.toString();

        for (int i = 0; i < data.size(); i++) {
            blanks = blanks + ChatColor.RESET.toString();
            String string = data.get(i).replace("{blank}", blanks).replace("{user}", player.getPlayer().getDisplayName())
                    .replace("{coins}", String.valueOf(player.getCoins())).replace("{exp}", String.valueOf(player.getXp()))
                    .replace("{lvl}", String.valueOf(player.getLevel())).replace("{wins}", String.valueOf(player.getTotalWins()))
                    .replace("{kills}", String.valueOf(player.getKills())).replace("{tk}", String.valueOf(player.getTotalKills()))
                    .replace("{deaths}", String.valueOf(player.getTotalDeaths())).replace("{date}", date).replace("{spawns}", spawns).replace("{seconds}", String.valueOf(game.getTimer().getSeconds()))
                    .replace("{map}", game.getName()).replace("{event}", game.getCurrentEvent().toString()).replace("{eventtime}", String.valueOf(game.getTimer().getGameTimeLeft()));

            scoreData.put(data.size() - i, string);
        }

        return scoreData;
    }

}
