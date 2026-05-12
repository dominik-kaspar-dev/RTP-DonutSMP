package dev.donutsmp.rtp.util;

import dev.donutsmp.rtp.DonutRtpPlugin;
import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.config.WorldConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class MessageUtil {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private MessageUtil() {
    }

    public static void send(CommandSender sender, String message, ConfigManager configManager) {
        if (message == null || message.isBlank()) {
            return;
        }
        String prefix = configManager.getMessage("prefix");
        String combined = (prefix == null ? "" : prefix) + message;
        sender.sendMessage(SERIALIZER.deserialize(combined));
    }

    public static void sendActionBar(Player player, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        player.sendActionBar(SERIALIZER.deserialize(message));
    }

    public static void logInfo(DonutRtpPlugin plugin, String message) {
        plugin.getLogger().info(message);
    }

    public static void logWarning(DonutRtpPlugin plugin, String message) {
        plugin.getLogger().warning(message);
    }

    public static String replacePlaceholders(String input, WorldConfig config) {
        return input
                .replace("{world}", config.getKey())
                .replace("{radius}", String.valueOf(config.getRadius()))
                .replace("{price}", String.valueOf(config.getPrice()))
                .replace("{cooldown}", String.valueOf(config.getCooldown()))
                .replace("{center}", config.getCenterX() + ", " + config.getCenterY() + ", " + config.getCenterZ());
    }

    public static List<Component> toComponentList(List<String> lines) {
        List<Component> components = new ArrayList<>();
        for (String line : lines) {
            components.add(SERIALIZER.deserialize(line));
        }
        return components;
    }
}
