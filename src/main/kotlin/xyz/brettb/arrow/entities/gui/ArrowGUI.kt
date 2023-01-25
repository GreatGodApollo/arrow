package xyz.brettb.arrow.entities.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import xyz.brettb.arrow.entities.gui.components.ArrowGUIComponent
import xyz.brettb.arrow.events.ArrowClickEvent
import xyz.brettb.arrow.util.Dimension
import xyz.brettb.arrow.util.colorizeText

class ArrowGUI(
    val plugin: JavaPlugin,
    rows: Int,
    @Suppress("MemberVisibilityCanBePrivate") val player: Player,
    @Suppress("MemberVisibilityCanBePrivate") val title: String
) : InventoryHolder {

    private val components: MutableList<ArrowGUIComponent> = mutableListOf()

    private val inv: Inventory

    private val size: Dimension

    init {
        if (rows < 1 || rows > 6) {
            throw IllegalArgumentException("rows must be between 1 and 6!")
        }

        inv = plugin.server.createInventory(this, rows * 9, title.colorizeText())
        size = Dimension.of(9, rows * 9)
    }

    @Suppress("unused")
    fun render() {
        inv.contents = emptyArray()

        components.forEach { comp ->
            val pos = comp.position
            if (!(pos + comp.size).fitsInside(size)) {
                plugin.logger.warning("Invalid component at $pos")
                return
            }
            comp.render(player, this, pos)
        }

        if (player.openInventory.topInventory.holder != this) {
            player.closeInventory()
            player.openInventory(inventory)
        }
    }

    fun setItem(x: Int, y: Int, item: ItemStack): ArrowGUI {
        val pos = Dimension.of(x, y)
        if (!pos.fitsInside(size)) {
            throw IllegalArgumentException("Can't set item at position $pos: position exceeds GUI bounds!")
        }
        inventory.setItem(x + y * 9, item)
        return this
    }

    fun onClick(e: InventoryClickEvent) {
        val slot = e.rawSlot

        val pos = Dimension.of(slot % 9, slot / 9)
        components.filter {
            pos.x >= it.position.x && pos.x < it.position.x + it.size.x
                    && pos.y >= it.position.y && pos.y < it.position.y + it.size.y
        }
            .reduceOrNull { _, b -> b }.let { comp ->
                run {
                    if (comp == null) return
                    val ce =
                        ArrowClickEvent(e.whoClicked as Player, this, e.currentItem!!, e.click, pos - comp.position)
                    comp.onClick(ce)
                    e.isCancelled = ce.isCancelled
                    if (ce.isClosing && player.openInventory.topInventory.holder == this)
                        player.closeInventory()
                }
            }
    }

    @Suppress("unused")
    fun addComponent(comp: ArrowGUIComponent): ArrowGUI {
        if (!(comp.size + comp.position).fitsInside(size)) return this
        components.add(comp)
        return this
    }

    @Suppress("unused")
    fun setComponents(comps: List<ArrowGUIComponent>): ArrowGUI {
        this.components.clear()
        this.components.addAll(comps)
        return this
    }

    override fun getInventory(): Inventory {
        return this.inv
    }
}
