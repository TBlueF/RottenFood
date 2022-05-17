package de.bluecolored.rottenfood;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public class ExtendedItemType {
    public static final TypeToken<ExtendedItemType> TOKEN = TypeToken.of(ExtendedItemType.class);

    private final ItemType itemType;
    private final int unsafeDamage;

    private final boolean useUnsafeDamage;

    public ExtendedItemType(ItemType itemType) {
        this.itemType = itemType;
        this.unsafeDamage = 0;

        this.useUnsafeDamage = false;
    }

    public ExtendedItemType(ItemType itemType, int unsafeDamage) {
        this.itemType = itemType;
        this.unsafeDamage = unsafeDamage;

        this.useUnsafeDamage = true;
    }

    public boolean matches(ItemStack is) {
        if (!is.getType().equals(itemType)) return false;
        if (!useUnsafeDamage) return true;

        Object damage = is.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(null);
        if (damage == null) return false;
        if (!(damage instanceof Number)) return false;
        int damageValue = ((Number) damage).intValue();
        return unsafeDamage == damageValue;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getUnsafeDamage() {
        return unsafeDamage;
    }

    public boolean isUseUnsafeDamage() {
        return useUnsafeDamage;
    }
}
