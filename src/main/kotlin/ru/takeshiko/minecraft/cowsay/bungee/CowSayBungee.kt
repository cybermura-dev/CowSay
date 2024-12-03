package ru.takeshiko.minecraft.cowsay.bungee

import net.md_5.bungee.api.plugin.Plugin
import ru.takeshiko.minecraft.cowsay.bungee.commands.CowSayCommand
import ru.takeshiko.minecraft.cowsay.bungee.managers.ConfigManager
import ru.takeshiko.minecraft.cowsay.bungee.managers.DatabaseManager
import java.sql.SQLException

class CowSayBungee : Plugin() {

    lateinit var configManager: ConfigManager
    lateinit var databaseManager: DatabaseManager

    override fun onEnable() {
        configManager = ConfigManager(this)
        databaseManager = DatabaseManager(this)

        configManager.loadConfig()
        databaseManager.setupDatabase()

        proxy.pluginManager.registerCommand(this, CowSayCommand(this))
        proxy.registerChannel("bungee:cowsay")

        logger.info("CowSay plugin has been enabled!")
    }

    override fun onDisable() {
        configManager.saveConfig()
        try {
            databaseManager.closeDatabaseConnection()
        } catch (e: SQLException) {
            logger.severe("Failed to close database connection: ${e.message}")
        }
        proxy.unregisterChannel("bungee:cowsay")

        logger.info("CowSay plugin has been disabled!")
    }
}