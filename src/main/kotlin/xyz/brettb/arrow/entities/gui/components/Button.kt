package xyz.brettb.arrow.entities.gui.components

import org.bukkit.inventory.ItemStack
import xyz.brettb.arrow.events.ArrowClickEvent
import xyz.brettb.arrow.util.Dimension

class Button : Label {
    private val clickHandler: (ce: ArrowClickEvent) -> Unit

    var closingOnClick = false

    constructor(
        item: ItemStack,
        size: Dimension,
        position: Dimension,
        clickHandler: (ce: ArrowClickEvent) -> Unit,
    ) : super(item, size, position) {
        this.clickHandler = clickHandler
    }

    constructor(
        item: ItemStack,
        size: Dimension,
        position: Dimension,
        clickHandler: (ce: ArrowClickEvent) -> Unit,
        name: String,
        vararg lore: String
    ) : super(item, size, position, name, *lore) {
        this.clickHandler = clickHandler
    }

    override fun onClick(e: ArrowClickEvent) {
        clickHandler(e)
        e.isClosing = closingOnClick
    }

}