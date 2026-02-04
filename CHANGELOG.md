# Changelog

## [1.0.1] - 2026-02-04

### Improved
- **Core Compatibility**: Fully updated to support SpyCore 1.0.4, ensuring seamless integration with the new world hibernation system.
- **Logging**: Enhanced startup and shutdown logs for better version tracking and diagnostics.
- **Data Safety**: Implemented a "safe load" system. If player data fails to load (e.g., I/O error), saving is disabled for that session to prevent overwriting valid data with an empty inventory.

### Fixed
- **Stability**: Minor improvements to inventory data handling during server reloads.
