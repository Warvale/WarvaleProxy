package net.warvale.bungee.data;

import net.warvale.bungee.WarvaleProxy;
import net.warvale.bungee.config.DatabaseCredentials;
import net.warvale.bungee.data.connection.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class StorageBackend {

    private ConnectionPoolManager poolManager;

    public StorageBackend(DatabaseCredentials credentials) {
        this.poolManager = new ConnectionPoolManager(credentials);
        this.createTables();
    }

    public ConnectionPoolManager getPoolManager() {
        return this.poolManager;
    }

    public void closeConnections() {
        this.poolManager.closePool();
    }

    public synchronized void createTables() {
        WarvaleProxy.getInstance().getProxy().getScheduler().runAsync(WarvaleProxy.getInstance(), new Runnable() {
            @Override
            public void run() {
                Connection connection = null;

                try {

                    connection = poolManager.getConnection();

                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `maxmind_ips` (" +
                            "`ip` BIGINT NULL DEFAULT 0 PRIMARY KEY," +
                            "`country_iso` VARCHAR(10) NULL," +
                            "`country_confidence` INTEGER NULL," +
                            "`country_geo_name_id` INTEGER NULL," +
                            "`state_iso` VARCHAR(255) NULL," +
                            "`state_confidence` INTEGER NULL," +
                            "`state_geo_name_id` INTEGER NULL," +
                            "`city_name` VARCHAR(255) NULL," +
                            "`city_confidence` INTEGER NULL," +
                            "`city_geo_name_id` INTEGER NULL," +
                            "`postal_code` VARCHAR(55) NULL," +
                            "`postal_confidence` INTEGER NULL," +
                            "`location_latitude` FLOAT NULL," +
                            "`location_longitude` FLOAT NULL," +
                            "`location_radius` INTEGER NULL," +
                            "`location_timezone` VARCHAR(255) NULL," +
                            "`asn` INTEGER NULL," +
                            "`aso` VARCHAR(255) NULL," +
                            "`gdomain` VARCHAR(255) NULL," +
                            "`isp` VARCHAR(255) NULL," +
                            "`organization` VARCHAR(255) NULL," +
                            "`user_type` VARCHAR(55) NULL," +
                            "`is_anon_proxy` BOOLEAN NULL," +
                            "`is_satellite_provider` BOOLEAN NULL," +
                            "`fetch_date` TIMESTAMP NULL" +
                            ") ENGINE = InnoDB;").executeUpdate();

                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_uuid_mapping` (" +
                            "`id` BIGINT NOT NULL AUTO_INCREMENT, " +
                            "`lower_username` VARCHAR(16) NOT NULL," +
                            "`username` VARCHAR(16) NOT NULL," +
                            "`uuid` VARCHAR(64) NOT NULL," +
                            "PRIMARY KEY (`id`)," +
                            "KEY `uuid_index` (`uuid`)," +
                            "KEY `username_index` (`username`)" +
                            ") ENGINE = InnoDB;").executeUpdate();

                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `discord_accounts` (" +
                            "`id` BIGINT NOT NULL AUTO_INCREMENT, " +
                            "`uuid` varchar(64) NOT NULL," +
                            "`discord_uuid` varchar(64) NOT NULL," +
                            "`time_set` TIMESTAMP NOT NULL," +
                            "PRIMARY KEY (`id`)," +
                            "KEY `uuid_index` (`uuid`)," +
                            "KEY `discord_uuid_index` (`discord_uuid`)" +
                            ") ENGINE = InnoDB;").executeUpdate();

                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `user_data` (" +
                            "`id` BIGINT NOT NULL AUTO_INCREMENT, " +
                            "`uuid` varchar(64) NOT NULL," +
                            "`currency` INTEGER NOT NULL DEFAULT 0," +
                            "`player_visibility` BOOLEAN NOT NULL DEFAULT true," +
                            "`cosmetics` json NOT NULL DEFAULT '{}'," +
                            "`cases` json NOT NULL DEFAULT '{}'," +
                            "`disguise_settings` json NOT NULL DEFAULT '{}'," +
                            "`chat_settings` json NOT NULL DEFAULT '{}'," +
                            "`minigames_settings` json NOT NULL DEFAULT '{}'," +
                            "`arena_settings` json NOT NULL DEFAULT '{}'," +
                            "`uhc_settings` json NOT NULL DEFAULT '{}'," +
                            "`stat_resets` json NOT NULL DEFAULT '{}'," +
                            "`banned_stats` json NOT NULL DEFAULT '{}'," +
                            "`false_ban` BOOLEAN NOT NULL DEFAULT false," +
                            "`lobby_flight` BOOLEAN NOT NULL DEFAULT false," +
                            "PRIMARY KEY (`id`)," +
                            "KEY `uuid_index` (`uuid`)" +
                            ") ENGINE = InnoDB;").executeUpdate();

                } catch (SQLException e) {
                    if (!e.getMessage().contains("already exists")) {
                        WarvaleProxy.getInstance().getLogger().log(Level.SEVERE, "Failed createTables");
                        e.printStackTrace();
                    }
                } finally {
                    poolManager.close(connection, null, null);
                }

            }
        });
    }



}
