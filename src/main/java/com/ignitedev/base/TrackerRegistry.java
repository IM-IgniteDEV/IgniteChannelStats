package com.ignitedev.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.ignitedev.base.tracker.StatisticsTracker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TrackerRegistry {

  private final Logger logger;
  private final Map<String, StatisticsTracker> trackers = new ConcurrentHashMap<>();

  public void register(StatisticsTracker tracker) {
    trackers.put(tracker.getId(), tracker);
    logger.info(
        "Registered tracker: " + tracker.getId() + " [" + tracker.getType().getConfigKey() + "]");
  }

  public void unregister(String trackerId) {
    trackers.remove(trackerId);
    logger.info("Unregistered tracker: " + trackerId);
  }

  public Optional<StatisticsTracker> findById(String trackerId) {
    return Optional.ofNullable(trackers.get(trackerId));
  }

  public Collection<StatisticsTracker> getAll() {
    return Collections.unmodifiableCollection(trackers.values());
  }

  public boolean exists(String trackerId) {
    return trackers.containsKey(trackerId);
  }

  public void clear() {
    trackers.clear();
  }

  public int size() {
    return trackers.size();
  }
}
