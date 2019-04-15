package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.github.marveldc.chatauthority.Filters.*;
import static com.github.marveldc.chatauthority.Main.prefix;
import static com.github.marveldc.chatauthority.Main.toggled;
import static com.github.marveldc.chatauthority.Util.translate;
import static org.bukkit.Bukkit.getServer;

public class StaffAnnounce implements CommandExecutor {
    public StaffAnnounce(Main plugin) {
        plugin.getCommand("staffannounce").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("playerOnlyCommand")));
            //sender.sendMessage(translate(prefix + "Player only command."));
            return true;
        }

        if (!sender.hasPermission("ca.staffannounce")) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            //sender.sendMessage(translate(prefix + "&7Not enough permissions."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("usageStaffAnnounce")));
            return true;
        } else {
            if ((isSpam(sender.getName())) && (!(sender.hasPermission("ca.bypass.spam")))) {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedSpam")));
                //sender.sendMessage(translate(prefix + "&cSlow down!"));
                return true;
            }

            StringBuilder msg1 = new StringBuilder();

            for (String arg : Arrays.copyOfRange(args, 0, args.length)) {
                msg1.append(arg);
                msg1.append(" ");
            }
            String msg = msg1.toString();

            if ((isSimilarity(sender.getName(), msg, getServer().getOnlinePlayers().size())) &&
                    !(sender.hasPermission("ca.bypass.similar"))) {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedSimilar")));
                //sender.sendMessage(translate(prefix + "&cBlocked private message due to similarity to previous messages."));
                return true;
            }

            if ((isCaps(msg, sender.getName())) && (!(sender.hasPermission("ca.bypass.capitals")))) {
                msg = msg.toLowerCase();
            }
            if ((!(sender.hasPermission("ca.bypass.punctuation"))) && (msg.length() >= 5)) {
                msg = autoPunctuation(msg);
            }

            String filteredMessage = isBlacklist(msg, sender.getName());
            String name = ((Player) sender).getDisplayName();

            if (!msg.equals(filteredMessage)) {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (toggled.contains(player.getUniqueId())) {
                        player.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("staffAnnounce")
                                .replace("{0}", name)
                                .replace("{1}", filteredMessage)));
                    } else {
                        sender.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("staffAnnounce")
                                .replace("{0}", name)
                                .replace("{1}", msg)));
                    }
                }
                return true;
            }
            getServer().broadcastMessage(translate(Main.getPlugin(Main.class).getMessages().getString("staffAnnounce")
                    .replace("{0}", ((Player) sender).getDisplayName())
                    .replace("{1}", msg)));
            return true;
        }
    }
}
