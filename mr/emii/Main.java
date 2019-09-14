package mr.emii;

import mr.emii.commands.SkywarsCommand;
import mr.emii.configurations.Messages;
import mr.emii.game.Game;
import mr.emii.game.GameManager;
import mr.emii.listeners.PlayerEvents;
import mr.emii.managers.KitsManager;
import mr.emii.managers.PlayerManager;
import mr.emii.models.KitModel;
import mr.emii.models.PlayerModel;
import mr.emii.scoreboards.ScoreboardTask;
import mr.emii.utils.FileUtils;
import mr.emii.utils.GameFileUtils;
import mr.emii.utils.TextUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin {

    private static Main instance;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private KitsManager kitsManager;
    private Location lobby;
    private Map<String, ScoreboardTask> scoreboardTask = new HashMap<>();
    public Messages messages;

    @Override
    public void onEnable() {
        instance = this;
        long startup = System.currentTimeMillis();
        this.saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage("§a[MrSkywars]");
        Bukkit.getConsoleSender().sendMessage("§bInitializing Plugin");


        this.gameManager = new GameManager();
        this.playerManager = new PlayerManager();
        this.kitsManager = new KitsManager();

        this.addEventListener(new PlayerEvents());

        readKits();
        readGames();

        lobby = TextUtils.StringToLocation(this.getConfig().getString("lobbyLocation"));
        messages = new Messages();
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aTotal Games: " + this.getGameManager().getGameList().size());
        if (this.getGameManager().getGameList().size() > 0) {
            this.gameManager.getGameList().forEach(a -> {
                Bukkit.getConsoleSender().sendMessage("§b-" + a.getName());
            });
        } else {
            Bukkit.getConsoleSender().sendMessage("§bNot found games");
        }
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aTotal Kits: " + this.getKitsManager().getKitList().size());
        this.getKitsManager().getKitList().forEach(a -> {
            Bukkit.getConsoleSender().sendMessage("§b-" + a.getName());
        });
        Bukkit.getConsoleSender().sendMessage("");

        getCommand("mrsk").setExecutor(new SkywarsCommand());


        Bukkit.getConsoleSender().sendMessage("§aSuccessfully system up in: " + (((System.currentTimeMillis() - startup) / 100) * 10) + "s");
    }

    public void addEventListener(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> {
            this.getServer().getPluginManager().registerEvents(listener, this);
        });
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = Main.getPlugin(Main.class);
            return instance;
        }
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }


    public Messages getMessages() {
        return messages;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public KitsManager getKitsManager() {
        return kitsManager;
    }

    public void readKits() {
        File kitsFolder = new File(this.getDataFolder().getPath() + "/kits");
        if (kitsFolder.exists()) {
            if (kitsFolder.listFiles().length > 0) {
                for (File file : kitsFolder.listFiles()) {
                    if (!file.isDirectory() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                        FileUtils kitFile = new FileUtils(file.getName(), this.getDataFolder().getPath() + "/kits");
                        kitFile.reload();
                        this.getKitsManager().addToList(
                                new KitModel(kitFile.getString("name"), TextUtils.ListStringToListItemStack(kitFile.getConfigurationSection("items")), kitFile.getInt("price"), kitFile)
                        );
                    }
                }
            }
        } else {
            kitsFolder.mkdirs();
            if (kitsFolder.listFiles().length > 0) {
                for (File file : kitsFolder.listFiles()) {
                    if (!file.isDirectory() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                        FileUtils kitFile = new FileUtils(file.getName(), this.getDataFolder().getPath() + "/kits");
                        kitFile.reload();
                        this.getKitsManager().addToList(
                                new KitModel(kitFile.getString("name"), TextUtils.ListStringToListItemStack(kitFile.getConfigurationSection("items")), kitFile.getInt("price"), kitFile)
                        );
                    }
                }
            }
        }
    }

    public void readGames() {
        File gamesFolder = new File(this.getDataFolder().getPath() + "/games");
        if (gamesFolder.exists()) {
            if (gamesFolder.listFiles().length > 0) {
                for (File file : gamesFolder.listFiles()) {
                    if (!file.isDirectory() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                        FileUtils gamefile = new FileUtils(file.getName(), this.getDataFolder().getPath() + "/games");
                        gamefile.reload();
                        this.getGameManager().addGame(
                                GameFileUtils.FileToGame(gamefile)
                        );
                    }
                }
            }
        } else {
            gamesFolder.mkdirs();
            if (gamesFolder.listFiles().length > 0) {
                for (File file : gamesFolder.listFiles()) {
                    if (!file.isDirectory() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                        FileUtils gamefile = new FileUtils(file.getName(), this.getDataFolder().getPath() + "/games");
                        gamefile.reload();
                        this.getGameManager().addGame(
                                GameFileUtils.FileToGame(gamefile)
                        );
                    }
                }
            }
        }

    }

    public Location getLobby() {
        return lobby;
    }

    public void lobbyScoreboard(PlayerModel player) {
        Map<Integer, String> scoreData = new HashMap<>();

        List<String> title = this.messages.getStringList("scoreboard.lobby.title");

        List<String> data = this.messages.getStringList("scoreboard.lobby.lines");

        String blanks = ChatColor.RESET.toString();
        int currentLength = 0;
        for (int i = 0; i < data.size(); i++) {
            blanks = blanks + ChatColor.RESET.toString();
            String string = data.get(i).replace("{blank}", blanks).replace("{user}", player.getPlayer().getDisplayName())
                    .replace("{coins}", String.valueOf(player.getCoins())).replace("{exp}", String.valueOf(player.getXp()))
                    .replace("{lvl}", String.valueOf(player.getLevel())).replace("{wins}", String.valueOf(player.getTotalWins()))
                    .replace("{kills}", String.valueOf(player.getKills())).replace("{tk}", String.valueOf(player.getTotalKills()))
                    .replace("{deaths}", String.valueOf(player.getTotalDeaths()));
            if (string.length() >= currentLength) currentLength = string.length();
            scoreData.put(data.size() - i, i == data.size() ? StringUtils.center(string, currentLength, "-") : string);
        }

        ScoreboardTask board = new ScoreboardTask(scoreData, title.size(), title, player.getPlayer(), "lobby", true);
        this.scoreboardTask.put("lb." + player.getPlayer().getDisplayName(), board);
    }

    public void prelobbyScoreboard(PlayerModel player, Game game) {
        player.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Map<Integer, String> scoreData = new HashMap<>();

        List<String> title = this.messages.getStringList("scoreboard.ingame.waiting.title");

        List<String> data = this.messages.getStringList("scoreboard.ingame.waiting.lines");

        String blanks = ChatColor.RESET.toString();
        int currentLength = 0;
        Calendar calendar = Calendar.getInstance();
        String date = this.messages.getString("formats.date").replace("{day}", String.valueOf(calendar.getTime().getDay())).replace("{month}", String.valueOf(calendar.getTime().getMonth())).replace("{year}", String.valueOf(calendar.getTime().getYear()));
        String spawns = this.messages.getString("formats.spawns").replace("{players}", String.valueOf(game.getInGame().size())).replace("{maxplayers}", String.valueOf(game.getMaxPlayers()));
        for (int i = 0; i < data.size(); i++) {
            blanks = blanks + ChatColor.RESET.toString();
            String string = data.get(i).replace("{blank}", blanks).replace("{user}", player.getPlayer().getDisplayName())
                    .replace("{coins}", String.valueOf(player.getCoins())).replace("{exp}", String.valueOf(player.getXp()))
                    .replace("{lvl}", String.valueOf(player.getLevel())).replace("{wins}", String.valueOf(player.getTotalWins()))
                    .replace("{kills}", String.valueOf(player.getKills())).replace("{tk}", String.valueOf(player.getTotalKills()))
                    .replace("{deaths}", String.valueOf(player.getTotalDeaths())).replace("{date}", date).replace("{spawns}", spawns).replace("{seconds}", String.valueOf(game.getTimer().getGameTimeLeft() / 60))
                    .replace("{map}", game.getName());
            if (string.length() >= currentLength) currentLength = string.length();
            scoreData.put(data.size() - i, i == data.size() ? StringUtils.center(string, currentLength, "-") : string);
        }

        ScoreboardTask board = new ScoreboardTask(scoreData, title.size(), title, player.getPlayer(), "prelobby", true);
        this.scoreboardTask.put("sw.lb." + player.getPlayer().getDisplayName(), board);
    }

    public void scoreboardGame(PlayerModel player, Game game) {
        player.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Map<Integer, String> scoreData = new HashMap<>();

        List<String> title = this.messages.getStringList("scoreboard.ingame.started.title");

        List<String> data = this.messages.getStringList("scoreboard.ingame.started.lines");

        scoreData = TextUtils.replaceGame(data, game, player);

        ScoreboardTask board = new ScoreboardTask(scoreData, title.size(), title, player.getPlayer(), "game", true);
        this.scoreboardTask.put("sw.game." + player.getPlayer().getDisplayName(), board);
    }

    public Map<String, ScoreboardTask> getScoreboardTask() {
        return scoreboardTask;
    }
}
