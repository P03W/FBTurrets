package mc.fbturrets.util;

import net.minecraft.util.math.Vec3d;

import java.util.Iterator;

public class VectorIterator implements Iterable<Vec3d> {
    private final Vec3d endVec;
    private Vec3d current = new Vec3d(0, 0, 0);
    private final Vec3d step;

    public VectorIterator(Vec3d vec, double scale) {
        endVec = vec;
        step = vec.normalize().multiply(scale);
    }

    @Override
    public Iterator<Vec3d> iterator() {
        return new Iterator<Vec3d>() {

            @Override
            public boolean hasNext() {
                return Double.compare(endVec.length(), current.length()) >= 0;
            }

            @Override
            public Vec3d next() {
                current = current.add(step);
                return current;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}