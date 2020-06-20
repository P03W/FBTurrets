package mc.fbturrets.blocks;

import mc.fbturrets.main.FBTurrets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

import java.util.Random;

public class TurretHolderRenderer extends BlockEntityRenderer<TurretHolderBlockEntity> {
    public TurretHolderRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TurretHolderBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 0, 0.5);

        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(entity.yaw));

        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
                FBTurrets.TURRET_HOLDER_MODEL.getDefaultState(),
                entity.getPos(),
                entity.getWorld(),
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getSolid()),
                true,
                new Random());

        matrices.translate(-0.5, 0.65, 0);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(entity.pitch + 180));

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
                entity.gun.getDefaultState(),
                matrices,
                vertexConsumers,
                light,
                OverlayTexture.DEFAULT_UV
                );

        matrices.pop();
    }
}
