package net.warvale.bungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.event.EventHandler;
import net.warvale.bungee.utils.communication.ChatUtils;

public class PingListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();
        serverPing.setDescriptionComponent(new TextComponent(ChatUtils.center(ChatColor.DARK_RED + "Warvale") +
                "\n" + ChatUtils.center("Play Compete Repeat")));

        event.setResponse(serverPing);
    }

}
