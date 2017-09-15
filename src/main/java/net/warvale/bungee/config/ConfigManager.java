package net.warvale.bungee.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.warvale.bungee.WarvaleProxy;
import net.warvale.bungee.utils.files.FileUtils;

import java.io.File;
import java.util.logging.Level;

public class ConfigManager {

    private static ConfigManager instance;

    private static Configuration config;
    private File configFile;


    public static ConfigManager get() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void setup() {

        if (!WarvaleProxy.getInstance().getDataFolder().exists()) {
            WarvaleProxy.getInstance().getLogger().log(Level.INFO, "Creating data folder...");
            WarvaleProxy.getInstance().getDataFolder().mkdir();
        }


        configFile = new File(WarvaleProxy.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            FileUtils.loadFile("config.yml");
        }

        //load the config
        reloadConfig();
    }

    public static Configuration getConfig() {
        return config;
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (Exception ex) {
            WarvaleProxy.getInstance().getLogger().log(Level.SEVERE, "Failed to load config: " + configFile.getName(), ex);
        }
    }


    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (Exception ex) {
            WarvaleProxy.getInstance().getLogger().log(Level.SEVERE, "Could not save config: " + configFile.getName(), ex);
        }
    }




}
