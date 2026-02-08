package com.ignitedev.base.tracker;

import com.ignitedev.base.TrackerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Getter
@SuperBuilder
public final class RoleMembersTracker extends AbstractTracker {

  private final String roleId;

  @Override
  public TrackerType getType() {
    return TrackerType.ROLE_MEMBERS;
  }

  @Override
  public int calculateCount(Guild guild) {
    Role role = guild.getRoleById(roleId);

    if (role == null) {
      return 0;
    }
    return guild.getMembersWithRoles(role).size();
  }
}
