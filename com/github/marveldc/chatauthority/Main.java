package com.github.marveldc.chatauthority;

import com.github.marveldc.chatauthority.commands.*;
import com.github.marveldc.chatauthority.inventory.InventoryEvent;
import com.github.marveldc.chatauthority.listeners.BotListener;
import com.github.marveldc.chatauthority.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static com.github.marveldc.chatauthority.Util.translate;

public final class Main extends JavaPlugin {
    public static String prefix;

//    private FileConfiguration blacklist;
//    private File blacklistFile;

    private FileConfiguration blacklistwords;
    private File blacklistwordsFile;

    private FileConfiguration messages;
    private File messagesFile;

//    private static FileConfiguration configPlayers;
//    private static File cfile;

    public static List<UUID> toggled = new ArrayList<>();
    public static HashMap<Player, Player> reply = new HashMap<>();
    public static List<Player> notMoved = new ArrayList<>();
    public static List<Player> staffChat = new ArrayList<>();
    static HashMap<String, Object> config = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        //saveDefaultConfig()
        new Config();
        System.out.println(config.get("test"));
        //ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolListener(this));
        //createBlacklist();
        createBlackListWords();
        createMessagesFile();
        registerEvents();

        new Toggle(this);
        new Chat(this);
        new Message(this);
        new Reply(this);
        new StaffAnnounce(this);
        new StaffChat(this);
        new Broadcast(this);
        new Infractions(this);
        //toggled.addAll(Main.getPlugin(Main.class).getBlacklist().getStringList("toggled"));

        addDefaults();

        prefix = translate(getMessages().getString("prefix"));

        Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
        for (Player name : players) {
            if (new PlayerFile(name).getPlayerConfig().getBoolean("toggles.blacklist")) {
                toggled.add(name.getUniqueId());
            }
        }
    }

    @Override
    public void onDisable() {
        //saveFile(2);
        //saveFile(3);
    }

    private void setDefaultValues(FileConfiguration config, Map<String, Object> configParams) {
        if (config == null) return;
        for (final Map.Entry<String, Object> e : configParams.entrySet())
            if (!config.contains(e.getKey()))
                config.set(e.getKey(), e.getValue());
    }

//    public FileConfiguration getBlacklist() {
//        return this.blacklist;
//    }

    FileConfiguration getBlacklistWords() {
        return this.blacklistwords;
    }

    public FileConfiguration getMessages(){
        return this.messages;
    }

//    public static FileConfiguration getPlayers() {
//        return configPlayers;
//    }

    private void saveFile(Integer type) {
        try {
            //if (type == 1) blacklist.save(blacklistFile);
            if (type == 2) blacklistwords.save(blacklistwordsFile);
            if (type == 3) messages.save(messagesFile);
            //if (type == 4) configPlayers.save(cfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessages() {
        prefix = translate(getMessages().getString("prefix"));
    }

    private void addDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("prefix", "&9&lChat&c&lAuthority &7>> &r");

        defaults.put("allAlone", "&No one to reply to, start with &c/msg <online player> <message>&7.");

        defaults.put("blockedChatMuted", "&7Chat is currently &cmuted&7, and you cannot chat for now.");
        defaults.put("blockedSimilar", "&cBlocked message due to similarity to previous messages.");
        defaults.put("blockedSpam", "&cSlow down!");
        defaults.put("broadcastChatClear", "&7Chat was cleared by &c{0}&7.");
        defaults.put("broadcastChatMuted", "&7Chat was muted by &c{0}&7.");
        defaults.put("broadcastChatUnmuted", "&7Chat was unmuted by &c{0}&7.");

        defaults.put("censoredWithChar", "*");
        defaults.put("consoleInfractions", "&cInfractions for &f{0}\n&cSpam &9VL: &7{1}\n&dSimilarity &9VL: &7{2}\n&eCapitals &9VL: &7{3}\n&aBlacklist &9VL: &7{4}\n&7Player has Blacklist toggled &6{5}&7.");

        defaults.put("formatBroadcast", "&8[&eALERT&8] &f{0}");
        defaults.put("formatMeToYou", "&9[&6-> &7{0} &9]&8: &r{1}");
        defaults.put("formatStaffChat", "&a&l<SC> &c{0} &7>> &9{1}");
        defaults.put("formatYouToMe", "&9[&7{0} &6-> &9]&8: &r{1}");

        defaults.put("infractionAlert", "&8(&7{0}&8) &a{1} &7has reached a VL of &9{2}&7.");
        defaults.put("insufficientPermission", "&7Not enough permissions.");

        defaults.put("mentionSubtitle", "&9by &d{0}");
        defaults.put("mentionTitle", "&dYou &9were mentioned");

        defaults.put("notOnline", "&c{0} &7is not online.");

        defaults.put("openingChatGui", "&7Opening Chat GUI...");
        defaults.put("openingInfractionsGui", "&7Opening Infractions GUI...");

        defaults.put("playerOnlyCommand", "&7Player only command.");

        defaults.put("reloadedConfig", "&7Reloaded configurations.");

        defaults.put("staffAnnounce", "\n&c{0} &6>> &c{1}\n ");

        defaults.put("toggleBlacklistOff", "&cToggled Blacklist off.");
        defaults.put("toggleBlacklistOn", "&cToggled Blacklist on.");
        defaults.put("toggleMentionOff", "&cToggled Mentions off.");
        defaults.put("toggleMentionOn", "&cToggled Mentions on.");
        defaults.put("toggleStaffChatOff", "&7Toggled Staff Chat off.");
        defaults.put("toggleStaffChatOn", "&7Toggled Staff Chat on.");

        defaults.put("usageBroadcast", "&7Usage: &c/broadcast <message>&7.");
        defaults.put("usageInfractions", "&7Usage: &c/infractions [online player]&7.");
        defaults.put("usageMessage", "&7Usage: &c/msg <online player> <message>&7.");
        defaults.put("usageReply", "&7Usage: &c/reply <message>&7.");
        defaults.put("usageStaffAnnounce", "&7Usage: &c/staffannounce <message>&7.");
        defaults.put("usageToggle", "&7Usage: &c/toggle <blacklist> | <mention>&7.");


        setDefaultValues(messages, defaults);
        saveFile(3);
    }

//    private void createBlacklist() {
//        blacklistFile = new File(getDataFolder(), "ToggledBlacklist.yml");
//        blacklist = YamlConfiguration.loadConfiguration(blacklistFile);
//        if (!blacklistFile.exists()) {
//            blacklistFile.getParentFile().mkdirs();
//            saveResource("ToggledBlacklist.yml", false);
//        }
//
//        blacklist = YamlConfiguration.loadConfiguration(blacklistFile);
//        try {
//            blacklist.load(blacklistFile);
//        } catch (IOException | InvalidConfigurationException e) {
//            e.printStackTrace();
//        }
//    }

    private void createBlackListWords() {
        blacklistwordsFile = new File(getDataFolder(), "blacklist.yml");
        blacklistwords = YamlConfiguration.loadConfiguration(blacklistwordsFile);
        if (!blacklistwordsFile.exists()) {
            blacklistwordsFile.getParentFile().mkdirs();
            saveResource("src/blacklist.yml", false);
        }

        blacklistwords = YamlConfiguration.loadConfiguration(blacklistwordsFile);
        try {
            blacklistwords.load(blacklistwordsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void createMessagesFile() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messages.load(messagesFile);
            addDefaults();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "[ChatAuthority] Suggest deleting 'messages.yml' and restarting the server.");
        }
    }

//    public void create(Player p) {
//        cfile = new File(getDataFolder(), "player data" + File.separator + p.getUniqueId() + ".yml");
//        if (!cfile.getParentFile().exists()) cfile.getParentFile().mkdir();
//        if (!cfile.exists()) {
//            try {
//                cfile.createNewFile();
//            } catch(Exception e) {
//                e.printStackTrace();
//                Bukkit.getLogger().log(Level.SEVERE, "Error creating file " + cfile.getName() + " for plugin ChatAuthority.");
//            }
//        }
//        configPlayers = YamlConfiguration.loadConfiguration(cfile);
//        final Map<String, Object> defaults = new HashMap<>();
//        defaults.put("logins", 0);
//        defaults.put("violations.spam", 0);
//        defaults.put("violations.similar", 0);
//        defaults.put("violations.capitals", 0);
//        defaults.put("violations.blacklist", 0);
//        defaults.put("toggles.blacklist", true);
//        defaults.put("toggles.mention", true);
//
//        setDefaultValues(configPlayers, defaults);
//        saveFile(4);
//        configPlayers = YamlConfiguration.loadConfiguration(cfile);
//    }

    public static void setDisabled() {
        getPlugin(Main.class).setEnabled(false);
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new InventoryEvent(), this);
        pm.registerEvents(new BotListener(), this);
    }


}
