package ru.takeshiko.minecraft.cowsay.bukkit

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import ru.takeshiko.minecraft.cowsay.bukkit.listeners.CowSayListener
import ru.takeshiko.minecraft.cowsay.bukkit.managers.CowManager
import ru.takeshiko.minecraft.cowsay.bukkit.managers.MessageManager

class CowSayBukkit : JavaPlugin(), PluginMessageListener {

    private lateinit var cowManager: CowManager
    private lateinit var messageManager: MessageManager

    override fun onEnable() {
        saveDefaultConfig()
        cowManager = CowManager(this)
        messageManager = MessageManager(this, cowManager)

        server.pluginManager.registerEvents(CowSayListener(cowManager), this)

        server.messenger.registerIncomingPluginChannel(this, "bungee:cowsay", this)
        server.messenger.registerOutgoingPluginChannel(this, "bungee:cowsay")

        logger.info("CowSay plugin has been enabled!")
    }

    override fun onDisable() {
        server.messenger.unregisterIncomingPluginChannel(this, "bungee:cowsay", this)
        server.messenger.unregisterOutgoingPluginChannel(this, "bungee:cowsay")

        logger.info("CowSay plugin has been disabled!")
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        messageManager.handlePluginMessage(channel, player, message)
    }
}