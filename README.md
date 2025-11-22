# ReStyle - Sustainable Fashion App

ReStyle adalah aplikasi Android yang membantu pengguna untuk berkontribusi dalam fashion sustainability dengan cara:
- **Resell**: Menjual kembali pakaian yang tidak digunakan
- **Donate**: Mendonasikan pakaian untuk yang membutuhkan  
- **Recycle**: Mendaur ulang limbah pakaian menjadi produk baru
- **Marketplace**: Membeli pakaian second-hand berkualitas

## 🌱 Fitur Utama

### 1. Home Screen
- **Loyalty Card**: Menampilkan koleksi pakaian user dan impact points
- **Feature Grid**: Akses cepat ke 4 fitur utama
- **Modern UI**: Menggunakan Material Design 3 dengan Jetpack Compose

### 2. Upload Photo
- Ambil foto dengan **Camera**
- Upload foto dari **Gallery**
- Permission handling otomatis (Camera & Storage)
- Preview foto sebelum submit
- Analisis AI untuk kondisi item (coming soon)

### 3. Navigation
- Bottom navigation untuk akses cepat
- Smooth transition antar screens
- Support untuk deep linking

## 🎨 Design System

### Color Palette
- **Primary Green**: `#6FCF97` - Sustainability theme
- **Dark Green**: `#2D5F3F` - Header & background
- **Medium Green**: `#3D7A52` - Buttons & accents
- **Cream White**: `#FFF8E7` - Light text & surfaces

### Feature Colors
- **Resell**: Yellow (`#FFE8B3` / `#FF9F43`)
- **Donate**: Pink (`#FFD6E0` / `#FF6B9D`)
- **Recycle**: Blue (`#D4E8FF` / `#4A90E2`)
- **Marketplace**: Green (primary colors)

## 🏗️ Struktur Project

```
app/src/main/
├── java/com/example/restyle/
│   ├── MainActivity.kt              # Entry point aplikasi
│   └── ui/
│       ├── screen/
│       │   ├── HomeScreen.kt        # Layar utama dengan feature grid
│       │   └── UploadPhotoScreen.kt # Upload foto untuk Resell/Donate/Recycle
│       └── theme/
│           ├── Color.kt             # Color definitions
│           ├── Theme.kt             # Material theme configuration
│           └── Type.kt              # Typography styles
├── res/
│   ├── values/
│   │   ├── strings.xml              # String resources untuk internationalization
│   │   ├── colors.xml               # Legacy XML colors
│   │   └── themes.xml               # Material themes
│   ├── drawable/                    # Icons & images
│   └── mipmap/                      # App launcher icons
└── AndroidManifest.xml              # App configuration & permissions
```

## 📱 Requirements

- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0
- **Compose**: BOM 2024.02.00
- **Android Gradle Plugin**: 8.1.0

## 🔧 Setup & Installation

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with API 34

### Steps
1. Clone repository:
   ```bash
   git clone https://github.com/nramd/ReStyle.git
   cd ReStyle
   ```

2. Open project di Android Studio

3. Sync Gradle files:
   ```bash
   ./gradlew sync
   ```

4. Build project:
   ```bash
   ./gradlew assembleDebug
   ```

5. Run on emulator or device:
   ```bash
   ./gradlew installDebug
   ```

## 🔑 Permissions

Aplikasi memerlukan permissions berikut:
- `CAMERA`: Untuk mengambil foto item
- `READ_MEDIA_IMAGES` (Android 13+): Untuk akses gallery
- `READ_EXTERNAL_STORAGE` (Android 12-): Fallback untuk akses gallery
- `INTERNET`: Untuk API calls (future implementation)

## 🚀 Roadmap

### Phase 1: Core Features ✅
- [x] Home screen dengan feature grid
- [x] Upload photo screen
- [x] Camera & gallery integration
- [x] Permission handling
- [x] Basic navigation

### Phase 2: In Progress 🚧
- [ ] Marketplace screen
- [ ] Item detail screen
- [ ] Pricing estimation dengan AI
- [ ] User profile screen
- [ ] History transactions

### Phase 3: Advanced Features 📋
- [ ] AI image analysis untuk kondisi item
- [ ] Price recommendation
- [ ] Chat/messaging feature
- [ ] Payment integration
- [ ] Recycling partner integration
- [ ] Carbon footprint tracking
- [ ] Gamification & rewards

### Phase 4: Polish 🎨
- [ ] Animation & transitions
- [ ] Dark mode support
- [ ] Multi-language support
- [ ] Accessibility improvements
- [ ] Performance optimization

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

### Test Coverage
- Unit tests untuk business logic
- UI tests untuk Composables
- Integration tests untuk navigation

## 📝 Code Style

Project ini mengikuti [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):
- 4 spaces indentation
- Max line length: 120 characters
- KDoc comments untuk public APIs
- Meaningful variable names

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Developer**: nramd
- **Design**: Material Design 3
- **Framework**: Jetpack Compose

## 📞 Contact

Project Link: [https://github.com/nramd/ReStyle](https://github.com/nramd/ReStyle)

---

Made with 💚 for a more sustainable fashion future
