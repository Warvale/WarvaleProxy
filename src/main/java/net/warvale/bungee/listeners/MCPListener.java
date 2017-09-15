package net.warvale.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.warvale.bungee.utils.player.LoginUtils;

public class MCPListener implements Listener {

    @EventHandler
    public void onPostLoginEvent(final PostLoginEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        //update uuid mapping
        LoginUtils.updateUUIDMapping(proxiedPlayer);

    }

}
