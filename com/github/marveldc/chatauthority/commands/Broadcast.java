package com.github.marveldc.chatauthority.commands;

import com.github.marveldc.chatauthority.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.github.marveldc.chatauthority.Util.translate;
import static org.bukkit.Bukkit.getServer;
import static com.github.marveldc.chatauthority.Main.prefix;

public class Broadcast implements CommandExecutor {

    public Broadcast(Main plugin) {
        plugin.getCommand("broadcast").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ca.broadcast")) {
            sender.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("insufficientPermission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(translate(Main.getPlugin(Main.class).getMessages().getString("usageBroadcast")));
        }
        StringBuilder msg1 = new StringBuilder();

        for (String arg : Arrays.copyOfRange(args, 0, args.length)) {
            msg1.append(arg);
            msg1.append(" ");
        }
        getServer().broadcastMessage(translate(Main.getPlugin(Main.class).getMessages().getString("formatBroadcast").replace("{0}", msg1.toString())));
        return true;
    }
}
