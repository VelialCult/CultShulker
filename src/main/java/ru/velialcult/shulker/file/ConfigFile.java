package ru.velialcult.shulker.file;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.shulker.CultShulker;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Written by Nilsson
 * 13.06.2024
 */
public class ConfigFile {

    private final FileConfiguration config;

    private List<Material> blackListItems;
    private List<Material> whileListShulkers;
    private boolean fastOpenShulker;
    private List<Material> shulkersCanOpen;
    private String openShulkerMessage;
    private boolean useNotify;
    private String notifyMessage;
    private long periodSendInfoMessage;
    private String fastOpenShulkerMessageInPvP;

    public ConfigFile(CultShulker cultShulker) {
        this.config = cultShulker.getConfig();
    }

    public void load() {
        this.blackListItems = config.getStringList("settings.shulker-inventory.black-list-items")
                .stream()
                .map(str -> XMaterial.matchXMaterial(str).orElseThrow( () -> new NoSuchElementException("No value present")).parseMaterial())
                .collect(Collectors.toList());
        this.whileListShulkers = config.getStringList("settings.shulker-inventory.white-list-shulkers")
                .stream()
                .map(str -> XMaterial.matchXMaterial(str).orElseThrow( () -> new NoSuchElementException("No value present")).parseMaterial())
                .collect(Collectors.toList());
        this.shulkersCanOpen = config.getStringList("settings.shulker-open.shulkers-list")
                .stream()
                .map(str -> XMaterial.matchXMaterial(str).orElseThrow( () -> new NoSuchElementException("No value present")).parseMaterial())
                .collect(Collectors.toList());
        this.fastOpenShulker = config.getBoolean("settings.shulker-open.enabled", false);
        this.useNotify = config.getBoolean("settings.shulker-inventory.message.enabled", true);
        this.notifyMessage = VersionAdapter.TextUtil().colorize(config.getString("settings.shulker-inventory.message.message"));
        this.periodSendInfoMessage = TimeUtil.parseStringToTime(config.getString("settings.shulker-inventory.message.period"));
        this.fastOpenShulkerMessageInPvP = VersionAdapter.TextUtil().colorize(config.getString("messages.fast-open-shulker.in-pvp"));
        this.openShulkerMessage = VersionAdapter.TextUtil().colorize(config.getString("messages.open-shulker.cant-click"));
    }

    public String getOpenShulkerMessage() {
        return openShulkerMessage;
    }

    public boolean isFastOpenShulker() {
        return fastOpenShulker;
    }

    public List<Material> getBlackListItems() {
        return blackListItems;
    }

    public List<Material> getWhileListShulkers() {
        return whileListShulkers;
    }

    public List<Material> getShulkersCanOpen() {
        return shulkersCanOpen;
    }

    public boolean isUseNotify() {
        return useNotify;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public long getPeriodSendInfoMessage() {
        return periodSendInfoMessage;
    }

    public String getFastOpenShulkerMessageInPvP() {
        return fastOpenShulkerMessageInPvP;
    }
}
