package mc.fbturrets.blocks.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import mc.fbturrets.main.FBTurrets
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class TurretHolderGuiDescription(
    syncId: Int,
    playerInventory: PlayerInventory?,
    context: ScreenHandlerContext?
) : SyncedGuiDescription(
    FBTurrets.TURRET_HOLDER_SCREEN_TYPE,
    syncId,
    playerInventory,
    getBlockInventory(context),
    getBlockPropertyDelegate(context)
) {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(300, 200)
        val itemSlot = WItemSlot.of(blockInventory, 0)
        root.add(itemSlot, 4, 1)
        root.add(this.createPlayerInventoryPanel(), 0, 3)
        root.validate(this)
    }
}