package xyz.brettb.arrow.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import xyz.brettb.arrow.entities.gui.ArrowGUI
import xyz.brettb.arrow.util.isInvalid

class ArrowGuiListener(val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked !is Player || e.inventory.holder !is ArrowGUI) return

        val gui = e.inventory.holder as ArrowGUI

        if (gui.plugin != plugin) return

        if (e.currentItem.isInvalid) return

        e.isCancelled = true

        if (e.rawSlot >= e.inventory.size) return

        (e.inventory.holder as ArrowGUI).onClick(e)
    }

}
