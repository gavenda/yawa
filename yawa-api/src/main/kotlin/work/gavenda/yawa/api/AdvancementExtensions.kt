/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */
package work.gavenda.yawa.api

import org.bukkit.advancement.Advancement

/**
 * Returns true if the advancement will be displayed chat, toast and advancement screen.
 */
val Advancement.displayAdvancement: Boolean
    get() {
        try {
            val craftAdvancement = (this as Any).javaClass.getMethod("getHandle").invoke(this)
            val advancementDisplay = craftAdvancement.javaClass.getMethod("c").invoke(craftAdvancement)
            return advancementDisplay.javaClass.getMethod("i").invoke(advancementDisplay) as Boolean
        } catch (e: NullPointerException) {
            return false
        } catch (e: Exception) {
            apiLogger.info("Failed to check if advancement should be displayed: $e")
        }
        return false
    }