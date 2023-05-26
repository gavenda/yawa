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

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.advancement.Advancement
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Scoreboard
import work.gavenda.yawa.api.apiLogger
import work.gavenda.yawa.api.asAudience
import work.gavenda.yawa.api.toComponent
import work.gavenda.yawa.api.toLegacyText
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class SpigotEnvironment : Environment {

    private val advancementTitleCache = ConcurrentHashMap<Advancement, String>()

    override fun teleportAsync(entity: Entity, location: Location): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(entity.teleport(location))
    }

    override fun teleportAsync(
        entity: Entity,
        location: Location,
        cause: PlayerTeleportEvent.TeleportCause
    ): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(entity.teleport(location, cause))
    }

    @Suppress("DEPRECATION")
    override fun displayName(itemMeta: ItemMeta): Component {
        return itemMeta.displayName.toComponent()
    }

    @Suppress("DEPRECATION")
    override fun displayName(itemMeta: ItemMeta, component: Component?) {
        itemMeta.setDisplayName(component?.toLegacyText())
    }

    override fun playSound(world: World, sound: Sound) {
        world.players.forEach { player -> player.asAudience().playSound(sound) }
    }

    @Suppress("DEPRECATION")
    override fun lore(itemStack: ItemStack): List<Component>? {
        return itemStack.itemMeta?.lore?.map { it.toComponent() }
    }

    @Suppress("DEPRECATION")
    override fun lore(itemStack: ItemStack, lore: List<Component>?) {
        itemStack.itemMeta = itemStack.itemMeta?.apply {
            setLore(lore?.map { it.toLegacyText() })
        }
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: ItemMeta): List<Component>? {
        return meta.lore?.map {
            it.toComponent()
        }
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: ItemMeta, lore: List<Component>?) {
        meta.lore = lore?.map { it.toLegacyText() }
    }

    @Suppress("DEPRECATION")
    override fun displayName(player: Player): Component {
        return player.displayName.toComponent()
    }

    @Suppress("DEPRECATION")
    override fun locale(player: Player): Locale {
        return Locale(player.locale)
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: SkullMeta, lore: List<Component>) {
        meta.lore = lore.map { it.toLegacyText() }
    }


    @Suppress("DEPRECATION")
    override fun registerNewObjective(
        scoreboard: Scoreboard,
        name: String,
        criteria: Criteria,
        displayName: Component,
        renderType: RenderType
    ): Objective {
        return scoreboard.registerNewObjective(name, criteria, displayName.toLegacyText(), renderType)
    }

    @Suppress("DEPRECATION")
    override fun deathMessage(deathEvent: PlayerDeathEvent): Component? {
        return deathEvent.deathMessage?.let {
            Component.text(it)
        }
    }

    @Suppress("DEPRECATION")
    override fun deathMessage(deathEvent: PlayerDeathEvent, component: Component?) {
        deathEvent.deathMessage = component?.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun quitMessage(quitEvent: PlayerQuitEvent, component: Component?) {
        quitEvent.quitMessage = component?.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun quitMessage(quitEvent: PlayerQuitEvent): Component? {
        return quitEvent.quitMessage?.let {
            Component.text(it)
        }
    }

    @Suppress("DEPRECATION")
    override fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?) {
        joinEvent.joinMessage = component?.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun joinMessage(joinEvent: PlayerJoinEvent): Component? {
        return joinEvent.joinMessage?.let {
            Component.text(it)
        }
    }

    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.asAudience().sendMessage(component)
    }

    override fun sendMessage(world: World, component: Component) {
        world.players.forEach { it.asAudience().sendMessage(component) }
    }

    override fun sendActionBar(world: World, component: Component) {
        world.players.forEach { it.asAudience().sendActionBar(component) }
    }

    override fun playerListHeader(player: Player, component: Component) {
        player.asAudience().sendPlayerListHeader(component)
    }

    @Suppress("DEPRECATION")
    override fun playerListHeader(player: Player): Component {
        return player.playerListHeader?.toComponent() ?: Component.empty()
    }

    override fun playerListFooter(player: Player, component: Component) {
        player.asAudience().sendPlayerListFooter(component)
    }


    @Suppress("DEPRECATION")
    override fun playerListFooter(player: Player): Component {
        return player.playerListFooter?.toComponent() ?: Component.empty()
    }

    @Suppress("DEPRECATION")
    override fun kickPlayer(player: Player, component: Component) {
        player.kickPlayer(component.toLegacyText())
    }

    @Suppress("DEPRECATION")
    override fun playerListName(player: Player, component: Component?) {
        player.setPlayerListName(component?.toLegacyText())
    }

    @Suppress("DEPRECATION")
    override fun playerListName(player: Player): Component {
        return player.playerListName.toComponent()
    }

    override fun title(advancement: Advancement): Component {
        val advancementTitle = advancementTitleCache.computeIfAbsent(advancement) {
            try {
                val handle = this.javaClass.getMethod("getHandle").invoke(this)
                val advancementDisplay = Arrays.stream(handle.javaClass.methods)
                    .filter { method -> method.returnType.simpleName.equals("AdvancementDisplay") }
                    .filter { method -> method.parameterCount == 0 }
                    .findFirst()
                    .orElseThrow { RuntimeException("Failed to find AdvancementDisplay getter for advancement handle") }
                    .invoke(handle) ?: error("Advancement doesn't have display properties")

                try {
                    val advancementMessageField: Field = advancementDisplay.javaClass.getDeclaredField("a")
                    advancementMessageField.isAccessible = true
                    val advancementMessage = advancementMessageField.get(advancementDisplay)
                    val advancementTitle =
                        advancementMessage.javaClass.getMethod("getString").invoke(advancementMessage)
                    return@computeIfAbsent advancementTitle as String
                } catch (_: Exception) {
                    apiLogger.info("Failed to get title of advancement using getString, trying JSON method")
                }

                val titleComponentField = Arrays.stream(advancementDisplay.javaClass.declaredFields)
                    .filter { field -> field.type.simpleName.equals("IChatBaseComponent") }
                    .findFirst().orElseThrow { RuntimeException("Failed to find advancement display properties field") }
                titleComponentField.isAccessible = true
                val titleChatBaseComponent = titleComponentField.get(advancementDisplay)
                val title =
                    titleChatBaseComponent.javaClass.getMethod("getText").invoke(titleChatBaseComponent) as String
                if (title.isNotBlank()) {
                    return@computeIfAbsent title
                }
                val chatSerializerClass = Arrays.stream(titleChatBaseComponent.javaClass.declaredClasses)
                    .filter { clazz -> clazz.simpleName.equals("ChatSerializer") }
                    .findFirst().orElseThrow { RuntimeException("Couldn't get component ChatSerializer class") }
                val componentJson = chatSerializerClass.getMethod("a", titleChatBaseComponent.javaClass)
                    .invoke(null, titleChatBaseComponent) as String

                return@computeIfAbsent GsonComponentSerializer.gson().deserialize(componentJson).toLegacyText()
            } catch (e: Exception) {
                val rawAdvancementName = advancement.key.key
                return@computeIfAbsent Arrays.stream(
                    rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1).lowercase(Locale.getDefault())
                        .split("_".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())
                    .map { s -> s.substring(0, 1).uppercase(Locale.getDefault()) + s.substring(1) }
                    .collect(Collectors.joining(" "))
            }
        }

        return advancementTitle.toComponent()
    }

    override fun getChunkAtAsync(world: World, location: Location): CompletableFuture<Chunk> {
        return CompletableFuture.completedFuture(world.getChunkAt(location))
    }
}
