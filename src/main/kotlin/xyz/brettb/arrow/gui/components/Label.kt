package xyz.brettb.arrow.gui.components

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.brettb.arrow.gui.ArrowClickEvent
import xyz.brettb.arrow.gui.ArrowGUIComponent
import xyz.brettb.arrow.gui.Dimension
import xyz.brettb.arrow.util.ItemBuilder
import xyz.brettb.arrow.util.colorize
import xyz.brettb.arrow.util.colorizeText

open class Label(item: ItemStack, size: Dimension, position: Dimension) :
    ArrowGUIComponent(item, size, position) {

    constructor(
        item: ItemStack,
        size: Dimension,
        position: Dimension,
        name: String,
        vararg lore: String
    ) : this(item, size, position) {
        val ib = ItemBuilder.of(item)
        ib.setDisplayName(name)
        ib.setLore(*lore.colorize())
        this.item = ib.build()
    }

    override fun simpleRender(player: Player, offsetX: Int, offsetY: Int): ItemStack {
        return item
    }

    override fun onClick(e: ArrowClickEvent) {
        // Ignore the event
    }

    private fun addLore(vararg lore: String) {
        val ib = ItemBuilder.of(item)
        ib.addLore(*lore.colorize())
        item = ib.build()
    }

    private fun setName(name: String) {
        val ib = ItemBuilder.of(item)
        ib.setDisplayName(name.colorizeText())
        item = ib.build()
    }

}