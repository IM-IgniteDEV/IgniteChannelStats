package com.ignitedev.base;

import java.util.Optional;
import java.util.logging.Logger;

import com.ignitedev.base.tracker.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Centralizes creation and persistence of {@link StatisticsTracker} instances.
 *
 * <p>All tracker type-specific logic (extra fields, validation) lives here, keeping
 * {@link com.ignitedev.config.ConfigManager} free of tracker construction details.
 */
public final class TrackerFactory {

  private TrackerFactory() {}

  /**
   * Creates a tracker from a Bukkit configuration section.
   *
   * @return the tracker, or empty if the section is invalid
   */
  public static Optional<StatisticsTracker> fromConfig(
      String id, ConfigurationSection section, Logger logger) {

    String typeStr = section.getString("type", "channel");
    Optional<TrackerType> typeOpt = TrackerType.fromConfigKey(typeStr);

    if (typeOpt.isEmpty()) {
      logger.warning("Unknown tracker type '" + typeStr + "' for tracker '" + id + "'");
      return Optional.empty();
    }
    String targetChannelId = section.getString("target-channel-id");
    
    if (targetChannelId == null) {
      logger.warning("Tracker '" + id + "' is missing target-channel-id");
      return Optional.empty();
    }
    String displayFormat = section.getString("display-format", "Count: {count}");

    return switch (typeOpt.get()) {
      case CHANNEL -> buildChannelTracker(id, targetChannelId, displayFormat, section, logger);
      case MEMBER_COUNT ->
          Optional.of(
              MemberCountTracker.builder()
                  .id(id).targetChannelId(targetChannelId).displayFormat(displayFormat)
                  .build());
      case MEMBERS_ONLINE ->
          Optional.of(
              MembersOnlineTracker.builder()
                  .id(id).targetChannelId(targetChannelId).displayFormat(displayFormat)
                  .build());
      case BOT_COUNT ->
          Optional.of(
              BotCountTracker.builder()
                  .id(id).targetChannelId(targetChannelId).displayFormat(displayFormat)
                  .build());
      case ROLE_MEMBERS ->
          buildRoleMembersTracker(id, targetChannelId, displayFormat, section, logger);
    };
  }

  /**
   * Persists a tracker's configuration under the {@code trackers.<id>} path.
   */
  public static void saveToConfig(StatisticsTracker tracker, FileConfiguration config) {
    String basePath = "trackers." + tracker.getId();

    config.set(basePath + ".type", tracker.getType().getConfigKey());
    config.set(basePath + ".target-channel-id", tracker.getTargetChannelId());
    config.set(basePath + ".display-format", tracker.getDisplayFormat());

    if (tracker instanceof ChannelTracker ct) {
      config.set(basePath + ".pattern", ct.getPattern());
      config.set(basePath + ".match-mode", ct.getMatchMode().name());
    } else if (tracker instanceof RoleMembersTracker rt) {
      config.set(basePath + ".role-id", rt.getRoleId());
    }
  }

  private static Optional<StatisticsTracker> buildChannelTracker(
      String id,
      String targetChannelId,
      String displayFormat,
      ConfigurationSection section,
      Logger logger) {
    String pattern = section.getString("pattern");
    
    if (pattern == null) {
      logger.warning("Channel tracker '" + id + "' is missing pattern");
      return Optional.empty();
    }
    MatchMode matchMode = parseMatchMode(section.getString("match-mode", "STARTS_WITH"), logger, id);

    return Optional.of(
        ChannelTracker.builder()
            .id(id)
            .targetChannelId(targetChannelId)
            .displayFormat(displayFormat)
            .pattern(pattern)
            .matchMode(matchMode)
            .build());
  }

  private static Optional<StatisticsTracker> buildRoleMembersTracker(
      String id,
      String targetChannelId,
      String displayFormat,
      ConfigurationSection section,
      Logger logger) {
    String roleId = section.getString("role-id");
    
    if (roleId == null) {
      logger.warning("Role members tracker '" + id + "' is missing role-id");
      return Optional.empty();
    }
    return Optional.of(
        RoleMembersTracker.builder()
            .id(id)
            .targetChannelId(targetChannelId)
            .displayFormat(displayFormat)
            .roleId(roleId)
            .build());
  }

  private static MatchMode parseMatchMode(String value, Logger logger, String trackerId) {
    try {
      return MatchMode.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException argumentException) {
      logger.warning("Invalid match mode for tracker '" + trackerId + "': " + value);
      return MatchMode.STARTS_WITH;
    }
  }
}
