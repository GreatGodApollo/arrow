package xyz.brettb.arrow.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

data class ArrowClickEvent(
    val player: Player,
    val gui: ArrowGUI,
    val item: ItemStack,
    val clickType: ClickType,
    val offset: Dimension,
) {
    @Suppress("unused")
    val offsetX = offset.x
    @Suppress("unused")
    val offsetY = offset.y

    var isCancelled = true
    var isClosing = false
}
