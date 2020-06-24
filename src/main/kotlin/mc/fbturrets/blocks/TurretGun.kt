package mc.fbturrets.blocks

import net.minecraft.block.Block
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

class TurretGun(override val aimTime: Int, override val rotSpeed: Double, override val damage: Int, override val effect: DefaultParticleType, override val targetBox: Box, override val iD: Identifier, settings: Settings?) : Block(settings), ITurretGun 