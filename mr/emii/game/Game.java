package mr.emii.game;

import mr.emii.Main;
import mr.emii.enums.ActionType;
import mr.emii.enums.GameEvents;
import mr.emii.enums.GameStates;
import mr.emii.models.PlayerModel;
import mr.emii.threads.Timer;
import mr.emii.utils.CacheBlock;
import mr.emii.utils.FileUtils;
import mr.emii.utils.WorldEdit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private Timer timer;
    private GameStates gameState = GameStates.waiting;
    private String name;
    private int maxPlayers, minPlayers, lobbyTimeLeft, gameTimeLeft, endTimeLeft;
    private List<Location> spawns;
    private Location lobby, specLobby;
    private List<PlayerModel> inGame, spectator, totalplayers;
    private FileUtils gameConfig;
    private Set<Chest> opened;
    private List<ItemStack> normalItems;
    private List<ItemStack> rareItems;
    private List<ItemStack> legendaryItems;
    private String worldName;
    private GameEvents currentEvent = GameEvents.Reffil;

    private List<Location> bounds;

    public List<CacheBlock> cacheBlock = new ArrayList<>();

    public Game(String name, int maxPlayers, int minPlayers, int lobbyTimeLeft, int gameTimeLeft, int endTimeLeft,
                List<Location> spawns, Location lobby, Location specLobby, FileUtils gameConfig, String worldName, List<Location> bounds) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyTimeLeft = lobbyTimeLeft;
        this.gameTimeLeft = gameTimeLeft;
        this.endTimeLeft = endTimeLeft;
        this.spawns = spawns;
        this.lobby = lobby;
        this.specLobby = specLobby;
        this.gameConfig = gameConfig;
        this.worldName = worldName;
        this.bounds = bounds;
        this.timer = new Timer(this);

        this.inGame = new ArrayList<>();
        this.spectator = new ArrayList<>();
        this.totalplayers = new ArrayList<>();

        this.opened = new HashSet<>();
        this.normalItems = new ArrayList<>();
        this.rareItems = new ArrayList<>();
        this.legendaryItems = new ArrayList<>();

        for (String item : Main.getInstance().getConfig().getStringList("game.normalItems")) {
            try {
                Material material = Material.getMaterial(item);
                int count = 1;
                if (material.isBlock()) {
                    count = 23;
                }
                if (material == Material.TORCH) {
                    count = 16;
                }
                if (material == Material.ARROW) {
                    count = 12;
                }
                this.normalItems.add(new ItemStack(material, count));
            } catch (Exception ex) {
                Main.getInstance().getLogger().severe(name + " tried to load normal item that doesn't exist: " + item);
            }
        }
        for (String item : Main.getInstance().getConfig().getStringList("game.rareItems")) {
            try {
                Material material = Material.getMaterial(item);
                int count = 1;
                if (material.isBlock()) {
                    count = 32;
                }
                if (material == Material.ARROW) {
                    count = 8;
                }
                this.rareItems.add(new ItemStack(material, count));
            } catch (Exception ex) {
                Main.getInstance().getLogger().severe(name + " tried to load rare item that doesn't exist: " + item);
            }
        }
        for (String item : Main.getInstance().getConfig().getStringList("game.legendaryItems")) {
            try {
                Material material = Material.getMaterial(item);
                int count = 1;
                if (material.isBlock()) {
                    count = 24;
                }
                if (material == Material.ARROW) {
                    count = 16;
                }
                this.legendaryItems.add(new ItemStack(material, count));
            } catch (Exception ex) {
                Main.getInstance().getLogger().severe(name + " tried to load legendary item that doesn't exist: " + item);
                ex.printStackTrace();
            }
        }

        if (bounds.size() > 0) {
            if (!readBlocks()) {
                Main.getInstance().getLogger().severe(" tried to read blocks on game " + name);
            }
        }
    }

    public void broadcast(String message) {
        this.getInGame().forEach(player -> {
            player.getPlayer().sendMessage(message);
        });

    }

    public boolean readBlocks() {
        if (bounds.size() > 0) {
            if (bounds.size() == 2) {
                Location loc1 = bounds.get(0);
                Location loc2 = bounds.get(1);

                int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
                int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
                int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
                int maxZ = Math.max(loc1.getBlockY(), loc2.getBlockZ());
                int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
                int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());

                double height = Math.round(Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY)));
                for (int x = minX; x <= maxX; x++) {
                    for (int y = 0; y < height; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if ((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                                Block b = loc1.getWorld().getBlockAt(x, minY + y, z);
                                this.getCacheBlock().add(WorldEdit.makeBlockToCacheBlock(b, ActionType.Original));
                            }
                            Block b = loc1.getWorld().getBlockAt(x, minY + y, z);
                            this.getCacheBlock().add(WorldEdit.makeBlockToCacheBlock(b, ActionType.Original));

                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean forceStart() {
        if (this.getTimer().isPaused()) {
            this.getTimer().setPaused(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean forceStop() {
        if (this.getGameState() != GameStates.waiting || this.getGameState() != GameStates.end) {
            this.setGameState(GameStates.end);
            return true;
        } else {
            return false;
        }
    }

    public void playerJoin(PlayerModel model) {
        if (this.getInGame().size() < maxPlayers) {
            if (!this.getInGame().contains(model)) {
                switch (this.getGameState()) {
                    case waiting:
                        if (this.getCacheBlock().isEmpty()) {
                            this.readBlocks();
                        }
                        getInGame().add(model);
                        getTotalplayers().add(model);
                        broadcast("§a" + model.getPlayer().getDisplayName() + " Joined! " + getInGame().size() + "/" + getMaxPlayers());
                        model.getPlayer().teleport(this.getLobby());
                        if (this.getInGame().size() == minPlayers) {
                            getTimer().setPaused(false);
                            model.toPlayer();
                        }
                        Main.getInstance().getScoreboardTask().get("lb." + model.getPlayer().getDisplayName()).cancel();
                        Main.getInstance().getScoreboardTask().get("lb." + model.getPlayer().getDisplayName()).setUsing(false);
                        Main.getInstance().prelobbyScoreboard(model, this);
                        Main.getInstance().getScoreboardTask().get("sw.lb." + model.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);
                        break;
                    case started:
                        getSpectator().add(model);
                        model.toSpectator();
                        model.getPlayer().teleport(this.getSpecLobby());
                        break;
                    default:
                        model.getPlayer().sendMessage("Could join in the game.");
                        break;
                }
            } else {
                model.getPlayer().kickPlayer("Connection Lost.");
            }
        } else {
            model.getPlayer().sendMessage("The game is fully");
        }
    }

    public void playerLeave(PlayerModel model) {
        if (gameState == GameStates.waiting) getTotalplayers().remove(model);
        if (getInGame().contains(model)) {
            getInGame().remove(model);
            model.setKills(0);
            model.getPlayer().teleport(Main.getInstance().getLobby());
        } else if (getSpectator().contains(model)) {
            getSpectator().remove(model);
            model.setKills(0);
            model.getPlayer().teleport(Main.getInstance().getLobby());
        }

    }

    public void restart() {
        this.timer = new Timer(this);
        this.rollback();
        this.getOpened().clear();
        this.getCacheBlock().clear();
        this.getInGame().clear();
        this.getSpectator().clear();
        this.getTotalplayers().clear();
    }

    public boolean rollback() {
        if (this.getCacheBlock().size() > 0) {
            this.getCacheBlock().forEach(cb -> {
                System.out.println(cb.getMaterialType());
                if (cb.getAction() == ActionType.Place) {
                    if (cb.getMaterialType() != null) {
                        cb.getBlock().getLocation().getBlock().setType(cb.getMaterialType());
                        cb.getBlock().getLocation().getBlock().setData(cb.getBlock().getData());
                    } else {
                        cb.getBlock().setType(Material.AIR);
                    }
                } else if (cb.getAction() == ActionType.Break || cb.getAction() == ActionType.Original) {
                    cb.getBlock().getLocation().getBlock().setType(cb.getMaterialType());
                    cb.getBlock().getLocation().getBlock().setData(cb.getBlockData());
                    for (Entity entities : cb.getBlock().getLocation().getChunk().getEntities()) {
                        if (!(entities instanceof ItemStack)) {
                            entities.remove();
                        }
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public List<ItemStack> getLegendaryItems() {
        return legendaryItems;
    }

    public FileUtils getGameConfig() {
        return gameConfig;
    }

    public String getName() {
        return name;
    }

    public List<PlayerModel> getTotalplayers() {
        return totalplayers;
    }

    public Timer getTimer() {
        return timer;
    }

    public GameStates getGameState() {
        return gameState;
    }

    public int getLobbyTimeLeft() {
        return lobbyTimeLeft;
    }

    public int getGameTimeLeft() {
        return gameTimeLeft;
    }

    public int getEndTimeLeft() {
        return endTimeLeft;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public Location getLobby() {
        return lobby;
    }


    public Location getSpecLobby() {
        return specLobby;
    }

    public List<PlayerModel> getInGame() {
        return inGame;
    }

    public List<Location> getBounds() {
        return bounds;
    }

    public List<PlayerModel> getSpectator() {
        return spectator;
    }

    public void setGameState(GameStates gameState) {
        this.gameState = gameState;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isState(GameStates state) {
        return getGameState() == state;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setLobbyTimeLeft(int lobbyTimeLeft) {
        this.lobbyTimeLeft = lobbyTimeLeft;
    }

    public void setGameTimeLeft(int gameTimeLeft) {
        this.gameTimeLeft = gameTimeLeft;
    }

    public void setEndTimeLeft(int endTimeLeft) {
        this.endTimeLeft = endTimeLeft;
    }

    public void setSpawns(ArrayList<Location> spawns) {
        this.spawns = spawns;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public void setSpecLobby(Location specLobby) {
        this.specLobby = specLobby;
    }

    public void setGameConfig(FileUtils gameConfig) {
        this.gameConfig = gameConfig;
    }

    public List<CacheBlock> getCacheBlock() {
        return cacheBlock;
    }

    public Set<Chest> getOpened() {
        return opened;
    }

    public List<ItemStack> getNormalItems() {
        return normalItems;
    }

    public List<ItemStack> getRareItems() {
        return rareItems;
    }

    public void setBounds(List<Location> bounds) {
        this.bounds = bounds;
    }

    public GameEvents getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(GameEvents currentEvent) {
        this.currentEvent = currentEvent;
    }

    public GameEvents getNextEvent() {
        return getCurrentEvent().getNext();
    }
}
