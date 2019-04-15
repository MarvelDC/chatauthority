package com.github.marveldc.chatauthority;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class PlayerFile {
    private Player player;
    private FileConfiguration playerConfig;
    private File playerFile;

    public PlayerFile(Player _player) {
        this.player = _player;
        this.playerFile = new File(getPlugin(Main.class).getDataFolder(), "player data" + File.separator + this.player.getUniqueId() + ".yml");
        if (!this.playerFile.getParentFile().exists()) this.playerFile.getParentFile().mkdir();
        if (!this.playerFile.exists()) {
            try {
                this.playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().log(Level.SEVERE, "Error creating file " + this.playerFile.getName() + " for plugin ChatAuthority.");
            }
        }
        this.playerConfig = YamlConfiguration.loadConfiguration(this.playerFile);
        addDefaults();
        this.playerConfig = YamlConfiguration.loadConfiguration(this.playerFile);
    }

    public Player getPlayer() {
        return this.player;
    }

    public File getPlayerFile() {
        return this.playerFile;
    }

    public FileConfiguration getPlayerConfig() {
        return this.playerConfig;
    }

    private void setDefaultValues(FileConfiguration config, Map<String, Object> configParams) {
        if (config == null) return;
        for (final Map.Entry<String, Object> e : configParams.entrySet()) {
            if (!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
    }

    private void addDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("logins", 0);
        defaults.put("violations.spam", 0);
        defaults.put("violations.similar", 0);
        defaults.put("violations.capitals", 0);
        defaults.put("violations.blacklist", 0);
        defaults.put("toggles.blacklist", true);
        defaults.put("toggles.mention", true);

        setDefaultValues(this.playerConfig, defaults);
        save();
    }

    public void save() {
        try {
            this.playerConfig.save(this.playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
