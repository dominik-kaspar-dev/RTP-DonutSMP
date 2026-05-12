package dev.donutsmp.rtp.menu;

import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.rtp.RtpService;
import dev.donutsmp.rtp.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public class MenuClickListener implements Listener {
    private final MenuManager menuManager;
    private final RtpService rtpService;
    private final ConfigManager configManager;

    public MenuClickListener(MenuManager menuManager, RtpService rtpService, ConfigManager configManager) {
        this.menuManager = menuManager;
        this.rtpService = rtpService;
        this.configManager = configManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof RtpMenuHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        Map<Integer, String> layout = menuManager.getLayout();
        if (!layout.containsKey(slot)) {
            return;
        }
        String worldKey = layout.get(slot);
        if (worldKey == null || worldKey.isBlank()) {
            return;
        }
        if (!player.hasPermission("rtpdonutsmp.use")) {
            MessageUtil.send(player, configManager.getMessage("no-permission"), configManager);
            return;
        }
        player.closeInventory();
        rtpService.teleport(player, worldKey);
    }
}
