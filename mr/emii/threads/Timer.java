package mr.emii.threads;

import mr.emii.Main;
import mr.emii.enums.GameStates;
import mr.emii.game.Game;
import mr.emii.models.PlayerModel;
import mr.emii.utils.TextUtils;
import mr.emii.utils.WorldEdit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;

public class Timer extends BukkitRunnable implements Listener {

    private Game theGame;

    private int lobbyTimeLeft, gameTimeLeft, endTimeLeft, restartTimeLeft, startingTimeLeft;
    private boolean isPaused;

    public Timer(Game theGame) {
        this.theGame = theGame;
        this.lobbyTimeLeft = theGame.getLobbyTimeLeft();
        this.gameTimeLeft = theGame.getGameTimeLeft();
        this.endTimeLeft = theGame.getEndTimeLeft();
        this.restartTimeLeft = 5;
        this.startingTimeLeft = 5;
        this.isPaused = true;


        this.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public void run() {
        if (!isPaused) {
            switch (theGame.getGameState()) {
                case waiting:
                    if (this.lobbyTimeLeft != 0) {
                        this.lobbyTimeLeft--;
                        if (lobbyTimeLeft <= 4 && lobbyTimeLeft != 0) {
                            theGame.broadcast("§aGame starts in §b" + lobbyTimeLeft);
                        }
                        for (int i = 0; i < theGame.getInGame().size(); i++) {
                            PlayerModel model = theGame.getInGame().get(i);

                        }
                    } else {
                        for (int i = 0; i < theGame.getInGame().size(); i++) {
                            theGame.getInGame().get(i).getPlayer().teleport(theGame.getSpawns().get(i));
                            WorldEdit.box(theGame.getSpawns().get(i), 3, 2, true, Material.GLASS, Material.GLASS);
                            PlayerModel model = theGame.getInGame().get(i);
                        }
                        theGame.setGameState(GameStates.starting);
                    }
                    break;
                case starting:
                    if (this.startingTimeLeft != 0) {
                        this.startingTimeLeft--;

                    } else {
                        for (int i = 0; i < theGame.getInGame().size(); i++) {
                            WorldEdit.box(theGame.getSpawns().get(i), 3, 2, true, Material.AIR, Material.AIR);
                            PlayerModel player = theGame.getInGame().get(i);
                            Main.getInstance().getScoreboardTask().get("sw.lb." + player.getPlayer().getDisplayName()).cancel();
                            Main.getInstance().getScoreboardTask().get("sw.lb." + player.getPlayer().getDisplayName()).setUsing(false);
                            Main.getInstance().scoreboardGame(player, theGame);
                            Main.getInstance().getScoreboardTask().get("sw.game." + player.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);
                        }
                        theGame.broadcast("§6FIGHT");
                        theGame.setGameState(GameStates.started);
                    }
                    break;
                case started:
                    if (this.gameTimeLeft != 0) {
                        this.gameTimeLeft--;
                        for (int i = 0; i < theGame.getInGame().size(); i++) {
                            PlayerModel player = theGame.getInGame().get(i);
                            Main.getInstance().getScoreboardTask().get("sw.game." + player.getPlayer().getDisplayName()).getScoreboardContent().clear();
                            List<String> data = Main.getInstance().messages.getStringList("scoreboard.ingame.started.lines");
                            player.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                            Main.getInstance().getScoreboardTask().get("sw.game." + player.getPlayer().getDisplayName()).setScoreboardContent(TextUtils.replaceGame(data, theGame, player));

                        }
                        if (this.gameTimeLeft == (this.gameTimeLeft - 20)) {
                            switch (theGame.getCurrentEvent()) {
                                case Reffil:
                                    if (theGame.getCurrentEvent().getTimeLeft() != 0) {
                                        int tm = theGame.getCurrentEvent().getTimeLeft() - 1;
                                        theGame.getCurrentEvent().setTimeLeft(tm);
                                        if (tm <= 5 && tm != 0) {
                                            theGame.broadcast("§a" + theGame.getCurrentEvent().toString() + " events in §b" + tm);
                                        }
                                    } else {
                                        theGame.broadcast("§aSucessfully refill!");
                                        theGame.setCurrentEvent(theGame.getNextEvent());
                                    }
                                    break;
                                case DropLoot:
                                    if (theGame.getCurrentEvent().getTimeLeft() != 0) {
                                        int tm = theGame.getCurrentEvent().getTimeLeft() - 1;
                                        theGame.getCurrentEvent().setTimeLeft(tm);
                                        if (tm <= 5 && tm != 0) {
                                            theGame.broadcast("§a" + theGame.getCurrentEvent().toString() + " events in §b" + tm);
                                        }
                                    } else {
                                        theGame.broadcast("§aSucessfully drops all lots!");
                                        theGame.setCurrentEvent(theGame.getNextEvent());
                                    }
                                    break;
                            }
                        }
                    } else {
                        theGame.setGameState(GameStates.end);
                        for (int i = 0; i < theGame.getTotalplayers().size(); i++) {
                            theGame.getTotalplayers().get(i).getFile().save();
                        }
                    }
                    break;
                case end:
                    if (this.endTimeLeft != 0) {
                        this.endTimeLeft--;
                        if (endTimeLeft <= 4 && endTimeLeft != 0) {
                            theGame.broadcast("§aGame ends in §b" + endTimeLeft);
                        }
                    } else {
                        for (int i = 0; i < theGame.getInGame().size(); i++) {
                            //theGame.getInGame().get(i).getPlayer().teleport(Main.getInstance().getLobby());
                            PlayerModel model = theGame.getInGame().get(i);
                            Main.getInstance().getScoreboardTask().get("sw.game." + model.getPlayer().getDisplayName()).cancel();
                            Main.getInstance().getScoreboardTask().get("sw.game." + model.getPlayer().getDisplayName()).setUsing(false);
                            Main.getInstance().lobbyScoreboard(model);
                            Main.getInstance().getScoreboardTask().get("lb." + model.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);
                        }
                        for (int i = 0; i < theGame.getSpectator().size(); i++) {
                            theGame.getSpectator().get(i).getPlayer().teleport(Main.getInstance().getLobby());
                            PlayerModel model = theGame.getInGame().get(i);
                            Main.getInstance().getScoreboardTask().get("sw.game." + model.getPlayer().getDisplayName()).cancel();
                            Main.getInstance().getScoreboardTask().get("sw.game." + model.getPlayer().getDisplayName()).setUsing(false);
                            Main.getInstance().lobbyScoreboard(model);
                            Main.getInstance().getScoreboardTask().get("lb." + model.getPlayer().getDisplayName()).runTaskTimer(Main.getPlugin(Main.class), 0, 5);

                        }
                        theGame.broadcast("§aGame Finished!");
                        theGame.setGameState(GameStates.restarting);
                        theGame.rollback();
                        theGame.restart();
                    }
                    break;
                case restarting:
                    if (this.restartTimeLeft != 0) {
                        this.restartTimeLeft--;
                    } else {
                        theGame.setGameState(GameStates.waiting);
                        this.isPaused = true;
                    }
                    break;
            }
        }
    }

    public int getSeconds() {
        switch (theGame.getGameState()) {
            case waiting:
                return lobbyTimeLeft % 60;
            case starting:
                return startingTimeLeft % 60;
            case started:
                return gameTimeLeft % 60;

            case end:
                return endTimeLeft % 60;
            case restarting:
                return restartTimeLeft % 60;
            default:
                return 0;

        }
    }

    public int getMinutes() {
        switch (theGame.getGameState()) {
            case waiting:
                return lobbyTimeLeft / 60;

            case started:
                return gameTimeLeft / 60;

            case end:
                return endTimeLeft / 60;

            case restarting:
                return restartTimeLeft / 60;

            default:
                return 0;

        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public String formatTime() {
        int iminutes = getMinutes(), iseconds = getSeconds();
        String minutes = String.valueOf(getMinutes()), seconds = String.valueOf(getSeconds());
        if (iminutes < 10) {
            minutes = "0".concat(minutes);
        }

        if (iseconds < 10) {
            seconds = "0".concat(seconds);
        }

        return minutes + ":" + seconds;
    }

    public Game getTheGame() {
        return theGame;
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

    public int getRestartTimeLeft() {
        return restartTimeLeft;
    }

    public int getStartingTimeLeft() {
        return startingTimeLeft;
    }
}
