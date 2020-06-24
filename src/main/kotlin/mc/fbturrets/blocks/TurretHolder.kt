package mc.fbturrets.blocks

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class TurretHolder(settings: Settings?) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(blockView: BlockView): BlockEntity? {
        return TurretHolderBlockEntity()
    }

    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.INVISIBLE
    }

    @Environment(EnvType.CLIENT)
    override fun getAmbientOcclusionLightLevel(state: BlockState, world: BlockView, pos: BlockPos): Float {
        return 1.0f
    }

    override fun isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return true
    }
}