/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.login

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.RegisteredServiceProvider
import work.gavenda.yawa.vaultPermission


class PremiumListener : Listener {

    private val rsp: RegisteredServiceProvider<Permission>? = Bukkit.getServer().servicesManager.getRegistration(
        Permission::class.java
    )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isVerified.not()) return

        vaultPermission.playerAddGroup(null, event.player, "premium")

        event.joinMessage(
            event.player.verifiedName
                .append(Component.text(" joined the game", NamedTextColor.YELLOW))
        )
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (event.player.isVerified.not()) return
        event.quitMessage(
            event.player.verifiedName
                .append(Component.text(" left the game", NamedTextColor.YELLOW))
        )
    }

}