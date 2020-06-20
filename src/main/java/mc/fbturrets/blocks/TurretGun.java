package mc.fbturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.Box;

public class TurretGun extends Block implements ITurretGun {
    private final int aimTime;
    private final int damage;
    private Box targetArea;

    public TurretGun(int gunAimTime, int gunDamage, Settings settings) {
        super(settings);
        aimTime = gunAimTime;
        damage = gunDamage;
    }

    @Override
    public int getAimTime() {
        return aimTime;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public Box getTargetBox() {
        return targetArea;
    }
}
