package dev.donutsmp.rtp.commands;

import dev.donutsmp.rtp.DonutRtpPlugin;
import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.config.WorldConfig;
import dev.donutsmp.rtp.menu.MenuManager;
import dev.donutsmp.rtp.rtp.RtpService;
import dev.donutsmp.rtp.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class RtpCommand implements CommandExecutor, TabCompleter {
    private final DonutRtpPlugin plugin;
    private final ConfigManager configManager;
    private final MenuManager menuManager;
    private final RtpService rtpService;

    public RtpCommand(DonutRtpPlugin plugin, ConfigManager configManager, MenuManager menuManager, RtpService rtpService) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.menuManager = menuManager;
        this.rtpService = rtpService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessageUtil.send(sender, configManager.getMessage("player-only"), configManager);
                return true;
            }
            if (!sender.hasPermission("rtpdonutsmp.use")) {
                MessageUtil.send(sender, configManager.getMessage("no-permission"), configManager);
                return true;
            }
            player.openInventory(menuManager.createMenu());
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("reload")) {
            if (!sender.hasPermission("rtpdonutsmp.admin")) {
                MessageUtil.send(sender, configManager.getMessage("no-permission"), configManager);
                return true;
            }
            configManager.load();
            MessageUtil.send(sender, configManager.getMessage("reload"), configManager);
            return true;
        }

        if (sub.equals("set")) {
            if (!sender.hasPermission("rtpdonutsmp.admin")) {
                MessageUtil.send(sender, configManager.getMessage("no-permission"), configManager);
                return true;
            }
            return handleSet(sender, args);
        }

        MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
            return true;
        }
        String field = args[1].toLowerCase(Locale.ROOT);
        if (field.equals("warmup")) {
            if (args.length < 3) {
                MessageUtil.send(sender, configManager.getMessage("warmup-usage"), configManager);
                return true;
            }
            try {
                long seconds = Long.parseLong(args[2]);
                configManager.setWarmupSeconds(seconds);
                MessageUtil.send(sender, configManager.getMessage("set-updated")
                        .replace("{field}", "warmup")
                        .replace("{world}", "global"), configManager);
            } catch (NumberFormatException e) {
                MessageUtil.send(sender, configManager.getMessage("warmup-usage"), configManager);
            }
            return true;
        }

        if (args.length < 4) {
            MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
            return true;
        }
        String worldKey = args[2];
        WorldConfig worldConfig = configManager.getWorldConfig(worldKey);
        if (worldConfig == null) {
            MessageUtil.send(sender, configManager.getMessage("world-not-found"), configManager);
            return true;
        }

        switch (field) {
            case "center" -> {
                return handleCenter(sender, worldKey, args);
            }
            case "radius" -> {
                try {
                    double radius = Double.parseDouble(args[3]);
                    configManager.setWorldRadius(worldKey, radius);
                    MessageUtil.send(sender, configManager.getMessage("set-updated")
                            .replace("{field}", "radius")
                            .replace("{world}", worldKey), configManager);
                } catch (NumberFormatException e) {
                    MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
                }
                return true;
            }
            case "cooldown" -> {
                try {
                    long cooldown = Long.parseLong(args[3]);
                    configManager.setWorldCooldown(worldKey, cooldown);
                    MessageUtil.send(sender, configManager.getMessage("set-updated")
                            .replace("{field}", "cooldown")
                            .replace("{world}", worldKey), configManager);
                } catch (NumberFormatException e) {
                    MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
                }
                return true;
            }
            case "price" -> {
                try {
                    double price = Double.parseDouble(args[3]);
                    configManager.setWorldPrice(worldKey, price);
                    MessageUtil.send(sender, configManager.getMessage("set-updated")
                            .replace("{field}", "price")
                            .replace("{world}", worldKey), configManager);
                } catch (NumberFormatException e) {
                    MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
                }
                return true;
            }
            case "max-y" -> {
                try {
                    int maxY = Integer.parseInt(args[3]);
                    configManager.setWorldMaxY(worldKey, maxY);
                    MessageUtil.send(sender, configManager.getMessage("set-updated")
                            .replace("{field}", "max-y")
                            .replace("{world}", worldKey), configManager);
                } catch (NumberFormatException e) {
                    MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
                }
                return true;
            }
            default -> {
                MessageUtil.send(sender, configManager.getMessage("invalid-args"), configManager);
                return true;
            }
        }
    }

    private boolean handleCenter(CommandSender sender, String worldKey, String[] args) {
        if (args.length < 4) {
            MessageUtil.send(sender, configManager.getMessage("center-usage"), configManager);
            return true;
        }
        if (args[3].equalsIgnoreCase("here")) {
            if (!(sender instanceof Player player)) {
                MessageUtil.send(sender, configManager.getMessage("player-only"), configManager);
                return true;
            }
            Location loc = player.getLocation();
            configManager.setWorldCenter(worldKey, loc.getX(), loc.getY(), loc.getZ());
            MessageUtil.send(sender, configManager.getMessage("set-updated")
                    .replace("{field}", "center")
                    .replace("{world}", worldKey), configManager);
            return true;
        }
        if (args.length < 6) {
            MessageUtil.send(sender, configManager.getMessage("center-usage"), configManager);
            return true;
        }
        try {
            double x = Double.parseDouble(args[3]);
            double y = Double.parseDouble(args[4]);
            double z = Double.parseDouble(args[5]);
            configManager.setWorldCenter(worldKey, x, y, z);
            MessageUtil.send(sender, configManager.getMessage("set-updated")
                    .replace("{field}", "center")
                    .replace("{world}", worldKey), configManager);
        } catch (NumberFormatException e) {
            MessageUtil.send(sender, configManager.getMessage("center-usage"), configManager);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("reload", "set"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return filter(List.of("center", "radius", "cooldown", "price", "max-y", "warmup"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("warmup")) {
            return filter(new ArrayList<>(configManager.getWorldKeys()), args[2]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("center")) {
            return filter(List.of("here"), args[3]);
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }
}
