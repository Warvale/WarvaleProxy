package net.warvale.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.warvale.bungee.message.MessageManager;

public class MCCommand extends Command {

    public MCCommand() {
        super("mc", "warvale.staff");
    }

    public void execute(CommandSender sender, String[] args)
    {

        if ((sender instanceof ProxyServer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 1)
        {
            player.sendMessage(new TextComponent("§cUsage: /mc <message>"));
            return;
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            b.append(args[i]);
            b.append(" ");
        }

        MessageManager.broadcast(ChatColor.AQUA + "[MC] " + player.getName() + " (" + player.getServer().getInfo().getName() + "): " + b, "warvale.staff");


    }

}
