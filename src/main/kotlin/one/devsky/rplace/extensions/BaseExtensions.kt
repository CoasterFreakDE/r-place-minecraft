package one.devsky.rplace.extensions

import de.moltenKt.core.tool.timing.calendar.Calendar
import org.bukkit.Bukkit
import org.bukkit.Location
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

fun String.toLocation(): Location {
    val split = this.split(",")
    return Location(Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
}

fun Location.toLocationString(): String {
    return "${world.name},${x},${y},${z}"
}

fun Instant.toCalendar() =
    Calendar(GregorianCalendar.from(ZonedDateTime.from(this.atZone(ZoneId.systemDefault()))))