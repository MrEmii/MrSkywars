package mr.emii.utils;

import mr.emii.Main;
import mr.emii.game.Game;

import java.io.*;
import java.util.ArrayList;

public class GameFileUtils {

    public static FileUtils GameFileUtils(Game game) {
        FileUtils gf = new FileUtils(game.getName(), Main.getInstance().getDataFolder().getPath() + "/games");
        if (gf.createFile()) {
            gf.set("name", game.getName());
            gf.set("prelobby", "");
            gf.set("specslobby", "");
            gf.set("spawns", new ArrayList<>());
            gf.set("maxplayers", game.getMaxPlayers());
            gf.set("minplayers", game.getMinPlayers());
            gf.set("lobbytimeleft", 0);
            gf.set("gametimeleft", 0);
            gf.set("endtimeleft", 0);
            gf.set("bounds", new ArrayList<>());
            return gf;
        }
        return null;
    }

    public static Game FileToGame(FileUtils file) {
        Game m_game = new Game(
                file.getString("name"),
                file.getInt("maxplayers"),
                file.getInt("minplayers"),
                file.getInt("lobbytimeleft"),
                file.getInt("gametimeleft"),
                file.getInt("endtimeleft"),
                TextUtils.ListStringToListLocation(file.getStringList("spawns")),
                TextUtils.StringToLocation(file.getString("prelobby")),
                TextUtils.StringToLocation(file.getString("specslobby")),
                file,
                "",
                TextUtils.ListStringToListLocation(file.getStringList("bounds")));
        return m_game;
    }

    public static Game cloneGame(FileUtils fileToClone, String newName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        FileUtils newFile = new FileUtils(newName, Main.getInstance().getDataFolder().getPath() + "/games");
        try {
            inputStream = new FileInputStream(fileToClone.getFile());
            outputStream = new FileOutputStream(newFile.getFile());

            // the size of the buffer doesn't have to be exactly 1024 bytes, try playing around with this number and see what effect it will have on the performance
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                newFile.reload();
                newFile.set("name", newName);
                return GameFileUtils.FileToGame(newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
