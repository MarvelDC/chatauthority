package com.github.marveldc.chatauthority.inventory;

import com.github.marveldc.chatauthority.Filters;
import com.github.marveldc.chatauthority.Main;
import com.github.marveldc.chatauthority.PlayerFile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.github.marveldc.chatauthority.Main.toggled;
import static com.github.marveldc.chatauthority.Util.translate;
import static com.github.marveldc.chatauthority.commands.Chat.muted;
import static org.bukkit.Bukkit.getServer;

public class CustomInventory implements Listener {

    public static HashMap cachedItems = new HashMap<String, ItemStack>() {{
        put("arrow", new ItemStack(Material.ARROW, 1));
        put("chatlogbook", new ItemStack(Material.ENCHANTED_BOOK, 1));
        put("messagepaper", new ItemStack(Material.PAPER, 1));
        put("resetViolations", new ItemStack(Material.BARRIER, 1));
        put("resetYes", new ItemStack(Material.WOOL, 1, (byte) 5));
        put("resetNo", new ItemStack(Material.WOOL, 1, (byte) 14));
        put("bedrock", new ItemStack(Material.BEDROCK, 1));
    }};

    public static void chatInventory(Player player) {
        Inventory i = getServer().createInventory(null, 9, translate("&c&lChat"));

        if (!player.hasPermission("ca.admin.mute")) {
            ItemStack mute = (ItemStack) cachedItems.get("bedrock");
            ItemMeta muteMeta = mute.getItemMeta();
            muteMeta.setDisplayName(translate("&4&lNo permission"));
            mute.setItemMeta(muteMeta);
            i.setItem(3, mute);
        } else if (muted == Boolean.TRUE) {
            ItemStack mute = new ItemStack(Material.WOOL, 1, (byte) 5);
            ItemMeta muteMeta = mute.getItemMeta();
            muteMeta.setDisplayName(translate("&a&lUnmute &7chat"));
            mute.setItemMeta(muteMeta);
            i.setItem(3, mute);
        } else if (muted == Boolean.FALSE) {
            ItemStack mute = new ItemStack(Material.WOOL, 1, (byte) 14);
            ItemMeta muteMeta = mute.getItemMeta();
            muteMeta.setDisplayName(translate("&c&lMute &7chat"));
            mute.setItemMeta(muteMeta);
            i.setItem(3, mute);
        }

        ItemStack close = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(translate("&4Close menu"));
        close.setItemMeta(closeMeta);
        i.setItem(8, close);

        if (!player.hasPermission("ca.admin.clear")) {
            ItemStack clear = (ItemStack) cachedItems.get("bedrock");
            ItemMeta clearMeta = clear.getItemMeta();
            clearMeta.setDisplayName(translate("&4&lNo permission"));
            clear.setItemMeta(clearMeta);
            i.setItem(5, clear);
        } else {
            ItemStack clear = new ItemStack(Material.MUSHROOM_SOUP, 1);
            ItemMeta clearMeta = clear.getItemMeta();
            clearMeta.setDisplayName(translate("&6&lClear &7chat"));
            clear.setItemMeta(clearMeta);
            i.setItem(5, clear);
        }

        if (!player.hasPermission("ca.admin.reload")) {
            ItemStack clear = (ItemStack) cachedItems.get("bedrock");
            ItemMeta clearMeta = clear.getItemMeta();
            clearMeta.setDisplayName(translate("&4&lNo permission"));
            clear.setItemMeta(clearMeta);
            i.setItem(1, clear);
        } else {
            ItemStack reload = new ItemStack(Material.BOOK, 1);
            ItemMeta reloadMeta = reload.getItemMeta();
            reloadMeta.setDisplayName(translate("&a&lReload &7configurations"));
            reload.setItemMeta(reloadMeta);
            i.setItem(1, reload);
        }

        player.closeInventory();
        player.openInventory(i);
    }

    public static Inventory InfractionsInventory(Player player, Player victim) {
        Inventory i = getServer().createInventory(null, 27, translate(victim.getName() + "&c Infractions"));

        //Victim head
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta playerHeadMeta = (SkullMeta) head.getItemMeta();
        playerHeadMeta.setOwningPlayer(victim);
        playerHeadMeta.setDisplayName(translate("&b&l" + victim.getName()));
        PlayerFile playerData = new PlayerFile(victim);
        playerHeadMeta.setLore(Collections.singletonList(translate("&cLogins: &7" + playerData.getPlayerConfig().getInt("logins") + "&c.")));
        head.setItemMeta(playerHeadMeta);

        i.setItem(4, head);
        ItemStack bedrock = new ItemStack(Material.BEDROCK, 1);
        ItemMeta bedrockMeta;
        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookMeta;

        //Spam
        if (victim.hasPermission("ca.bypass.spam") || victim.hasPermission("ca.bypass.*")) {
            bedrockMeta = bedrock.getItemMeta();
            bedrockMeta.setDisplayName(translate("&c&lSpam"));

            bedrockMeta.setLore(Collections.singletonList(translate(("&cPlayer bypasses Spam."))));
            bedrock.setItemMeta(bedrockMeta);
            i.setItem(10, bedrock);
        } else {
            bookMeta = book.getItemMeta();
            bookMeta.setDisplayName(translate("&c&lSpam"));
            bookMeta.setLore(Collections.singletonList(translate(("&9VL: &7"
                    + playerData.getPlayerConfig().getInt("violations.spam")))));
            book.setItemMeta(bookMeta);
            i.setItem(10, book);
        }

        //Similarity
        if (victim.hasPermission("ca.bypass.similar") || victim.hasPermission("ca.bypass.*")) {
            bedrockMeta = bedrock.getItemMeta();
            bedrockMeta.setDisplayName(translate("&d&lSimilarity"));

            bedrockMeta.setLore(Collections.singletonList(translate(("&cPlayer bypasses Similar messages."))));
            bedrock.setItemMeta(bedrockMeta);
            i.setItem(12, bedrock);
        } else {
            bookMeta = book.getItemMeta();
            bookMeta.setDisplayName(translate("&d&lSimilarity"));
            bookMeta.setLore(Collections.singletonList(translate(("&9VL: &7"
                    + playerData.getPlayerConfig().getInt("violations.similar")))));
            book.setItemMeta(bookMeta);
            i.setItem(12, book);
        }

        //Capitals
        if (victim.hasPermission("ca.bypass.capitals") || victim.hasPermission("ca.bypass.*")) {
            bedrockMeta = bedrock.getItemMeta();
            bedrockMeta.setDisplayName(translate("&e&lCapitals"));

            bedrockMeta.setLore(Collections.singletonList(translate(("&cPlayer bypasses Capitals."))));
            bedrock.setItemMeta(bedrockMeta);
            i.setItem(14, bedrock);
        } else {
            bookMeta = book.getItemMeta();
            bookMeta.setDisplayName(translate("&e&lCapitals"));
            bookMeta.setLore(Collections.singletonList(translate(("&9VL: &7"
                    + playerData.getPlayerConfig().getInt("violations.capitals")))));
            book.setItemMeta(bookMeta);
            i.setItem(14, book);
        }

        //Blacklist
        bookMeta = bedrock.getItemMeta();
        bookMeta.setDisplayName(translate("&a&lBlacklist"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(translate("&9VL: &7" + playerData.getPlayerConfig().getInt("violations.blacklist")));
        if (toggled.contains(victim.getUniqueId())) {
            lore.add(translate("&7Player has Blacklist toggled &aon&7."));
        } else {
            lore.add(translate("&7Player has Blacklist toggled &coff&7."));
        }
        bookMeta.setLore(lore);
        book.setItemMeta(bookMeta);
        i.setItem(16, book);

        //Arrow
        ItemStack arrow = (ItemStack) cachedItems.get("arrow");
        ItemMeta arrowMeta = arrow.getItemMeta();
        arrowMeta.setDisplayName(translate("&7Next"));
        arrow.setItemMeta(arrowMeta);
        i.setItem(26, arrow);

        if (player.hasPermission("ca.infractions.reset")) {
            ItemStack reset = (ItemStack) cachedItems.get("resetViolations");
            ItemMeta resetMeta = reset.getItemMeta();
            resetMeta.setDisplayName(translate("&4&lReset violations"));
            resetMeta.setLore(Collections.singletonList(translate("&7This is a &6one time &7operation!")));
            reset.setItemMeta(resetMeta);
            i.setItem(18, reset);
        }

        return i;
    }

    static Inventory InfractionsConfirm(Player player, Player victim) {
        Inventory i = getServer().createInventory(null, 9, translate(victim.getName() + "&c Infractions"));

        //yes
        ItemStack yes = (ItemStack) cachedItems.get("resetYes");
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.setDisplayName(translate("&a&lRESET"));
        yes.setItemMeta(yesMeta);

        //no
        ItemStack no = (ItemStack) cachedItems.get("resetNo");
        ItemMeta noMeta = no.getItemMeta();
        noMeta.setDisplayName(translate("&4&lCANCEL"));
        no.setItemMeta(noMeta);

        i.setItem(3, yes);
        i.setItem(5, no);
        return i;
    }

    static Inventory InfractionsInventory2(Player player, Player victim) {
        Inventory i = getServer().createInventory(null, 27, translate(victim.getName() + "&c Infractions"));

        //Arrow
        ItemStack arrow = (ItemStack) cachedItems.get("arrow");
        ItemMeta arrowMeta = arrow.getItemMeta();
        arrowMeta.setDisplayName(translate("&7Back"));
        arrow.setItemMeta(arrowMeta);
        i.setItem(18, arrow);

        //Chatlog book
        ItemStack chatlogBook = (ItemStack) cachedItems.get("chatlogbook");
        ItemMeta bookMeta = chatlogBook.getItemMeta();
        if (!Filters.messages.containsKey(victim.getName())) {
            bookMeta.setDisplayName(translate("&c&lChatlog &7(user has not spoken)"));
            chatlogBook.setItemMeta(bookMeta);
            i.setItem(4, chatlogBook);
            return i;
        }
        List<String> message = Filters.messages.get(victim.getName());
        ItemStack paper = (ItemStack) cachedItems.get("messagepaper");
        ItemMeta paperMeta;
        int size = message.size();

        if (size == 1) bookMeta.setDisplayName(translate("&c&lChatlog &7(user has &6" + size + " &7message)"));
        else bookMeta.setDisplayName(translate("&c&lChatlog &7(user has &6" + size + " &7messages)"));
        bookMeta.setLore(Collections.singletonList(translate("&8Maximum is &65&8 messages.")));
        chatlogBook.setItemMeta(bookMeta);
        i.setItem(4, chatlogBook);

        for (int x = 0; x <= size-1; x++) { //10 + 1,2,3,4,5
            paper.setAmount(x+1);
            paperMeta = paper.getItemMeta();
            paperMeta.setDisplayName(" ");
            paperMeta.setLore(Collections.singletonList(translate("&r" + message.get(x))));
            paper.setItemMeta(paperMeta);
            i.setItem(11+x, paper);
        }
        return i;
    }
}