package com.ignitedev.bot;

import com.ignitedev.IgniteChannelStats;
import com.ignitedev.bot.listener.SlashCommandListener;
import com.ignitedev.config.ConfigManager;
import com.ignitedev.base.TrackerRegistry;
import com.ignitedev.base.TrackerScheduler;

import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public final class DiscordBot {

  private final IgniteChannelStats plugin;
  private final ConfigManager configManager;
  private final Logger logger;

  @Getter private JDA jda;
  @Getter private TrackerRegistry trackerRegistry;
  @Getter private TrackerScheduler trackerScheduler;

  public DiscordBot(IgniteChannelStats plugin, ConfigManager configManager) {
    this.plugin = plugin;
    this.configManager = configManager;
    this.logger = plugin.getLogger();
  }

  private static SubcommandData simpleTrackerSubcommand(String name, String description) {
    return new SubcommandData(name, description)
        .addOptions(idOption(), targetOption(), formatOption());
  }

  private static OptionData idOption() {
    return new OptionData(OptionType.STRING, "id", "Unique identifier for the tracker", true);
  }

  private static OptionData targetOption() {
    return new OptionData(
        OptionType.CHANNEL, "target", "Voice channel to update with the count", true);
  }

  // ── private ──────────────────────────────────────────────────────────────

  private static OptionData formatOption() {
    return new OptionData(
        OptionType.STRING, "format", "Display format (use {count} for the number)", true);
  }

  public void start() {
    try {
      trackerRegistry = new TrackerRegistry(logger);
      configManager.loadTrackersIntoRegistry(trackerRegistry);

      String token = configManager.getToken();

      if (token.equals("YOUR_BOT_TOKEN_HERE")) {
        logger.warning("Please configure your Discord bot token in config.yml!");
        return;
      }
      jda =
          JDABuilder.createDefault(token)
              .enableIntents(
                  GatewayIntent.GUILD_MEMBERS,
                  GatewayIntent.GUILD_PRESENCES,
                  GatewayIntent.GUILD_MESSAGES,
                  GatewayIntent.MESSAGE_CONTENT)
              .setMemberCachePolicy(MemberCachePolicy.ALL)
              .setChunkingFilter(ChunkingFilter.ALL)
              .enableCache(CacheFlag.ONLINE_STATUS)
              .addEventListeners(new SlashCommandListener(this, configManager))
              .build();

      jda.awaitReady();
      loadAllGuildMembers();
      registerSlashCommands();
      trackerScheduler = TrackerScheduler.create(plugin, jda, trackerRegistry, configManager);
      trackerScheduler.start();

      logger.info("Discord bot connected successfully as " + jda.getSelfUser().getName());
    } catch (InterruptedException exception) {
      logger.severe("Failed to start Discord bot: " + exception.getMessage());
      Thread.currentThread().interrupt();
    } catch (Exception exception) {
      logger.severe("Failed to start Discord bot: " + exception.getMessage());
    }
  }

  // ── option helpers (DRY) ─────────────────────────────────────────────────

  public void shutdown() {
    if (trackerScheduler != null) {
      trackerScheduler.stop();
    }
    if (jda != null) {
      jda.shutdown();
      logger.info("Discord bot disconnected");
    }
  }

  public boolean isConnected() {
    return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
  }

  private void loadAllGuildMembers() {
    configManager
        .getGuildIds()
        .forEach(
            guildId -> {
              Guild guild = jda.getGuildById(guildId);

              if (guild == null) {
                return;
              }
              try {
                List<Member> members = guild.loadMembers().get();
                logger.info(
                    "Loaded " + members.size() + " members for guild: " + guild.getName());
              } catch (Exception exception) {
                logger.warning(
                    "Failed to load members for guild " + guildId + ": " + exception.getMessage());
              }
            });
  }

  private void registerSlashCommands() {
    jda.updateCommands()
        .addCommands(
            Commands.slash("tracker", "Manage channel trackers")
                .setDefaultPermissions(
                    DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .addSubcommands(
                    new SubcommandData("channel", "Add a channel pattern tracker")
                        .addOptions(
                            idOption(),
                            targetOption(),
                            new OptionData(
                                OptionType.STRING,
                                "pattern",
                                "Pattern to match channel names",
                                true),
                            new OptionData(OptionType.STRING, "mode", "Match mode", true)
                                .addChoice("Starts With", "STARTS_WITH")
                                .addChoice("Contains", "CONTAINS"),
                            formatOption()),
                    simpleTrackerSubcommand("members", "Add a total member count tracker"),
                    simpleTrackerSubcommand("online", "Add an online members tracker"),
                    simpleTrackerSubcommand("bots", "Add a bot count tracker"),
                    new SubcommandData("role", "Add a role members tracker")
                        .addOptions(
                            idOption(),
                            targetOption(),
                            new OptionData(
                                OptionType.ROLE, "role", "Role to count members with", true),
                            formatOption()),
                    new SubcommandData("remove", "Remove an existing tracker")
                        .addOptions(
                            new OptionData(
                                OptionType.STRING,
                                "id",
                                "Identifier of the tracker to remove",
                                true)),
                    new SubcommandData("list", "List all configured trackers"),
                    new SubcommandData("refresh", "Manually refresh all trackers")))
        .queue(
            success -> logger.info("Discord slash commands registered successfully"),
            error -> logger.warning("Failed to register slash commands: " + error.getMessage()));
  }
}
