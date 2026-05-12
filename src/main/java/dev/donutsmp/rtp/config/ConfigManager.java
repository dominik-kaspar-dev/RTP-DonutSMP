package dev.donutsmp.rtp.config;

import dev.donutsmp.rtp.DonutRtpPlugin;
import dev.donutsmp.rtp.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private final DonutRtpPlugin plugin;
    private File menuFile;
    private YamlConfiguration menuConfig;

    public ConfigManager(DonutRtpPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        ensureMenuFile();
        menuConfig = YamlConfiguration.loadConfiguration(menuFile);
    }

    private void ensureMenuFile() {
        menuFile = new File(plugin.getDataFolder(), "menu.yml");
        if (!menuFile.exists()) {
            plugin.saveResource("menu.yml", false);
        }
    }

    public void saveConfig() {
        plugin.saveConfig();
        try {
            if (menuConfig != null && menuFile != null) {
                menuConfig.save(menuFile);
            }
        } catch (IOException e) {
            MessageUtil.logWarning(plugin, "Failed to save menu.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public YamlConfiguration getMenuConfig() {
        return menuConfig;
    }

    public int getMaxTries() {
        return plugin.getConfig().getInt("settings.max-tries", 20);
    }

    public boolean isGlobalCooldown() {
        return plugin.getConfig().getBoolean("settings.global-cooldown", true);
    }

    public long getWarmupSeconds() {
        return plugin.getConfig().getLong("settings.warmup-seconds", 0L);
    }

    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "");
    }

    public Set<String> getWorldKeys() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("worlds");
        if (section == null) {
            return Collections.emptySet();
        }
        return section.getKeys(false);
    }

    public WorldConfig getWorldConfig(String key) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("worlds." + key);
        if (section == null) {
            return null;
        }
        String displayName = section.getString("display-name", key);
        Material icon = Material.matchMaterial(section.getString("icon", "GRASS_BLOCK"));
        if (icon == null) {
            icon = Material.GRASS_BLOCK;
        }
        ConfigurationSection center = section.getConfigurationSection("center");
        double centerX = center != null ? center.getDouble("x", 0.0) : 0.0;
        double centerY = center != null ? center.getDouble("y", 64.0) : 64.0;
        double centerZ = center != null ? center.getDouble("z", 0.0) : 0.0;
        double radius = section.getDouble("radius", 5000.0);
        long cooldown = section.getLong("cooldown", 300L);
        double price = section.getDouble("price", 0.0);
        boolean enabled = section.getBoolean("enabled", true);
        List<String> lore = section.getStringList("lore");
        return new WorldConfig(key, displayName, icon, centerX, centerY, centerZ, radius, cooldown, price, enabled, lore);
    }

    public void setWorldCenter(String key, double x, double y, double z) {
        plugin.getConfig().set("worlds." + key + ".center.x", x);
        plugin.getConfig().set("worlds." + key + ".center.y", y);
        plugin.getConfig().set("worlds." + key + ".center.z", z);
        plugin.saveConfig();
    }

    public void setWorldRadius(String key, double radius) {
        plugin.getConfig().set("worlds." + key + ".radius", radius);
        plugin.saveConfig();
    }

    public void setWorldCooldown(String key, long cooldown) {
        plugin.getConfig().set("worlds." + key + ".cooldown", cooldown);
        plugin.saveConfig();
    }

    public void setWorldPrice(String key, double price) {
        plugin.getConfig().set("worlds." + key + ".price", price);
        plugin.saveConfig();
    }

    public void setWarmupSeconds(long seconds) {
        plugin.getConfig().set("settings.warmup-seconds", seconds);
        plugin.saveConfig();
    }

    public int getMenuSize() {
        return menuConfig.getInt("menu.size", 27);
    }

    public String getMenuTitle() {
        return menuConfig.getString("menu.title", "RTP Worlds");
    }

    public Map<Integer, String> getMenuLayout() {
        ConfigurationSection section = menuConfig.getConfigurationSection("menu.layout");
        if (section == null) {
            return Collections.emptyMap();
        }
        Map<Integer, String> layout = new HashMap<>();
        for (String key : section.getKeys(false)) {
            try {
                int slot = Integer.parseInt(key);
                layout.put(slot, section.getString(key, ""));
            } catch (NumberFormatException e) {
                MessageUtil.logWarning(plugin, "Invalid menu slot: " + key);
            }
        }
        return layout;
    }

    public boolean isWorldLoaded(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
}
