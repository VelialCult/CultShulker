package ru.velialcult.shulker.handler;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import ru.velialcult.library.bukkit.notification.NotificationService;
import ru.velialcult.library.bukkit.utils.items.ItemUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.shulker.file.ConfigFile;
import ru.velialcult.shulker.file.TranslationsFile;
import ru.velialcult.shulker.providers.ProvidersManager;
import ru.velialcult.shulker.providers.antirelog.CombatLogXProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Written by Nilsson
 * 13.06.2024
 */
public class ShulkerHandler implements Listener {

    private final Map<Player, ItemStack> openShulkers;
    private final ConfigFile configFile;
    private final NotificationService notificationService;
    private final ProvidersManager providersManager;

    public ShulkerHandler(ConfigFile configFile, ProvidersManager providersManager) {
        this.configFile = configFile;
        this.providersManager  = providersManager;
        this.openShulkers = new HashMap<>();
        this.notificationService = VersionAdapter.getNotificationService();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (openShulkers.containsKey(player)) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null && ItemUtil.areSimilar(openShulkers.get(player), itemStack)) {
                notificationService.sendMessage(player.getUniqueId(), "shulker-open", 2, () -> {
                    VersionAdapter.MessageUtils().sendMessage(player, configFile.getOpenShulkerMessage());
                });
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (openShulkers.containsKey(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack[] inventoryContents = player.getInventory().getContents();

        if (player.getInventory().firstEmpty() != -1) {
            return;
        }

        player.getNearbyEntities(1, 1, 1).stream()
                .filter(entity -> entity instanceof Item)
                .map(entity -> (Item) entity)
                .forEach(item -> {
                    ItemStack itemStack = item.getItemStack();
                    if (configFile.getBlackListItems().contains(itemStack.getType())) {
                        return;
                    }

                    for (ItemStack shulker : inventoryContents) {
                        if (shulker == null || !configFile.getWhileListShulkers().contains(shulker.getType())) {
                            continue;
                        }

                        BlockStateMeta blockStateMeta = (BlockStateMeta) shulker.getItemMeta();
                        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();

                        if (shulkerBox.getInventory().firstEmpty() == -1) {
                            continue;
                        }

                        shulkerBox.getInventory().addItem(itemStack);
                        blockStateMeta.setBlockState(shulkerBox);
                        shulker.setItemMeta(blockStateMeta);
                        item.remove();

                        if (configFile.isUseNotify()) {
                            notificationService.sendMessage(player.getUniqueId(), "shulker-info", configFile.getPeriodSendInfoMessage(), () -> {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getNotifyMessage()
                                        .replace("{item}", TranslationsFile.getTranslation(itemStack)));
                            });
                        }
                        break;
                    }
                });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item != null) {
            if (action == Action.RIGHT_CLICK_AIR) {
                if (configFile.isFastOpenShulker()) {
                    if (configFile.getShulkersCanOpen().contains(item.getType())) {
                        if (providersManager.useCombatLogX()) {
                            CombatLogXProvider combatLogXProvider = providersManager.getCombatLogXProvider();

                            if (combatLogXProvider.isInCombat(player)) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getFastOpenShulkerMessageInPvP());
                                return;
                            }
                        }

                        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                        player.openInventory(shulkerBox.getInventory());
                        openShulkers.put(player, item);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.SHULKER_BOX) {
            Player player = (Player) event.getPlayer();
            ItemStack item = openShulkers.remove(player);

            if (item != null && item.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                ShulkerBox shulkerBox = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
                shulkerBox.getInventory().setContents(event.getInventory().getContents());
                blockStateMeta.setBlockState(shulkerBox);
                item.setItemMeta(blockStateMeta);
            }
        }
    }
}
