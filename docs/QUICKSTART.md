# Quick Start Guide

---

## Prerequisites

- Minecraft Server running **Spigot/Paper 1.21+**
- **Java 21** or higher
- A **Discord Bot**

---

## Step 1: Create a Discord Bot

If you already have a bot, skip to Step 2.

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Click **New Application**
3. Enter a name (e.g., "Server Stats Bot")
4. Go to **Bot** → Click **Add Bot**
5. Under **Privileged Gateway Intents**, enable:
    - ✅ PRESENCE INTENT
    - ✅ SERVER MEMBERS INTENT
    - ✅ MESSAGE CONTENT INTENT
6. Click **Reset Token** and copy your token

---

## Step 2: Invite the Bot

1. Go to **OAuth2** → **URL Generator**
2. Select scopes:
    - ✅ `bot`
    - ✅ `applications.commands`
3. Select permissions:
    - ✅ `Manage Channels`
    - ✅ `View Channels`
4. Copy the generated URL
5. Open the URL and add the bot to your server

---

## Step 3: Install the Plugin

1. Download `IgniteChannelStats-X.X.X.jar`
2. Place it in your server's `plugins/` folder
3. Start (or restart) your server
4. Stop the server

---

## Step 4: Configure the Plugin

Open `plugins/IgniteChannelStats/config.yml`:

```yaml
discord:
  token: "YOUR_BOT_TOKEN_HERE"  # <-- Paste your bot token here
  guild-ids:
    - "YOUR_GUILD_ID"           # <-- Paste your server ID here

settings:
  update-interval-seconds: 60

trackers:
  # Your first tracker example:
  all-members:
    type: member-count
    target-channel-id: "VOICE_CHANNEL_ID"  # <-- Paste voice channel ID
    display-format: "Members: {count}"
```

### Getting IDs

**Guild ID:**

1. Enable Developer Mode (Discord Settings → Advanced → Developer Mode)
2. Right-click your server name → Copy Server ID

**Voice Channel ID:**

1. Right-click the voice channel → Copy Channel ID

---

## Step 5: Create a Stats Voice Channel

1. Create a new voice channel in your Discord
2. Name it anything (e.g., "loading...")
3. Set user limit to `0`
4. Copy the channel ID
5. Add it to your config as `target-channel-id`

---

## Step 6: Start the Server

1. Start your Minecraft server
2. Check the console for:
   ```
   [IgniteChannelStats] Discord bot connected successfully as YourBotName
   ```
3. Your voice channel should update with the count!

---

## Example Configuration

Here's a complete working configuration:

```yaml
discord:
  token: "MTIzNDU2Nzg5MDEyMzQ1Njc4OQ.XXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
  guild-ids:
    - "123456789012345678"

settings:
  update-interval-seconds: 60

trackers:
  total-members:
    type: member-count
    target-channel-id: "111111111111111111"
    display-format: "👥 Members: {count}"

  online-now:
    type: members-online
    target-channel-id: "222222222222222222"
    display-format: "🟢 Online: {count}"

  open-tickets:
    type: channel
    target-channel-id: "333333333333333333"
    pattern: "ticket-"
    match-mode: STARTS_WITH
    display-format: "🎫 Tickets: {count}"
```

---

## What's Next?

- Add more trackers via config or Discord commands
- Read the [Tracker Types Reference](TRACKERS.md) for all options
- Check [Troubleshooting](TROUBLESHOOTING.md) if something isn't working

---

## Using Discord Commands

Instead of editing the config, you can add trackers directly from Discord:

```
/tracker members id:total-members target:#stats-members format:Members: {count}
```

```
/tracker online id:online-now target:#stats-online format:Online: {count}
```

```
/tracker channel id:tickets target:#stats-tickets pattern:ticket- mode:Starts With format:Tickets: {count}
```

```
/tracker bots id:bot-count target:#stats-bots format:Bots: {count}
```

```
/tracker role id:staff-count target:#stats-staff role:@Staff format:Staff: {count}
```

> You need **Administrator** permission in Discord to use these commands.

---