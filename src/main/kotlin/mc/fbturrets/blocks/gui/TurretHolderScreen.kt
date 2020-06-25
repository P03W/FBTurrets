package mc.fbturrets.blocks.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import mc.fbturrets.main.FBTurrets
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class TurretHolderScreen(
    gui: TurretHolderGuiDescription?,
    player: PlayerEntity?,
    title: Text?
) : SyncedGuiDescription(FBTurrets.TURRET_HOLDER_SCREEN_TYPE, player, title)