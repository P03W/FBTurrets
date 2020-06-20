package mc.fbturrets.main;

import mc.fbturrets.blocks.TurretGun;
import mc.fbturrets.blocks.TurretHolder;
import mc.fbturrets.blocks.TurretHolderBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

public class FBTurrets implements ModInitializer {
	public static final String MOD_ID = "fbturrets";

	public static final Identifier TURRET_ANGLES_UPDATE = new Identifier(MOD_ID, "turret_angles");
	public static final Identifier TURRET_SHOOT_PARTICLES = new Identifier(MOD_ID, "turret_shoot");

	public static Block TURRET_HOLDER_MODEL;
	public static TurretGun BASIC_GUN;
	public static TurretGun SNIPER_GUN;

	public static final TurretHolder TURRET_HOLDER = new TurretHolder(FabricBlockSettings.of(Material.BARRIER).hardness(4.0f).nonOpaque());
	public static BlockEntityType<TurretHolderBlockEntity> TURRET_HOLDER_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		TURRET_HOLDER_MODEL = new Block(FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque());
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "turret_holder_render_only"), TURRET_HOLDER_MODEL);

		BASIC_GUN = registerTurretGun("basic_gun", 0.3, 20, ParticleTypes.CRIT, 5, new Box(10, 5, 10, -10, -5, -10));
		SNIPER_GUN = registerTurretGun("sniper_gun", 0.2, 35, ParticleTypes.ENCHANTED_HIT, 15, new Box(32, 15, 32, -32, -15, -32));

		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "turret_holder"), TURRET_HOLDER);
		TURRET_HOLDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":turret_holder", BlockEntityType.Builder.create(TurretHolderBlockEntity::new, TURRET_HOLDER).build(null));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "turret_holder"), new BlockItem(TURRET_HOLDER, new Item.Settings().group(ItemGroup.MISC)));
	}

	private TurretGun registerTurretGun(String ident, double aimSpeed, int aimTime, DefaultParticleType effect, int damage, Box box) {
		TurretGun block = new TurretGun(aimTime, aimSpeed, damage, effect, box, FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque());
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, ident), block);
		return block;
	}
}
