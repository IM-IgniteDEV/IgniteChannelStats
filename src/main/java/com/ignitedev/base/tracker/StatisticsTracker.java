package com.ignitedev.base.tracker;

import com.ignitedev.base.TrackerType;
import net.dv8tion.jda.api.entities.Guild;

public interface StatisticsTracker {

  String getId();

  String getTargetChannelId();

  String getDisplayFormat();

  TrackerType getType();

  int calculateCount(Guild guild);

  default String formatDisplay(int count) {
    return getDisplayFormat().replace("{count}", String.valueOf(count));
  }
}
