package net.warvale.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.warvale.bungee.config.ConfigManager;

public class BIPCommand extends Command {

    public BIPCommand() {
        super("ip");
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        player.sendMessage(new TextComponent(ChatColor.GREEN + "Current Bungee IP: " + ChatColor.GOLD + ConfigManager.get().getConfig().getString("cloudflare.ip")));
    }

}
