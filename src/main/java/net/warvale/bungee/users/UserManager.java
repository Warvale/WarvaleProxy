package net.warvale.bungee.users;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import net.warvale.bungee.WarvaleProxy;
import net.warvale.bungee.utils.misc.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserManager implements Listener {

    private static Map<UUID, UserData> users = new ConcurrentHashMap<>();
    private static ConcurrentLinkedQueue<UserDataUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();


    public static UserData getUserData(ProxiedPlayer player) {
        return UserManager.getUserData(player.getUniqueId());
    }

    public static UserData getUserData(UUID uuid) {
        return UserManager.users.get(uuid);
    }

    public static void addPendingUpdate(UserDataUpdate userDataUpdate) {
        UserManager.pendingUpdates.add(userDataUpdate);
    }

    public static Map<UUID, UserData> getUsers() {
        return UserManager.users;
    }

    public static void initialize() {
        // For debugging
        WarvaleProxy.doAsync(new Runnable() {
            @Override
            public void run() {
                UserManager.executeUpdates();
            }
        });

        UserManager userManager = new UserManager();
        WarvaleProxy.getInstance().getProxy().getPluginManager().registerListener(WarvaleProxy.getInstance(), userManager);
    }

    @EventHandler
    public void onPlayerLogin(LoginEvent e) {
        PendingConnection connection = e.getConnection();

        if(!connection.isOnlineMode()) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(ChatColor.RED + "Your account is not authenticated"));
        } else if(connection.getUniqueId() == null || connection.getName() == null){
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(ChatColor.RED + "Error in authentication"));
        } else if(!e.isCancelled()) {

            UserData userData = UserManager.getUserDataFromDB(connection.getUniqueId());
            // Error
            if (userData == null) {
                e.setCancelled(true);
                e.setCancelReason(new TextComponent(ChatColor.RED + "Error loading data. If this issue " +
                        "persists contact support@devotedpvp.rip"));
            } else {
                UserManager.getUsers().put(connection.getUniqueId(), userData);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(ServerDisconnectEvent e) {
        UserManager.getUsers().remove(e.getPlayer().getUniqueId());
    }

    /**
     * Call ASYNC
     */
    public static UserData getUserDataFromDB(UUID uuid) {
        String query = "SELECT * FROM user_data WHERE uuid = ?;";

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet set = null;
        UserData userData = null;

        try {
            connection = WarvaleProxy.getStorageBackend().getPoolManager().getConnection();
            stmt = connection.prepareStatement(query);
            stmt.setString(1, uuid.toString());
            set = stmt.executeQuery();

            if (set.next()) {

                JSONParser parser = new JSONParser();
                JSONObject cosmetics = new JSONObject();
                JSONObject cases = new JSONObject();
                JSONObject minigamesSettings = new JSONObject();
                JSONObject arenaSettings = new JSONObject();
                JSONObject uhcSettings = new JSONObject();
                JSONObject disguiseSettings = new JSONObject();
                JSONObject chatSettings = new JSONObject();

                try {
                    // Parse all our json
                    cosmetics = (JSONObject) parser.parse(set.getString("cosmetics"));
                    cases = (JSONObject) parser.parse(set.getString("cases"));
                    minigamesSettings = (JSONObject) parser.parse(set.getString("minigames_settings"));
                    arenaSettings = (JSONObject) parser.parse(set.getString("arena_settings"));
                    uhcSettings = (JSONObject) parser.parse(set.getString("uhc_settings"));
                    disguiseSettings = (JSONObject) parser.parse(set.getString("disguise_settings"));
                    chatSettings = (JSONObject) parser.parse(set.getString("chat_settings"));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                userData = new UserData(uuid, set.getInt("currency"),
                        set.getBoolean("player_visibility"), set.getBoolean("lobby_flight"), cosmetics, cases,
                        minigamesSettings, arenaSettings, uhcSettings, disguiseSettings, chatSettings);

            } else {
                String insertQuery = "INSERT INTO `user_data` (`uuid`, `currency`, `player_visibility`, `cosmetics`, `cases`, " +
                        "`minigames_settings`, `arena_settings`, `uhc_settings`,  `disguise_settings`, `chat_settings`) " +
                        "VALUES (?, 0, true, '{}', '{}', '{}', '{}', '{}', '{}', '{}');";
                stmt = connection.prepareStatement(insertQuery);
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();

                userData = new UserData(uuid, 0, false, false, new JSONObject(), new JSONObject(),
                        new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            WarvaleProxy.getStorageBackend().getPoolManager().close(connection, stmt, set);
        }

        return userData;
    }

    private static void executeUpdates() {
        if (UserManager.pendingUpdates.size() == 0) {
            return;
        }

        // Create pairs of data for queries
        List<UserDataUpdate> queries = new ArrayList<>();
        Iterator<UserDataUpdate> iterator = UserManager.pendingUpdates.iterator();
        while (iterator.hasNext()) {
            queries.add(iterator.next());
            iterator.remove();
        }

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = WarvaleProxy.getStorageBackend().getPoolManager().getConnection();

            for (UserDataUpdate userDataUpdate : queries) {
                // Generate a query
                int i = 1;
                List<Object> objects = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append("UPDATE user_data SET ");

                boolean flag = false;
                for (Pair<String, Object> pair : userDataUpdate.getData()) {
                    if (flag) {
                        builder.append(", ");
                    }

                    // Build Query
                    builder.append(pair.getA());
                    builder.append(" = ?");

                    // Store for later
                    objects.add(pair.getB());
                    flag = true;
                }

                builder.append(" WHERE uuid = ?;");

                // Add the params to the query
                //Bukkit.getLogger().info(builder.toString());
                stmt = connection.prepareStatement(builder.toString());
                for (Object o : objects) {
                    stmt.setObject(i++, o);
                }

                stmt.setString(i, userDataUpdate.getUserData().getUUID().toString());

                stmt.executeUpdate();

                // Did a player's disguised settings change?
                /*for (Pair<String, Object> pair : userDataUpdate.getData()) {
                    if (pair.getA().equals("disguise_settings")) {
                        // Update the player's chat prefix in MCP
                        break;
                    }
                }*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            WarvaleProxy.getStorageBackend().getPoolManager().close(connection, stmt, null);
        }

    }

}
