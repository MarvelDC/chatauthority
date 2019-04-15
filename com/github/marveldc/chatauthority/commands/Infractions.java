package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.PlayerFile;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;

import static com.github.marveldc.chatauthority.Main.toggled;
import static com.github.marveldc.chatauthority.Util.translate;
import static com.github.marveldc.chatauthority.inventory.CustomInventory.InfractionsInventory;
import static com.github.marveldc.chatauthority.Main.prefix;
import static org.bukkit.Bukkit.*;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Infractions  implements CommandExecutor {
    public Infractions(Main plugin) {
        plugin.getCommand("infractions").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ca.infractions.self")) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            return true;
        }

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(translate(prefix + "&7Usage: &c/infractions <online player>"));
                return true;
            }
            //sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("playerOnlyCommand")));
            if (getPlayer(args[0]) != null) {
                PlayerFile playerData = new PlayerFile(getServer().getPlayer(args[0]));
                //String.valueOf(playerData.getPlayerConfig().getInt("violations."))
                //"consoleInfractions", "&cInfractions for &f{0}\n&cSpam: &9VL: &7{1}\n&dSimilarity: &9VL: &7{2}
                // \n&eCapitals: &9VL: &7{3}\n&aBlacklist: &9VL: &7{4}\n&7Player has Blacklist toggled &6{5}&7.");
                String message = Main.getPlugin(Main.class).getMessages().getString("consoleInfractions")
                        .replace("{0}", (getServer().getPlayer(args[0]).getName() + " (" + playerData.getPlayerConfig().getInt("logins") + ")"))
                        .replace("{1}", String.valueOf(playerData.getPlayerConfig().getInt("violations.spam")))
                        .replace("{2}", String.valueOf(playerData.getPlayerConfig().getInt("violations.similar")))
                        .replace("{3}", String.valueOf(playerData.getPlayerConfig().getInt("violations.capitals")))
                        .replace("{4}", String.valueOf(playerData.getPlayerConfig().getInt("violations.blacklist")));
                if (toggled.contains(getServer().getPlayer(args[0]).getUniqueId())) {
                    message = message.replace("{5}", "on");
                } else {
                    message = message.replace("{5}", "off");
                }
                sender.sendMessage(translate(message));
                return true;
            }
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("notOnline")
                    .replace("{0}", args[0])));
            return true;
        }

        if (args.length == 0 && (sender.hasPermission("ca.infractions.self") || sender.hasPermission("ca.infractions.others"))) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("openingInfractionsGui")));
            ((Player) sender).closeInventory();
            ((Player) sender).openInventory(InfractionsInventory(getServer().getPlayer(sender.getName()), getServer().getPlayer(sender.getName())));
            return true;
        } else if (args.length >= 1 && (sender.hasPermission("ca.infractions.others"))) {
            if (getPlayer(args[0]) != null) {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("openingInfractionsGui")));
                new PlayerFile(getPlayer(args[0]));
                ((Player) sender).closeInventory();
                ((Player) sender).openInventory(InfractionsInventory(getServer().getPlayer(sender.getName()), getServer().getPlayer(args[0])));
                return true;
            }
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("notOnline")
                    .replace("{0}", args[0])));
            return true;
        } else {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            return true;
        }
    }
}
