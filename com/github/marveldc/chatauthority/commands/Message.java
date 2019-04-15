package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.github.marveldc.chatauthority.Filters.*;
import static com.github.marveldc.chatauthority.Main.prefix;
import static com.github.marveldc.chatauthority.Main.reply;
import static com.github.marveldc.chatauthority.Main.toggled;
import static com.github.marveldc.chatauthority.Util.translate;
import static org.bukkit.Bukkit.getServer;

public class Message extends ChatListener implements CommandExecutor {

    public Message(Main plugin){
        plugin.getCommand("message").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("playerOnlyCommand")));
            //sender.sendMessage(translate(prefix + "Player only command."));
            return true;
        }
        if (sender.hasPermission("ca.msg")) {
            if (args.length == 0) {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("usageMessage")));
                //sender.sendMessage(translate(prefix + "&7Usage: &c/msg <online player> <message>"));
                return true;
            } else {
                Player pl = Bukkit.getPlayer(args[0]);
                if (pl != null && pl.isOnline()) {
                    if ((isSpam(sender.getName())) && (!(sender.hasPermission("ca.bypass.spam")))) {
                        sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedSpam")));
                        //sender.sendMessage(translate(prefix + "&cSlow down!"));
                        return true;
                    }

                    StringBuilder msg1 = new StringBuilder();
                    //String[] newArray = Arrays.copyOfRange(args, 1, args.length);

                    for (String arg : Arrays.copyOfRange(args, 1, args.length))
                    {
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

                    String finMessage = isBlacklist(msg, sender.getName());
                    reply.remove(sender);
                    reply.remove(pl);
                    reply.put(getServer().getPlayer(sender.getName()), pl);
                    reply.put(pl, getServer().getPlayer(sender.getName()));

                    if (toggled.contains(((Player) sender).getUniqueId())) {
                        sender.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("formatMeToYou")
                                .replace("{0}", pl.getDisplayName())
                                .replace("{1}", finMessage)));
                        //sender.sendMessage(translate("&9[&6-> &7" + pl.getDisplayName() + "&9]&8: &r" + finMessage));
                    } else {
                        sender.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("formatMeToYou")
                                .replace("{0}", pl.getDisplayName())
                                .replace("{1}", msg)));
                        //sender.sendMessage(translate("&9[&6-> &7" + pl.getDisplayName() + "&9]&8: &r" + msg));
                    }

                    if (toggled.contains(pl.getUniqueId())) {
                        pl.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("formatYouToMe")
                                .replace("{0}", getServer().getPlayer(sender.getName()).getDisplayName())
                                .replace("{1}", finMessage)));
                        //pl.sendMessage(translate("&9[&7" + getServer().getPlayer(sender.getName()).getDisplayName() + " &6-> &9]&8: &r" + finMessage));
                    } else {
                        pl.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("formatYouToMe")
                                .replace("{0}", getServer().getPlayer(sender.getName()).getDisplayName())
                                .replace("{1}", msg)));
                        //pl.sendMessage(translate("&9[&7" + getServer().getPlayer(sender.getName()).getDisplayName() + " &6-> &9]&8: &r" + msg));
                    }
                    return true;
                } else {
                    sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("notOnline").replace("{0}", args[0])));
                    //sender.sendMessage(translate(prefix + "&c" + args[0] + " &7is not online."));
                    return true;
                }
            }
        } else {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            //sender.sendMessage(translate(prefix + "&7Not enough permissions."));
            return true;
        }
    }
}
