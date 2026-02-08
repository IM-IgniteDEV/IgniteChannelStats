package com.ignitedev.base.tracker;

import com.ignitedev.base.TrackerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Guild;

@Getter
@SuperBuilder
public final class BotCountTracker extends AbstractTracker {

  @Override
  public TrackerType getType() {
    return TrackerType.BOT_COUNT;
  }

  @Override
  public int calculateCount(Guild guild) {
    return (int)
        guild.getMembers().stream().filter(member -> member.getUser().isBot()).count();
  }
}
