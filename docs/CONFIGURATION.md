# Configuration Guide

## Discord Settings

Configure your Discord bot connection settings.

```yaml
discord:
  # Your Discord bot token (from Discord Developer Portal)
  token: "YOUR_BOT_TOKEN_HERE"

  # Your Discord server (guild) IDs - supports multiple servers
  guild-ids:
    - "123456789012345678"
    - "987654321098765432"
```

### Getting Your Bot Token

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Select your application (or create a new one)
3. Navigate to **Bot** section
4. Click **Reset Token** and copy the token

> ⚠️ **Never share your bot token publicly!** Treat it like a password.

### Getting Guild IDs

1. Enable **Developer Mode** in Discord (User Settings → App Settings → Advanced → Developer Mode)
2. Right-click on your server name
3. Click **Copy Server ID**

---

## General Settings

```yaml
settings:
  # How often to update channel names (in seconds)
  # Note: Discord has rate limits, recommended minimum is 60 seconds
  update-interval-seconds: 60
```

### Update Interval Recommendations

| Number of Trackers | Recommended Interval |
|--------------------|----------------------|
| 1-3                | 60 seconds           |
| 4-6                | 90 seconds           |
| 7-10               | 120 seconds          |
| 10+                | 180+ seconds         |

> 💡 Discord rate limits channel name updates. If updates fail, try increasing the interval.

---

## Trackers Configuration

Trackers are defined as a map where each key is the unique tracker ID.

### Basic Structure

```yaml
trackers:
  tracker-id:
    type: <tracker-type>
    target-channel-id: "<voice-channel-id>"
    display-format: "Display Text: {count}"
    # Additional options depending on type...
```

### Common Properties

| Property            | Type   | Required | Description                              |
|---------------------|--------|----------|------------------------------------------|
| `type`              | string | Yes      | Tracker type (see below)                 |
| `target-channel-id` | string | Yes      | ID of the voice channel to update        |
| `display-format`    | string | Yes      | Format string with `{count}` placeholder |

---

## Tracker Types

### Channel Tracker

Counts channels matching a specific pattern.

```yaml
report-tickets:
  type: channel
  target-channel-id: "123456789012345678"
  pattern: "report-"
  match-mode: STARTS_WITH
  display-format: "Open Report Tickets: {count}"
```

**Additional Properties:**

| Property     | Type   | Required | Values                    | Description                            |
|--------------|--------|----------|---------------------------|----------------------------------------|
| `pattern`    | string | Yes      | Any text                  | Pattern to match against channel names |
| `match-mode` | string | Yes      | `STARTS_WITH`, `CONTAINS` | How to match the pattern               |

**Example Use Cases:**

- Count open tickets: pattern `ticket-` with `STARTS_WITH`
- Count all voice channels: pattern `voice` with `CONTAINS`
- Count support channels: pattern `support` with `CONTAINS`

---

### Member Count Tracker

Tracks the total number of members in the server.

```yaml
all-members:
  type: member-count
  target-channel-id: "123456789012345678"
  display-format: "All Members: {count}"
```

**Notes:**

- Includes all members (users and bots)
- Updates based on cached member list

---

### Members Online Tracker

Tracks the number of online members (excludes bots).

```yaml
members-online:
  type: members-online
  target-channel-id: "123456789012345678"
  display-format: "Members Online: {count}"
```

**Counted Statuses:**

- Online
- Idle
- Do Not Disturb

**Excluded:**

- Offline members
- Invisible members
- Bots

---

### Bot Count Tracker

Tracks the total number of bots in the server.

```yaml
bots:
  type: bot-count
  target-channel-id: "123456789012345678"
  display-format: "Bots: {count}"
```

---

### Role Members Tracker

Tracks the number of members with a specific role.

```yaml
staff-members:
  type: role-members
  target-channel-id: "123456789012345678"
  role-id: "123456789012345678"
  display-format: "Staff Members: {count}"
```

**Additional Properties:**

| Property  | Type   | Required | Description             |
|-----------|--------|----------|-------------------------|
| `role-id` | string | Yes      | ID of the role to count |

**Getting Role IDs:**

1. Enable Developer Mode in Discord
2. Go to Server Settings → Roles
3. Right-click on the role
4. Click **Copy Role ID**

---

## Complete Configuration Example

```yaml
# IgniteChannelStats Configuration

discord:
  token: "YOUR_BOT_TOKEN_HERE"
  guild-ids:
    - "123456789012345678"

settings:
  update-interval-seconds: 60

trackers:
  # Count open report tickets
  report-tickets:
    type: channel
    target-channel-id: "111111111111111111"
    pattern: "report-"
    match-mode: STARTS_WITH
    display-format: "Open Report Tickets: {count}"

  # Count support tickets
  support-tickets:
    type: channel
    target-channel-id: "222222222222222222"
    pattern: "support-"
    match-mode: STARTS_WITH
    display-format: "Support Tickets: {count}"

  # Track total members
  all-members:
    type: member-count
    target-channel-id: "333333333333333333"
    display-format: "👥 All Members: {count}"

  # Track online members
  members-online:
    type: members-online
    target-channel-id: "444444444444444444"
    display-format: "🟢 Online: {count}"

  # Track bots
  bots:
    type: bot-count
    target-channel-id: "555555555555555555"
    display-format: "🤖 Bots: {count}"

  # Track staff members
  staff:
    type: role-members
    target-channel-id: "666666666666666666"
    role-id: "777777777777777777"
    display-format: "Staff Online: {count}"

  # Track VIP members
  vip-members:
    type: role-members
    target-channel-id: "888888888888888888"
    role-id: "999999999999999999"
    display-format: "VIP Members: {count}"
```

---

## Troubleshooting

### Bot Token Invalid

**Error:** `Please configure your Discord bot token in config.yml!`

**Solution:** Ensure your token is correctly copied from the Discord Developer Portal.

### Guild Not Found

**Error:** `Guild not found: <guild-id>`

**Solution:**

1. Verify the guild ID is correct
2. Ensure the bot is invited to the server

### Target Channel Not Found

**Error:** `Target channel not found for tracker '<id>': <channel-id>`

**Solution:**

1. Verify the channel ID is correct
2. Ensure the channel is a **voice channel**
3. Ensure the bot has access to the channel

### Updates Not Working

**Possible Causes:**

1. Rate limiting - increase `update-interval-seconds`
2. Missing `Manage Channels` permission
3. Bot doesn't have access to the target channel

---

## Multi-Server Setup

IgniteChannelStats supports monitoring multiple Discord servers simultaneously.

```yaml
discord:
  guild-ids:
    - "123456789012345678"  # Server 1
    - "987654321098765432"  # Server 2
    - "111111111111111111"  # Server 3
```

**Important Notes:**

- All trackers run for all configured guilds
- Ensure target channel IDs exist in the respective guild
- Consider increasing update interval with multiple servers

