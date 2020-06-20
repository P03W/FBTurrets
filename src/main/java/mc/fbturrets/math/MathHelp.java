package mc.fbturrets.math;

import net.minecraft.util.math.Vec3d;

public class MathHelp {
    public static Vec3d getDirectionVector(double pitch, double yaw) {
        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        double sinPitch = Math.sin(pitchRadians);
        double cosPitch = Math.cos(pitchRadians);
        double sinYaw = Math.sin(yawRadians);
        double cosYaw = Math.cos(yawRadians);

        return new Vec3d(-cosPitch * sinYaw, sinPitch, -cosPitch * cosYaw);
    }

    public static float lerpAngle(float start, float end, float delta) {
        float f = end - start;
        while (f < -180.0F) {
            f += 360.0F;
        }

        while(f >= 180.0F) {
            f -= 360.0F;
        }

        return start + delta * f;
    }
}
