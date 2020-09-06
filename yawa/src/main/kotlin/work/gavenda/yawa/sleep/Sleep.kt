package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.event.HandlerList
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*

private var sleepAnimationTaskId = -1
private var sleepTaskId = -1
private val sleepingWorlds = mutableSetOf<World>()
private lateinit var sleepBedListener: SleepBedListener

/**
 * Enable sleep feature.
 */
fun Plugin.enableSleep() {
    if (Config.Sleep.Disabled) return

    // Placeholders
    Placeholder.register(SleepPlaceholderProvider())

    // Event listeners
    sleepBedListener = SleepBedListener(this, sleepingWorlds)

    // Tasks
    sleepTaskId = bukkitAsyncTimerTask(this, 0, 20) {
        server.worlds.asSequence()
            // World is not sleeping
            .filter { it !in sleepingWorlds }
            // And is night time
            .filter { it.isNightTime }
            // And happens on the over world
            .filter { it.environment == World.Environment.NORMAL }
            .forEach(this::checkWorldForSleeping)
    }

    // Register events
    server.pluginManager.registerEvents(sleepBedListener, this)
}

/**
 * Disable sleep feature.
 */
fun Plugin.disableSleep() {
    if (Config.Sleep.Disabled) return

    // Event listeners
    HandlerList.unregisterAll(sleepBedListener)
    // Tasks
    server.scheduler.cancelTask(sleepTaskId)
}

private fun Plugin.checkWorldForSleeping(world: World) {
    // Someone is asleep, and we lack more people.
    if (world.hasBegunSleeping) {
        val message = Placeholder
            .withContext(world)
            .parse(Config.Sleep.ActionBar.Sleeping)

        world.broadcastActionBarIf(message) {
            Config.Sleep.ActionBar.Enabled
        }
    }
    // Everyone is asleep, and we have enough people
    else if (world.isEveryoneSleeping) {
        val message = Placeholder
            .withContext(world)
            .parse(Config.Sleep.ActionBar.SleepingDone)

        world.broadcastActionBarIf(message) {
            Config.Sleep.ActionBar.Enabled
        }

        sleepingWorlds.add(world)

        val sleepingMessage = Config.Sleep.Chat.Sleeping.random()

        // Broadcast everyone sleeping
        world.broadcastMessageIf(sleepingMessage) {
            Config.Sleep.Chat.Enabled
        }

        sleepAnimationTaskId = bukkitTimerTask(this, 1, 1) {
            val time = world.time
            val dayTime = 1200
            val timeRate = 50
            val timeStart = (dayTime - timeRate * 1.5).toInt()
            val isMorning = time in timeStart..dayTime

            // Time within range, we have reached morning
            if (isMorning) {
                // Remove world from set
                sleepingWorlds.remove(world)

                val sleepingDoneMessage = Config.Sleep.Chat.SleepingDone.random()
                // Broadcast successful sleep
                world.broadcastMessageIf(sleepingDoneMessage) {
                    Config.Sleep.Chat.Enabled
                }
                // Finish
                server.scheduler.cancelTask(sleepAnimationTaskId)
            }
            // Out of range, keep animating
            else {
                world.time = time + timeRate
            }
        }
    }
}
