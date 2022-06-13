package work.gavenda.yawa.discord

import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toPlainText

val Player.avatarUrl: String
    get() {
        return Placeholders.withContext(this)
            .parse(Config.Discord.AvatarUrl)
            .toPlainText()
    }