package xyz.brettb.arrow.entities.gui.components

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.brettb.arrow.entities.gui.ArrowGUI
import xyz.brettb.arrow.util.Dimension
import xyz.brettb.arrow.events.ArrowClickEvent
import xyz.brettb.arrow.util.isInvalid

abstract class ArrowGUIComponent(
    var item: ItemStack,
    var size: Dimension,
    var position: Dimension
) {

    init {
        if (item.isInvalid)
            throw IllegalArgumentException("Item cannot be AIR!")
    }

    private val ID = counter++

    open fun render(player: Player, gui: ArrowGUI, offset: Dimension) {
        for (x in 0 until size.x) {
            for (y in 0 until size.y) {
                gui.setItem(offset.x + x, offset.y + y, simpleRender(player, x, y))
            }
        }
    }

    open fun render(player: Player, gui: ArrowGUI) {
        render(player, gui, Dimension.square(0))
    }

    open fun simpleRender(player: Player, offsetX: Int, offsetY: Int): ItemStack {
        return item
    }

    abstract fun onClick(e: ArrowClickEvent)

    companion object {
        var counter = 0
    }

}
