package com.ignitedev.base;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrackerType {
  CHANNEL("channel", "Tracks channels matching a pattern"),
  MEMBER_COUNT("member-count", "Tracks total member count"),
  MEMBERS_ONLINE("members-online", "Tracks online members count"),
  ROLE_MEMBERS("role-members", "Tracks members with a specific role"),
  BOT_COUNT("bot-count", "Tracks total bot count");

  private final String configKey;
  private final String description;

  public static Optional<TrackerType> fromConfigKey(String key) {
    for (TrackerType type : values()) {
      if (type.configKey.equalsIgnoreCase(key)) {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }
}
