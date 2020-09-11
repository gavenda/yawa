/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.translateColorCodes

class SkinResetCommand : Command("yawa.skin.reset") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return
        sender.restoreSkin()

        transaction {
            PlayerTexture.findById(sender.uniqueId)?.delete()
        }

        sender.sendMessage(
            Placeholder
                .withContext(sender)
                .parse(Config.Messages.SkinReset)
                .translateColorCodes()
        )
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}