package mc.fbturrets.main

import mc.fbturrets.blocks.TurretGun
import mc.fbturrets.blocks.TurretHolder
import mc.fbturrets.blocks.TurretHolderBlockEntity
import mc.fbturrets.blocks.gui.TurretHolderGuiDescription
import mc.fbturrets.items.GunItem
import mc.fbturrets.util.MathHelp
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.*
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

class FBTurrets : ModInitializer {
    override fun onInitialize() {
        TURRET_HOLDER_MODEL = Block(FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque())
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "turret_holder_render_only"), TURRET_HOLDER_MODEL)
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "turret_holder"), TURRET_HOLDER)
        TURRET_HOLDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:turret_holder", BlockEntityType.Builder.create(Supplier { TurretHolderBlockEntity() }, TURRET_HOLDER).build(null))
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "turret_holder"), BlockItem(TURRET_HOLDER, Item.Settings().group(ITEM_GROUP)))
        BASIC_GUN = registerTurretGun("basic_gun", 0.3, 20, ParticleTypes.CRIT, 5, MathHelp.buildSimpleBox(15))
        SNIPER_GUN = registerTurretGun("sniper_gun", 0.2, 35, ParticleTypes.ENCHANTED_HIT, 15, MathHelp.buildSimpleBox(32))
        AUTO_GUN = registerTurretGun("auto_gun", 0.35, 11, ParticleTypes.CLOUD, 3, MathHelp.buildSimpleBox(10))
        TURRET_HOLDER_SCREEN_TYPE = ScreenHandlerRegistry.registerSimple(Identifier(MOD_ID, "turret_holder")) { syncId, inventory-> TurretHolderGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) }
    }

    private fun registerTurretGun(ident: String, aimSpeed: Double, aimTime: Int, effect: DefaultParticleType, damage: Int, box: Box): TurretGun {
        val id = Identifier(MOD_ID, ident)
        val block = TurretGun(aimTime, aimSpeed, damage, effect, box, id, FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque())
        Registry.register(Registry.BLOCK, id, block)
        Registry.register(Registry.ITEM, id, GunItem(id, Item.Settings().group(ITEM_GROUP)))
        return block
    }

    companion object {
        const val MOD_ID = "fbturrets"
        val ITEM_GROUP: ItemGroup = FabricItemGroupBuilder.build(
                Identifier(MOD_ID, "item_group")
        ) { ItemStack(Items.CROSSBOW) }
		val TURRET_ANGLES_UPDATE = Identifier(MOD_ID, "turret_angles")
		val TURRET_SHOOT_PARTICLES = Identifier(MOD_ID, "turret_shoot")
		val TURRET_GUN_UPDATE = Identifier(MOD_ID, "turret_update")
		lateinit var TURRET_HOLDER_MODEL: Block
        lateinit var BASIC_GUN: TurretGun
        lateinit var SNIPER_GUN: TurretGun
        lateinit var AUTO_GUN: TurretGun
        @JvmField
		val TURRET_HOLDER = TurretHolder(FabricBlockSettings.of(Material.BARRIER).hardness(4.0f).nonOpaque())
		lateinit var TURRET_HOLDER_BLOCK_ENTITY: BlockEntityType<TurretHolderBlockEntity>
        lateinit var TURRET_HOLDER_SCREEN_TYPE : ScreenHandlerType<TurretHolderGuiDescription>
    }
}