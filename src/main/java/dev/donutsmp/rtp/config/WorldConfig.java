package dev.donutsmp.rtp.config;

import org.bukkit.Material;

import java.util.List;

public class WorldConfig {
    private final String key;
    private final String displayName;
    private final Material icon;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final double radius;
    private final int maxY;
    private final long cooldown;
    private final double price;
    private final boolean enabled;
    private final List<String> lore;

    public WorldConfig(String key, String displayName, Material icon, double centerX, double centerY, double centerZ,
                       double radius, int maxY, long cooldown, double price, boolean enabled, List<String> lore) {
        this.key = key;
        this.displayName = displayName;
        this.icon = icon;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.maxY = maxY;
        this.cooldown = cooldown;
        this.price = price;
        this.enabled = enabled;
        this.lore = lore;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public double getRadius() {
        return radius;
    }

    public int getMaxY() {
        return maxY;
    }

    public long getCooldown() {
        return cooldown;
    }

    public double getPrice() {
        return price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getLore() {
        return lore;
    }
}
