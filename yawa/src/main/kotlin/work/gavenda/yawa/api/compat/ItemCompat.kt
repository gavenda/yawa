/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2023 Gavenda <gavenda@disroot.org>
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
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

var ItemMeta.displayNameCompat
    get(): Component? {
        return pluginEnvironment.displayName(this)
    }
    set(value) = pluginEnvironment.displayName(this, value)

var ItemStack.loreCompat: List<Component>?
    get() {
        return pluginEnvironment.lore(this)
    }
    set(lore) {
        pluginEnvironment.lore(this, lore)
    }

var ItemMeta.loreCompat: List<Component>?
    get() {
        return pluginEnvironment.lore(this)
    }
    set(value) = pluginEnvironment.lore(this, value)
