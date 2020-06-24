package mc.fbturrets.blocks

import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

interface ITurretGun {
    val aimTime: Int
    val rotSpeed: Double
    val damage: Int
    val effect: DefaultParticleType
    val targetBox: Box
    val iD: Identifier
}