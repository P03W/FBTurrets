package mc.fbturrets.blocks

import io.netty.buffer.Unpooled
import mc.fbturrets.main.FBTurrets
import mc.fbturrets.util.MathHelp.absDist
import mc.fbturrets.util.MathHelp.facingDifference
import mc.fbturrets.util.MathHelp.lerpAngle
import mc.fbturrets.util.MathHelp.smallRandom
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PigEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.RayTraceContext

class TurretHolderBlockEntity : BlockEntity(FBTurrets.TURRET_HOLDER_BLOCK_ENTITY), Tickable, ImplementedInventory,
    NamedScreenHandlerFactory {
    private var inventory = DefaultedList.ofSize(4, ItemStack.EMPTY)
    private var currentAimTime = 0
    private var turretVec3d: Vec3d? = null
    private var tickCount = 0

    @JvmField
    var yaw = 0f

    @JvmField
    var pitch = 0f

    @JvmField
    var gun: TurretGun? = null
    private var target: Entity? = null
    override fun tick() {
        if (gun != null && world != null) {
            val targetBox = gun!!.targetBox
            if (!world!!.isClient && gun != null) {
                tickCount++
                if (tickCount % 20 == 0) {
                    tickCount = 0
                    val playersAround = PlayerStream.watching(world, pos)
                    val gunPassedData = PacketByteBuf(Unpooled.buffer())
                    gunPassedData.writeBlockPos(pos)
                    gunPassedData.writeString(gun!!.iD.toString())
                    playersAround.forEach { player: PlayerEntity? ->
                        ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                            player,
                            FBTurrets.TURRET_GUN_UPDATE,
                            gunPassedData
                        )
                    }
                }
                turretVec3d = Vec3d(pos.x + 0.5, pos.y + 0.65, pos.z + 0.5)
                val targets = world!!.getEntities(null, targetBox.offset(pos))
                if (!targets.contains(target)) {
                    target = null
                }
                if (targets.isNotEmpty()) {
                    targets.removeIf { target: Entity? -> cantBeAttacked(target) }
                    for (possible in targets) {
                        if (target == null) {
                            target = possible
                            currentAimTime = 0
                            break
                        }
                    }
                }
                if (target != null && !target!!.removed && target!!.isAlive) {
                    val d = target!!.x - turretVec3d!!.x
                    val e = target!!.y + target!!.boundingBox.yLength / 2 - turretVec3d!!.y
                    val f = target!!.z - turretVec3d!!.z
                    val g = MathHelper.sqrt(d * d + f * f).toDouble()
                    val targetPitch = MathHelper.wrapDegrees((-(MathHelper.atan2(e, g) * 57.2957763671875)).toFloat())
                    val targetYaw =
                        MathHelper.wrapDegrees((MathHelper.atan2(f, d) * 57.2957763671875).toFloat() - 90.0f)
                    val lastPitch = pitch
                    val lastYaw = yaw
                    pitch = lerpAngle(pitch, targetPitch, 0.1f)
                    yaw = lerpAngle(yaw, targetYaw, 0.1f)
                    if (absDist(pitch, lastPitch) >= 0.05 || absDist(yaw, lastYaw) >= 0.05) {
                        val watchingPlayers = PlayerStream.watching(world, pos)
                        val passedData = PacketByteBuf(Unpooled.buffer())
                        passedData.writeBlockPos(pos)
                        passedData.writeFloat(pitch)
                        passedData.writeFloat(yaw)
                        watchingPlayers.forEach { player: PlayerEntity? ->
                            ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                                player,
                                FBTurrets.TURRET_ANGLES_UPDATE,
                                passedData
                            )
                        }
                    }
                    if (facingDifference(pitch, MathHelper.wrapDegrees(yaw), targetPitch, targetYaw) < 3) {
                        currentAimTime++
                        if (currentAimTime >= gun!!.aimTime && !cantBeAttacked(target)) {
                            currentAimTime = 0
                            target!!.damage(DamageSource.GENERIC, gun!!.damage.toFloat())
                            val newWatchingPlayers = PlayerStream.watching(world, pos)
                            val newPassedData = PacketByteBuf(Unpooled.buffer())
                            newPassedData.writeBlockPos(pos)
                            newPassedData.writeDouble(d)
                            newPassedData.writeDouble(e)
                            newPassedData.writeDouble(f)
                            newWatchingPlayers.forEach { player: PlayerEntity? ->
                                ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                                    player,
                                    FBTurrets.TURRET_SHOOT_PARTICLES,
                                    newPassedData
                                )
                            }
                        } else {
                            if (currentAimTime >= gun!!.aimTime) {
                                target = null
                            }
                        }
                    }
                } else {
                    target = null
                }
            } else {
                target = null
            }
        }
    }

    private fun cantBeAttacked(target: Entity?): Boolean {
        if (target != null && world != null) {
            if (!target.isAlive) {
                return true
            }
            if (target !is Monster) {
                return true
            }
            val targetPos = Vec3d(target.x, target.pos.y + target.boundingBox.yLength / 2, target.z)
            val dir = targetPos.subtract(turretVec3d).normalize()
            val result = world!!.rayTrace(
                RayTraceContext(
                    turretVec3d!!.add(dir.multiply(1.2)),
                    targetPos,
                    RayTraceContext.ShapeType.COLLIDER,
                    RayTraceContext.FluidHandling.ANY,
                    PigEntity(EntityType.PIG, world)
                )
            ).type
            return result == HitResult.Type.BLOCK
        }
        return true
    }

    fun dropGun() {
        if (gun != null) {
            val itemEntity =
                ItemEntity(world, pos.x + 0.5, (pos.y + 1).toDouble(), pos.z + 0.5, ItemStack(Registry.ITEM[gun!!.iD]))
            assert(world != null)
            itemEntity.setVelocity(smallRandom(), 0.15, smallRandom())
            world!!.spawnEntity(itemEntity)
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        if (gun != null) {
            tag.putString("gunID", gun!!.iD.toString())
        } else {
            tag.putString("gunID", "")
        }
        Inventories.toTag(tag, items);
        return super.toTag(tag)
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        val id = tag.getString("gunID")
        if (id.isNotEmpty()) {
            gun = Registry.BLOCK[Identifier(id)] as TurretGun
        }
        Inventories.fromTag(tag,items);
        super.fromTag(state, tag)
    }

    override fun getItems(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }
}