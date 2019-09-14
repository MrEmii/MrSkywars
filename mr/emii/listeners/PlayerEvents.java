package mr.emii.listeners;

import mr.emii.Main;
import mr.emii.enums.GameStates;
import mr.emii.game.Game;
import mr.emii.models.PlayerModel;
import mr.emii.utils.CacheBlock;
import mr.emii.utils.FileUtils;
import mr.emii.utils.PlayerFileUtils;
import mr.emii.utils.TextUtils;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Random;

public class PlayerEvents implements Listener {

    @EventHandler
    public void playerChangeWorldEvent(PlayerChangedWorldEvent e) {
        World newWorld = e.getPlayer().getWorld();
        Player player = e.getPlayer();
        if (newWorld.getName().equalsIgnoreCase(TextUtils.StringToLocation(Main.getInstance().getConfig().getString("lobbyLocation")).getWorld().getName())) {
            FileUtils playerFile = PlayerFileUtils.getPlayerFile(e.getPlayer());
            PlayerModel m_player = new PlayerModel(e.getPlayer(), playerFile.getStringList("kits"), playerFile.getInt("level"),
                    playerFile.getInt("xp"), playerFile.getInt("coins"), playerFile.getInt("wins"), playerFile.getInt("kills"), playerFile.getInt("played"),
                    playerFile.getInt("deaths"), playerFile.getString("currentKit"));
            m_player.setFile(playerFile);
            Main.getInstance().getPlayerManager().addToList(m_player);
            e.getPlayer().teleport(TextUtils.StringToLocation(Main.getInstance().getConfig().getString("lobbyLocation")));

            if (Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName()) == null) {
                Main.getInstance().lobbyScoreboard(m_player);
                Main.getInstance().getScoreboardTask().get("lb." + m_player.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);
            } else {
                player.setScoreboard(Main.getInstance().getServer().getScoreboardManager().getNewScoreboard());
            }
        } else {
            player.setScoreboard(Main.getInstance().getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
            PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
            if (game != null && game.getInGame().contains(m_player)) {
                if (!(game.isState(GameStates.started))) {
                    event.setFoodLevel(25);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null && game.getInGame().contains(m_player)) {
            if (game.isState(GameStates.waiting) || game.isState(GameStates.starting) || game.isState(GameStates.end)) {
                event.setCancelled(true);
                return;
            }
            chestHandle(event, game);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        World newWorld = player.getWorld();
        if (newWorld.getName().equalsIgnoreCase(TextUtils.StringToLocation(Main.getInstance().getConfig().getString("lobbyLocation")).getWorld().getName())) {
            FileUtils playerFile = PlayerFileUtils.getPlayerFile(e.getPlayer());
            PlayerModel m_player = new PlayerModel(e.getPlayer(), playerFile.getStringList("kits"), playerFile.getInt("level"),
                    playerFile.getInt("xp"), playerFile.getInt("coins"), playerFile.getInt("wins"), playerFile.getInt("kills"), playerFile.getInt("played"),
                    playerFile.getInt("deaths"), playerFile.getString("currentKit"));
            m_player.setFile(playerFile);
            Main.getInstance().getPlayerManager().addToList(m_player);
            player.teleport(TextUtils.StringToLocation(Main.getInstance().getConfig().getString("lobbyLocation")));
            Main.getInstance().lobbyScoreboard(m_player);

            if (Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName()) == null) {
                Main.getInstance().lobbyScoreboard(m_player);
                Main.getInstance().getScoreboardTask().get("lb." + m_player.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);
            } else {
                player.setScoreboard(Main.getInstance().getServer().getScoreboardManager().getNewScoreboard());
            }
        } else {
            player.setScoreboard(Main.getInstance().getServer().getScoreboardManager().getNewScoreboard());
        }
    }


    private void chestHandle(PlayerInteractEvent event, Game game) {
        if (event.hasBlock() && event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest) {
            Block bb = event.getClickedBlock();
            Chest chest = (Chest) event.getClickedBlock().getState();

            if (game.getOpened().contains(chest) || game.getRareItems().size() == 0 || game.getLegendaryItems().size() == 0 || game.getNormalItems().size() == 0) {
                return;
            }
            for (CacheBlock cc : game.getCacheBlock()) {
                if (cc.getBlock().getState() == event.getClickedBlock().getState()) {
                    return;
                }
            }

            chest.getBlockInventory().clear();

            if (new Random().nextFloat() < 1) {
                int toFill = new Random().nextInt(8);
                for (int x = 0; x < toFill; x++) {
                    int rrslot = new Random().nextInt(26);
                    int selected = new Random().nextInt(game.getRareItems().size());
                    chest.getBlockInventory().setItem(rrslot, game.getRareItems().get(selected));
                }
            }
            if (new Random().nextFloat() < 0.30) {
                int toFill = new Random().nextInt(8);
                for (int x = 0; x < toFill; x++) {
                    int rrslot = new Random().nextInt(26);
                    int selected = new Random().nextInt(game.getLegendaryItems().size());
                    chest.getBlockInventory().setItem(rrslot, game.getLegendaryItems().get(selected));
                }
            }

            int toFill = new Random().nextInt(15);
            for (int x = 0; x < toFill; x++) {
                int selected = new Random().nextInt(game.getNormalItems().size());
                int rrslot = new Random().nextInt(26);
                chest.getBlockInventory().setItem(rrslot, game.getNormalItems().get(selected));
            }


            game.getOpened().add(chest);
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        blockHandle(event, player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        blockHandle(event, player);
    }

    private void blockHandle(Cancellable event, Player player) {
        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null && game.getInGame().contains(m_player)) {
            if (game.isState(GameStates.waiting) || game.isState(GameStates.starting) || game.isState(GameStates.end)) {
                event.setCancelled(true);
                return;
            }
            if (!game.getInGame().contains(m_player)) {
                event.setCancelled(true);
            }


        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
            PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
            if (game != null && game.getInGame().contains(m_player)) {
                if (game.isState(GameStates.waiting) || game.isState(GameStates.starting) || game.isState(GameStates.end)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null && game.getInGame().contains(m_player)) {

            playerDeathHandle(event, game);

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null && game.getInGame().contains(m_player)) {
            playerMovementHandle(e, game);

        }
    }

    private void playerMovementHandle(PlayerMoveEvent event, Game game) {
        Player player = event.getPlayer();
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        Player killer = player.getKiller();
        if (game.isState(GameStates.waiting) || game.isState(GameStates.starting) || game.isState(GameStates.end)) {
            return;
        }

        int minY = (int) Math.min(game.getBounds().get(0).getBlockY(), game.getBounds().get(1).getBlockY());

        if (player.getLocation().getBlockY() <= minY) {
            if (game.getInGame().size() <= 1) {
                try {
                    PlayerModel winner = game.getInGame().get(0);
                    if (winner != m_player) {
                        winner.setTotalWins(winner.getTotalWins() + 1);
                        winner.setTotalKills(winner.getTotalKills() + winner.getKills());
                        List<String> msgw = Main.getInstance().messages.getStringList("messages.winnermessage");
                        msgw.forEach(m -> {
                            String mr = m.replace("{player}", player.getDisplayName()).replace("{game}", game.getName()).replace("{kills}", String.valueOf(winner.getKills())).replace("&", "§");
                            game.broadcast(mr);
                        });
                        game.setGameState(GameStates.end);
                    } else {
                        player.teleport(event.getFrom());
                        winner.setTotalWins(winner.getTotalWins() + 1);
                        winner.setTotalKills(winner.getTotalKills() + winner.getKills());
                        List<String> msgw = Main.getInstance().messages.getStringList("messages.winnermessage");
                        msgw.forEach(m -> {
                            String mr = m.replace("{player}", player.getDisplayName()).replace("{game}", game.getName()).replace("{kills}", String.valueOf(winner.getKills())).replace("&", "§");
                            game.broadcast(mr);
                        });
                        game.setGameState(GameStates.end);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            } else {
                m_player.toSpectator(game.getSpecLobby());
                game.getInGame().remove(m_player);
                game.getSpectator().add(m_player);
            }

        }

    }

    private void playerDeathHandle(PlayerDeathEvent event, Game game) {
        Player player = event.getEntity();
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        Player killer = player.getKiller();
        if (game.isState(GameStates.waiting) || game.isState(GameStates.starting) || game.isState(GameStates.end)) {
            return;
        }

        event.setDeathMessage(null);

        if (game.getInGame().size() <= 1) {
            try {
                PlayerModel winner = game.getInGame().get(0);
                {
                    winner.setTotalWins(winner.getTotalWins() + 1);
                    winner.setTotalKills(winner.getTotalKills() + winner.getKills());
                    List<String> msgw = Main.getInstance().messages.getStringList("messages.winnermessage");
                    msgw.forEach(m -> {
                        String mr = m.replace("{player}", player.getDisplayName()).replace("{game}", game.getName()).replace("{kills}", String.valueOf(winner.getKills())).replace("&", "§");
                        game.broadcast(mr);
                    });
                    game.setGameState(GameStates.end);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        } else {
            if (killer != null) {
                PlayerModel m_killer = (PlayerModel) Main.getInstance().getPlayerManager().getByString(killer.getDisplayName());
                m_killer.setKills(m_killer.getKills() + 1);
                game.broadcast(Main.getInstance().messages.getString("messages.deaths.playerdeathnyplayer").replace("{player}", player.getDisplayName()).replace("{murder}", killer.getDisplayName()));
                game.getInGame().remove(m_player);
                game.getSpectator().add(m_player);
                if (game.getSpecLobby() == null) {
                    m_player.toSpectator(m_killer.getPlayer().getLocation());
                } else {
                    m_player.toSpectator(game.getSpecLobby());
                }
            } else {

                int minY = (int) Math.min(game.getBounds().get(0).getBlockY(), game.getBounds().get(1).getBlockY());

                if (player.getLocation().getBlockY() <= minY) {
                    m_player.toSpectator(game.getSpecLobby());
                    game.broadcast("Se murió jassja");
                }
            }
        }
    }
}
