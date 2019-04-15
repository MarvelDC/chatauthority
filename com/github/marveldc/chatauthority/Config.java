package com.github.marveldc.chatauthority;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getServer;
import static com.github.marveldc.chatauthority.Main.config;

public class Config {
    private static String path = Main.getPlugin(Main.class).getDataFolder() + File.separator + "config.properties";


    public Config() {
        reloadConfig();
    }

    private void reloadConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            checkConfig();

            input = (new FileInputStream(path));
            prop.load(input);

            config.put("version", prop.getProperty("version", Main.getPlugin(Main.class).getDescription().getVersion()));
            config.put("test", prop.getProperty("mytest"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void checkConfig() {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs()) System.out.println("[ChatAuthority] Failed to create parent directories for ChatAuthority.");
            Main.getPlugin(Main.class).saveResource("config.properties", false);
        }
    }

}
