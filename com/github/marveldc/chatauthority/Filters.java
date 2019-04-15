package com.github.marveldc.chatauthority;

//import com.comphenix.protocol.wrappers.EnumWrappers;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.SoundCategory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static com.github.marveldc.chatauthority.Util.translate;
import static java.lang.Math.pow;
import static org.bukkit.Bukkit.*;

public class Filters {
    public static Map<String, List<String>> messages = new HashMap<>();
    private static Map<String, Instant> times = new HashMap<>();

    public static Boolean isSimilarity(String player, String message, int online) {
        if (messages.isEmpty()) {
            messages.put(player, new ArrayList<String>(){{ add(message); }});
        } else {
            if (messages.size() >= (online * 4)) {
                messages.clear();
                messages.put(player, new ArrayList<String>(){{ add(message); }});
            }

            if (!(messages.containsKey(player))) {
                messages.put(player, new ArrayList<String>(){{ add(message); }});
            } else {
//                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
//                });
                if (!getPlayer(player).hasPermission("ca.bypass.similar")) {
                    for (String element : messages.get(player)) {
                        Similar str1 = new Similar(element);
                        if (str1.isSimilar(message).equals("Yes")) {
                            PlayerFile playerData = new PlayerFile(getServer().getPlayer(player));
                            int total = playerData.getPlayerConfig().getInt("violations.similar") + 1;
                            playerData.getPlayerConfig().set("violations.similar", total);
                            playerData.save();
                            if (total % 10 == 0) broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("infractionAlert")
                                    .replace("{0}", "Similar")
                                    .replace("{1}", getPlayer(player).getName())
                                    .replace("{2}", String.valueOf(total))), "ca.infractions.alert");
                            return true;
                        }
                    }
                }
                List<String> oldmessages = messages.get(player);
                oldmessages.add(message);
                messages.put(player, oldmessages);

                if (messages.get(player).size() == 6) {
                    oldmessages = messages.get(player);
                    oldmessages.remove(0);
                    messages.put(player, oldmessages);

//                    List<String> finalThemessage = messages.get(player);
//                    for (ListIterator<String> it = finalThemessage.listIterator(); it.hasNext(); ) {
//                        String element = it.next();
//                        it.remove();
//                        finalThemessage.remove(element);
//                        if (finalThemessage.size() <= 3) {
//                            break;
//                        }
//                    }
                }
            }
        }
        return false;
    }

    public static Boolean isCaps(String message, String player) {
        if (getPlayer(player).hasPermission("ca.bypass.caps")) return false;
        int isUp = 0;
        for (int k = 0; k < message.length(); k++) {
            if(Character.isUpperCase((message.charAt(k)))) isUp++;
        }
        try {
            if (((message.length()-isUp) * (100/isUp)) <= 60) {
                PlayerFile playerData = new PlayerFile(getServer().getPlayer(player));
                int total = playerData.getPlayerConfig().getInt("violations.capitals") + 1;
                playerData.getPlayerConfig().set("violations.capitals", total);
                playerData.save();
                if (total % 10 == 0) broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("infractionAlert")
                        .replace("{0}", "Capitals")
                        .replace("{1}", getPlayer(player).getName())
                        .replace("{2}", String.valueOf(total))), "ca.infractions.alert");
                return true;
            }
        } catch(java.lang.ArithmeticException e) {
            return false;
        }
        return false;
    }

    public static Boolean isSpam(String player) {
        if (getPlayer(player).hasPermission("ca.bypass.spam")) return false;
        if (!(times.containsKey(player))) {
            times.put(player, Instant.now());
            return false;
        } else {
            if (Duration.between(times.get(player), Instant.now()).toMillis() < 2000) {
                times.put(player, Instant.now());
                PlayerFile playerData = new PlayerFile(getServer().getPlayer(player));
                int total = playerData.getPlayerConfig().getInt("violations.spam") + 1;
                playerData.getPlayerConfig().set("violations.spam", total);
                if (total % 10 == 0) broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("infractionAlert")
                        .replace("{0}", "Spam")
                        .replace("{1}", getPlayer(player).getName())
                        .replace("{2}", String.valueOf(total))), "ca.infractions.alert");
                playerData.save();
                return true;
            }
        }
        times.put(player, Instant.now());
        return false;
    }

    public static String isBlacklist(String message, String player) {
        int violations = 0;
        @SuppressWarnings("unchecked")
        List<String> blacklist = (List<String>) Main.getPlugin(Main.class).getBlacklistWords().get("blacklist");
        @SuppressWarnings("unchecked")
        List<String> exempt = (List<String>) Main.getPlugin(Main.class).getBlacklistWords().get("exempt");

        for (String word : message.split("\\s+")) {
            word = word.toLowerCase();
            if ((exempt.parallelStream().noneMatch(word::contains))) {
                Optional<String> theWord = blacklist.parallelStream().filter(word::contains).findAny();
                if ((theWord.isPresent())) {
                    message = message.replaceAll("(?i)" + Pattern.quote(theWord.get()), StringUtils.repeat(translate(Main.getPlugin(Main.class).getMessages().getString("censoredWithChar")), theWord.get().length()));
                    violations++;
                }
            }
        }
        if (violations != 0) {
            PlayerFile playerData = new PlayerFile(getServer().getPlayer(player));
            int total = playerData.getPlayerConfig().getInt("violations.blacklist") + violations;
            playerData.getPlayerConfig().set("violations.blacklist", total);
            if (total % 10 == 0) broadcast(translate(Main.getPlugin(Main.class).getMessages().getString("infractionAlert")
            .replace("{0}", "Blacklist")
            .replace("{1}", getPlayer(player).getName())
            .replace("{2}", String.valueOf(total))), "ca.infractions.alert");
            playerData.save();
        }
//            for (Object aBlacklist : blacklist) {
//                message = message.replaceAll("(?i)" + Pattern.quote(aBlacklist.toString()),
//                        StringUtils.repeat("*", aBlacklist.toString().length()));
//        }
        return message;
    }

    public static String autoPunctuation(String message) {
        char lastChar = message.charAt(message.length() - 1);
        char[] identifiers = {'.', '!', ' ', ')', ',', ':', ';', '?', '(', '='};
        int i = 0;

        for (Character character : identifiers) {
            if (character.equals(lastChar)) {
                i++;
                break;
            }
        }
        message = StringUtils.capitalize(message);
        if (i >= 1) {
            return message;
        }
        message = message + ".";
        message = message.replace(":d", ":D");
        message = message.replace(":D.", ":D");
        message = message.replace(":p", ":P");
        message = message.replace(":P.", ":P");
        message = message.replace(":C", ":c");
        message = message.replace(":c.", ":c");
        message = message.replace(":O", ":o");
        message = message.replace(":o.", ":o");
        return message;
    }

    public static String mention(String message, Player sentBy) {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        String stripped = ChatColor.stripColor(message);
        message = stripped;
        for (Player player : players) {
            if (message.toLowerCase().contains(player.getName().toLowerCase()) /*&& !message.toLowerCase().equals(sentBy)*/){
                if (sentBy.canSee(player)) {
                    int index = stripped.toLowerCase().indexOf(player.getName().toLowerCase());
                    if (!stripped.substring(index, (player.getName().length() + index)).equalsIgnoreCase(sentBy.getName())) {
                        //message = stripped.substring(0, index) + ChatColor.AQUA + ChatColor.BOLD + stripped.substring(index, (player.getName().length() + index)) + ChatColor.RESET +
                        //stripped.substring((index + player.getName().length()));
                        message = stripped.substring(0, index) + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.RESET +
                                stripped.substring((index + player.getName().length()));
//                Main.getPlugin(Main.class).create(player);
//                if (!Main.getPlayers().getBoolean("toggles.mention")) continue;
                        PlayerFile playerData = new PlayerFile(player);
                        if (!playerData.getPlayerConfig().getBoolean("toggles.mention")) continue;
                        //player.sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);
                        player.sendTitle(translate(Main.getPlugin(Main.class).getMessages().getString("mentionTitle")), translate(Main.getPlugin(Main.class).getMessages().getString("mentionSubtitle")
                                        .replace("{0}", sentBy.getName()))
                                , 40, 60, 40);

                        //public PacketPlayOutNamedSoundEffect(SoundEffect var1, SoundCategory var2, double var3, double var5, double var7, float var9, float var10)
                        Location l = player.getLocation();
                        SoundEffect effect = SoundEffect.a.get(new MinecraftKey("entity.player.levelup"));
                        if (effect != null) {
                            PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(effect, SoundCategory.PLAYERS, l.getX(), l.getY(), l.getZ(), 1f, 0.5f);
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                        }

                        //player.spawnParticle(Particle.VILLAGER_HAPPY , player.getLocation().getX(), player.getLocation().getY()+2.5, player.getLocation().getZ(), 2);
                        createHelix(player);
                        break;
                    }
                }
            }
//            message = message.replaceFirst(player.getName(), (ChatColor.AQUA + player.getName()));
//            message = message.replaceFirst(player.getDisplayName(), (ChatColor.AQUA + player.getDisplayName() + ChatColor.RESET));
        }
        return message;
    }

    private static void createHelix(Player player) {
        new BukkitRunnable(){
            double t = 0; // t for Time
            double pi = Math.PI;
            //Location loc = player.getLocation();
            float elevation = -1;

            public void run(){

                t += 4*pi/(4.0*20);
                elevation += 0.02;

                for(double phi = 0; phi <= 2 * pi; phi += pi / 2) {
                    float x = (float) (0.4 * (4 * pi - t) * Math.cos(t + phi));
                    float y = (float) (0.3 * t) + elevation;
                    float z = (float) (0.4 * (4 * pi - t) * Math.sin(t + phi));
                    //loc.add(x,y,z);
                    //ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1, 5, loc, 100);
                    //PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true, (float) loc.getX()+x, (float) loc.getY()+y, (float) loc.getZ()+z, 0, 0, 0, 1, 5, 100);
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true, locX(player)+x, locY(player)+y, locZ(player)+z, 0, 0, 0, 1, 5, 100);
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                    //loc.subtract(x,y,z);
                    if(t >= 4 * pi){
                        this.cancel();
                        //loc.add(x,y,z);
                        //ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1, 100, loc, 100);
                        PacketPlayOutWorldParticles packet1 = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true, locX(player)+x, locY(player)+y, locZ(player)+z, 0, 0, 0, 1, 100, 100);
                        PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, locX(player)+x, locY(player)+y, locZ(player)+z, 0, 0, 0, 1, 100, 100);
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet1);
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet2);

                        //loc.subtract(x,y,z);
                    }
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    private static float locX(Player player) {
        return (float) player.getLocation().getX();
    }

    private static float locY(Player player) {
        return (float) player.getLocation().getY();
    }

    private static float locZ(Player player) {
        return (float) player.getLocation().getZ();
    }
}
