# ReStyle Development Guide

This guide provides detailed information for developers working on the ReStyle project.

## 📋 Table of Contents

- [Getting Started](#getting-started)
- [Project Architecture](#project-architecture)
- [Development Workflow](#development-workflow)
- [Building and Testing](#building-and-testing)
- [Common Tasks](#common-tasks)
- [Troubleshooting](#troubleshooting)

## 🚀 Getting Started

### Prerequisites

1. **Android Studio**: Hedgehog (2023.1.1) or newer
2. **JDK**: Version 17 or higher
3. **Android SDK**: API Level 34 (Android 14)
4. **Git**: For version control

### Initial Setup

```bash
# Clone the repository
git clone https://github.com/nramd/ReStyle.git
cd ReStyle

# Open in Android Studio
# File -> Open -> Select ReStyle folder

# Sync Gradle
./gradlew sync

# Build the project
./gradlew assembleDebug
```

## 🏗️ Project Architecture

### Overview

ReStyle follows **Clean Architecture** principles with **MVVM** pattern:

```
app/src/main/java/com/example/restyle/
├── data/           # Data layer
│   ├── model/      # Data models (Item, User)
│   ├── repository/ # Data repositories (to be implemented)
│   └── api/        # API services (to be implemented)
├── ui/             # Presentation layer
│   ├── screen/     # Composable screens
│   ├── theme/      # App theming
│   └── components/ # Reusable UI components (to be implemented)
└── MainActivity.kt # App entry point
```

### Current Implementation Status

✅ **Completed:**
- Data models (Item, User)
- UI screens (Home, UploadPhoto)
- Theme system (Colors, Typography)
- Navigation setup
- Permission handling

🚧 **To Be Implemented:**
- ViewModels for state management
- Repository layer
- API integration
- Local database (Room)
- Dependency injection (Hilt/Koin)

## 💻 Development Workflow

### Creating a New Feature

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Implement the feature**
   - Create data models if needed in `data/model/`
   - Create ViewModel in `ui/viewmodel/`
   - Create Composable screen in `ui/screen/`
   - Add navigation in `HomeScreen.kt`

3. **Add tests**
   - Unit tests in `src/test/`
   - UI tests in `src/androidTest/`

4. **Update documentation**
   - Add KDoc comments
   - Update README if needed
   - Update CHANGELOG.md

5. **Create Pull Request**
   - Use the PR template
   - Reference related issues
   - Request review

### Code Style

Follow the guidelines in [CONTRIBUTING.md](CONTRIBUTING.md):
- 4 spaces indentation
- Max 120 characters per line
- KDoc for public APIs
- Use Material Design 3 components

### Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/):
```
feat: add marketplace item listing
fix: resolve camera permission crash
docs: update README with new features
refactor: improve theme color organization
test: add tests for upload functionality
```

## 🔨 Building and Testing

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug

# Install on connected device
./gradlew installDebug
```

### Testing Commands

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Linting

```bash
# Run Android Lint
./gradlew lint

# View lint report
open app/build/reports/lint-results.html
```

## 📝 Common Tasks

### Adding a New Screen

1. **Create Composable in `ui/screen/`:**
   ```kotlin
   @Composable
   fun NewScreen(
       navController: NavController,
       viewModel: NewViewModel = viewModel()
   ) {
       // Screen implementation
   }
   ```

2. **Add navigation route in HomeScreen.kt:**
   ```kotlin
   composable("new_screen") {
       NewScreen(navController = navController)
   }
   ```

3. **Navigate to screen:**
   ```kotlin
   navController.navigate("new_screen")
   ```

### Adding a New Data Model

1. **Create model in `data/model/`:**
   ```kotlin
   @Parcelize
   data class NewModel(
       val id: String,
       val name: String
   ) : Parcelable
   ```

2. **Add Room annotations if needed:**
   ```kotlin
   @Entity(tableName = "new_models")
   data class NewModel(
       @PrimaryKey val id: String,
       @ColumnInfo(name = "name") val name: String
   )
   ```

### Adding String Resources

1. **Add to `res/values/strings.xml` (English):**
   ```xml
   <string name="feature_name">Feature Name</string>
   ```

2. **Add to `res/values-id/strings.xml` (Indonesian):**
   ```xml
   <string name="feature_name">Nama Fitur</string>
   ```

### Adding New Colors

Add to `ui/theme/Color.kt`:
```kotlin
val NewFeatureColor = Color(0xFF...)
```

Then use in composables:
```kotlin
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.primary
)
```

## 🐛 Troubleshooting

### Build Fails with "Could not resolve..."

**Solution:** Sync Gradle and clear cache:
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### Compose Preview Not Showing

**Solution:** 
1. Invalidate caches: `File -> Invalidate Caches -> Restart`
2. Rebuild project: `Build -> Rebuild Project`

### Permission Denied on gradlew

**Solution:**
```bash
chmod +x gradlew
```

### App Crashes on Device

**Solution:**
1. Check Logcat for error messages
2. Verify permissions in AndroidManifest.xml
3. Check ProGuard rules if using release build

### Gradle Sync Failed

**Solution:**
1. Check internet connection
2. Verify JDK version (should be 17+)
3. Check Gradle version compatibility
4. Delete `.gradle` folder and sync again

## 🔐 Security Best Practices

1. **Never commit sensitive data:**
   - API keys
   - Passwords
   - Private keys

2. **Use BuildConfig for configuration:**
   ```kotlin
   val apiKey = BuildConfig.API_KEY
   ```

3. **Validate user inputs:**
   ```kotlin
   fun isValidEmail(email: String): Boolean {
       return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
   }
   ```

4. **Use HTTPS for network calls:**
   ```kotlin
   retrofit.baseUrl("https://api.example.com/")
   ```

## 📚 Resources

### Documentation
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

### Tools
- [Android Studio](https://developer.android.com/studio)
- [Compose Layout Inspector](https://developer.android.com/jetpack/compose/tooling)
- [Android Profiler](https://developer.android.com/studio/profile)

### Community
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- [Android Developers](https://www.youtube.com/@AndroidDevelopers)
- [Kotlin Slack](https://kotlinlang.slack.com/)

## 🎯 Next Steps

Ready to contribute? Here are some good first tasks:

1. **Implement Marketplace Screen**
   - Create item list composable
   - Add filter and search functionality
   - Implement item detail view

2. **Add ViewModel Layer**
   - Create ViewModels for existing screens
   - Implement StateFlow for state management
   - Add proper error handling

3. **Setup Room Database**
   - Define entities
   - Create DAOs
   - Implement repository pattern

4. **Add Unit Tests**
   - Test data models
   - Test ViewModels
   - Test utility functions

5. **Improve UI/UX**
   - Add animations
   - Implement loading states
   - Add error states

---

Need help? Check [CONTRIBUTING.md](CONTRIBUTING.md) or open an issue!
