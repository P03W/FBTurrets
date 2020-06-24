package mc.fbturrets.util

import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MathHelp {
    @JvmStatic
    fun lerpAngle(start: Float, end: Float, delta: Float): Float {
        var f = end - start
        while (f < -180.0f) {
            f += 360.0f
        }
        while (f >= 180.0f) {
            f -= 360.0f
        }
        return start + delta * f
    }

    @JvmStatic
    fun facingDifference(pitch: Float, yaw: Float, target_pitch: Float, target_yaw: Float): Float {
        var result = 0f
        result += target_pitch - pitch
        result += target_yaw - yaw
        return sqrt(abs(result).toDouble()).toFloat()
    }

    @JvmStatic
    fun absDist(a: Float, b: Float): Double {
        return abs(b - a).toDouble()
    }

    fun buildSimpleBox(side: Int): Box {
        return Box(side.toDouble(), side.toDouble(), side.toDouble(), (-side).toDouble(), (-side).toDouble(), (-side).toDouble())
    }

    @JvmStatic
    fun smallRandom(): Double {
        val random = Random()
        return if (random.nextBoolean()) random.nextDouble() / 5 else -random.nextDouble() / 5
    }
}