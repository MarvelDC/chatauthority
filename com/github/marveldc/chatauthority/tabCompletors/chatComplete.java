package com.github.marveldc.chatauthority.tabCompletors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class chatComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        if (sender.hasPermission("ca.admin.clear")) cmds.add("clear");
        if (sender.hasPermission("ca.toggle.mute")) cmds.add("mute");
        if (sender.hasPermission("ca.toggle.reload")) cmds.add("reload");
        return (args.length > 0) ? StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<>()) : null;
    }
}
