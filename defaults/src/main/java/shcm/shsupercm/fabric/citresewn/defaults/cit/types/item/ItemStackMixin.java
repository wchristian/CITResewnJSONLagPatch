package shcm.shsupercm.fabric.citresewn.defaults.cit.types.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

@Mixin(ItemStack.class)
public class ItemStackMixin implements TypeItem.CITCacheItem {
    private final TypeItem.CITDisplayCache citresewn$cacheTypeItem = new TypeItem.CITDisplayCache(TypeItem.CONTAINER::getRealTimeCIT);

    @Override
    public TypeItem.CITDisplayCache citresewn$getCacheTypeItem() {
        return this.citresewn$cacheTypeItem;
    }
}
