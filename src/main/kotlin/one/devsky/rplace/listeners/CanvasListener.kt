package one.devsky.rplace.listeners

import de.moltenKt.core.tool.timing.calendar.Calendar
import de.moltenKt.unfold.text
import one.devsky.rplace.extensions.toCalendar
import one.devsky.rplace.extensions.toLocation
import one.devsky.rplace.extensions.toLocationString
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

class CanvasListener : Listener {

    @EventHandler
    fun onCanvasDestroy(event: BlockBreakEvent): Unit = with(event) {

        if(player.persistentDataContainer.getOrDefault(NamespacedKey.minecraft("rplace_canvas_placemode"), PersistentDataType.BYTE, 0.toByte()) == 1.toByte()) {
            isCancelled = true
            if(player.persistentDataContainer.has(NamespacedKey.minecraft("rplace_canvas_point_1"))) {
                  val point1 = player.persistentDataContainer.get(NamespacedKey.minecraft("rplace_canvas_point_1"), PersistentDataType.STRING)
                  val point2 = "${block.x},${block.y},${block.z}"

                  block.world.persistentDataContainer.set(NamespacedKey.minecraft("canvas"), PersistentDataType.STRING, "$point1:$point2")
                 player.persistentDataContainer.remove(NamespacedKey.minecraft("rplace_canvas_point_1"))
                 player.persistentDataContainer.remove(NamespacedKey.minecraft("rplace_canvas_placemode"))
                player.sendMessage(text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Canvas setup complete</gradient>"))
            } else {
                player.persistentDataContainer.set(NamespacedKey.minecraft("rplace_canvas_point_1"), PersistentDataType.STRING, "${block.x},${block.y},${block.z}")
                player.sendMessage(text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Please select the second point on the canvas</gradient>"))
            }
        }
    }

    @EventHandler
    fun onCanvasDraw(event: PlayerInteractEvent): Unit = with(event) {
        if(clickedBlock == null) return@with
        if(hand != EquipmentSlot.HAND) return@with
        if(player.inventory.itemInMainHand.type != Material.AIR) return@with

        val canvas =
            clickedBlock!!.world.persistentDataContainer.get(NamespacedKey.minecraft("canvas"), PersistentDataType.STRING)
                ?: return@with

        val points = canvas.split(":")
        val point1 = points[0].split(",").let { clickedBlock!!.world.getBlockAt(it[0].toInt(), it[1].toInt(), it[2].toInt()).location }
        val point2 = points[1].split(",").let { clickedBlock!!.world.getBlockAt(it[0].toInt(), it[1].toInt(), it[2].toInt()).location }

        val clickedPoint = clickedBlock!!.location

        // is clickedPoint in the canvas of point 1 and 2?
        if(clickedPoint.x in point1.x..point2.x && clickedPoint.y in point1.y..point2.y && clickedPoint.z in point1.z..point2.z) {
            val lastTime = player.persistentDataContainer.get(NamespacedKey.minecraft("rplace_canvas_last"), PersistentDataType.STRING)

            if(lastTime != null) {
                val last = Instant.parse(lastTime).toCalendar().add(1.minutes)
                if(!last.isExpired) {
                    val durationLeft = last.durationFrom(Calendar.now())
                    player.sendMessage(text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Please wait $durationLeft before placing the next block</gradient>"))
                    return@with
                }
            }

            val inventory = Bukkit.createInventory(null, 54, text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Canvas</gradient>"))
            Material.values().filter { it.name.contains("WOOL") || it.name.contains("CONCRETE") || it.name.contains("TERRACOTTA") && !it.name.contains("GLAZED")}.forEach { material ->
                inventory.addItem(ItemStack(material, 1))
            }
            player.persistentDataContainer.set(NamespacedKey.minecraft("rplace_canvas_selected"), PersistentDataType.STRING, clickedPoint.toLocationString())
            player.openInventory(inventory)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent): Unit = with(event) {
        if(view.title() != text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Canvas</gradient>")) return@with
        isCancelled = true

        val player = whoClicked as Player
        val selected = player.persistentDataContainer.get(NamespacedKey.minecraft("rplace_canvas_selected"), PersistentDataType.STRING)
            ?: return@with

        val selectedBlock = selected.toLocation()
        currentItem?.let {
            selectedBlock.block.type = it.type
            player.closeInventory()
            player.persistentDataContainer.set(NamespacedKey.minecraft("rplace_canvas_last"), PersistentDataType.STRING, Calendar.now().javaInstant.toString())
        }
    }
}