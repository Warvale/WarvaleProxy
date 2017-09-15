package net.warvale.bungee.message;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.warvale.bungee.WarvaleProxy;

import java.util.HashMap;
import java.util.logging.Level;

public class MessageManager {

    private static MessageManager instance;
    private static HashMap<PrefixType, String> prefix = new HashMap<>();

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public void setup() {
        prefix.put(PrefixType.MAIN, ChatColor.DARK_RED + "Warvale" + " " + ChatColor.DARK_GRAY + "»");
        prefix.put(PrefixType.ARROW, ChatColor.DARK_GRAY + "»");
        prefix.put(PrefixType.PERMISSIONS, ChatColor.GOLD + "Permissions" + " " + ChatColor.DARK_GRAY + "»");
        prefix.put(PrefixType.PORTAL, ChatColor.GOLD + "Portal" + " " + ChatColor.DARK_GRAY + "»");

    }

    public static String getPrefix(PrefixType type) {
        return prefix.get(type) + " " + ChatColor.RESET;
    }

    /**
     * Broadcasts a message to everyone online.
     *
     * @param type the prefix to use
     * @param message the message.
     */
    public static void broadcast(PrefixType type, String message) {
        broadcast(type, message, null);
    }

    /**
     * Broadcasts a message to everyone online that has a given permission
     *
     * @param type the prefix type to use
     * @param message the message
     * @param permission the permission users need to see the message
     */
    public static void broadcast(PrefixType type, String message, String permission) {
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (permission != null && !online.hasPermission(permission)) {
                continue;
            }

            online.sendMessage(new TextComponent(getPrefix(type) + message));
        }

        message = message.replaceAll("§l", "");
        message = message.replaceAll("§o", "");
        message = message.replaceAll("§r", "§f");
        message = message.replaceAll("§m", "");
        message = message.replaceAll("§n", "");

        WarvaleProxy.getInstance().getLogger().log(Level.INFO, message);
    }

    /**
     * Broadcasts a message without a prefix to everyone online
     *
     * @param message the message
     */
    public static void broadcast(String message) {
        broadcast(message, null);
    }

    /**
     * Broadcasts a message without a prefix to everyone online that has the required permission
     *
     * @param message the message
     * @param permission the permission users need to see the message
     */
    public static void broadcast(String message, String permission) {
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (permission != null && !online.hasPermission(permission)) {
                continue;
            }

            online.sendMessage(new TextComponent(message));
        }

        message = message.replaceAll("§l", "");
        message = message.replaceAll("§o", "");
        message = message.replaceAll("§r", "§f");
        message = message.replaceAll("§m", "");
        message = message.replaceAll("§n", "");

        WarvaleProxy.getInstance().getLogger().log(Level.INFO, message);
    }
}
