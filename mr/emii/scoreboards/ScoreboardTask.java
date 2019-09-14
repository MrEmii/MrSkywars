package mr.emii.scoreboards;

import mr.emii.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Map;

public class ScoreboardTask extends BukkitRunnable {

    private Map<Integer, String> scoreboardContent;
    private int currentCycle = 0;
    private int maxCycle;
    private List<String> title;
    private String previusTitle = null;
    private Player currentPlayer;
    private boolean using;
    private String type;

    public ScoreboardTask(Map<Integer, String> scoreboardContent, int maxCycle, List<String> title, Player currentPlayer, String type, boolean using) {
        this.scoreboardContent = scoreboardContent;
        this.maxCycle = maxCycle;
        this.title = title;
        this.currentPlayer = currentPlayer;
        this.using = using;
        this.type = type;
    }

    @Override
    public void run() {
        Scoreboard scoreboard = currentPlayer.getScoreboard() == null ? Main.getInstance().getServer().getScoreboardManager().getNewScoreboard() : currentPlayer.getScoreboard();
        Objective obj = scoreboard.getObjective(type) == null ? scoreboard.registerNewObjective(type, "dummy") : scoreboard.getObjective(type);
        if (using) {
            if (obj != null) {
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                if (currentCycle == maxCycle) {
                    currentCycle = 0;
                }

                if (title.size() > currentCycle) {
                    obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title.get(currentCycle)));
                    previusTitle = title.get(currentCycle);
                } else {
                    if (previusTitle == null) {
                        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aNo Title, we sorry :("));
                    } else {
                        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', previusTitle));
                    }
                }

                for (Map.Entry<Integer, String> entry : scoreboardContent.entrySet()) {
                    obj.getScore(ChatColor.translateAlternateColorCodes('&', entry.getValue())).setScore(entry.getKey());
                }
                currentCycle++;

                currentPlayer.setScoreboard(scoreboard);
            }

        } else {
            scoreboard.getObjectives().clear();
        }
    }

    public Map<Integer, String> getScoreboardContent() {
        return scoreboardContent;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public int getMaxCycle() {
        return maxCycle;
    }

    public List<String> getTitle() {
        return title;
    }

    public String getPreviusTitle() {
        return previusTitle;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public String getType() {
        return type;
    }

    public void setScoreboardContent(Map<Integer, String> scoreboardContent) {
        this.scoreboardContent = scoreboardContent;
    }
}
