# Tracker Types Reference

IgniteChannelStats provides five tracker types to monitor different Discord server statistics:

| Type                   | Config Key       | Description                           |
|------------------------|------------------|---------------------------------------|
| Channel Tracker        | `channel`        | Counts channels matching a pattern    |
| Member Count Tracker   | `member-count`   | Counts total server members           |
| Members Online Tracker | `members-online` | Counts online members (excludes bots) |
| Bot Count Tracker      | `bot-count`      | Counts bots in the server             |
| Role Members Tracker   | `role-members`   | Counts members with a specific role   |

---

## Channel Tracker

Counts all channels in your Discord server that match a specific pattern. Perfect for tracking open tickets, temporary
channels, or any dynamically created channels.

### Configuration

```yaml
tracker-id:
  type: channel
  target-channel-id: "<voice-channel-id>"
  pattern: "<pattern-to-match>"
  match-mode: STARTS_WITH  # or CONTAINS
  display-format: "Display: {count}"
```

### Properties

| Property            | Required | Description                         |
|---------------------|----------|-------------------------------------|
| `type`              | Yes      | Must be `channel`                   |
| `target-channel-id` | Yes      | Voice channel to update             |
| `pattern`           | Yes      | Text to match against channel names |
| `match-mode`        | Yes      | `STARTS_WITH` or `CONTAINS`         |
| `display-format`    | Yes      | Format with `{count}` placeholder   |

### Match Modes

#### STARTS_WITH

Matches channels whose names **begin with** the specified pattern.

```yaml
pattern: "ticket-"
match-mode: STARTS_WITH
```

**Matches:**

- `ticket-001`
- `ticket-support-123`
- `ticket-vip-request`

**Does NOT match:**

- `old-ticket-001`
- `support-ticket`

#### CONTAINS

Matches channels whose names **contain** the specified pattern anywhere.

```yaml
pattern: "support"
match-mode: CONTAINS
```

**Matches:**

- `support-001`
- `vip-support-room`
- `tech-support`

### Examples

#### Open Ticket Counter

```yaml
open-tickets:
  type: channel
  target-channel-id: "123456789012345678"
  pattern: "ticket-"
  match-mode: STARTS_WITH
  display-format: "🎫 Open Tickets: {count}"
```

#### Report Tracker

```yaml
reports:
  type: channel
  target-channel-id: "123456789012345678"
  pattern: "report-"
  match-mode: STARTS_WITH
  display-format: "📋 Active Reports: {count}"
```

#### Voice Room Counter

```yaml
voice-rooms:
  type: channel
  target-channel-id: "123456789012345678"
  pattern: "voice"
  match-mode: CONTAINS
  display-format: "🔊 Voice Rooms: {count}"
```

---

## Member Count Tracker

Tracks the total number of members in your Discord server, including both users and bots.

### Configuration

```yaml
tracker-id:
  type: member-count
  target-channel-id: "<voice-channel-id>"
  display-format: "Display: {count}"
```

### Properties

| Property            | Required | Description                       |
|---------------------|----------|-----------------------------------|
| `type`              | Yes      | Must be `member-count`            |
| `target-channel-id` | Yes      | Voice channel to update           |
| `display-format`    | Yes      | Format with `{count}` placeholder |

### Example

```yaml
total-members:
  type: member-count
  target-channel-id: "123456789012345678"
  display-format: "👥 Total Members: {count}"
```

---

## Members Online Tracker

Tracks the number of members currently online. This tracker:

- **Excludes** bots
- **Excludes** offline members
- **Excludes** invisible members

### Online Statuses Counted

| Status               | Counted |
|----------------------|---------|
| Online (green)       | ✅ Yes   |
| Idle (yellow)        | ✅ Yes   |
| Do Not Disturb (red) | ✅ Yes   |
| Offline              | ❌ No    |
| Invisible            | ❌ No    |

### Configuration

```yaml
tracker-id:
  type: members-online
  target-channel-id: "<voice-channel-id>"
  display-format: "Display: {count}"
```

### Properties

| Property            | Required | Description                       |
|---------------------|----------|-----------------------------------|
| `type`              | Yes      | Must be `members-online`          |
| `target-channel-id` | Yes      | Voice channel to update           |
| `display-format`    | Yes      | Format with `{count}` placeholder |

### Example

```yaml
online-now:
  type: members-online
  target-channel-id: "123456789012345678"
  display-format: "🟢 Online Now: {count}"
```

---

## Bot Count Tracker

Tracks the total number of bots in your Discord server.

### Configuration

```yaml
tracker-id:
  type: bot-count
  target-channel-id: "<voice-channel-id>"
  display-format: "Display: {count}"
```

### Properties

| Property            | Required | Description                       |
|---------------------|----------|-----------------------------------|
| `type`              | Yes      | Must be `bot-count`               |
| `target-channel-id` | Yes      | Voice channel to update           |
| `display-format`    | Yes      | Format with `{count}` placeholder |

### Example

```yaml
server-bots:
  type: bot-count
  target-channel-id: "123456789012345678"
  display-format: "🤖 Bots: {count}"
```

---

## Role Members Tracker

Tracks the number of members with a specific role. You can create unlimited role-based trackers to monitor different
groups.

### Configuration

```yaml
tracker-id:
  type: role-members
  target-channel-id: "<voice-channel-id>"
  role-id: "<role-id>"
  display-format: "Display: {count}"
```

### Properties

| Property            | Required | Description                       |
|---------------------|----------|-----------------------------------|
| `type`              | Yes      | Must be `role-members`            |
| `target-channel-id` | Yes      | Voice channel to update           |
| `role-id`           | Yes      | ID of the role to count           |
| `display-format`    | Yes      | Format with `{count}` placeholder |

### Getting Role ID

1. Enable **Developer Mode** in Discord (User Settings → Advanced → Developer Mode)
2. Go to **Server Settings** → **Roles**
3. Right-click on the role you want to track
4. Click **Copy Role ID**

### Examples

#### Staff Counter

```yaml
staff-count:
  type: role-members
  target-channel-id: "123456789012345678"
  role-id: "111111111111111111"
  display-format: "👮 Staff: {count}"
```

#### VIP Counter

```yaml
vip-count:
  type: role-members
  target-channel-id: "123456789012345678"
  role-id: "222222222222222222"
  display-format: "⭐ VIP Members: {count}"
```

#### Subscribers Counter

```yaml
subscribers:
  type: role-members
  target-channel-id: "123456789012345678"
  role-id: "333333333333333333"
  display-format: "💎 Subscribers: {count}"
```

#### Multiple Role Trackers

```yaml
trackers:
  admins:
    type: role-members
    target-channel-id: "111111111111111111"
    role-id: "444444444444444444"
    display-format: "🔴 Admins: {count}"

  moderators:
    type: role-members
    target-channel-id: "222222222222222222"
    role-id: "555555555555555555"
    display-format: "🟡 Moderators: {count}"

  helpers:
    type: role-members
    target-channel-id: "333333333333333333"
    role-id: "666666666666666666"
    display-format: "🟢 Helpers: {count}"

  verified:
    type: role-members
    target-channel-id: "444444444444444444"
    role-id: "777777777777777777"
    display-format: "✅ Verified: {count}"
```

---

## Display Format Placeholders

All tracker types support the `{count}` placeholder in their display format.

### Placeholder Reference

| Placeholder | Description                |
|-------------|----------------------------|
| `{count}`   | The calculated count value |

### Format Examples

| Format                | Result (count = 42) |
|-----------------------|---------------------|
| `Members: {count}`    | `Members: 42`       |
| `👥 {count} Members`  | `👥 42 Members`     |
| `Online [{count}]`    | `Online [42]`       |
| `({count}) Staff`     | `(42) Staff`        |
| `🎫 Tickets: {count}` | `🎫 Tickets: 42`    |

### Using Emojis

Discord supports Unicode emojis in channel names:

```yaml
display-format: "🎫 Open Tickets: {count}"
display-format: "👥 Members: {count}"
display-format: "🟢 Online: {count}"
display-format: "🤖 Bots: {count}"
display-format: "⭐ VIP: {count}"
display-format: "👮 Staff: {count}"
```

---

## Creating Trackers via Discord Commands

In addition to config file configuration, you can create trackers using Discord slash commands.

### Command Overview

| Tracker Type   | Command            |
|----------------|--------------------|
| Channel        | `/tracker channel` |
| Member Count   | `/tracker members` |
| Members Online | `/tracker online`  |
| Bot Count      | `/tracker bots`    |
| Role Members   | `/tracker role`    |

### Example: Creating a Channel Tracker

```
/tracker channel id:ticket-tracker target:#stats-tickets pattern:ticket- mode:Starts With format:Open Tickets: {count}
```

### Example: Creating a Role Tracker

```
/tracker role id:staff-count target:#stats-staff role:@Staff format:Staff Online: {count}
```

Trackers created via commands are automatically saved to the configuration file and persist across server restarts.

---

## Best Practices

### Naming Conventions

Use descriptive IDs for your trackers:

✅ **Good:**

- `open-tickets`
- `staff-count`
- `vip-members`
- `support-channels`

❌ **Avoid:**

- `tracker1`
- `t1`
- `test`

### Performance Considerations

1. **Update Interval**: Keep the interval at 60+ seconds
2. **Channel Access**: Ensure the bot has proper permissions
3. **Multiple Trackers**: With many trackers, increase the update interval

### Voice Channel Setup

1. Create a voice channel in a statistics category
2. Set user limit to `0` (prevents joining)
3. Remove `Connect` permission for @everyone
4. The channel will display your statistics as its name

