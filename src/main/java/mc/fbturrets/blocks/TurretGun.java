package mc.fbturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class TurretGun extends Block implements ITurretGun {
    private final int aimTime;
    private final double rotSpeed;
    private final int damage;
    private final DefaultParticleType effect;
    private final Box targetArea;
    private final Identifier transitiveID;

    public TurretGun(int gunAimTime, double gunRotSpeed, int gunDamage, DefaultParticleType shootEffect, Box targetBox, Identifier id, Settings settings) {
        super(settings);
        aimTime = gunAimTime;
        rotSpeed = gunRotSpeed;
        damage = gunDamage;
        effect = shootEffect;
        targetArea = targetBox;
        transitiveID = id;
    }

    @Override
    public int getAimTime() {
        return aimTime;
    }

    @Override
    public double getRotSpeed() {
        return rotSpeed;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public DefaultParticleType getEffect() {
        return effect;
    }

    @Override
    public Box getTargetBox() {
        return targetArea;
    }
    
    @Override
    public Identifier getID() {
        return transitiveID;
    }
}
