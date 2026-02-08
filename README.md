# IgniteChannelStats

A Spigot plugin that integrates with Discord to automatically update voice channel names with real-time statistics.
Perfect for displaying live server metrics like open tickets, member counts, online users, and role-based statistics.

## Features

- **Channel Pattern Tracking** - Count channels matching specific patterns (e.g., open tickets)
- **Member Statistics** - Track total members, online members, and bot counts
- **Role-Based Tracking** - Count members with specific roles (unlimited configurations)
- **Multi-Server Support** - Monitor multiple Discord guilds simultaneously
- **Discord Slash Commands** - Manage trackers directly from Discord
- **Configurable Update Interval** - Respect Discord rate limits with adjustable intervals
- **Persistent Configuration** - All trackers are saved to config and survive restarts

## Documentation

### [Quick Start Guide](docs/QUICKSTART.md)

Get up and running in 5 minutes. Perfect for first-time setup.

---

### [Configuration Guide](docs/CONFIGURATION.md)

Complete guide for setting up and configuring the plugin.

---

### [Tracker Types Reference](docs/TRACKERS.md)

Detailed documentation for all available tracker types with examples.

---

### [Troubleshooting Guide](docs/TROUBLESHOOTING.md)

Solutions for common issues and problems.

---

## Discord Commands

All commands require **Administrator** permission in Discord.

### Tracker Management Commands

| Command            | Description                      |
|--------------------|----------------------------------|
| `/tracker channel` | Add a channel pattern tracker    |
| `/tracker members` | Add a total member count tracker |
| `/tracker online`  | Add an online members tracker    |
| `/tracker bots`    | Add a bot count tracker          |
| `/tracker role`    | Add a role members tracker       |
| `/tracker remove`  | Remove an existing tracker       |
| `/tracker list`    | List all configured trackers     |
| `/tracker refresh` | Manually refresh all trackers    |

### Command Options

#### `/tracker channel`

| Option    | Required | Description                                   |
|-----------|----------|-----------------------------------------------|
| `id`      | Yes      | Unique identifier for the tracker             |
| `target`  | Yes      | Voice channel to update with the count        |
| `pattern` | Yes      | Pattern to match channel names                |
| `mode`    | Yes      | Match mode: `Starts With` or `Contains`       |
| `format`  | Yes      | Display format (use `{count}` for the number) |

<img width="422" height="303" alt="image" src="https://github.com/user-attachments/assets/f40ef215-2d7c-4d69-a0cd-23caf65c8e55" />


#### `/tracker members`

| Option   | Required | Description                                   |
|----------|----------|-----------------------------------------------|
| `id`     | Yes      | Unique identifier for the tracker             |
| `target` | Yes      | Voice channel to update with the count        |
| `format` | Yes      | Display format (use `{count}` for the number) |

<img width="290" height="45" alt="image" src="https://github.com/user-attachments/assets/bc727a3a-bb56-4e99-8cf6-60482087e86f" />


#### `/tracker online`

| Option   | Required | Description                                   |
|----------|----------|-----------------------------------------------|
| `id`     | Yes      | Unique identifier for the tracker             |
| `target` | Yes      | Voice channel to update with the count        |
| `format` | Yes      | Display format (use `{count}` for the number) |

<img width="290" height="45" alt="image" src="https://github.com/user-attachments/assets/cb8d28d6-463b-43ca-b0c9-5c019001a636" />


#### `/tracker bots`

| Option   | Required | Description                                   |
|----------|----------|-----------------------------------------------|
| `id`     | Yes      | Unique identifier for the tracker             |
| `target` | Yes      | Voice channel to update with the count        |
| `format` | Yes      | Display format (use `{count}` for the number) |

<img width="290" height="45" alt="image" src="https://github.com/user-attachments/assets/c6a0b919-f79d-40f6-b46b-09532811575d" />


#### `/tracker role`

| Option   | Required | Description                                   |
|----------|----------|-----------------------------------------------|
| `id`     | Yes      | Unique identifier for the tracker             |
| `target` | Yes      | Voice channel to update with the count        |
| `role`   | Yes      | Role to count members with                    |
| `format` | Yes      | Display format (use `{count}` for the number) |

#### `/tracker remove`

| Option | Required | Description                         |
|--------|----------|-------------------------------------|
| `id`   | Yes      | Identifier of the tracker to remove |

---

## Discord Bot Setup

### Creating Your Bot

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Click **New Application** and give it a name
3. Navigate to **Bot** section
4. Click **Reset Token** and copy your bot token
5. Enable the following **Privileged Gateway Intents**:
    - `PRESENCE INTENT`
    - `SERVER MEMBERS INTENT`
    - `MESSAGE CONTENT INTENT`

### Bot Permissions

Your bot needs the following permissions:

- `Manage Channels` - To update voice channel names
- `View Channels` - To read channel information

### Inviting Your Bot

Generate an invite URL with these permissions:

```
https://discord.com/api/oauth2/authorize?client_id=YOUR_CLIENT_ID&permissions=16&scope=bot%20applications.commands
```

Replace `YOUR_CLIENT_ID` with your application's client ID.

---

## Display Format

The display format uses `{count}` as a placeholder for the calculated number.

### Examples

| Format                  | Result (with count = 5) |
|-------------------------|-------------------------|
| `Open Tickets: {count}` | `Open Tickets: 5`       |
| `👥 Members: {count}`   | `👥 Members: 5`         |
| `🟢 Online: {count}`    | `🟢 Online: 5`          |
| `Staff ({count})`       | `Staff (5)`             |

---

## Rate Limits

Discord has rate limits for channel name updates. It is recommended to set `update-interval-seconds` to at least **60
seconds** to avoid hitting these limits. If you configure multiple trackers, consider increasing this value.

---

## Building from Source

**Requirements:** Java 21+

```bash
git clone https://github.com/YOUR_USERNAME/IgniteChannelStats.git
cd IgniteChannelStats
./gradlew build
```

The compiled plugin JAR will be in `build/libs/`.

---

## Contributing

Contributions are welcome! Please open an issue first to discuss proposed changes.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
