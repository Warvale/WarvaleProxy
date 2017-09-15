package net.warvale.bungee.utils.logging;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.FieldEmbed;
import net.warvale.bungee.WarvaleProxy;

import java.util.Arrays;

public class DiscordUtils {

    private static String statusHookURl = "https://canary.discordapp.com/api/webhooks/358353279345885194/wnnRRTb3OUyXRl3YAbviifNo_AKQl_cB5RYaOZyIvF6u_vFGLGVllVJ1s-jqwDRicdlU";

    public static final int GENERAL_MSG = 3447003;
    public static final int SUCCESS_MSG = 0x23c540;
    public static final int ERROR_MSG = 0xfd0006;
    public static final int WARNING_MSG = 0xfff30d;

    public static void sendStatusUpdate(String message) {
        TemmieWebhook temmie = new TemmieWebhook(statusHookURl);

        DiscordEmbed de = DiscordEmbed.builder()
                .title(WarvaleProxy.getRegion().toString() + " ProxyStatus Update") // We are creating a embed with this title...
                .description(message)
                .fields(Arrays.asList(
                        FieldEmbed.builder()
                                .name("Status Code:")
                                .value(message)
                                .build()
                ))
                .build();

        DiscordMessage dm = DiscordMessage.builder()
                .username("WarvaleProxy Bot") // We are creating a message with the username "DevotedStatus Bot"...
                .content("") // with no content because we are going to use the embed...
                .embeds(Arrays.asList(de)) // with the our embed...
                .build(); // and now we build the message!

        temmie.sendMessage(dm);
    }

    public static void sendStatusUpdate(String message, int color) {
        TemmieWebhook temmie = new TemmieWebhook(statusHookURl);

        DiscordEmbed de = DiscordEmbed.builder()
                .title(WarvaleProxy.getRegion().toString() + " ProxyStatus Update") // We are creating a embed with this title...
                .color(color)
                .fields(Arrays.asList(
                        FieldEmbed.builder()
                                .name("Status Code:")
                                .value(message)
                                .build()
                ))
                .build();

        DiscordMessage dm = DiscordMessage.builder()
                .username("WarvaleProxy Bot") // We are creating a message with the username "DevotedStatus Bot"...
                .content("") // with no content because we are going to use the embed...
                .embeds(Arrays.asList(de)) // with the our embed...
                .build(); // and now we build the message!

        temmie.sendMessage(dm);
    }

}
