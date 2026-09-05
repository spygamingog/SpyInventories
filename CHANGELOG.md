# Changelog - SpyInventories

All notable changes to the SpyInventories project will be documented in this file.

## [1.0.9] - 2026-09-06

### Added
- **Zero-Loss Legacy Auto-Migration**: Added automatic detection and migration of legacy SpyCore inventory files (`plugins/SpyCore/players/<UUID>/<group>.yml`) when player data is loaded.
- **Alias-Aware Group Resolution**: `GroupManager` now resolves world aliases via `SpyAPI.getAliasForWorld(world)` for seamless container world grouping support.
- **Inherited Base Dimension Grouping**: Dimensional worlds (`_nether`, `_the_end`) now automatically inherit any custom group assigned to their base world in `groups.yml`.
- **Potion Effect Key Compatibility**: Supports loading potion effects stored under both `potion-effects` and legacy `potion_effects`.

### Changed
- **Authoritative Inventory Manager**: Promoted to sole authoritative multi-world inventory manager for the SpyCore ecosystem.
- **Core Dependency**: Updated to **SpyCore 1.1.2**.

## [1.0.8] - 2026-09-05

### Changed
- **Core Dependency**: Updated to **SpyCore 1.1.1**.
- **Dimension State Alignment**: Verified seamless gamemode and inventory preservation across linked dimensions in the same world set.

## [1.0.7] - 2026-09-04

### Changed
- **Core Dependency**: Updated to **SpyCore 1.1.0** for improved VFS compatibility, thread safety, and modern Paper 1.21+ support.
- **Modern Build**: Replaced brittle annotation processing with standard native Java getters and clean Maven build configuration.
- **Reliability**: Ensured synchronized state tracking with SpyCore's updated dimension grouping and death-respawn handling.

## [1.0.6] - 2026-02-20

### Fixed
- **Inventory Clearing**: Fixed critical issue where players retained previous world's inventory when entering a new world without saved data. Now strictly clears all player data (inventory, armor, offhand, enderchest, effects, gamemode) when no valid save file exists.
- **Dimension Sync**: Improved `_nether` and `_the_end` suffix handling to be case-insensitive, ensuring correct inventory synchronization between dimensions.
- **Dependency**: Updated to **SpyCore 1.0.9** to resolve conflicting inventory management (double-loading issue).

## [1.0.5] - Earlier

## [1.0.4] - 2026-02-12

### Changed
- **Dependency**: Updated to SpyCore 1.0.7 to ensure compatibility with the latest VFS and world management improvements.
- Internal optimizations for inventory state management.

## [1.0.3] - 2026-02-11

### Added
- Initial release with world-group inventory management.
- Synchronized with SpyCore v1.0.6 updates.
- Improved inventory saving reliability during world transitions.
- Added support for new metadata tags introduced in the core plugin.

