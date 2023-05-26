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
package work.gavenda.yawa.api.compat

import net.kyori.adventure.text.Component
import org.bukkit.advancement.Advancement
import org.bukkit.command.CommandSender
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Scoreboard
import work.gavenda.yawa.api.displayAdvancement

var SkullMeta.loreCompat: List<Component>?
    get() {
        return pluginEnvironment.lore(this)
    }
    set(value) = pluginEnvironment.lore(this, value)

/**
 * Returns the display title. Make sure to check [displayAdvancement] before calling.
 */
val Advancement.displayTitle: Component
    get() = pluginEnvironment.title(this)

fun Scoreboard.registerNewObjectiveCompat(
    name: String,
    criteria: Criteria,
    displayName: Component,
    renderType: RenderType = RenderType.INTEGER
): Objective {
    return pluginEnvironment.registerNewObjective(this, name, criteria, displayName, renderType)
}

fun CommandSender.sendMessageCompat(component: Component) {
    pluginEnvironment.sendMessage(this, component)
}
