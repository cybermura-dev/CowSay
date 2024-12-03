package ru.takeshiko.minecraft.cowsay.bukkit.listeners

import org.bukkit.entity.Cow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import ru.takeshiko.minecraft.cowsay.bukkit.managers.CowManager

class CowSayListener(private val cowManager: CowManager) : Listener {

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (damager is Cow) {
            if (cowManager.isCowAlive(damager.entityId)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is Cow) {
            if (cowManager.isCowAlive(entity.entityId)) {
                event.isCancelled = true
            }
        }
    }
}