package ru.velialcult.shulker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.update.UpdateChecker;
import ru.velialcult.shulker.file.ConfigFile;
import ru.velialcult.shulker.handler.ShulkerHandler;
import ru.velialcult.shulker.providers.ProvidersManager;

/**
 * Written by Nilsson
 * 13.06.2024
 */
public class CultShulker extends JavaPlugin {

    private static CultShulker instance;

    private ConfigFile configFile;
    private ProvidersManager providersManager;

    @Override
    public void onEnable() {
        instance = this;
        long mills = System.currentTimeMillis();

        try {

            if (!Bukkit.getPluginManager().isPluginEnabled("CultLibrary")) {
                getLogger().severe("CultLibrary не найден, пожалуйста, установите его и попробуйте снова.");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            UpdateChecker updateChecker = new UpdateChecker(this, "CultShulker");
            updateChecker.check();

            providersManager  = new ProvidersManager(this);
            providersManager.load();

            this.saveDefaultConfig();
            setDefaultConfig();
            configFile = new ConfigFile(this);
            configFile.load();
            ConfigurationUtil.loadConfiguration(this, "translation.yml");
            FileRepository.load(this);

            Bukkit.getPluginManager().registerEvents(new ShulkerHandler(configFile, providersManager), this);

            getLogger().info("Плагин был загружен за " + ChatColor.YELLOW + (System.currentTimeMillis() - mills) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefaultConfig() {
        if (!getConfig().contains("messages.open-shulker.cant-click"))
            getConfig().set("messages.open-shulker.cant-click", "&6➤ &fНельзя перемещать &6шалкер&f, когда он открыт");
        ConfigurationUtil.saveFile(getConfig(), getDataFolder().getAbsolutePath(), "config.yml");
        ConfigurationUtil.reloadFile(this, "config.yml");
    }

    public static CultShulker getInstance() {
        return instance;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public ProvidersManager getProvidersManager() {
        return providersManager;
    }
}
