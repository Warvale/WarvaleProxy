package net.warvale.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.warvale.bungee.message.MessageManager;
import net.warvale.bungee.message.PrefixType;

public class TitleBroadcastCommand extends Command {

    public TitleBroadcastCommand(String name, String permission, String... aliases)
    {
        super(name, permission, aliases);
    }

    public void execute(CommandSender commandSender, String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage(new TextComponent("§7[§6TB§7] §c/tb <message>"));
            return;
        }
        Title title = ProxyServer.getInstance().createTitle();


        title.fadeIn(5);
        title.fadeIn(5);
        title.stay(5 * 20);

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            b.append(args[i]);
            b.append(" ");
        }

        title.title(new TextComponent(ChatColor.GOLD + "Announcement"));
        title.subTitle(new TextComponent(ChatColor.translateAlternateColorCodes('&', b.toString())));

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            p.sendTitle(title);
        }

        ProxyServer.getInstance().broadcast(new TextComponent(MessageManager.getPrefix(PrefixType.MAIN) +
                ChatColor.translateAlternateColorCodes('&', b.toString())));

        commandSender.sendMessage(new TextComponent("§7[§6TB§7] §aTitle has been sent."));
    }

}
