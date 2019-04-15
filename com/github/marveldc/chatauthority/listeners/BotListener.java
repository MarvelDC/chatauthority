package com.github.marveldc.chatauthority.listeners;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.PlayerFile;
import com.github.marveldc.chatauthority.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.github.marveldc.chatauthority.Main.*;

public class BotListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        //showExplosion(e.getPlayer());
        //showAdvancement(e.getPlayer());
        if (!notMoved.contains(e.getPlayer())) return;
        Player player = e.getPlayer();
        double velocity = player.getVelocity().getY();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
            if (velocity > 0) {
                if (notMoved.contains(player)) {
                    if (!player.isOnGround() && Double.compare(velocity, (double) 0.42F) == 0) {
                        notMoved.remove(player);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
//        if (!(toggled.contains(e.getPlayer().getName()))) {
//            Main.getPlugin(Main.class).create(e.getPlayer());
//            if (getPlayers().getBoolean("toggles.blacklist")) {
//                toggled.add(e.getPlayer().getName());
//            }
//        }
        notMoved.add(e.getPlayer());
        PlayerFile playerData = new PlayerFile(e.getPlayer());
        playerData.getPlayerConfig().set("logins", (playerData.getPlayerConfig().getInt("logins") + 1));
        playerData.save();
        if (!toggled.contains(e.getPlayer().getUniqueId())) {
            if (playerData.getPlayerConfig().getBoolean("toggles.blacklist")) toggled.add(e.getPlayer().getUniqueId());
        }

//        Main.getPlugin(Main.class).create(e.getPlayer());
//        int logins = getPlayers().getInt("logins");
//        getPlayers().set("logins", logins+1);
//        Main.getPlugin(Main.class).saveFile(4);
        //Util.addToggled(e.getPlayer());
    }

//    @EventHandler
//    public void onLeave(PlayerQuitEvent e) {
//        notMoved.remove(e.getPlayer());
//    }
}
