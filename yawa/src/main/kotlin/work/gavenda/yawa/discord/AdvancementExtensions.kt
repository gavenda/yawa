package work.gavenda.yawa.discord

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.advancement.Advancement
import work.gavenda.yawa.api.toLegacyText
import work.gavenda.yawa.logger
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

private val ADVANCEMENT_TITLE_CACHE = ConcurrentHashMap<Advancement, String>()

val Advancement.title: String
    get() {
        return ADVANCEMENT_TITLE_CACHE.computeIfAbsent(this) {
            try {
                val handle: Any = this.javaClass.getMethod("getHandle").invoke(this)
                val advancementDisplay: Any = Arrays.stream(handle.javaClass.methods)
                    .filter { method -> method.returnType.simpleName.equals("AdvancementDisplay") }
                    .filter { method -> method.parameterCount == 0 }
                    .findFirst()
                    .orElseThrow { RuntimeException("Failed to find AdvancementDisplay getter for advancement handle") }
                    .invoke(handle) ?: throw RuntimeException("Advancement doesn't have display properties")

                try {
                    val advancementMessageField: Field = advancementDisplay.javaClass.getDeclaredField("a")
                    advancementMessageField.isAccessible = true
                    val advancementMessage: Any = advancementMessageField.get(advancementDisplay)
                    val advancementTitle =
                        advancementMessage.javaClass.getMethod("getString").invoke(advancementMessage)
                    return@computeIfAbsent advancementTitle as String
                } catch (_: Exception) {
                    logger.info("Failed to get title of advancement using getString, trying JSON method")
                }

                val titleComponentField: Field = Arrays.stream(advancementDisplay.javaClass.declaredFields)
                    .filter { field -> field.type.simpleName.equals("IChatBaseComponent") }
                    .findFirst().orElseThrow { RuntimeException("Failed to find advancement display properties field") }
                titleComponentField.isAccessible = true
                val titleChatBaseComponent: Any = titleComponentField.get(advancementDisplay)
                val title = titleChatBaseComponent.javaClass.getMethod("getText").invoke(titleChatBaseComponent) as String
                if (title.isNotBlank()) {
                    return@computeIfAbsent title
                }
                val chatSerializerClass: Class<*> = Arrays.stream(titleChatBaseComponent.javaClass.declaredClasses)
                    .filter { clazz -> clazz.simpleName.equals("ChatSerializer") }
                    .findFirst().orElseThrow { RuntimeException("Couldn't get component ChatSerializer class") }
                val componentJson = chatSerializerClass.getMethod("a", titleChatBaseComponent.javaClass)
                    .invoke(null, titleChatBaseComponent) as String

                return@computeIfAbsent GsonComponentSerializer.gson().deserialize(componentJson).toLegacyText()
            } catch (e: Exception) {
                val rawAdvancementName = key.key
                val arr = rawAdvancementName
                    .substring(rawAdvancementName.lastIndexOf("/") + 1)
                    .lowercase()
                    .split("_")
                    .toTypedArray()

                return@computeIfAbsent Arrays.stream(arr)
                    .map { s -> s.substring(0, 1).uppercase() + s.substring(1) }
                    .collect(Collectors.joining(" "))
            }
        }
    }