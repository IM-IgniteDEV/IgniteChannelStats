package com.ignitedev.bot.listener;

import com.ignitedev.base.MatchMode;
import com.ignitedev.base.tracker.BotCountTracker;
import com.ignitedev.base.tracker.ChannelTracker;
import com.ignitedev.base.tracker.MemberCountTracker;
import com.ignitedev.base.tracker.MembersOnlineTracker;
import com.ignitedev.base.tracker.RoleMembersTracker;
import com.ignitedev.base.tracker.StatisticsTracker;
import com.ignitedev.bot.DiscordBot;
import com.ignitedev.config.ConfigManager;
import java.awt.Color;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@RequiredArgsConstructor
public final class SlashCommandListener extends ListenerAdapter {

  private final DiscordBot bot;
  private final ConfigManager configManager;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("tracker")) {
      return;
    }

    if (!hasAdminPermission(event)) {
      event.reply("You don't have permission to use this command.").setEphemeral(true).queue();
      return;
    }

    String subcommand = event.getSubcommandName();
    if (subcommand == null) {
      return;
    }

    event
        .deferReply(true)
        .queue(
            hook -> {
              switch (subcommand) {
                case "channel" -> handleAddChannel(event, hook);
                case "members" -> handleAddMembers(event, hook);
                case "online" -> handleAddOnline(event, hook);
                case "role" -> handleAddRole(event, hook);
                case "bots" -> handleAddBots(event, hook);
                case "remove" -> handleRemove(event, hook);
                case "list" -> handleList(hook);
                case "refresh" -> handleRefresh(hook);
              }
            });
  }

  // ── add commands ─────────────────────────────────────────────────────────

  private void handleAddChannel(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);
    GuildChannelUnion targetChannel = event.getOption("target", OptionMapping::getAsChannel);
    String pattern = event.getOption("pattern", OptionMapping::getAsString);
    String modeString = event.getOption("mode", OptionMapping::getAsString);
    String format = event.getOption("format", OptionMapping::getAsString);

    if (id == null
        || targetChannel == null
        || pattern == null
        || modeString == null
        || format == null) {
      hook.sendMessage("Missing required options.").queue();
      return;
    }

    if (!validateTrackerCreation(id, targetChannel, hook)) {
      return;
    }

    ChannelTracker tracker =
        ChannelTracker.builder()
            .id(id)
            .targetChannelId(targetChannel.getId())
            .pattern(pattern)
            .matchMode(MatchMode.valueOf(modeString))
            .displayFormat(format)
            .build();

    registerAndSaveTracker(tracker, hook, "Channel tracker");
  }

  private void handleAddMembers(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);
    GuildChannelUnion targetChannel = event.getOption("target", OptionMapping::getAsChannel);
    String format = event.getOption("format", OptionMapping::getAsString);

    if (id == null || targetChannel == null || format == null) {
      hook.sendMessage("Missing required options.").queue();
      return;
    }

    if (!validateTrackerCreation(id, targetChannel, hook)) {
      return;
    }

    MemberCountTracker tracker =
        MemberCountTracker.builder()
            .id(id)
            .targetChannelId(targetChannel.getId())
            .displayFormat(format)
            .build();

    registerAndSaveTracker(tracker, hook, "Member count tracker");
  }

  private void handleAddOnline(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);
    GuildChannelUnion targetChannel = event.getOption("target", OptionMapping::getAsChannel);
    String format = event.getOption("format", OptionMapping::getAsString);

    if (id == null || targetChannel == null || format == null) {
      hook.sendMessage("Missing required options.").queue();
      return;
    }

    if (!validateTrackerCreation(id, targetChannel, hook)) {
      return;
    }

    MembersOnlineTracker tracker =
        MembersOnlineTracker.builder()
            .id(id)
            .targetChannelId(targetChannel.getId())
            .displayFormat(format)
            .build();

    registerAndSaveTracker(tracker, hook, "Members online tracker");
  }

  private void handleAddRole(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);
    GuildChannelUnion targetChannel = event.getOption("target", OptionMapping::getAsChannel);
    Role role = event.getOption("role", OptionMapping::getAsRole);
    String format = event.getOption("format", OptionMapping::getAsString);

    if (id == null || targetChannel == null || role == null || format == null) {
      hook.sendMessage("Missing required options.").queue();
      return;
    }

    if (!validateTrackerCreation(id, targetChannel, hook)) {
      return;
    }

    RoleMembersTracker tracker =
        RoleMembersTracker.builder()
            .id(id)
            .targetChannelId(targetChannel.getId())
            .roleId(role.getId())
            .displayFormat(format)
            .build();

    registerAndSaveTracker(tracker, hook, "Role members tracker");
  }

  private void handleAddBots(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);
    GuildChannelUnion targetChannel = event.getOption("target", OptionMapping::getAsChannel);
    String format = event.getOption("format", OptionMapping::getAsString);

    if (id == null || targetChannel == null || format == null) {
      hook.sendMessage("Missing required options.").queue();
      return;
    }

    if (!validateTrackerCreation(id, targetChannel, hook)) {
      return;
    }

    BotCountTracker tracker =
        BotCountTracker.builder()
            .id(id)
            .targetChannelId(targetChannel.getId())
            .displayFormat(format)
            .build();

    registerAndSaveTracker(tracker, hook, "Bot count tracker");
  }

  // ── management commands ──────────────────────────────────────────────────

  private void handleRemove(SlashCommandInteractionEvent event, InteractionHook hook) {
    String id = event.getOption("id", OptionMapping::getAsString);

    if (id == null) {
      hook.sendMessage("Missing tracker ID.").queue();
      return;
    }

    if (!bot.getTrackerRegistry().exists(id)) {
      hook.sendMessage("No tracker found with ID `" + id + "`.").queue();
      return;
    }

    bot.getTrackerRegistry().unregister(id);
    configManager.removeTracker(id);

    hook.sendMessage("Tracker `" + id + "` removed successfully!").queue();
  }

  private void handleList(InteractionHook hook) {
    Collection<StatisticsTracker> trackers = bot.getTrackerRegistry().getAll();

    if (trackers.isEmpty()) {
      hook.sendMessage("No trackers configured.").queue();
      return;
    }

    EmbedBuilder embed =
        new EmbedBuilder()
            .setTitle("Channel Trackers")
            .setColor(Color.CYAN)
            .setFooter("Total trackers: " + trackers.size());

    for (StatisticsTracker tracker : trackers) {
      String fieldValue = buildTrackerFieldValue(tracker);
      embed.addField(
          tracker.getId() + " [" + tracker.getType().getConfigKey() + "]", fieldValue, false);
    }

    hook.sendMessageEmbeds(embed.build()).queue();
  }

  private void handleRefresh(InteractionHook hook) {
    if (bot.getTrackerScheduler() == null) {
      hook.sendMessage("Tracker scheduler is not running.").queue();
      return;
    }

    bot.getTrackerScheduler().refreshNow();

    int trackerCount = bot.getTrackerRegistry().size();
    int guildCount = configManager.getGuildIds().size();

    hook.sendMessage("Refreshed " + trackerCount + " tracker(s) for " + guildCount + " guild(s).")
        .queue();
  }

  // ── helpers ──────────────────────────────────────────────────────────────

  private boolean validateTrackerCreation(
      String id, GuildChannelUnion targetChannel, InteractionHook hook) {
    if (targetChannel.getType() != ChannelType.VOICE) {
      hook.sendMessage("Target channel must be a voice channel.").queue();
      return false;
    }

    if (bot.getTrackerRegistry().exists(id)) {
      hook.sendMessage("A tracker with ID `" + id + "` already exists.").queue();
      return false;
    }

    return true;
  }

  private void registerAndSaveTracker(
      StatisticsTracker tracker, InteractionHook hook, String trackerTypeName) {
    bot.getTrackerRegistry().register(tracker);
    configManager.addTracker(tracker);

    String details = buildTrackerFieldValue(tracker);
    hook.sendMessage(
            trackerTypeName + " `" + tracker.getId() + "` created successfully!\n" + details)
        .queue();
  }

  private String buildTrackerFieldValue(StatisticsTracker tracker) {
    StringBuilder builder = new StringBuilder();
    builder.append("**Target:** <#").append(tracker.getTargetChannelId()).append(">\n");
    builder.append("**Format:** `").append(tracker.getDisplayFormat()).append("`");

    if (tracker instanceof ChannelTracker channelTracker) {
      builder.append("\n**Pattern:** `").append(channelTracker.getPattern()).append("`");
      builder.append("\n**Mode:** ").append(channelTracker.getMatchMode());
    } else if (tracker instanceof RoleMembersTracker roleMembersTracker) {
      builder.append("\n**Role:** <@&").append(roleMembersTracker.getRoleId()).append(">");
    }

    return builder.toString();
  }

  private boolean hasAdminPermission(SlashCommandInteractionEvent event) {
    return event.getMember() != null && event.getMember().hasPermission(Permission.ADMINISTRATOR);
  }
}
