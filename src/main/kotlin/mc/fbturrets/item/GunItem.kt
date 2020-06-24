package mc.fbturrets.item

import mc.fbturrets.blocks.TurretGun
import mc.fbturrets.blocks.TurretHolderBlockEntity
import mc.fbturrets.main.FBTurrets
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class GunItem(private val ID: Identifier, settings: Settings?) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val be = context.world.getBlockEntity(context.blockPos)
        if (be != null && be.type === FBTurrets.TURRET_HOLDER_BLOCK_ENTITY) {
            val gun = (be as TurretHolderBlockEntity).gun
            if (gun != null && gun.iD !== ID) {
                if (!context.player!!.isCreative) {
                    be.dropGun()
                    context.player!!.mainHandStack.decrement(1)
                }
            }
            be.gun = Registry.BLOCK[ID] as TurretGun
            return ActionResult.SUCCESS
        }
        return ActionResult.CONSUME
    }

}