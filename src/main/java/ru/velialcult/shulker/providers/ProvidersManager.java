package ru.velialcult.shulker.providers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.velialcult.shulker.providers.antirelog.CombatLogXProvider;

import java.util.HashMap;
import java.util.Map;

public class ProvidersManager {

    private final Map<String, Boolean> providers = new HashMap<>();
    private final Plugin plugin;

    public ProvidersManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadProvider("CombatLogX", "11.5.0.0.1242");
    }

    private void loadProvider(String pluginName, String minVersion) {
        boolean isPluginLoaded = false;
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            String version = plugin.getDescription().getVersion();

            if (version.compareTo(minVersion) >= 0) {
                this.plugin.getLogger().info(pluginName + " найден, использую " + pluginName + " API");
                isPluginLoaded = true;
            } else {
                this.plugin.getLogger().warning("Версия " + pluginName + " < " + minVersion + " не поддерживается. Игнорирую данную зависимость");
            }
        }
        providers.put(pluginName, isPluginLoaded);
    }

    public CombatLogXProvider getCombatLogXProvider() {
        return useCombatLogX() ? new CombatLogXProvider() : null;
    }

    public boolean useCombatLogX()  {
        return providers.getOrDefault("CombatLogX", false);
    }
}
