# SpyInventories

[![Platform](https://img.shields.io/badge/Platform-Paper%20%2F%20Purpur-blue)](https://papermc.io)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-green)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.oracle.com/java/)
[![Core](https://img.shields.io/badge/Requires-SpyCore%201.1.2-purple)](https://github.com/spygamingog/SpyCore)
[![License](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-lightgrey)](LICENSE)

SpyInventories is the authoritative multi-world inventory management and state-sync plugin for Paper and Purpur (1.21+). Built as a companion extension for [SpyCore](https://github.com/spygamingog/SpyCore), it isolates and manages player inventories, armor, offhand items, ender chests, health, food, potion effects, and gamemodes across different world groups.

---

## Features

- **World Grouping**: Group worlds together so they share inventory data. For example, `survival`, `survival_nether`, and `survival_the_end` can share one inventory, while `creative` or `minigames` are kept completely separate.
- **Automatic Dimension Sync**: Dimensions ending with `_nether` and `_the_end` automatically inherit the base world's inventory and custom group configuration without requiring manual enumeration.
- **Full State Separation**: Manages inventory slots, armor, offhand, ender chest, health, food levels, active potion effects, and gamemodes.
- **Clean First-Time Joins**: When a player enters a new world group for the first time without prior data, their inventory and effects are cleanly cleared to prevent item bleeding or duplication.
- **Legacy Auto-Migration**: Automatically detects and migrates legacy player inventory files from `plugins/SpyCore/players/` with zero data loss.
- **Asynchronous Saving**: Player inventory files are saved asynchronously to prevent main-thread lag spikes when players switch worlds or disconnect.

---

## Requirements & Installation

1. **Requirements**:
   - Paper or Purpur 1.21+
   - Java 21+
   - [SpyCore](https://github.com/spygamingog/SpyCore) 1.1.2 or newer
2. **Installation**:
   - Put both `spycore-1.1.2.jar` and `spyinventories-1.0.9.jar` into your server's `plugins/` directory.
   - Restart the server.

---

## Configuration

World groups are configured in `plugins/SpyInventories/groups.yml`. General exemptions are configured in `config.yml`.

Worlds listed in the same group share the same inventory pool. Dimensions following standard naming (`_nether` and `_the_end`) are handled automatically:

```yaml
groups:
  survival:
    worlds:
      - survival
      - survival_nether
      - survival_the_end
  creative:
    worlds:
      - creative
  minigames:
    worlds:
      - bedwars
      - skywars
```

Player data is stored per-player in `plugins/SpyInventories/players/<UUID>/<group>.yml`.

---

## Sibling Plugins

- **[SpyCore](https://github.com/spygamingog/SpyCore)**: Multi-world management engine and VFS container system.
- **[SpyNetherPortals](https://github.com/spygamingog/SpyNetherPortals)**: Custom Nether and End portal routing for multi-world setups.

---

## License

This project is licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (CC BY-NC-SA 4.0)](LICENSE).
