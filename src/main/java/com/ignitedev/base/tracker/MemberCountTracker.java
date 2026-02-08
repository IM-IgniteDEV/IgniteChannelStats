package com.ignitedev.base.tracker;

import com.ignitedev.base.TrackerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Guild;

@Getter
@SuperBuilder
public final class MemberCountTracker extends AbstractTracker {

  @Override
  public TrackerType getType() {
    return TrackerType.MEMBER_COUNT;
  }

  @Override
  public int calculateCount(Guild guild) {
    return guild.getMembers().size();
  }
}
