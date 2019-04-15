package com.github.marveldc.chatauthority;

import org.bukkit.ChatColor;
public class Util {

//    public static void addToggled(Player player) {
//        if (!toggled.contains(player.getUniqueId())) {
//            Main.getPlugin(Main.class).create(player.getPlayer());
//            if (getPlayers().getBoolean("toggles.blacklist")) {
//                toggled.add(player.getUniqueId());
//            }
//        }
//    }

    public static String translate(String input){
        return (ChatColor.translateAlternateColorCodes('&', input));
    }
}
