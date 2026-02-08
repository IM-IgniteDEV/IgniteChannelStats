# Troubleshooting Guide

Common issues and their solutions for IgniteChannelStats.

---

## Member Count / Online Members Show 0 or Wrong Numbers

### Problem

The `member-count` and `members-online` trackers display `0` or incorrect counts.

### Cause

Discord requires **Privileged Gateway Intents** to be enabled for your bot to access member lists and presence
information.

### Solution

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Select your application
3. Navigate to **Bot** section
4. Scroll down to **Privileged Gateway Intents**
5. Enable the following intents:
    - ✅ **PRESENCE INTENT** - Required for online status tracking
    - ✅ **SERVER MEMBERS INTENT** - Required for member list access
    - ✅ **MESSAGE CONTENT INTENT** - Optional but recommended

6. Click **Save Changes**
7. Restart your Minecraft server

> ⚠️ **Important:** After enabling intents, you may need to re-invite your bot or wait a few minutes for changes to take
> effect.

---

## Bot Token Invalid or Not Working

### Problem

```
Please configure your Discord bot token in config.yml!
```

### Solution

1. Check that you've replaced `YOUR_BOT_TOKEN_HERE` with your actual token
2. Ensure there are no extra spaces or quotes around the token
3. If the token was recently reset, make sure you're using the new one

### Regenerating Your Token

1. Go to Discord Developer Portal → Your Application → Bot
2. Click **Reset Token**
3. Copy the new token
4. Update your `config.yml`
5. Restart the server

---

## Guild Not Found Error

### Problem

```
Guild not found: <guild-id>
```

### Causes & Solutions

**1. Incorrect Guild ID**

- Enable Developer Mode in Discord
- Right-click on your server name → Copy Server ID
- Update the config with the correct ID

**2. Bot Not in Server**

- Re-invite the bot to your server with proper permissions

**3. Bot Permissions Issue**

- Ensure the bot has basic access to the server

---

## Target Channel Not Found

### Problem

```
Target channel not found for tracker '<id>': <channel-id>
```

### Causes & Solutions

**1. Wrong Channel ID**

- Make sure you copied the correct voice channel ID
- Right-click the voice channel → Copy Channel ID

**2. Channel is Not a Voice Channel**

- Target channels must be **voice channels**
- Text channels cannot have their names updated by this method

**3. Channel in Different Guild**

- The channel must be in one of the configured guilds
- Check your `guild-ids` configuration

---

## Channel Names Not Updating

### Problem

Voice channel names don't change or update very slowly.

### Causes & Solutions

**1. Discord Rate Limits**

- Discord limits channel name updates to approximately 2 per 10 minutes per channel
- Increase your `update-interval-seconds` to at least 60

**2. Missing Permissions**

- Bot needs **Manage Channels** permission
- Check channel-specific permission overwrites

**3. No Changes Detected**

- The plugin only updates when the count changes
- If count stays the same, no update is sent

---

## Slash Commands Not Working

### Problem

Discord slash commands don't appear or give errors.

### Causes & Solutions

**1. Commands Not Registered**

- Wait up to 1 hour for global command registration
- Restart the Minecraft server

**2. Missing Permissions**

- You need **Administrator** permission to use tracker commands
- Check your Discord role permissions

**3. Bot Missing Scopes**

When inviting your bot, ensure these scopes are included:

- `bot`
- `applications.commands`

Re-invite your bot with the correct URL:

```
https://discord.com/api/oauth2/authorize?client_id=YOUR_CLIENT_ID&permissions=16&scope=bot%20applications.commands
```

---

## Interaction Failed Error

### Problem

```
This interaction failed
```

or

```
Unknown interaction
```

### Cause

The command took too long to respond (over 3 seconds).

### Solution

This is usually a one-time issue. The plugin uses deferred replies to prevent this. If it persists:

1. Check server performance
2. Restart the Minecraft server
3. Check for network issues

---

## Role Members Tracker Shows 0

### Problem

Role tracker shows `0` even though the role has members.

### Causes & Solutions

**1. Wrong Role ID**

- Right-click the role in Server Settings → Roles → Copy Role ID
- Update your config with the correct ID

**2. Role Has No Members**

- Verify members actually have this role assigned

**3. Bot Can't See Role**

- The bot's highest role must be above or equal to the tracked role
- Check role hierarchy in server settings

---

## Plugin Not Starting

### Problem

Plugin fails to enable or throws exceptions.

### Check List

1. **Java Version**: Requires Java 21+
2. **Server Version**: Requires Spigot/Paper 1.21+
3. **Dependencies**: No external dependencies required
4. **Config Syntax**: Check for YAML syntax errors

### Common Config Errors

**Wrong indentation:**

```yaml
# ❌ Wrong
trackers:
report-tickets:
  type: channel

# ✅ Correct
trackers:
  report-tickets:
    type: channel
```

**Missing quotes on IDs:**

```yaml
# ❌ Can cause issues with large numbers
target-channel-id: 123456789012345678

# ✅ Always quote Discord IDs
target-channel-id: "123456789012345678"
```

---

## Logs and Debugging

### Enable Verbose Logging

For detailed debugging, check your server logs for messages from `IgniteChannelStats`.

Log messages include:

- Tracker registration confirmations
- Update success/failure notifications
- Connection status
- Error details

### Log Locations

- **Paper/Spigot**: `logs/latest.log`
- **Console**: Real-time output

### What to Include in Bug Reports

1. Server version (output of `/version`)
2. Plugin version
3. Relevant log entries
4. Your config (remove sensitive data like tokens)
5. Steps to reproduce the issue

---

## Performance Issues

### High CPU/Memory Usage

**Causes:**

- Too many trackers
- Very low update interval
- Large server with many members

**Solutions:**

1. Increase `update-interval-seconds` to 120 or higher
2. Reduce the number of active trackers
3. Combine similar trackers where possible

### Recommended Limits

| Server Size          | Max Trackers | Min Interval |
|----------------------|--------------|--------------|
| Small (<100 members) | 10           | 60s          |
| Medium (100-1000)    | 8            | 90s          |
| Large (1000+)        | 5            | 120s         |

---

## Still Need Help?

If you've tried the solutions above and still have issues:

1. Double-check all configurations
2. Review the full error message in logs
3. Restart both Discord bot and Minecraft server
4. Open an issue on the project's GitHub repository with detailed information

