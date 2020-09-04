package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.event.HandlerList
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*

private var skipNightTaskId = -1
private var sleepTaskId = -1
private val skippingWorlds = mutableSetOf<World>()
private lateinit var sleepBedListener: SleepBedListener

/**
 * Enable sleep feature.
 */
fun Plugin.enableSleep() {
    if (Config.Sleep.Disabled) return

    // Placeholders
    Placeholder.register(SleepPlaceholderProvider())

    // Event listeners
    sleepBedListener = SleepBedListener(this, skippingWorlds)

    // Tasks
    sleepTaskId = bukkitAsyncTimerTask(this, 0, 20) {
        server.worlds.asSequence()
            // World is not skipping day
            .filter { it !in skippingWorlds }
            // And is night time
            .filter { it.isNightTime }
            // And happens on the over world
            .filter { it.environment == World.Environment.NORMAL }
            .forEach(this::check)
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

private fun Plugin.check(world: World) {
    // Someone is asleep, and we lack more people.
    if (world.hasBegunSleeping) {
        prepareAndBroadcast(world, Config.Sleep.ActionBar.PlayerSleeping)
    }
    // Everyone is asleep, and we have enough people
    else if (world.isEveryoneSleeping) {
        prepareAndBroadcast(world, Config.Sleep.ActionBar.NightSkipping)
        skippingWorlds.add(world)

        val nightSkipMessage = Config.Sleep.Chat.NightSkipping.random()

        // Broadcast skipping the night.
        world.broadcastMessageIf(nightSkipMessage) {
            Config.Sleep.Chat.Enabled
        }

        skipNightTaskId = bukkitTimerTask(this, 1, 1) {
            val time = world.time
            val dayTime = 1200
            val timeRate = 50
            val timeStart = (dayTime - timeRate * 1.5).toInt()
            val isMorning = time in timeStart..dayTime

            // Time within range, we have reached morning
            if (isMorning) {
                // Remove world from set
                skippingWorlds.remove(world)

                val nightSkippedMessage = Config.Sleep.Chat.NightSkipped.random()
                // Broadcast successful night skip
                world.broadcastMessageIf(nightSkippedMessage) {
                    Config.Sleep.Chat.Enabled
                }
                // Finish
                server.scheduler.cancelTask(skipNightTaskId)
            }
            // Out of range, keep accelerating
            else {
                world.time = time + timeRate
            }
        }
    }
}

private fun prepareAndBroadcast(world: World, rawMessage: String) {
    val message = Placeholder
        .withContext(world)
        .parse(rawMessage)

    world.broadcastActionBarIf(message) {
        Config.Sleep.ActionBar.Enabled
    }
}