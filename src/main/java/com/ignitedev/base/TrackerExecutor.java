package com.ignitedev.base;

import java.util.Optional;
import java.util.logging.Logger;

import com.ignitedev.base.tracker.StatisticsTracker;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

@RequiredArgsConstructor
public final class TrackerExecutor {

  private final JDA jda;
  private final Logger logger;

  public void execute(StatisticsTracker tracker, String guildId) {
    findGuild(guildId)
        .ifPresentOrElse(
            guild -> processTracker(guild, tracker),
            () -> logger.warning("Guild not found: " + guildId));
  }

  private void processTracker(Guild guild, StatisticsTracker tracker) {
    updateTargetChannel(guild, tracker, tracker.calculateCount(guild));
  }

  private void updateTargetChannel(Guild guild, StatisticsTracker tracker, int count) {
    findTargetChannel(guild, tracker.getTargetChannelId())
        .ifPresentOrElse(
            channel -> {
              String newName = tracker.formatDisplay(count);
              if (!channel.getName().equals(newName)) {
                channel
                    .getManager()
                    .setName(newName)
                    .queue(
                        success ->
                            logger.fine(
                                "Updated channel '" + tracker.getId() + "' to '" + newName + "'"),
                        error ->
                            logger.warning(
                                "Failed to update channel '"
                                    + tracker.getId()
                                    + "': "
                                    + error.getMessage()));
              }
            },
            () -> {
              GuildChannel raw = guild.getGuildChannelById(tracker.getTargetChannelId());
              if (raw != null) {
                logger.warning(
                    "Channel exists but is unsupported type "
                        + raw.getType()
                        + " for tracker '"
                        + tracker.getId()
                        + "': "
                        + tracker.getTargetChannelId());
              } else {
                logger.warning(
                    "Target channel not found for tracker '"
                        + tracker.getId()
                        + "': "
                        + tracker.getTargetChannelId()
                        + " (guild has "
                        + guild.getChannels().size()
                        + " channels cached, "
                        + guild.getVoiceChannels().size()
                        + " voice)");
              }
            });
  }

  private Optional<Guild> findGuild(String guildId) {
    return Optional.ofNullable(jda.getGuildById(guildId));
  }

  private Optional<StandardGuildChannel> findTargetChannel(Guild guild, String channelId) {
    GuildChannel channel = guild.getGuildChannelById(channelId);
    if (channel instanceof StandardGuildChannel standard) {
      return Optional.of(standard);
    }
    return Optional.empty();
  }
}
