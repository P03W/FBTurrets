package mc.fbturrets.blocks;

import io.netty.buffer.Unpooled;
import mc.fbturrets.main.FBTurrets;
import mc.fbturrets.util.MathHelp;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;

import java.util.List;
import java.util.stream.Stream;

public class TurretHolderBlockEntity extends BlockEntity implements Tickable {
    private int currentAimTime = 0;
    private Vec3d turretVec3d;
    private int tickCount = 0;
    
    public float yaw = 0;
    public float pitch = 0;
    
    public TurretGun gun;
    public Entity target;
    
    public TurretHolderBlockEntity() {
        super(FBTurrets.TURRET_HOLDER_BLOCK_ENTITY);
    }
    
    @Override
    public void tick() {
        assert world != null;
        if (gun != null) {
            Box targetBox = gun.getTargetBox();
            if (!world.isClient && gun != null) {
    
                tickCount++;
                
                if (tickCount % 20 == 0) {
                    tickCount = 0;
                    Stream<PlayerEntity> playersAround = PlayerStream.watching(world, pos);
                    PacketByteBuf gunPassedData = new PacketByteBuf(Unpooled.buffer());
                    gunPassedData.writeBlockPos(pos);
                    gunPassedData.writeString(gun.getID().toString());
                    playersAround.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FBTurrets.TURRET_GUN_UPDATE, gunPassedData));
                }
                
                turretVec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5);
        
                List<Entity> targets = world.getEntities(null, targetBox.offset(pos));
        
                if (!targets.contains(target)) {
                    target = null;
                }
        
                if (!targets.isEmpty()) {
            
                    targets.removeIf(this::cantBeAttacked);
            
                    for (Entity possible : targets) {
                        if (target == null) {
                            target = possible;
                            currentAimTime = 0;
                            break;
                        }
                    }
                }
                
                if (target != null && !target.removed && target.isAlive()) {
                    Vec3d targetPos = new Vec3d(target.getX(), (target.getPos().y + target.getBoundingBox().getYLength() / 2), target.getZ());
            
                    double d = target.getX() - turretVec3d.x;
                    double e = (target.getY() + target.getBoundingBox().getYLength() / 2) - turretVec3d.y;
                    double f = target.getZ() - turretVec3d.z;
                    double g = MathHelper.sqrt(d * d + f * f);
            
                    float target_pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875D)));
                    float target_yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875D) - 90.0F);
            
                    float last_pitch = pitch;
                    float last_yaw = yaw;
                    
                    pitch = MathHelp.lerpAngle(pitch, target_pitch, 0.1f);
                    yaw = MathHelp.lerpAngle(yaw, target_yaw, 0.1f);
                    
                    if (MathHelp.absDist(pitch, last_pitch) >= 0.05 || MathHelp.absDist(yaw, last_yaw) >= 0.05) {
                        Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, pos);
                        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                        passedData.writeBlockPos(pos);
                        passedData.writeFloat(pitch);
                        passedData.writeFloat(yaw);
                        watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FBTurrets.TURRET_ANGLES_UPDATE, passedData));
                    }
                    
                    if (MathHelp.facingDiffrence(pitch, MathHelper.wrapDegrees(yaw), target_pitch, target_yaw) < 3) {
                
                        currentAimTime++;
                        
                        if (currentAimTime >= gun.getAimTime() && !cantBeAttacked(target)) {
                            currentAimTime = 0;
                            target.damage(DamageSource.GENERIC, gun.getDamage());
                    
                            Stream<PlayerEntity> newWatchingPlayers = PlayerStream.watching(world, pos);
                            PacketByteBuf newPassedData = new PacketByteBuf(Unpooled.buffer());
                            newPassedData.writeBlockPos(pos);
                            newPassedData.writeDouble(d);
                            newPassedData.writeDouble(e);
                            newPassedData.writeDouble(f);
                            newWatchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FBTurrets.TURRET_SHOOT_PARTICLES, newPassedData));
                        } else {
                            if (currentAimTime >= gun.getAimTime()) {
                                target = null;
                            }
                        }
                    }
                } else {
                    target = null;
                }
            } else {
                target = null;
            }
        }
    }
    
    boolean cantBeAttacked(Entity target) {
        assert world != null;
        if (target != null) {
            if (!target.isAlive()) {
                return true;
            }
            
            if (!(target instanceof Monster)) {
                return true;
            }
            
            Vec3d targetPos = new Vec3d(target.getX(), (target.getPos().y + target.getBoundingBox().getYLength() / 2), target.getZ());
            Vec3d dir = targetPos.subtract(turretVec3d).normalize();
            HitResult.Type result = world.rayTrace(new RayTraceContext(turretVec3d.add(dir.multiply(1.2)), targetPos, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.ANY, new PigEntity(EntityType.PIG, world))).getType();
            return result == HitResult.Type.BLOCK;
        }
        return true;
    }
    
    public void dropGun() {
        if (gun != null) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, new ItemStack(Registry.ITEM.get(gun.getID())));
            assert world != null;
            itemEntity.setVelocity(0, 0.15, 0);
            world.spawnEntity(itemEntity);
        }
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (gun != null) {
            tag.putString("gunID", gun.getID().toString());
        } else {
            tag.putString("gunID", "");
        }
        return super.toTag(tag);
    }
    
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        String id = tag.getString("gunID");
        if (!id.isEmpty()) {
            gun = (TurretGun)Registry.BLOCK.get(new Identifier(id));
        }
        super.fromTag(state, tag);
    }
}
