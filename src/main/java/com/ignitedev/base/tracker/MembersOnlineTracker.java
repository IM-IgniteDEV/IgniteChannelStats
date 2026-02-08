package com.ignitedev.base.tracker;

import java.util.Set;

import com.ignitedev.base.TrackerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Getter
@SuperBuilder
public final class MembersOnlineTracker extends AbstractTracker {

  private static final Set<OnlineStatus> ONLINE_STATUSES =
      Set.of(OnlineStatus.ONLINE, OnlineStatus.IDLE, OnlineStatus.DO_NOT_DISTURB);

  @Override
  public TrackerType getType() {
    return TrackerType.MEMBERS_ONLINE;
  }

  @Override
  public int calculateCount(Guild guild) {
    return (int)
        guild.getMembers().stream()
            .filter(this::isOnline)
            .filter(member -> !member.getUser().isBot())
            .count();
  }

  private boolean isOnline(Member member) {
    return ONLINE_STATUSES.contains(member.getOnlineStatus());
  }
}
