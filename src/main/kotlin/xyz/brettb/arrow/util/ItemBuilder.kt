package xyz.brettb.arrow.util

import com.cryptomorin.xseries.XMaterial
import me.ialistannen.mininbt.ItemNBTUtil
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import java.util.function.Consumer

class ItemBuilder private constructor(private var item: ItemStack) {
    fun mutateMeta(mutator: (im: ItemMeta) -> Unit): ItemBuilder {
        if (this.item.hasItemMeta()) {
            val im = item.itemMeta!!
            mutator(im)
            item.itemMeta = im
        }
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun setDisplayName(name: String): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.setDisplayName(name) }
    }

    fun setDurability(durability: Int): ItemBuilder {
        return mutateMeta { im: ItemMeta -> if (im is Damageable) im.damage = durability}
    }

    fun setLore(vararg lore: String): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.lore = listOf(*lore) }
    }

    fun setLore(lore: List<String>): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.lore = lore }
    }

    fun addLore(vararg lore: String): ItemBuilder {
        val newLore = item.itemMeta!!.lore
        newLore!!.addAll(listOf(*lore))
        return mutateMeta { im: ItemMeta -> im.lore = newLore }
    }

    fun addLore(lore: List<String>): ItemBuilder {
        val newLore = item.itemMeta!!.lore
        newLore!!.addAll(lore)
        return mutateMeta { im: ItemMeta? -> im.lore = newLore }
    }

    fun addEnchantment(enchant: Enchantment, level: Int, ignoreRestrictions: Boolean): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.addEnchant(enchant, level, ignoreRestrictions) }
    }

    fun addEnchantments(enchants: Map<Enchantment, Int>, ignoreRestrictions: Boolean): ItemBuilder {
        enchants.forEach { (enchant: Enchantment, level: Int) -> addEnchantment(enchant, level, ignoreRestrictions) }
        return this
    }

    fun addItemFlags(vararg flags: ItemFlag): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.addItemFlags(*flags) }
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        return mutateMeta { im: ItemMeta -> im.isUnbreakable = unbreakable }
    }

    fun setNBTTag(key: String, value: String): ItemBuilder {
        val nbt = ItemNBTUtil.getTag(item)
        nbt.setString(key, value)
        item = ItemNBTUtil.setNBTTag(nbt, item)
        return this
    }

    fun build(): ItemStack {
        return item
    }

    companion object {
        fun of(mat: Material): ItemBuilder {
            return of(ItemStack(mat))
        }

        fun of(mat: XMaterial): ItemBuilder {
            return if (mat.parseItem() == null) {
                of(ItemStack(Material.STONE))
            } else of(ItemStack(mat.parseItem()!!))
        }

        fun of(itemStack: ItemStack): ItemBuilder {
            return ItemBuilder(itemStack)
        }
    }
}