package work.gavenda.yawa.api

import net.kyori.adventure.audience.Audience
import org.bukkit.Server

fun Server.asAudience(): Audience {
    return YawaAPI.Instance.adventure.server(name)
}