package ru.takeshiko.minecraft.cowsay.bungee.managers

import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import ru.takeshiko.minecraft.cowsay.bungee.CowSayBungee
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ConfigManager(private val plugin: CowSayBungee) {

    lateinit var config: Configuration
    private lateinit var configFile: File

    fun loadConfig() {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()

        configFile = File(plugin.dataFolder, "config.yml")
        if (!configFile.exists()) {
            try {
                Files.copy(plugin.getResourceAsStream("config.yml"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: IOException) {
                plugin.logger.severe("Failed to copy default config: ${e.message}")
            }
        }

        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)
    }

    fun saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, configFile)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to save config: ${e.message}")
        }
    }
}