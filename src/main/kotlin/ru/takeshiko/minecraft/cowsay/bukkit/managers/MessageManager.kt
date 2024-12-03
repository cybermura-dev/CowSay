package ru.takeshiko.minecraft.cowsay.bukkit.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class MessageManager(private val plugin: JavaPlugin, private val cowManager: CowManager) {

    fun handlePluginMessage(channel: String, player: Player, message: ByteArray) {
        if (channel != "bungee:cowsay") return

        try {
            ByteArrayInputStream(message).use { byteArray ->
                DataInputStream(byteArray).use { data ->
                    val text = data.readUTF()
                    val sayCount = data.readInt()

                    handleCowSayMessage(player.name, text, sayCount)
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Failed to process plugin message: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun handleCowSayMessage(username: String, text: String, sayCount: Int) {
        val player = Bukkit.getPlayer(username)
        if (player == null || !player.isOnline) {
            plugin.logger.warning("Player $username is not online.")
            return
        }

        val location = player.location
        val cow = cowManager.spawnCustomCow(location, text, sayCount)

        val config = plugin.config
        val radius = config.getDouble("cow.radius", 5.0)
        val speed = config.getDouble("cow.speed", 1.5)
        val duration = config.getLong("cow.duration", 10)
        val mooInterval = config.getDouble("cow.moo_interval", 0.5)
        val particle = config.getString("cow.particle", "HEART")

        cowManager.startCowMovementTask(cow, location, radius, speed, duration, mooInterval, particle)
    }
}