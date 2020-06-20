package mc.fbturrets.main;

import mc.fbturrets.blocks.TurretGun;
import mc.fbturrets.blocks.TurretHolderBlockEntity;
import mc.fbturrets.blocks.TurretHolderRenderer;
import mc.fbturrets.math.VectorIterator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FBTurretsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(FBTurrets.TURRET_HOLDER_BLOCK_ENTITY, TurretHolderRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(FBTurrets.TURRET_HOLDER, RenderLayer.getCutout());

        ClientSidePacketRegistry.INSTANCE.register(FBTurrets.TURRET_ANGLES_UPDATE,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    float pitch = attachedData.readFloat();
                    float yaw = attachedData.readFloat();
                    packetContext.getTaskQueue().execute(() -> {
                                BlockEntity blockEntity = packetContext.getPlayer().world.getBlockEntity(pos);
                                if (blockEntity instanceof TurretHolderBlockEntity) {
                                    ((TurretHolderBlockEntity) blockEntity).pitch = pitch;
                                    ((TurretHolderBlockEntity) blockEntity).yaw = yaw;
                                }
                            }
                    );
                }
        );

        ClientSidePacketRegistry.INSTANCE.register(FBTurrets.TURRET_SHOOT_PARTICLES,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    double x = attachedData.readDouble();
                    double y = attachedData.readDouble();
                    double z = attachedData.readDouble();
                    Vec3d target = new Vec3d(x, y, z);
                    packetContext.getTaskQueue().execute(() -> {
                                World world = packetContext.getPlayer().world;
                                BlockEntity blockEntity = world.getBlockEntity(pos);
                                if (blockEntity instanceof TurretHolderBlockEntity) {
                                    TurretGun gun = ((TurretHolderBlockEntity) blockEntity).gun;
                                    VectorIterator vecIter = new VectorIterator(target, 0.5);
                                    Vec3d turretVec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5);
                                    for (Vec3d vec3d : vecIter) {
                                        world.addParticle(ParticleTypes.CRIT, true, turretVec3d.x + vec3d.x, turretVec3d.y + vec3d.y, turretVec3d.z + vec3d.z, 0, 0.12, 0);
                                    }
                                }
                            }
                    );
                }
        );
    }
}
