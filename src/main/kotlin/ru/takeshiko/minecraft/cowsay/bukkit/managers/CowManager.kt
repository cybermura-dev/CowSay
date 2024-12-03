package ru.takeshiko.minecraft.cowsay.bukkit.managers

import net.minecraft.server.v1_12_R1.EntityCow
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.entity.Cow
import org.bukkit.plugin.java.JavaPlugin
import ru.takeshiko.minecraft.cowsay.bukkit.tasks.CowMovementTask

class CowManager(private val plugin: JavaPlugin) {

    private val livingCows: MutableSet<Int> = mutableSetOf()

    fun spawnCustomCow(location: Location, text: String, sayCount: Int): Cow {
        val world = (location.world as CraftWorld).handle
        val cow = EntityCow(world)
        cow.setPosition(location.x, location.y, location.z)
        cow.customName = "$sayCount: $text"
        cow.customNameVisible = true
        world.addEntity(cow)
        val bukkitCow = cow.bukkitEntity as Cow
        livingCows.add(bukkitCow.entityId)
        return bukkitCow
    }

    fun startCowMovementTask(cow: Cow, location: Location, radius: Double, speed: Double, duration: Long, mooInterval: Double, particle: String) {
        CowMovementTask(this, cow, location, radius, speed, duration, mooInterval, particle).runTaskTimer(plugin, 0L, 2L)
    }

    fun removeCow(cowId: Int) {
        livingCows.remove(cowId)
    }

    fun isCowAlive(cowId: Int): Boolean {
        return livingCows.contains(cowId)
    }
}