# Changelog - SpyInventories

All notable changes to the SpyInventories project will be documented in this file.

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

