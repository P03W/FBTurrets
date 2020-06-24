package mc.fbturrets.util

import net.minecraft.util.math.Vec3d

class VectorIterator(private val endVec: Vec3d, scale: Double) : Iterable<Vec3d?> {
    private var current = Vec3d(0.0, 0.0, 0.0)
    private val step: Vec3d = endVec.normalize().multiply(scale)
    override fun iterator(): MutableIterator<Vec3d?> {
        return object : MutableIterator<Vec3d?> {
            override fun hasNext(): Boolean {
                return endVec.length().compareTo(current.length()) >= 0
            }

            override fun next(): Vec3d {
                current = current.add(step)
                return current
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

}