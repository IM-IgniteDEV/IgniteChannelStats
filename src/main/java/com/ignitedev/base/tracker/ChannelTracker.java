package com.ignitedev.base.tracker;

import com.ignitedev.base.MatchMode;
import com.ignitedev.base.TrackerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@Getter
@SuperBuilder
public final class ChannelTracker extends AbstractTracker {

  private final String pattern;
  private final MatchMode matchMode;

  @Override
  public TrackerType getType() {
    return TrackerType.CHANNEL;
  }

  @Override
  public int calculateCount(Guild guild) {
    String lowerPattern = pattern.toLowerCase();

    return (int)
        guild.getChannels().stream()
            .filter(channel -> matchesPattern(channel, lowerPattern))
            .count();
  }

  private boolean matchesPattern(GuildChannel channel, String lowerPattern) {
    String channelName = channel.getName().toLowerCase();

    return switch (matchMode) {
      case STARTS_WITH -> channelName.startsWith(lowerPattern);
      case CONTAINS -> channelName.contains(lowerPattern);
    };
  }
}
