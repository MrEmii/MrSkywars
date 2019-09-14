package mr.emii.models;

import mr.emii.Main;
import mr.emii.utils.FileUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerModel {

    private Player player;
    private List<String> kitsBought;
    private String currentKit = "";
    private int level;
    private int xp;
    private int coins;
    private FileUtils file;
    private int wins = 0, kills = 0;

    private int totalWins, totalKills, played, totalDeaths;

    public PlayerModel(Player player, List<String> kitsBought, int level, int xp, int coins, int totalWins, int totalKills, int played, int totalDeaths, String currentKit) {
        this.player = player;
        this.kitsBought = kitsBought;
        this.level = level;
        this.xp = xp;
        this.coins = coins;
        this.totalWins = totalWins;
        this.totalKills = totalKills;
        this.played = played;
        this.totalDeaths = totalDeaths;
        this.currentKit = currentKit;
    }

    public void toSpectator() {
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void toSpectator(Location location) {
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(location);
    }

    public void toPlayer() {
        player.setMaxHealth(20);
        getPlayer().setGameMode(GameMode.SURVIVAL);
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public String getCurrentKitName() {
        return currentKit;
    }

    public KitModel getCurrentKit() {
        return (KitModel) Main.getInstance().getKitsManager().getByString(currentKit);
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getKitsBought() {
        return kitsBought;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getCoins() {
        return coins;
    }

    public int getWins() {
        return wins;
    }

    public int getKills() {
        return kills;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setKitsBought(List<String> kitsBought) {
        this.kitsBought = kitsBought;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setKills(int kills) {
        this.kills = kills;

    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public FileUtils getFile() {
        return file;
    }

    public void setFile(FileUtils file) {
        this.file = file;
    }
}
