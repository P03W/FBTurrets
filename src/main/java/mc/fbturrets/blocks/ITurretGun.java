package mc.fbturrets.blocks;

import net.minecraft.util.math.Box;

public interface ITurretGun {
    int getAimTime();
    int getDamage();
    Box getTargetBox();
}
