package net.warvale.bungee.utils.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.warvale.bungee.WarvaleProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUtils {

    public static void updateUUIDMapping(ProxiedPlayer proxiedPlayer) {
        //attempt to update database
        WarvaleProxy.doAsync(new Runnable() {
            @Override
            public void run() {

                Connection connection = null;
                PreparedStatement stmt = null;
                ResultSet set = null;

                try {

                    connection = WarvaleProxy.getStorageBackend().getPoolManager().getConnection();
                    stmt = connection.prepareStatement("SELECT * FROM `player_uuid_mapping` WHERE `uuid` = ? LIMIT 1");
                    stmt.setString(1, proxiedPlayer.getUniqueId().toString());
                    set = stmt.executeQuery();

                    if (set.next()) {
                        String username = set.getString("username");
                        if (!username.equals(proxiedPlayer.getName())) {
                            //update user in database

                            stmt = connection.prepareStatement("UPDATE `player_uuid_mapping` SET `lower_username` = ?, `username` = ? WHERE `uuid` = ?");
                            stmt.setString(1, proxiedPlayer.getName().toLowerCase());
                            stmt.setString(2, proxiedPlayer.getName());
                            stmt.setString(3, proxiedPlayer.getUniqueId().toString());
                            stmt.executeUpdate();

                        }
                    } else {

                        //user doesn't exist so add them
                        stmt = connection.prepareStatement("INSERT INTO `player_uuid_mapping` (`lower_username`, `username`, `uuid` ) VALUES (?, ?, ?)");
                        stmt.setString(1, proxiedPlayer.getName().toLowerCase());
                        stmt.setString(2, proxiedPlayer.getName());
                        stmt.setString(3, proxiedPlayer.getUniqueId().toString());
                        stmt.executeUpdate();

                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    WarvaleProxy.getStorageBackend().getPoolManager().close(connection, stmt, set);
                }

            }
        });
    }

}
