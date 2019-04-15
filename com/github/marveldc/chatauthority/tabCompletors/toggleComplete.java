package com.github.marveldc.chatauthority.tabCompletors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class toggleComplete implements TabCompleter {
    //private static final List<String> cmds = Arrays.asList("mention", "blacklist");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        if (sender.hasPermission("ca.toggle.mention")) cmds.add("mention");
        if (sender.hasPermission("ca.toggle.blacklist")) cmds.add("blacklist");
        return (args.length > 0) ? StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<>()) : null;
    }
}
