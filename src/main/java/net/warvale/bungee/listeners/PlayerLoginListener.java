package net.warvale.bungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.warvale.bungee.WarvaleProxy;
import net.warvale.bungee.config.ConfigManager;
import net.warvale.bungee.message.MessageManager;
import net.warvale.bungee.message.PrefixType;
import net.warvale.bungee.utils.logging.DiscordUtils;

import java.util.*;

public class PlayerLoginListener implements Listener {

    private boolean allowLogins = true;
    private List<UUID> beingsent = Collections.synchronizedList(new ArrayList<UUID>());

    public PlayerLoginListener() {
        this.allowLogins = ConfigManager.getConfig().getBoolean("allow-logins", true);
    }


    @EventHandler
    public void onPlayerPreLogin(final PreLoginEvent event) {

        if (this.allowLogins) {
            return;
        }

        event.setCancelled(true);
        WarvaleProxy.getInstance().getLogger().info(ChatColor.BLUE + event.getConnection().getName() + " tried to connect to mc.warvale.net");
        event.setCancelReason(new TextComponent("\n\n" + ChatColor.RED + "Please connect using " + ChatColor.YELLOW + "na.warvale.net" + ChatColor.RED + ",  " + ChatColor.YELLOW + "eu.warvale.net" + ChatColor.RED + " or " + ChatColor.YELLOW + "au.warvale.net" + ChatColor.RED + " instead." +
                "\n\n" + "Por favor conéctate usando " + ChatColor.YELLOW + "na.warvale.net" + ChatColor.RED + ", " + ChatColor.YELLOW + "eu.warvale.net " + ChatColor.RED + " or " + ChatColor.YELLOW + "au.warvale.net"));

    }


    /*@EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        if (e.getTarget().getName().equals("lobby")) {
            ServerInfo lobby = LobbyTransfer.getLobby();
            if (lobby != null) {
                e.getPlayer().connect(lobby);
                DiscordUtils.sendStatusUpdate("Sending " + e.getPlayer().getName() + " to " + lobby.getName(), DiscordUtils.GENERAL_MSG);
            } else {
                e.getPlayer().disconnect(new TextComponent(ChatColor.RED + "There are no lobby servers available at this time."));
                DiscordUtils.sendStatusUpdate("No lobby servers available for players", DiscordUtils.ERROR_MSG);
            }
        }
    }*/

    @EventHandler
    public void onServerTransfer(ServerConnectEvent e) {
        ProxiedPlayer player = e.getPlayer();

        if (player.getServer() == null) {
            return;
        }

        ServerInfo server = player.getServer().getInfo();
        ServerInfo targetServer = e.getTarget();

        if (server.getName().equals(targetServer.getName())) {
            return;
        }

        player.sendMessage(new TextComponent(MessageManager.getPrefix(PrefixType.PORTAL) + ChatColor.GRAY + "You have been sent from " + ChatColor.GOLD + server.getName() +
                ChatColor.GRAY + " to " + ChatColor.GOLD + targetServer.getName()));

    }



    @EventHandler
    public void onPlayerKicked(ServerKickEvent event) {
        // Not on a server yet or no cancel server
        if (event.getKickedFrom() == null) {
            // Be Safe
            event.setCancelled(true);

            event.setCancelServer(WarvaleProxy.getInstance().getProxy().getServerInfo(WarvaleProxy.getInstance().getProxy().getConfig().getListeners().iterator().next().getDefaultServer()));
            return;
        }

        if (!event.getKickedFrom().getName().contains("lobby")) {
            event.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You were kicked to the lobby: " + BaseComponent.toLegacyText(event.getKickReasonComponent())));
            event.setCancelled(true);

            event.setCancelServer(WarvaleProxy.getInstance().getProxy().getServerInfo(WarvaleProxy.getInstance().getProxy().getConfig().getListeners().iterator().next().getDefaultServer()));
        }
    }

}
