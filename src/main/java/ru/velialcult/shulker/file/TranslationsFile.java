package ru.velialcult.shulker.file;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.shulker.CultShulker;

public class TranslationsFile {

    private static final FileConfiguration config = FileRepository.getByName(CultShulker.getInstance(), "translation.yml").getConfiguration();

    public static String getTranslationObj(Object object) {
        String translation = "Перевод не найден";
        try {
            translation = getTranslation(EntityType.valueOf(object.toString()));
        } catch (Exception e) {
            translation = getTranslationItem(XMaterial.valueOf(object.toString()).parseMaterial());
        }
        return translation;
    }

    private static String getTranslation(String path) {
        if (!config.contains(path)) {
            return "&cПеревод не найден";
        } else {
            return config.getString(path);
        }
    }

    public static String getTranslation(EntityType entityType) {
        return getTranslation("mobs." + entityType.toString());
    }

    public static String getTranslationItem(Material material) {
        return getTranslation("items." + material.toString());
    }

    public static String getTranslation(ItemStack itemStack) {
        if (itemStack.getType() == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            return getTranslation("items.POTION.EFFECT" + potionMeta.getBasePotionData().getType().getEffectType().getName());
        } else if (itemStack.getType() == Material.LINGERING_POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            return getTranslation("items.LINGERING_POTION.EFFECT" + potionMeta.getBasePotionData().getType().getEffectType().getName());
        } else if (itemStack.getType() == Material.SPLASH_POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            return getTranslation("items.SPLASH_POTION.EFFECT" + potionMeta.getBasePotionData().getType().getEffectType().getName());
        } else {
            return getTranslationItem(itemStack.getType());
        }
    }

    public static String getTranslationEffect(PotionEffectType effectType) {
        return getTranslation("effects." + effectType.getName());
    }
}
