package mc.fbturrets.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

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

    public static float facingDiffrence(float pitch, float yaw, float target_pitch, float target_yaw) {
        float result = 0;
        result += (target_pitch - pitch);
        result += (target_yaw - yaw);
        return (float)Math.sqrt(Math.abs(result));
    }
    
    public static double absDist(float a, float b) {
        return Math.abs(b - a);
    }
    
    public static Box buildSimpleBox(int side) {
        return new Box(side, side, side, -side, -side, -side);
    }
    
    public static double smallRandom() {
        Random random = new Random();
        return random.nextBoolean() ? random.nextDouble() / 5 : -random.nextDouble() / 5;
    }
}
