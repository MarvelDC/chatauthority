package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

import static com.github.marveldc.chatauthority.Main.prefix;
import static com.github.marveldc.chatauthority.Main.staffChat;
import static com.github.marveldc.chatauthority.Util.translate;
import static org.bukkit.Bukkit.getServer;

public class StaffChat implements CommandExecutor {

    public StaffChat(Main plugin){
        plugin.getCommand("staffchat").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ca.staffchat")) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            return true;
        }

        if (args.length == 0) {
            Player player = getServer().getPlayer(sender.getName());
            if (staffChat.contains(player)) {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleStaffChatOff")));
                staffChat.remove(player);
            } else {
                sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("toggleStaffChatOn")));
                staffChat.add(player);
            }
            return true;
        }
        StringBuilder msg1 = new StringBuilder();

        for (String arg : Arrays.copyOfRange(args, 0, args.length)) {
            msg1.append(arg);
            msg1.append(" ");
        }
        sendStaffChat(getServer().getPlayer(sender.getName()), msg1.toString());
//        if (args[0].substring(0,1).equals("!")) {
//            msg = msg.substring(1);
//        }
//        getServer().broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("formatStaffChat")
//                .replace("{0}", sender.getName())
//                .replace("{1}", msg))
//                , "ca.staffchat");

        return true;
    }

    public static void sendStaffChat(Player player, String message) {
        if (message.substring(0,1).equals("!")) {
            message = message.substring(1);
        }
        getServer().broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("formatStaffChat")
                        .replace("{0}", player.getName())
                        .replace("{1}", message))
                , "ca.staffchat");
    }
}
