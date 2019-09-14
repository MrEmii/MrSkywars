package mr.emii.listeners;

import mr.emii.Main;
import mr.emii.enums.ActionType;
import mr.emii.enums.GameStates;
import mr.emii.game.Game;
import mr.emii.models.PlayerModel;
import mr.emii.utils.CacheBlock;
import mr.emii.utils.WorldEdit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class RollbackEvent implements Listener {



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null) {
            if (game.isState(GameStates.waiting) || game.isState(GameStates.starting)  || game.isState(GameStates.end)) {
                event.setCancelled(true);
                return;
            } else {
                if (!game.getInGame().contains(m_player) && game.getSpectator().contains(m_player)) {
                    event.setCancelled(true);
                    return;
                } else {
                    if (event.getBlockReplacedState().getType() == Material.STATIONARY_LAVA || event.getBlockReplacedState().getType() == Material.STATIONARY_WATER) {
                        game.getCacheBlock().add(new CacheBlock(event.getBlock(), ActionType.Place, event.getBlockReplacedState().getType()));
                    } else {
                        game.getCacheBlock().add(new CacheBlock(event.getBlock(), ActionType.Place));
                    }

                }
            }

        }
    }

    @EventHandler
    public void onTntExplote(ExplosionPrimeEvent e) {
        //e.setCancelled(true);
        TNTPrimed tnt = (TNTPrimed) e.getEntity();
        Location tntLocation = tnt.getLocation();
        Game game = Main.getInstance().getGameManager().getGameByBlockLocation(tntLocation);
        if(game != null){
            int radius = (int) e.getRadius();
            List<Location> sphere = WorldEdit.circle(tntLocation, radius, radius, false, true, 8);
            sphere.forEach(a -> {
                game.getCacheBlock().add(new CacheBlock(a.getBlock(), ActionType.Break, a.getBlock().getType(), a.getBlock().getData()));
            });
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null) {
            if (game.isState(GameStates.waiting) || game.isState(GameStates.starting)  || game.isState(GameStates.end)) {
                e.setCancelled(true);
                return;
            } else {
                if (!game.getInGame().contains(m_player) && game.getSpectator().contains(m_player)) {
                    e.setCancelled(true);
                    return;
                } else {
                    if (e.getItem() != null && e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (e.getItem().getType() == Material.LAVA_BUCKET || e.getItem().getType() == Material.WATER_BUCKET) {
                            Block bb = e.getClickedBlock().getRelative(e.getBlockFace());
                            game.getCacheBlock().add(new CacheBlock(bb, ActionType.Place));
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Game game = Main.getInstance().getGameManager().getGameByPlayerName(player.getDisplayName());
        PlayerModel m_player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(player.getDisplayName());
        if (game != null) {
            if (game.isState(GameStates.waiting) || game.isState(GameStates.starting)  || game.isState(GameStates.end)) {
                event.setCancelled(true);
                return;
            } else {
                if (!game.getInGame().contains(m_player) && game.getSpectator().contains(m_player)) {
                    event.setCancelled(true);
                    return;
                } else {
                    game.getCacheBlock().add(new CacheBlock(event.getBlock(), ActionType.Break, event.getBlock().getType(), event.getBlock().getData()));
                }
            }
        }
    }


}
