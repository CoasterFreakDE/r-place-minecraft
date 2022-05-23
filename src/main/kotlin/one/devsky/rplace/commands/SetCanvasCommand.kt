package one.devsky.rplace.commands

import de.moltenKt.unfold.text
import one.devsky.rplace.annotations.RegisterCommand
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import org.bukkit.persistence.PersistentDataType

@RegisterCommand(
    name = "setCanvas",
    description = "Set canvas for rplace",
    permission = "one.devsky.rplace.commands.setCanvas",
    permissionDefault = PermissionDefault.OP,
    aliases = ["canvas"]
)
class SetCanvasCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return true
        }

        sender.sendMessage(
            text("<#74b9ff><bold>RPlace »</bold> <gradient:#ffeaa7:#74b9ff>Please select the first point on the canvas</gradient>")
        )

        sender.persistentDataContainer.set(
            NamespacedKey.minecraft("rplace_canvas_placemode"),
            PersistentDataType.BYTE,
            1
        )

        return true
    }



}