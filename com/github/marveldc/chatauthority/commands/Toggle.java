package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.PlayerFile;
import com.github.marveldc.chatauthority.tabCompletors.toggleComplete;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.marveldc.chatauthority.Main.*;
import static com.github.marveldc.chatauthority.Util.translate;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class Toggle implements CommandExecutor {
    public Toggle(Main plugin) {
        plugin.getCommand("toggle").setExecutor(this);
        plugin.getCommand("toggle").setTabCompleter(new toggleComplete());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("playerOnlyCommand")));
            //sender.sendMessage(translate(prefix + "Player only command."));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("usageToggle")));
            return true;
        }
        if (args[0].equalsIgnoreCase("blacklist")) {
            if (sender.hasPermission("ca.toggle.blacklist")) {
                if (toggled.contains(((Player) sender).getUniqueId())) {
                    toggled.remove(((Player) sender).getUniqueId());
//                    Main.getPlugin(Main.class).create(getServer().getPlayer(sender.getName()));
//                    getPlayers().set("toggles.blacklist", false);
//                    Main.getPlugin(Main.class).saveFile(4);

                    PlayerFile playerData = new PlayerFile(((Player) sender).getPlayer());
                    playerData.getPlayerConfig().set("toggles.blacklist", false);
                    playerData.save();
//                    Main.getPlugin(Main.class).getBlacklist().set("toggled", toggled);
//                    Main.getPlugin(Main.class).saveFile(1);
                    sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleBlacklistOff")));
                    //sender.sendMessage(translate(prefix + "&cToggled Blacklist off."));

                    return true;

                } else {
                    toggled.add(((Player) sender).getUniqueId());
//                    Main.getPlugin(Main.class).create(getServer().getPlayer(sender.getName()));
//                    getPlayers().set("toggles.blacklist", true);
//                    Main.getPlugin(Main.class).saveFile(4);

                    PlayerFile playerData = new PlayerFile(((Player) sender).getPlayer());
                    playerData.getPlayerConfig().set("toggles.blacklist", true);
                    playerData.save();

//                    Main.getPlugin(Main.class).getBlacklist().set("toggled", toggled);
//                    Main.getPlugin(Main.class).saveFile(1);
                    sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleBlacklistOn")));
                    //sender.sendMessage(translate(prefix + "&cToggled Blacklist on."));
                    return true;
                }

            } else {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
                //sender.sendMessage(translate(prefix + "&7Not enough permissions."));
                return true;
            }
        } else if ("mention".equalsIgnoreCase(args[0]) || "mentions".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("ca.toggle.mention")){sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission"))); return true;}
            PlayerFile playerData = new PlayerFile(((Player) sender).getPlayer());
            boolean value = playerData.getPlayerConfig().getBoolean("toggles.mention");

            //Main.getPlugin(Main.class).create(((Player) sender).getPlayer());
            //boolean value = getPlayers().getBoolean("toggles.mention");
            if (value) {//true
                //getPlayers().set("toggles.mention", false);
                playerData.getPlayerConfig().set("toggles.mention", false);
                playerData.save();
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleMentionOff")));
                return true;
            } else {
                playerData.getPlayerConfig().set("toggles.mention", true);
                playerData.save();
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleMentionOn")));
                return true;
            }
        }

        return true;
    }
}
