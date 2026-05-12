package dev.donutsmp.rtp.rtp;

import dev.donutsmp.rtp.DonutRtpPlugin;
import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.config.WorldConfig;
import dev.donutsmp.rtp.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RtpService {
    private final DonutRtpPlugin plugin;
    private final ConfigManager configManager;
    private final Random random = new Random();
    private final Map<UUID, Long> lastUse = new ConcurrentHashMap<>();
    private final Map<UUID, WarmupTask> warmups = new ConcurrentHashMap<>();

    public RtpService(DonutRtpPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void teleport(Player player, String worldKey) {
        if (warmups.containsKey(player.getUniqueId())) {
            return;
        }

        long warmupSeconds = configManager.getWarmupSeconds();
        if (warmupSeconds > 0) {
            if (!canStartTeleport(player, worldKey)) {
                return;
            }
            startWarmup(player, worldKey, warmupSeconds);
            return;
        }

        performTeleport(player, worldKey);
    }

    private boolean canStartTeleport(Player player, String worldKey) {
        WorldConfig worldConfig = configManager.getWorldConfig(worldKey);
        if (worldConfig == null) {
            MessageUtil.send(player, configManager.getMessage("world-not-found"), configManager);
            return false;
        }
        if (!worldConfig.isEnabled()) {
            MessageUtil.send(player, configManager.getMessage("world-disabled"), configManager);
            return false;
        }
        World world = Bukkit.getWorld(worldKey);
        if (world == null) {
            MessageUtil.send(player, configManager.getMessage("world-not-found"), configManager);
            return false;
        }

        if (configManager.isGlobalCooldown()) {
            long remaining = getCooldownRemaining(player.getUniqueId(), worldConfig.getCooldown());
            if (remaining > 0) {
                MessageUtil.send(player, configManager.getMessage("cooldown").replace("{seconds}", String.valueOf(remaining)), configManager);
                return false;
            }
        }

        double price = worldConfig.getPrice();
        Economy economy = plugin.getEconomy();
        if (price > 0.0) {
            if (economy == null) {
                MessageUtil.send(player, configManager.getMessage("economy-missing"), configManager);
                return false;
            }
            if (!economy.has(player, price)) {
                MessageUtil.send(player, configManager.getMessage("not-enough-money").replace("{price}", String.valueOf(price)), configManager);
                return false;
            }
        }

        return true;
    }

    private void startWarmup(Player player, String worldKey, long seconds) {
        WarmupTask task = new WarmupTask(player.getUniqueId(), worldKey, player.getLocation().clone(), seconds);
        warmups.put(player.getUniqueId(), task);
        task.runTaskTimer(plugin, 0L, 20L);
    }

    private void performTeleport(Player player, String worldKey) {
        WorldConfig worldConfig = configManager.getWorldConfig(worldKey);
        if (worldConfig == null) {
            MessageUtil.send(player, configManager.getMessage("world-not-found"), configManager);
            return;
        }
        if (!worldConfig.isEnabled()) {
            MessageUtil.send(player, configManager.getMessage("world-disabled"), configManager);
            return;
        }
        World world = Bukkit.getWorld(worldKey);
        if (world == null) {
            MessageUtil.send(player, configManager.getMessage("world-not-found"), configManager);
            return;
        }

        if (configManager.isGlobalCooldown()) {
            long remaining = getCooldownRemaining(player.getUniqueId(), worldConfig.getCooldown());
            if (remaining > 0) {
                MessageUtil.send(player, configManager.getMessage("cooldown").replace("{seconds}", String.valueOf(remaining)), configManager);
                return;
            }
        }

        double price = worldConfig.getPrice();
        Economy economy = plugin.getEconomy();
        if (price > 0.0) {
            if (economy == null) {
                MessageUtil.send(player, configManager.getMessage("economy-missing"), configManager);
                return;
            }
            if (!economy.has(player, price)) {
                MessageUtil.send(player, configManager.getMessage("not-enough-money").replace("{price}", String.valueOf(price)), configManager);
                return;
            }
        }

        MessageUtil.send(player, configManager.getMessage("teleporting"), configManager);
        Location target = findSafeLocation(world, worldConfig);
        if (target == null) {
            MessageUtil.send(player, configManager.getMessage("teleport-failed"), configManager);
            return;
        }

        if (price > 0.0 && economy != null) {
            economy.withdrawPlayer(player, price);
        }

        player.teleportAsync(target).thenRun(() -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            lastUse.put(player.getUniqueId(), System.currentTimeMillis());
            String message = configManager.getMessage("teleport-success").replace("{world}", worldConfig.getDisplayName());
            MessageUtil.send(player, message, configManager);
        }));
    }

    private long getCooldownRemaining(UUID uuid, long cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return 0;
        }
        long last = lastUse.getOrDefault(uuid, 0L);
        long elapsed = (System.currentTimeMillis() - last) / 1000L;
        if (elapsed >= cooldownSeconds) {
            return 0;
        }
        return cooldownSeconds - elapsed;
    }

    private Location findSafeLocation(World world, WorldConfig config) {
        int maxTries = configManager.getMaxTries();
        for (int i = 0; i < maxTries; i++) {
            Location candidate = randomLocation(world, config);
            Block ground = world.getBlockAt(candidate.getBlockX(), candidate.getBlockY() - 1, candidate.getBlockZ());
            Block feet = world.getBlockAt(candidate);
            Block head = world.getBlockAt(candidate.getBlockX(), candidate.getBlockY() + 1, candidate.getBlockZ());
            if (isSafeGround(ground) && isSafeAir(feet) && isSafeAir(head)) {
                return candidate;
            }
        }
        return null;
    }

    private Location randomLocation(World world, WorldConfig config) {
        double angle = random.nextDouble() * Math.PI * 2.0;
        double distance = Math.sqrt(random.nextDouble()) * config.getRadius();
        double x = config.getCenterX() + distance * Math.cos(angle);
        double z = config.getCenterZ() + distance * Math.sin(angle);
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        int y = world.getHighestBlockYAt(blockX, blockZ) + 1;
        return new Location(world, blockX + 0.5, y, blockZ + 0.5);
    }

    private boolean isSafeGround(Block block) {
        Material type = block.getType();
        if (type == Material.LAVA || type == Material.WATER) {
            return false;
        }
        if (Tag.LEAVES.isTagged(type)) {
            return false;
        }
        return type.isSolid();
    }

    private boolean isSafeAir(Block block) {
        return block.getType().isAir();
    }

    private boolean hasMoved(Location origin, Location current) {
        return origin.getBlockX() != current.getBlockX()
                || origin.getBlockY() != current.getBlockY()
                || origin.getBlockZ() != current.getBlockZ();
    }

    private final class WarmupTask extends BukkitRunnable {
        private final UUID uuid;
        private final String worldKey;
        private final Location origin;
        private long remaining;

        private WarmupTask(UUID uuid, String worldKey, Location origin, long remaining) {
            this.uuid = uuid;
            this.worldKey = worldKey;
            this.origin = origin;
            this.remaining = remaining;
        }

        @Override
        public void run() {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                warmups.remove(uuid);
                cancel();
                return;
            }
            if (hasMoved(origin, player.getLocation())) {
                warmups.remove(uuid);
                cancel();
                MessageUtil.sendActionBar(player, configManager.getMessage("warmup-cancelled"));
                return;
            }
            if (remaining <= 0) {
                warmups.remove(uuid);
                cancel();
                performTeleport(player, worldKey);
                return;
            }
            String bar = configManager.getMessage("warmup-actionbar")
                    .replace("{seconds}", String.valueOf(remaining));
            MessageUtil.sendActionBar(player, bar);
            remaining--;
        }
    }
}
