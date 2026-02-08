package com.ignitedev.base;

import java.util.Optional;
import java.util.logging.Logger;

import com.ignitedev.base.tracker.StatisticsTracker;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

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
    findVoiceChannel(guild, tracker.getTargetChannelId())
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
            () ->
                logger.warning(
                    "Target channel not found for tracker '"
                        + tracker.getId()
                        + "': "
                        + tracker.getTargetChannelId()));
  }

  private Optional<Guild> findGuild(String guildId) {
    return Optional.ofNullable(jda.getGuildById(guildId));
  }

  private Optional<VoiceChannel> findVoiceChannel(Guild guild, String channelId) {
    return Optional.ofNullable(guild.getVoiceChannelById(channelId));
  }
}
