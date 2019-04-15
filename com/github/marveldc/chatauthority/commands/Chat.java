package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Config;
import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.tabCompletors.chatComplete;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

import static com.github.marveldc.chatauthority.Main.prefix;
import static com.github.marveldc.chatauthority.Main.setDisabled;
import static com.github.marveldc.chatauthority.Util.translate;
import static com.github.marveldc.chatauthority.inventory.CustomInventory.chatInventory;
import static org.bukkit.Bukkit.getServer;

public class Chat implements CommandExecutor, Listener {
    public static Boolean muted = Boolean.FALSE;

    public Chat(Main plugin){
        plugin.getCommand("chat").setExecutor(this);
        plugin.getCommand("chat").setTabCompleter(new chatComplete());
    }

    public static void clearChat(String sender) {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
            for (Entity player : players) {
                if (!(player.hasPermission("ca.bypass.clear"))) {
                    player.sendMessage(StringUtils.repeat(" \n ", 1000));
                }
            }
            getServer().broadcastMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("broadcastChatClear").replace("{0}", sender)));
        });
        //getServer().broadcastMessage(translate(prefix + "&7Chat was cleared by &c" + sender + "&7."));
        //translate(prefix + Main.getPlugin(Main.class).getMessages().getString("").replace("{0}", sender))
        //translate(prefix + Main.getPlugin(Main.class).getMessages().getString(""))
    }

    public static void muteChat(String sender) {
        if (muted == Boolean.FALSE) {
            muted = Boolean.TRUE;
            getServer().broadcastMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("broadcastChatMuted").replace("{0}", sender)));
            //getServer().broadcastMessage(translate(prefix + "&7Chat was muted by &c" + sender + "&7."));
        } else {
            muted = Boolean.FALSE;
            getServer().broadcastMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("broadcastChatUnmuted").replace("{0}", sender)));
            //getServer().broadcastMessage(translate(prefix + "&7Chat was unmuted by &c" + sender + "&7."));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //if (sender.hasPermission("ca.admin")) {
            if (args.length == 0 && (sender.hasPermission("ca.admin.clear") || sender.hasPermission("ca.admin.mute")
            || sender.hasPermission("ca.reload"))) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("playerOnlyCommand")));
                    //sender.sendMessage(translate(prefix + "Player only command."));
                    return true;
                }
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("openingChatGui")));
                //sender.sendMessage(translate(prefix + "&7Opening Chat GUI..."));
                chatInventory(getServer().getPlayer(sender.getName()));
                return true;
            } else if (args.length == 0 && !(sender.hasPermission("ca.admin.clear") || sender.hasPermission("ca.admin.mute")
                    || sender.hasPermission("ca.reload"))) {
                sender.sendMessage(translate(prefix + "\n&6Created by &cMarvelDC\n&7Version " + Main.getPlugin(Main.class).getDescription().getVersion()));
                return true;
            } else if (args[0].equalsIgnoreCase("clear") && sender.hasPermission("ca.admin.clear")) {
                //getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->), 100L);
                clearChat(sender.getName());
                return true;
            } else if (args[0].equalsIgnoreCase("mute") && sender.hasPermission("ca.admin.mute")) {
                muteChat(sender.getName());
                return true;
            } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("ca.admin.reload")) {
                //Main.getPlugin(Main.class).saveConfig();
                new Config();
                try {
                    //Main.getPlugin(Main.class).getBlacklist().save("blacklist.yml");
                    Main.getPlugin(Main.class).getMessages().save("messages.yml");
                    Main.getPlugin(Main.class).createMessagesFile();
                    Main.getPlugin(Main.class).reloadMessages();
                } catch (IOException e){
                    sender.sendMessage(translate("&f[ChatAuthority] &4ERROR: Read console for errors."));
                    e.printStackTrace();
                    Bukkit.getLogger().log(Level.SEVERE, "[ChatAuthority] Suggest deleting 'messages.yml' and restarting the server.");
                    return true;
                }
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("reloadedConfig")));
                //sender.sendMessage(translate(prefix + "&7Reloaded configurations."));
                return true;
            } else {
            sender.sendMessage(translate(prefix + "\n&6Created by &cMarvelDC\n&7Version " + Main.getPlugin(Main.class).getDescription().getVersion()));
            return true;
            }
    }
}
