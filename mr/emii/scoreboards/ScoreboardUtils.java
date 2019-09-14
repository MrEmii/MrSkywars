package mr.emii.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardUtils {
    public static void ScoreNull(Player p) {

        Scoreboard boards = p.getScoreboard();

        Objective obj = boards.registerNewObjective("null", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§a");


        p.setScoreboard(boards);

    }
}
