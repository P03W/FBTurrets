package mc.fbturrets.blocks

import mc.fbturrets.main.FBTurrets
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import java.util.*

class TurretHolderRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<TurretHolderBlockEntity>(dispatcher) {
    override fun render(entity: TurretHolderBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        matrices.translate(0.5, 0.0, 0.5)
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(entity.yaw + 180))
        MinecraftClient.getInstance().blockRenderManager.renderBlock(
                FBTurrets.TURRET_HOLDER_MODEL!!.defaultState,
                entity.pos,
                entity.world,
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getSolid()),
                true,
                Random())
        matrices.translate(-0.5, 0.65, 0.0)
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-entity.pitch))
        if (entity.gun != null) {
            MinecraftClient.getInstance().blockRenderManager.renderBlockAsEntity(
                    entity.gun!!.defaultState,
                    matrices,
                    vertexConsumers,
                    light,
                    OverlayTexture.DEFAULT_UV
            )
        }
        matrices.pop()
    }
}