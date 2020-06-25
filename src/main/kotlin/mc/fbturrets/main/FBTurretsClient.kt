package mc.fbturrets.main

import mc.fbturrets.blocks.TurretGun
import mc.fbturrets.blocks.TurretHolderBlockEntity
import mc.fbturrets.blocks.TurretHolderRenderer
import mc.fbturrets.blocks.gui.TurretHolderScreen
import mc.fbturrets.util.VectorIterator
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class FBTurretsClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(FBTurrets.TURRET_HOLDER_BLOCK_ENTITY) { dispatcher: BlockEntityRenderDispatcher? ->
            TurretHolderRenderer(
                dispatcher
            )
        }
        BlockRenderLayerMap.INSTANCE.putBlock(FBTurrets.TURRET_HOLDER, RenderLayer.getCutout())
        ClientSidePacketRegistry.INSTANCE.register(
            FBTurrets.TURRET_ANGLES_UPDATE
        ) { packetContext: PacketContext, attachedData: PacketByteBuf ->
            val pos = attachedData.readBlockPos()
            val pitch = attachedData.readFloat()
            val yaw = attachedData.readFloat()
            packetContext.taskQueue.execute {
                val blockEntity = packetContext.player.world.getBlockEntity(pos)
                if (blockEntity is TurretHolderBlockEntity) {
                    blockEntity.pitch = pitch
                    blockEntity.yaw = yaw
                }
            }
        }
        ClientSidePacketRegistry.INSTANCE.register(
            FBTurrets.TURRET_SHOOT_PARTICLES
        ) { packetContext: PacketContext, attachedData: PacketByteBuf ->
            val pos = attachedData.readBlockPos()
            val x = attachedData.readDouble()
            val y = attachedData.readDouble()
            val z = attachedData.readDouble()
            val target = Vec3d(x, y, z)
            packetContext.taskQueue.execute {
                val world = packetContext.player.world
                val blockEntity = world.getBlockEntity(pos)
                if (blockEntity is TurretHolderBlockEntity) {
                    val gun = blockEntity.gun
                    if (gun != null) {
                        val vecIter = VectorIterator(target, 0.5)
                        val turretVec3d = Vec3d(pos.x + 0.5, pos.y + 0.65, pos.z + 0.5)
                        for (vec3d in vecIter) {
                            if (vec3d != null) {
                                world.addParticle(
                                    gun.effect,
                                    true,
                                    turretVec3d.x + vec3d.x,
                                    turretVec3d.y + vec3d.y,
                                    turretVec3d.z + vec3d.z,
                                    0.0,
                                    0.0,
                                    0.0
                                )
                            }
                        }
                    }
                }
            }
        }
        ClientSidePacketRegistry.INSTANCE.register(
            FBTurrets.TURRET_GUN_UPDATE
        ) { packetContext: PacketContext, attachedData: PacketByteBuf ->
            val pos = attachedData.readBlockPos()
            val gunID = attachedData.readString()
            packetContext.taskQueue.execute {
                val world = packetContext.player.world
                val blockEntity = world.getBlockEntity(pos)
                if (blockEntity is TurretHolderBlockEntity) {
                    blockEntity.gun = Registry.BLOCK[Identifier(gunID)] as TurretGun
                }
            }
        }

        ScreenRegistry.register(FBTurrets.TURRET_HOLDER_SCREEN_TYPE) { gui, inventory, title-> TurretHolderScreen(gui, inventory, title) }
    }
}