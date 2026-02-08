package com.ignitedev.base.tracker;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractTracker implements StatisticsTracker {

  protected final String id;
  protected final String targetChannelId;
  protected final String displayFormat;
}
