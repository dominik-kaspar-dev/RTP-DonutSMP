package dev.donutsmp.rtp;

import dev.donutsmp.rtp.commands.RtpCommand;
import dev.donutsmp.rtp.config.ConfigManager;
import dev.donutsmp.rtp.menu.MenuClickListener;
import dev.donutsmp.rtp.menu.MenuManager;
import dev.donutsmp.rtp.rtp.RtpService;
import dev.donutsmp.rtp.util.MessageUtil;
import org.bstats.bukkit.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DonutRtpPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private MenuManager menuManager;
    private RtpService rtpService;
    private Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.load();
        menuManager = new MenuManager(this, configManager);
        rtpService = new RtpService(this, configManager);
        setupEconomy();
        new Metrics(this, 31275);

        RtpCommand command = new RtpCommand(this, configManager, menuManager, rtpService);
        if (getCommand("rtp") != null) {
            getCommand("rtp").setExecutor(command);
            getCommand("rtp").setTabCompleter(command);
        }

        Bukkit.getPluginManager().registerEvents(new MenuClickListener(menuManager, rtpService, configManager), this);
        MessageUtil.logInfo(this, "RTP-DonutSMP enabled.");
    }

    @Override
    public void onDisable() {
        MessageUtil.logInfo(this, "RTP-DonutSMP disabled.");
    }

    public Economy getEconomy() {
        return economy;
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            economy = null;
            return;
        }
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            economy = provider.getProvider();
        }
    }
}
