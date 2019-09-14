package mr.emii.configurations;


import mr.emii.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Messages extends YamlConfiguration {

    private static Messages config;

    public static Messages getConfig() {
        if (config == null) {
            config = new Messages();
        }
        return config;
    }

    private Plugin main() {
        return Main.getInstance();
    }

    private Plugin plugin;
    private File configFile;


    public Messages() {
        plugin = main();
        configFile = new File(plugin.getDataFolder(), "messages.yml");
        if(!configFile.exists())
            saveDefault();
        reload();
    }

    public void reload() {
        try {
            super.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        return super.getString(path).replace("&", "§");
    }

    public void save() {
        try {
            super.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        plugin.saveResource("messages.yml", false);
    }
}