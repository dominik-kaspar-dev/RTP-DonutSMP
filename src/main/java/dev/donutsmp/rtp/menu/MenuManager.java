package dev.donutsmp.rtp.menu;

import dev.donutsmp.rtp.DonutRtpPlugin;
import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.config.WorldConfig;
import dev.donutsmp.rtp.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private final DonutRtpPlugin plugin;
    private final ConfigManager configManager;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    public MenuManager(DonutRtpPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public Inventory createMenu() {
        int size = normalizeSize(configManager.getMenuSize());
        Component title = serializer.deserialize(configManager.getMenuTitle());
        Inventory inventory = Bukkit.createInventory(new RtpMenuHolder(), size, title);
        Map<Integer, String> layout = configManager.getMenuLayout();
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {
            int slot = entry.getKey();
            String worldKey = entry.getValue();
            if (slot < 0 || slot >= size) {
                continue;
            }
            ItemStack item = buildWorldItem(worldKey);
            inventory.setItem(slot, item);
        }
        return inventory;
    }

    public Map<Integer, String> getLayout() {
        return configManager.getMenuLayout();
    }

    private ItemStack buildWorldItem(String worldKey) {
        WorldConfig worldConfig = configManager.getWorldConfig(worldKey);
        if (worldConfig == null || !worldConfig.isEnabled()) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.displayName(serializer.deserialize("&cUnavailable"));
            barrier.setItemMeta(meta);
            return barrier;
        }
        ItemStack item = new ItemStack(worldConfig.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(serializer.deserialize(worldConfig.getDisplayName()));
        List<String> loreLines = new ArrayList<>();
        for (String line : worldConfig.getLore()) {
            loreLines.add(MessageUtil.replacePlaceholders(line, worldConfig));
        }
        if (!loreLines.isEmpty()) {
            meta.lore(MessageUtil.toComponentList(loreLines));
        }
        item.setItemMeta(meta);
        return item;
    }

    private int normalizeSize(int size) {
        int clamped = Math.max(9, Math.min(size, 54));
        int remainder = clamped % 9;
        if (remainder == 0) {
            return clamped;
        }
        return clamped + (9 - remainder);
    }
}
