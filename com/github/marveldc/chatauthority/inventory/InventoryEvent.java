package com.github.marveldc.chatauthority.inventory;

import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.PlayerFile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Level;

import static com.github.marveldc.chatauthority.Main.prefix;
import static com.github.marveldc.chatauthority.Util.translate;
import static com.github.marveldc.chatauthority.commands.Chat.clearChat;
import static com.github.marveldc.chatauthority.commands.Chat.muteChat;
import static com.github.marveldc.chatauthority.inventory.CustomInventory.*;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class InventoryEvent implements Listener {

    @EventHandler
    public void InventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory open = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if (open == null) {
            return;
        }

        if (open.getName().equals(translate("&c&lChat"))) {
            event.setCancelled(true);

            if (item == null || !item.hasItemMeta()) {
                return;
            }

            if (item.getItemMeta().getDisplayName().equals(translate("&a&lUnmute &7chat"))) {
                muteChat(player.getName());
                ItemStack mute = new ItemStack(Material.WOOL, 1, (byte) 14);
                ItemMeta muteMeta = mute.getItemMeta();
                muteMeta.setDisplayName(translate("&c&lMute &7chat"));
                mute.setItemMeta(muteMeta);
                open.setItem(3, mute);
            } else if (item.getItemMeta().getDisplayName().equals(translate("&c&lMute &7chat"))) {
                muteChat(player.getName());
                ItemStack mute = new ItemStack(Material.WOOL, 1, (byte) 5);
                ItemMeta muteMeta = mute.getItemMeta();
                muteMeta.setDisplayName(translate("&a&lUnmute &7chat"));
                mute.setItemMeta(muteMeta);
                open.setItem(3, mute);
            } else if (item.getItemMeta().getDisplayName().equals(translate("&4Close menu"))) {
                player.closeInventory();
            } else if (item.getItemMeta().getDisplayName().equals(translate("&6&lClear &7chat"))) {
                clearChat(player.getName());
                ItemStack clear = new ItemStack(Material.BOWL, 1);
                ItemMeta clearMeta = clear.getItemMeta();
                clearMeta.setDisplayName(translate("&6Clearing chat..."));
                clear.setItemMeta(clearMeta);
                open.setItem(5, clear);
                getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    final ItemStack clear1 = new ItemStack(Material.MUSHROOM_SOUP, 1);
                    final ItemMeta clearMeta1 = clear1.getItemMeta();
                    clearMeta1.setDisplayName(translate("&6&lClear &7chat"));
                    clear1.setItemMeta(clearMeta1);
                    open.setItem(5, clear1);
                }, 60L);
            } else if (item.getItemMeta().getDisplayName().contains(translate("&a&lReload"))) {
                player.closeInventory();
                Main.getPlugin(Main.class).saveConfig();
                try {
                    //Main.getPlugin(Main.class).getBlacklist().save("blacklist.yml");
                    Main.getPlugin(Main.class).getMessages().save("messages.yml");
                    Main.getPlugin(Main.class).createMessagesFile();
                    Main.getPlugin(Main.class).reloadMessages();
                } catch (IOException e){
                    player.sendMessage(translate("&f[ChatAuthority] &4ERROR: Read console for errors."));
                    e.printStackTrace();
                    Bukkit.getLogger().log(Level.SEVERE, "[ChatAuthority] Suggest deleting 'messages.yml' and restarting the server.");
                    return;
                }
                player.sendMessage(translate(prefix + Main.getPlugin(Main.class).getMessages().getString("reloadedConfig")));
            }
        } else if ((open.getName().contains(translate("&c Infractions")))) {
            String name = open.getName().substring(0, open.getName().indexOf(translate("&c")));
            if (!item.hasItemMeta()) {
                return;
            }
            if (item.getItemMeta().getDisplayName().equals(translate("&4&lReset violations"))) {
                event.setCancelled(true);
                player.closeInventory();
                player.openInventory(InfractionsConfirm(player, getPlayer(name)));
                return;
            }
            if (item.getItemMeta().getDisplayName().equals(translate("&a&lRESET"))) {
                event.setCancelled(true);
                open.clear();

                ItemStack waiting = (ItemStack) cachedItems.get("resetYes");
                ItemMeta waitingMeta = waiting.getItemMeta();
                new BukkitRunnable() {
                    int x = 1;
                    int y = 0;
                    @Override
                    public void run() {
                        waitingMeta.setDisplayName(translate("&6" + StringUtils.repeat(".", x)));
                        waiting.setItemMeta(waitingMeta);
                        open.setItem(4, waiting);
                        x++;
                        y++;
                        if (x >= 4) x = 0;
                        if (y >= 7) {
                            //open.clear();
                            //open.setContents(InfractionsInventory(player, getPlayer(name)).getContents());
                            resetViolations(getPlayer(name));
                            player.closeInventory();
                            player.openInventory(InfractionsInventory(player, getPlayer(name)));
                            this.cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(Main.getPlugin(Main.class), 5, 20);
                return;
            }
            if (item.getItemMeta().getDisplayName().equals(translate("&4&lCANCEL"))) {
                event.setCancelled(true);
                player.closeInventory();
                player.openInventory(InfractionsInventory(player, getPlayer(name)));
                return;
            }
            if (!item.getType().equals(Material.ARROW)) {
                event.setCancelled(true);
                return;
            }
            if (open.contains(Material.SKULL_ITEM)) {
                event.setCancelled(true);
                open.clear();
                open.setContents(InfractionsInventory2(player, getPlayer(name)).getContents());
                return;
            }
            if (item.getItemMeta().getDisplayName().equals(translate("&7Back"))) {
                event.setCancelled(true);
                open.clear();
                open.setContents(InfractionsInventory(player, getPlayer(name)).getContents());
            }
        }
    }

    private void resetViolations(Player player) {
        PlayerFile playerData = new PlayerFile(player);
        playerData.getPlayerConfig().set("violations.spam", 0);
        playerData.getPlayerConfig().set("violations.similar", 0);
        playerData.getPlayerConfig().set("violations.capitals", 0);
        playerData.getPlayerConfig().set("violations.blacklist", 0);
        playerData.save();
    }
}
