package com.ignitedev;

import com.ignitedev.bot.DiscordBot;
import com.ignitedev.config.ConfigManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class IgniteChannelStats extends JavaPlugin {

  @Getter private static IgniteChannelStats instance;

  @Getter private ConfigManager configManager;

  @Getter private DiscordBot discordBot;

  @Override
  public void onEnable() {
    instance = this;

    getLogger().info("Starting IgniteChannelStats...");

    this.configManager = new ConfigManager(this);
    this.configManager.loadConfig();

    this.discordBot = new DiscordBot(this, configManager);
    this.discordBot.start();

    getLogger().info("IgniteChannelStats enabled successfully!");
  }

  @Override
  public void onDisable() {
    if (discordBot != null) {
      discordBot.shutdown();
    }
    getLogger().info("IgniteChannelStats disabled.");
  }
}
