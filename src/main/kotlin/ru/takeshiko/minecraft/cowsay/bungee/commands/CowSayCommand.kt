package ru.takeshiko.minecraft.cowsay.bungee.commands

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import ru.takeshiko.minecraft.cowsay.bungee.CowSayBungee
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.concurrent.CompletableFuture

class CowSayCommand(private val plugin: CowSayBungee) : Command("cowsay") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) {
            sender.sendMessage(TextComponent("This command can only be used by players."))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(TextComponent("Usage: /cowsay <message>"))
            return
        }

        val message = args.joinToString(" ")

        CompletableFuture.runAsync {
            plugin.databaseManager.updateOrInsertSay(sender.name, message)
        }.thenApplyAsync {
            plugin.databaseManager.getSayCount(sender.name)
        }.thenAcceptAsync { sayCount ->
            sendPluginMessage(sender, message, sayCount)
        }.exceptionally { e ->
            plugin.logger.severe("Failed to process cowsay command: ${e.message}")
            null
        }
    }

    private fun sendPluginMessage(player: ProxiedPlayer, text: String, sayCount: Int) {
        val server = player.server.info
        val message = ByteArrayOutputStream().use { byteArray ->
            DataOutputStream(byteArray).use { data ->
                data.writeUTF(text)
                data.writeInt(sayCount)
                byteArray.toByteArray()
            }
        }
        server.sendData("bungee:cowsay", message)
    }
}