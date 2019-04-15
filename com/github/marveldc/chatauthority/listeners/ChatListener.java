package com.github.marveldc.chatauthority.listeners;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.commands.StaffChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static com.github.marveldc.chatauthority.Filters.*;
import static com.github.marveldc.chatauthority.Main.*;
import static com.github.marveldc.chatauthority.Util.translate;
import static com.github.marveldc.chatauthority.commands.Chat.muted;
import static com.github.marveldc.chatauthority.commands.StaffChat.sendStaffChat;
import static org.bukkit.Bukkit.getServer;

public class ChatListener implements Listener {

    @EventHandler
    public void onMessageSent(AsyncPlayerChatEvent event) {
        if (notMoved.contains(event.getPlayer()) && (!(event.getPlayer().isOp())) && (!event.getPlayer().hasPermission("ca.admin"))) {
            event.setCancelled(true);
            return;
        }
        if (muted == Boolean.TRUE) {
            if (!(event.getPlayer().hasPermission("ca.bypass.mute"))) {
                event.getPlayer().sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedChatMuted")));
                //event.getPlayer().sendMessage(translate(prefix + "&7Chat is currently &cmuted&7, and you cannot chat for now."));
                event.setCancelled(true);
                return;
            }
        }

        if ((isSpam(event.getPlayer().getName())) && (!(event.getPlayer().hasPermission("ca.bypass.spam")))) {
            event.getPlayer().sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedSpam")));
            //event.getPlayer().sendMessage(translate(prefix + "&cSlow down!"));
            event.setCancelled(true);
            return;
        }

        if ((isSimilarity(event.getPlayer().getName(), event.getMessage(), getServer().getOnlinePlayers().size())) &&
                (!(event.getPlayer().hasPermission("ca.bypass.similar")))) {
            event.getPlayer().sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("blockedSimilar")));
            //event.getPlayer().sendMessage(translate(prefix + "&cBlocked chat message due to similarity to previous messages."));
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();
        if ((isCaps(event.getMessage(), event.getPlayer().getName())) && (!(event.getPlayer().hasPermission("ca.bypass.capitals")))) {
            message = message.toLowerCase();
        }

        if ((!(event.getPlayer().hasPermission("ca.bypass.punctuation"))) && (message.length() >= 5)) {
            message = autoPunctuation(message);
        }

        message = mention(message, event.getPlayer());
        if (event.getPlayer().hasPermission("ca.colour")) {
            message = translate(message);
        }

        if (message.substring(0,1).equals("!") && event.getPlayer().hasPermission("ca.staffchat")) {
            //event.getPlayer().performCommand("staffchat " + message.substring(1));
            sendStaffChat(event.getPlayer(), message);
            event.setCancelled(true);
            return;
        }

        if (event.getPlayer().hasPermission("ca.staffchat") && staffChat.contains(event.getPlayer())) {
            //event.getPlayer().performCommand("staffchat " + message);
            sendStaffChat(event.getPlayer(), message);
            event.setCancelled(true);
            return;
        }

        String finMessage = isBlacklist(message, event.getPlayer().getName());
        if (!(finMessage.equals(message))) {
            //getServer().broadcast((translate(prefix + "&8[&fBL Filter&8] &7" + event.getPlayer().getName() + " &c> &f" + event.getMessage())), "ca.admin");
            //event.setMessage(isBlacklist(message));
            for (Player player : getServer().getOnlinePlayers()) {
                if (toggled.contains(player.getUniqueId())) {
                    event.getRecipients().remove(player);
                    player.sendMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), finMessage));
                }
            }
        }
        event.setMessage(message);
    }

////    Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
////    });
}
