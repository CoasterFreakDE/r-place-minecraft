package one.devsky.rplace

import one.devsky.rplace.managers.RegisterManager.registerAll
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class RPlace : JavaPlugin() {

    companion object {
        lateinit var instance: RPlace
            private set
    }

    init {
        instance = this
    }

    override fun onEnable() {
        // Plugin startup logic
        val time = measureTimeMillis {
            registerAll()
        }
        println("Plugin enabled in $time ms")
        println("PaperBoilerplate is now tweaking your vanilla behavior!")
    }
}