package com.ignitedev.base;

import com.ignitedev.IgniteChannelStats;
import com.ignitedev.config.ConfigManager;
import java.util.List;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public final class TrackerScheduler {

  private final IgniteChannelStats plugin;
  private final TrackerRegistry registry;
  private final TrackerExecutor executor;
  private final ConfigManager configManager;
  private final Logger logger;

  private BukkitTask schedulerTask;

  public static TrackerScheduler create(
      IgniteChannelStats plugin, JDA jda, TrackerRegistry registry, ConfigManager configManager) {
    Logger logger = plugin.getLogger();
    TrackerExecutor executor = new TrackerExecutor(jda, logger);
    return new TrackerScheduler(plugin, registry, executor, configManager, logger);
  }

  public void start() {
    int updateInterval = configManager.getUpdateIntervalSeconds();
    List<String> guildIds = configManager.getGuildIds();
    long intervalTicks = updateInterval * 20L;

    schedulerTask =
        plugin
            .getServer()
            .getScheduler()
            .runTaskTimerAsynchronously(
                plugin, () -> executeAllTrackers(guildIds), 0L, intervalTicks);

    logger.info(
        "Tracker scheduler started with "
            + updateInterval
            + " seconds interval for "
            + guildIds.size()
            + " guild(s)");
  }

  public void stop() {
    if (schedulerTask != null && !schedulerTask.isCancelled()) {
      schedulerTask.cancel();
      logger.info("Tracker scheduler stopped");
    }
  }

  public void restart() {
    stop();
    start();
  }

  public void refreshNow() {
    executeAllTrackers(configManager.getGuildIds());
  }

  private void executeAllTrackers(List<String> guildIds) {
    for (String guildId : guildIds) {
      registry
          .getAll()
          .forEach(
              tracker -> {
                try {
                  executor.execute(tracker, guildId);
                } catch (Exception exception) {
                  logger.warning(
                      "Error executing tracker '"
                          + tracker.getId()
                          + "' for guild "
                          + guildId
                          + ": "
                          + exception.getMessage());
                }
              });
    }
  }
}
