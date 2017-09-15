package net.warvale.bungee.tasks;

import net.md_5.bungee.api.ProxyServer;
import net.warvale.api.libraries.HTTPCommon;
import net.warvale.api.exceptions.HTTPRequestFailException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;
import net.warvale.bungee.WarvaleProxy;
import net.warvale.bungee.utils.network.CloudflareUtils;

public class RebootTimeTask implements Runnable {

    private WarvaleProxy plugin;
    private boolean threeHour;
    private boolean twoHour;
    private boolean oneHour;
    private boolean thirtyMin;
    private boolean tenMin;
    private boolean fiveMin;
    private boolean oneMin;
    private boolean tenSec;
    private boolean needsDelete;

    public RebootTimeTask(WarvaleProxy plugin) {
        this.plugin = plugin;
        this.threeHour = false;
        this.twoHour = false;
        this.oneHour = false;
        this.thirtyMin = false;
        this.tenMin = false;
        this.fiveMin = false;
        this.oneMin = false;
        this.tenSec = false;
        this.needsDelete = false;
    }

    public void run() {
        if (needsDelete) {
            try {
                String rec_id = CloudflareUtils.getRecID(WarvaleProxy.cloudflareIP);

                if (rec_id.equals("-1")) {
                    needsDelete = false;
                } else if (!rec_id.equals("-2")){
                    String urlString = "https://www.cloudflare.com/api_json.html?a=rec_delete&z=badlion.net"
                            + "&tkn=" + WarvaleProxy.cloudflareKey
                            + "&email=" + WarvaleProxy.cloudflareEmail
                            + "&id=" + rec_id;
                    JSONObject json = new JSONObject();

                    JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
                    if (response != null) {
                        if (response.containsKey("result")) {
                            if (((String) response.get("result")).equals("success")) {
                                needsDelete = false;
                            }
                        }
                    }
                }
            } catch (HTTPRequestFailException e) {

            }

        }

        DateTime currentTime = new DateTime(DateTimeZone.UTC);
        if (!threeHour && currentTime.isAfter(WarvaleProxy.restartTime.minusHours(3))) {
            threeHour = true;
            if (WarvaleProxy.delete_record == 1) {
                needsDelete = true;
            }
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 3 hours!");
        } else if (!twoHour && currentTime.isAfter(WarvaleProxy.restartTime.minusHours(2))) {
            twoHour = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 2 hours!");
        } else if (!oneHour && currentTime.isAfter(WarvaleProxy.restartTime.minusHours(1))) {
            oneHour = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 1 hour!");
        } else if (!thirtyMin && currentTime.isAfter(WarvaleProxy.restartTime.minusMinutes(30))) {
            thirtyMin = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 30 minutes!");
        } else if (!tenMin && currentTime.isAfter(WarvaleProxy.restartTime.minusMinutes(10))) {
            tenMin = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 10 minutes!");
        } else if (!fiveMin && currentTime.isAfter(WarvaleProxy.restartTime.minusMinutes(5))) {
            fiveMin = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 5 minutes!");
        } else if (!oneMin && currentTime.isAfter(WarvaleProxy.restartTime.minusMinutes(1))) {
            oneMin = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 1 minute!");
        } else if (!tenSec && currentTime.isAfter(WarvaleProxy.restartTime.minusSeconds(10))) {
            tenSec = true;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "alert This bungee proxy is going down for a restart in 10 seconds!");
        } else if (currentTime.isAfter(WarvaleProxy.restartTime)) {
            ProxyServer.getInstance().stop();
        }
    }

}
