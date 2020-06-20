package mc.fbturrets.item;

import mc.fbturrets.blocks.TurretGun;
import mc.fbturrets.blocks.TurretHolderBlockEntity;
import mc.fbturrets.main.FBTurrets;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class GunItem extends Item {
    private final Identifier ID;
    
    public GunItem(Identifier itemID, Settings settings) {
        super(settings);
        ID = itemID;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity be = context.getWorld().getBlockEntity(context.getBlockPos());
        if (be != null && be.getType() == FBTurrets.TURRET_HOLDER_BLOCK_ENTITY) {
            if (((TurretHolderBlockEntity)be).gun.getID() != ID) {
                if (!Objects.requireNonNull(context.getPlayer()).isCreative()) {
                    ((TurretHolderBlockEntity)be).dropGun();
                    context.getPlayer().getMainHandStack().decrement(1);
                }
                ((TurretHolderBlockEntity)be).gun = (TurretGun)Registry.BLOCK.get(ID);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
}
