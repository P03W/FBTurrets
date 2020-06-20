package mc.fbturrets.blocks;

import io.netty.buffer.Unpooled;
import mc.fbturrets.main.FBTurrets;
import mc.fbturrets.math.MathHelp;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;

import java.util.List;
import java.util.stream.Stream;

public class TurretHolderBlockEntity extends BlockEntity implements Tickable {
    private final Box targetBox = new Box(-15, -15, -15, 15, 15, 15);
    private int currentAimTime = 0;
    private Vec3d turretVec3d;

    public float yaw = 0;
    public float pitch = 0;

    public TurretGun gun = FBTurrets.TEST_GUN;
    public Entity target;

    public TurretHolderBlockEntity() {
        super(FBTurrets.TURRET_HOLDER_BLOCK_ENTITY);
    }

    @Override
    public void tick() {
        assert world != null;
        if (!world.isClient) {

            turretVec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5);

            currentAimTime++;

            List<Entity> targets = world.getEntities(null, targetBox.offset(pos));
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

                pitch = MathHelp.lerpAngle(pitch, target_pitch, 0.4f);
                yaw = MathHelp.lerpAngle(yaw, target_yaw, 0.4f);

                Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, pos);
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeFloat(pitch);
                passedData.writeFloat(yaw);
                watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FBTurrets.TURRET_ANGLES_UPDATE, passedData));

                Vec3d offset = turretVec3d.add(MathHelp.getDirectionVector(pitch - 180, yaw).multiply(1.2));

                HitResult.Type result = world.rayTrace(new RayTraceContext(offset, targetPos, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.ANY, new PigEntity(EntityType.PIG, world))).getType();

                if (currentAimTime >= gun.getAimTime() && (result == HitResult.Type.MISS || result == HitResult.Type.ENTITY)) {
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
                    if (result == HitResult.Type.BLOCK) {
                        target = null;
                    }
                }
            } else {
                target = null;
            }
        } else {
            target = null;
        }
    }

    boolean cantBeAttacked(Entity target) {
        assert world != null;
        if (target != null) {
            if (!target.isAlive() || (
                    target instanceof PlayerEntity ||
                            target instanceof ItemEntity ||
                            target instanceof ExperienceOrbEntity ||
                            target instanceof ProjectileEntity ||
                            target instanceof ArmorStandEntity ||
                            target instanceof TntEntity ||
                            target instanceof EndCrystalEntity ||
                            target instanceof EndermanEntity ||
                            target instanceof AreaEffectCloudEntity)
            ) {
                return true;
            }

            Vec3d targetPos = new Vec3d(target.getX(), (target.getPos().y + target.getBoundingBox().getYLength() / 2), target.getZ());
            Vec3d dir = targetPos.subtract(turretVec3d).normalize();
            HitResult.Type result = world.rayTrace(new RayTraceContext(turretVec3d.add(dir.multiply(1.2)), targetPos, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.ANY, new PigEntity(EntityType.PIG, world))).getType();
            return result == HitResult.Type.BLOCK;
        }
        return true;
    }
}
