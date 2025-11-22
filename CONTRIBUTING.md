# Contributing to ReStyle

Thank you for your interest in contributing to ReStyle! This document provides guidelines and instructions for contributing to the project.

## 🌟 How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear description of the bug
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots (if applicable)
- Device/emulator information
- Android version

### Suggesting Features

We welcome feature suggestions! Please create an issue with:
- Clear description of the feature
- Use case/problem it solves
- Proposed solution
- Any mockups or examples (if applicable)

### Pull Requests

1. **Fork the repository**
   ```bash
   git clone https://github.com/nramd/ReStyle.git
   cd ReStyle
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow the code style guidelines below
   - Add tests for new functionality
   - Update documentation as needed

4. **Test your changes**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add amazing feature"
   ```
   
   Follow [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat:` new feature
   - `fix:` bug fix
   - `docs:` documentation changes
   - `style:` formatting, missing semicolons, etc
   - `refactor:` code refactoring
   - `test:` adding tests
   - `chore:` maintenance tasks

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Provide a clear description
   - Reference any related issues
   - Include screenshots for UI changes

## 📝 Code Style Guidelines

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use meaningful variable and function names

### Compose Best Practices

```kotlin
// ✅ Good - Clear, documented composable
/**
 * Displays a feature card with icon and title.
 * 
 * @param title The feature title
 * @param icon The emoji icon
 * @param onClick Callback when card is clicked
 */
@Composable
fun FeatureCard(
    title: String,
    icon: String,
    onClick: () -> Unit
) {
    // Implementation
}

// ❌ Bad - No documentation, unclear naming
@Composable
fun FC(t: String, i: String, o: () -> Unit) {
    // Implementation
}
```

### Documentation

- Add KDoc comments for all public APIs
- Include parameter descriptions
- Document complex logic with inline comments
- Keep comments up-to-date with code changes

### Resource Naming

```kotlin
// Strings
<string name="feature_resell_title">Resell Item</string>
<string name="toast_permission_required">Permission required</string>

// Colors
val DarkGreen = Color(0xFF2D5F3F)
val ResellYellow = Color(0xFFFFE8B3)

// Drawables
ic_launcher.xml
icon_camera.xml
bg_card_gradient.xml
```

## 🏗️ Architecture Guidelines

### MVVM Pattern

```kotlin
// ViewModel
class UploadViewModel : ViewModel() {
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    fun uploadPhoto(bitmap: Bitmap) {
        // Implementation
    }
}

// State
sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val result: AnalysisResult) : UploadState()
    data class Error(val message: String) : UploadState()
}
```

### Repository Pattern

```kotlin
interface ItemRepository {
    suspend fun getItems(): Result<List<Item>>
    suspend fun uploadItem(item: Item): Result<Unit>
}

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {
    override suspend fun getItems(): Result<List<Item>> {
        // Implementation
    }
}
```

## 🧪 Testing Guidelines

### Unit Tests

```kotlin
class UploadViewModelTest {
    @Test
    fun `uploadPhoto should update state to Loading`() {
        // Arrange
        val viewModel = UploadViewModel()
        
        // Act
        viewModel.uploadPhoto(mockBitmap)
        
        // Assert
        assertEquals(UploadState.Loading, viewModel.uploadState.value)
    }
}
```

### UI Tests

```kotlin
@Test
fun homeScreen_displaysFeatureGrid() {
    composeTestRule.setContent {
        ReStyleTheme {
            HomeScreen()
        }
    }
    
    composeTestRule.onNodeWithText("Resell").assertIsDisplayed()
    composeTestRule.onNodeWithText("Donate").assertIsDisplayed()
}
```

## 🎨 Design Guidelines

### Color Usage

- Use theme colors instead of hardcoded values
- Follow Material Design 3 guidelines
- Maintain accessibility standards (contrast ratios)

```kotlin
// ✅ Good - Using theme colors
MaterialTheme.colorScheme.primary

// ❌ Bad - Hardcoded colors
Color(0xFF6FCF97)
```

### Spacing

Use consistent spacing from theme:
- 4dp: tight spacing
- 8dp: default spacing
- 16dp: content padding
- 24dp: screen padding
- 32dp: section spacing

### Typography

```kotlin
// Use Material 3 typography scale
Text(
    text = "Title",
    style = MaterialTheme.typography.titleLarge
)
```

## 🔒 Security Guidelines

- Never commit API keys or secrets
- Use BuildConfig for configuration values
- Validate all user inputs
- Handle permissions properly
- Sanitize data before storage

## 📱 Compatibility

- Test on multiple screen sizes
- Test on different Android versions (API 26+)
- Consider RTL languages
- Test with TalkBack enabled
- Test in dark mode

## 🚀 Performance

- Use LazyColumn/LazyRow for lists
- Optimize image loading
- Avoid unnecessary recompositions
- Profile with Android Studio Profiler
- Keep UI thread responsive

## 📖 Documentation

When adding features, update:
- README.md (if adding major feature)
- CHANGELOG.md
- Code comments
- API documentation
- User-facing help text

## ❓ Questions?

If you have questions:
1. Check existing issues and discussions
2. Read the documentation
3. Ask in Pull Request comments
4. Create a new discussion

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project (MIT License).

## 🙏 Thank You!

Thank you for contributing to ReStyle and helping make sustainable fashion more accessible!

---

Happy coding! 💚
