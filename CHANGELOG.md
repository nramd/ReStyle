# Changelog

All notable changes to the ReStyle project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Home Screen dengan Loyalty Card
  - Menampilkan jumlah item dalam koleksi
  - Impact points tracking
  - User profile display
- Feature Grid dengan 4 fitur utama:
  - Marketplace: Browse pakaian second-hand
  - Resell: Jual pakaian yang tidak dipakai
  - Donate: Donasikan pakaian
  - Recycle: Daur ulang limbah pakaian
- Upload Photo Screen
  - Camera capture dengan permission handling
  - Gallery picker dengan permission handling
  - Image preview before upload
  - Support untuk Android 13+ media permissions
- Navigation system dengan Jetpack Compose Navigation
- Material Design 3 theming
  - Green sustainability color scheme
  - Feature-specific colors (Resell yellow, Donate pink, Recycle blue)
  - Light theme support
  - Status bar theming
- Comprehensive documentation
  - KDoc untuk semua major components
  - README.md dengan project overview
  - CONTRIBUTING.md untuk contributors
  - CHANGELOG.md untuk version tracking
- String resources untuk internationalization
- Permission handling untuk Camera dan Storage

### Changed
- Updated Gradle configuration
  - Android Gradle Plugin: 8.1.0
  - Kotlin: 1.9.0
  - Compose BOM: 2024.02.00
- Consolidated theme colors dari UploadPhotoScreen ke Theme.kt
- Improved MainActivity dengan proper theme integration
- Enhanced color scheme consistency

### Fixed
- Gradle plugin repository configuration
- Build configuration compatibility issues
- Theme color duplication

## [0.1.0] - 2025-11-22

### Added
- Initial project setup
- Basic Android project structure
- Gradle configuration
- AndroidManifest with permissions

---

## Version Guidelines

### Version Number Format: MAJOR.MINOR.PATCH

- **MAJOR**: Breaking changes, incompatible API changes
- **MINOR**: New features, backwards-compatible
- **PATCH**: Bug fixes, backwards-compatible

### Change Categories

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security vulnerability fixes
