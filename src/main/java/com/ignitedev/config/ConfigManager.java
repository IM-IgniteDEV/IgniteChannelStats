package com.ignitedev.config;

import com.ignitedev.IgniteChannelStats;
import com.ignitedev.base.tracker.StatisticsTracker;
import com.ignitedev.base.TrackerFactory;
import com.ignitedev.base.TrackerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Loads, caches and persists the plugin configuration.
 *
 * <p>Tracker creation and serialization are delegated to {@link TrackerFactory}, keeping this class
 * focused on file I/O and general settings.
 */
public final class ConfigManager {

  private final IgniteChannelStats plugin;
  private final Logger logger;

  @Getter private String token;
  @Getter private List<String> guildIds;
  @Getter private int updateIntervalSeconds;

  public ConfigManager(IgniteChannelStats plugin) {
    this.plugin = plugin;
    this.logger = plugin.getLogger();
    this.guildIds = new ArrayList<>();
  }

  public void loadConfig() {
    plugin.saveDefaultConfig();
    plugin.reloadConfig();

    FileConfiguration config = plugin.getConfig();

    this.token = config.getString("discord.token", "YOUR_BOT_TOKEN_HERE");
    this.guildIds = config.getStringList("discord.guild-ids");
    this.updateIntervalSeconds = config.getInt("settings.update-interval-seconds", 60);

    logger.info("Configuration loaded successfully");
  }

  /** Reads every tracker section from the config file and populates the registry. */
  public void loadTrackersIntoRegistry(TrackerRegistry registry) {
    registry.clear();

    ConfigurationSection trackersSection = plugin.getConfig().getConfigurationSection("trackers");

    if (trackersSection == null) {
      return;
    }
    for (String id : trackersSection.getKeys(false)) {
      ConfigurationSection section = trackersSection.getConfigurationSection(id);
      if (section == null) {
        continue;
      }
      TrackerFactory.fromConfig(id, section, logger).ifPresent(registry::register);
    }
    logger.info("Loaded " + registry.size() + " tracker(s) from configuration");
  }

  public void addTracker(StatisticsTracker tracker) {
    TrackerFactory.saveToConfig(tracker, plugin.getConfig());
    plugin.saveConfig();
  }

  public void removeTracker(String trackerId) {
    plugin.getConfig().set("trackers." + trackerId, null);
    plugin.saveConfig();
  }
}
