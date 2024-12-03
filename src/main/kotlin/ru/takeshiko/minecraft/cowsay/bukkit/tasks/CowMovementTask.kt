package ru.takeshiko.minecraft.cowsay.bukkit.tasks

import net.minecraft.server.v1_12_R1.EntityCow
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCow
import org.bukkit.entity.Cow
import org.bukkit.scheduler.BukkitRunnable
import ru.takeshiko.minecraft.cowsay.bukkit.managers.CowManager
import java.lang.ref.WeakReference
import kotlin.math.cos
import kotlin.math.sin

class CowMovementTask(
    private val cowManager: CowManager,
    cow: Cow,
    private val center: Location,
    private val radius: Double,
    private val speed: Double,
    private val duration: Long,
    private val mooInterval: Double,
    private val particle: String
) : BukkitRunnable() {

    private val cowRef: WeakReference<EntityCow> = WeakReference((cow as CraftCow).handle)
    private var time = 0.0
    private var lastMooTime = 0.0

    override fun run() {
        val cow = cowRef.get() ?: run {
            this.cancel()
            return
        }

        if (time >= duration) {
            explodeCow(cow)
            this.cancel()
            return
        }

        val angle = time * speed
        val x = center.x + radius * cos(angle)
        val z = center.z + radius * sin(angle)
        val newLocation = Location(center.world, x, center.y, z)
        cow.setPosition(x, newLocation.y, z)
        cow.yaw = (time * speed * 57.2958).toFloat()

        val yOffset = 0.5
        center.world.spawnParticle(Particle.valueOf(particle), newLocation.x, newLocation.y + yOffset, newLocation.z, 3, 0.0, 0.0, 0.0, 0.0)

        if (time - lastMooTime >= mooInterval) {
            center.world.playSound(newLocation, Sound.ENTITY_COW_AMBIENT, 1.0F, 1.0F)
            lastMooTime = time
        }

        time += 0.1
    }

    private fun explodeCow(cow: EntityCow) {
        cow.world.createExplosion(cow, cow.locX, cow.locY, cow.locZ, 4.0F, false, false)
        cow.die()
        cowManager.removeCow(cow.id)
    }
}