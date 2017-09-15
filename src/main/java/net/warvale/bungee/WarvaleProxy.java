package net.warvale.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.warvale.api.exceptions.HTTPRequestFailException;
import net.warvale.api.files.PropertiesFile;
import net.warvale.api.libraries.HTTPCommon;
import net.warvale.api.servers.ServerRegion;
import net.warvale.bungee.commands.BIPCommand;
import net.warvale.bungee.commands.MCCommand;
import net.warvale.bungee.commands.ManagerServerCommand;
import net.warvale.bungee.commands.TitleBroadcastCommand;
import net.warvale.bungee.config.ConfigManager;
import net.warvale.bungee.config.DatabaseCredentials;
import net.warvale.bungee.data.StorageBackend;
import net.warvale.bungee.listeners.MCPListener;
import net.warvale.bungee.listeners.PingListener;
import net.warvale.bungee.listeners.PlayerLoginListener;
import net.warvale.bungee.message.MessageManager;
import net.warvale.bungee.tasks.RebootTimeTask;
import net.warvale.bungee.users.UserManager;
import net.warvale.bungee.utils.logging.DiscordUtils;
import net.warvale.bungee.utils.network.CloudflareUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WarvaleProxy extends Plugin {

    private static WarvaleProxy instance;

    private static boolean shutdown = false;

    //server data
    public boolean DEBUG;
    public static String BUNGEE_NAME;
    private static ServerRegion region;
    private final File networkFile = new File("network.properties");
    private PropertiesFile networkProperties;
    public static DateTime restartTime;
    public static String mainframeURL = "";
    public static String mainframeKey = "";
    public static int mainframeTimeout = 2000;

    //backend
    private static StorageBackend storageBackend;

    //cloudflare settings
    public static boolean cloudflareEnabled = true;
    public static String cloudflareKey = "";
    public static String cloudflareEmail = "";
    public static int delete_record = 1;
    public static String cloudflareIP = "";
    public static String cloudflareName = "";
    public static int oddDay = 0;
    public static int timeForReboot = 0;
    public static Runnable timeTask;

    @Override
    public void onEnable() {
        WarvaleProxy.instance = this;

        getLogger().log(Level.INFO, "Loading ConfigManager...");

        //setup our config manager
        ConfigManager.get().setup();

        //setup message manager
        MessageManager.getInstance().setup();

        //make sure region is not UNKNOWN
        if (region.equals(ServerRegion.UNKNOWN)) {
            endSetup("Unknown region, please set your region before continuing...");
        }

        getLogger().log(Level.INFO, "Loading StorageBackend...");

        //initialize our storage backend
        storageBackend = new StorageBackend(new DatabaseCredentials(
                ConfigManager.getConfig().getString("database.host"),
                ConfigManager.getConfig().getInt("database.port"),
                ConfigManager.getConfig().getString("database.user"),
                ConfigManager.getConfig().getString("database.pass"),
                ConfigManager.getConfig().getString("database.dbName")
        ));

        //attempt to set name for dns based on region
        try {
            ConfigManager.getConfig().set("cloudflare.name", region.getInternalName());
            ConfigManager.get().saveConfig();
        } catch (Exception ex) {
            endSetup("Could not set proxy dns name");
        }

        //schedule restart if needed
        if (cloudflareEnabled) {
            restartTime = new DateTime(DateTimeZone.UTC);

            long oddVSeven = restartTime.getMillis() / 1000 / 86400;

            restartTime = restartTime.withHourOfDay(timeForReboot);
            restartTime = restartTime.withMinuteOfHour(0);
            restartTime = restartTime.withSecondOfMinute(0);

            if (oddVSeven % 2 == oddDay) {
                DateTime currentTime = new DateTime(DateTimeZone.UTC);
                if (currentTime.isAfter(restartTime)) {
                    restartTime = restartTime.plusHours(48);
                }
            } else {
                restartTime = restartTime.plusHours(24);
            }

            ProxyServer.getInstance().getLogger().log(Level.INFO,"Next reboot scheduled for - " + restartTime.toString());
            DiscordUtils.sendStatusUpdate("Next reboot scheduled for - " + restartTime.toString(), DiscordUtils.WARNING_MSG);

            try {
                String rec_id = CloudflareUtils.getRecID(WarvaleProxy.cloudflareIP);
                if (rec_id.equals("-2")) {
                    ProxyServer.getInstance().stop();
                } else if (rec_id.equals("-1")) {
                    String urlString = "https://www.cloudflare.com/api_json.html?a=rec_new&z=warvale.net&ttl=1&type=A"
                            + "&tkn=" + WarvaleProxy.cloudflareKey
                            + "&email=" + WarvaleProxy.cloudflareEmail
                            + "&content=" + WarvaleProxy.cloudflareIP
                            + "&name=" + WarvaleProxy.cloudflareName;
                    JSONObject json = new JSONObject();

                    JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
                    if (response == null) {
                        ProxyServer.getInstance().stop();
                    } else if (response.containsKey("result")) {
                        if (!((String) response.get("result")).equals("success")) {
                            ProxyServer.getInstance().stop();
                        }
                    }
                }
            } catch (HTTPRequestFailException e) {
                ProxyServer.getInstance().stop();
            }

            WarvaleProxy.timeTask = new RebootTimeTask(this);
            ProxyServer.getInstance().getScheduler().schedule(this, timeTask, 10, 10, TimeUnit.SECONDS);

        }

        //register listeners
        registerListener(new PlayerLoginListener());
        registerListener(new PingListener());
        registerListener(new MCPListener());

        //register commands
        registerCommand(new BIPCommand());
        registerCommand(new ManagerServerCommand());
        //registerCommand(new HubCommand());
        registerCommand(new TitleBroadcastCommand("titlebroadcast", "titlebroadcaster.use", "tb"));
        registerCommand(new MCCommand());

        //initialize the user manager
        UserManager.initialize();

    }

    @Override
    public void onDisable() {

        getLogger().log(Level.INFO, "Disabling StorageBackend...");

        //disable our storage backend
        storageBackend.closeConnections();
    }

    @Override
    public void onLoad() {
        instance = this;

        getLogger().log(Level.INFO, "Loading Network Properties...");

        if (!networkFile.exists()) {
            try {
                PropertiesFile.generateFresh(networkFile, new String[]{"server-region"},new String[]{ServerRegion.UNKNOWN.toString()});
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Could not generate fresh properties file");
            }
        }

        //load network properties
        try {
            networkProperties = new PropertiesFile(networkFile);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load Network Properties file", e);
            endSetup("Exception occurred when loading Network Properties");
        }

        //attempt to set server region
        try {
            region = ServerRegion.valueOf(getNetworkProperties().getString("server-region"));
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE,"Could not determine server region");
            endSetup("Could not determine server region");
        }

    }

    public static WarvaleProxy getInstance() {
        return WarvaleProxy.instance;
    }

    public static void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), listener);
    }

    public static void registerCommand(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(getInstance(), command);
    }

    public static void doAsync(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(instance, runnable);
    }

    public PropertiesFile getNetworkProperties() {
        return networkProperties;
    }

    public static StorageBackend getStorageBackend() {
        return storageBackend;
    }

    public static ServerRegion getRegion() {
        return region;
    }

    public void endSetup(String s) {
        getLogger().log(Level.SEVERE, s);
        if (!shutdown) {
            stop();
            shutdown = true;
        }
        throw new IllegalArgumentException("Disabling... " + s);
    }

    private void stop() {
        getProxy().stop();
    }

}
