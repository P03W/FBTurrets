package mc.fbturrets.blocks;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Box;

public interface ITurretGun {
    int getAimTime();
    double getRotSpeed();
    int getDamage();
    DefaultParticleType getEffect();
    Box getTargetBox();
}
