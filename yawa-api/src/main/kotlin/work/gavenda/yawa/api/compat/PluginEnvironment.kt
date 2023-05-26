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

import work.gavenda.yawa.api.apiLogger

enum class PluginEnvironment {
    SPIGOT, PAPER, FOLIA
}

val PLUGIN_ENVIRONMENT by lazy {
    try {
        Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler")
        PluginEnvironment.FOLIA
    } catch (e: ClassNotFoundException) {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
            PluginEnvironment.PAPER
        } catch (e: ClassNotFoundException) {
            PluginEnvironment.SPIGOT
        }
    }
}

val pluginEnvironment: Environment by lazy {
    if (PLUGIN_ENVIRONMENT == PluginEnvironment.PAPER || PLUGIN_ENVIRONMENT == PluginEnvironment.FOLIA) {
        apiLogger.info("Paper/Folia detected, using paper as platform for all bukkit calls")
        PaperEnvironment()
    } else {
        apiLogger.info("Spigot detected, using spigot as platform for all bukkit calls")
        SpigotEnvironment()
    }
}
