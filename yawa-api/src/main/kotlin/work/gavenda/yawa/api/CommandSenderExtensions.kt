package work.gavenda.yawa.api

import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender

/**
 * Returns this command sender instance as audience.
 */
fun CommandSender.asAudience(): Audience {
    return YawaAPI.Instance.adventure.sender(this)
}