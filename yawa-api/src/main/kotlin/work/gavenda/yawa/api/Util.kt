package work.gavenda.yawa.api

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.net.HttpURLConnection
import java.net.URL

/**
 * Returns the response in text of the URL using an HTTP GET request.
 */
fun URL.asText(): String {
    return openConnection().run {
        this as HttpURLConnection
        inputStream.bufferedReader().readText()
    }
}

/**
 * Simple helper for running bukkit tasks asynchronously.
 * @param plugin executing plugin
 * @param runnable task to execute
 */
fun bukkitAsyncTask(plugin: Plugin, runnable: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
}

/**
 * Simple helper for running bukkit tasks asynchronously.
 * @param plugin executing plugin
 * @param ticks number of ticks before running
 * @param runnable task to execute
 */
fun bukkitAsyncTask(plugin: Plugin, ticks: Long, runnable: () -> Unit) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks)
}

/**
 * Simple helper for running bukkit tasks.
 * @param plugin executing plugin
 * @param runnable task to execute
 */
fun bukkitTask(plugin: Plugin, runnable: () -> Unit) {
    Bukkit.getScheduler().runTask(plugin, runnable)
}

/**
 * Simple helper for running bukkit tasks.
 * @param plugin executing plugin
 * @param ticks number of ticks before running
 * @param runnable task to execute
 */
fun bukkitTask(plugin: Plugin, ticks: Long, runnable: () -> Unit) {
    Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks)
}

/**
 * Simple helper for running bukkit timer tasks.
 * @param plugin executing plugin
 * @param runnable task to execute
 */
fun bukkitTimerTask(plugin: Plugin, delay: Long, period: Long, runnable: () -> Unit): Int {
    val task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period)
    return task.taskId
}

/**
 * Simple helper for running bukkit timer tasks.
 * @param plugin executing plugin
 * @param runnable task to execute
 * @return task id
 */
fun bukkitAsyncTimerTask(plugin: Plugin, delay: Long, period: Long, runnable: () -> Unit): Int {
    val task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period)
    return task.taskId
}
