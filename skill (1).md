# LuckyPDF — GitHub Copilot Skill (Codespaces Edition)
> **Version:** 2.0 · **Target:** Native Android (Kotlin + Jetpack Compose) · **IDE:** GitHub Codespaces

---

## 0. Copilot Behavior Contract

Copilot MUST follow every rule in this document without deviation.  
When generating, editing, or reviewing code:

- **Always** output Kotlin. Never Java, never Groovy.  
- **Always** use `build.gradle.kts` (Kotlin DSL) — never `build.gradle` (Groovy DSL).  
- **Always** use `libs.versions.toml` (Version Catalog) for dependency declarations.  
- **Always** use Jetpack Compose for every UI surface. No XML layouts, no `inflate()`.  
- **Always** follow the MVVM + Clean Architecture pattern defined in Section 7.  
- **Always** use `StateFlow` / `UiState` sealed classes — never `LiveData`.  
- **Always** scope state to the closest appropriate ViewModel.  
- **Always** handle permissions with the `rememberPermissionState` / `rememberMultiplePermissionsState` pattern (Accompanist or Compose built-in).  
- **Never** block the main thread — all I/O on `Dispatchers.IO`, all CPU work on `Dispatchers.Default`.  
- **Never** use `GlobalScope`. Always use `viewModelScope` or a scoped `CoroutineScope`.  
- **Never** hardcode strings — use `stringResource(R.string.*)`.  
- **Never** hardcode dimensions — use the spacing tokens defined in `Dimens.kt`.  
- **Never** skip error states — every UiState sealed class must include an `Error` variant.  
- **Always** write KDoc for every `public` / `internal` function and class.  
- **Always** keep composable functions pure — no side effects inside the composable body.  
- **Always** hoist state out of leaf composables.  
- After every code change, mentally verify the app still compiles: no unresolved references, no missing imports.

---

## 1. Project Identity

| Field | Value |
|---|---|
| App Name | LuckyPDF |
| Package | `com.luckypdf.app` |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |
| Compile SDK | 35 |
| Language | Kotlin 2.0.21 |
| Build System | Gradle 8.9 (Kotlin DSL) |
| Architecture | MVVM + Clean Architecture |
| UI Toolkit | Jetpack Compose (BOM 2025.05.00) |
| Design System | Material 3 (Dynamic Color off, custom palette) |

---

## 2. GitHub Codespaces Setup

### 2.1 `.devcontainer/devcontainer.json`
```json
{
  "name": "LuckyPDF Android Dev",
  "image": "mcr.microsoft.com/devcontainers/android:1-34",
  "features": {
    "ghcr.io/devcontainers/features/java:1": {
      "version": "17",
      "jdkDistro": "ms"
    }
  },
  "customizations": {
    "vscode": {
      "extensions": [
        "fwcd.kotlin",
        "GitHub.copilot",
        "GitHub.copilot-chat",
        "mathiasfrohlich.Kotlin",
        "esbenp.prettier-vscode",
        "redhat.java",
        "vscjava.vscode-gradle"
      ],
      "settings": {
        "java.jdt.ls.java.home": "/usr/local/sdkman/candidates/java/current",
        "kotlin.languageServer.enabled": true,
        "editor.formatOnSave": true,
        "editor.defaultFormatter": "esbenp.prettier-vscode",
        "[kotlin]": {
          "editor.defaultFormatter": "fwcd.kotlin"
        }
      }
    }
  },
  "postCreateCommand": "chmod +x ./gradlew && ./gradlew dependencies --quiet",
  "remoteEnv": {
    "ANDROID_SDK_ROOT": "/opt/android-sdk",
    "JAVA_HOME": "/usr/local/sdkman/candidates/java/current"
  },
  "mounts": [
    "source=${localWorkspaceFolder},target=/workspaces/luckypdf,type=bind,consistency=cached"
  ],
  "forwardPorts": [],
  "portsAttributes": {}
}
```

### 2.2 Codespaces Build Commands
```bash
# Assemble debug APK (outputs to app/build/outputs/apk/debug/)
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lintDebug

# Clean build
./gradlew clean assembleDebug

# Generate signed release APK (after keystore setup)
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=$KEYSTORE_PATH \
  -Pandroid.injected.signing.store.password=$KEYSTORE_PASS \
  -Pandroid.injected.signing.key.alias=$KEY_ALIAS \
  -Pandroid.injected.signing.key.password=$KEY_PASS
```

---

## 3. Project File Structure (Complete)

```
LuckyPDF/
├── .devcontainer/
│   └── devcontainer.json
├── .github/
│   └── workflows/
│       └── ci.yml
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml               ← ALL dependency versions live here
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── res/
│       │   │   ├── drawable/
│       │   │   │   ├── ic_launcher_foreground.xml
│       │   │   │   └── ic_launcher_background.xml
│       │   │   ├── mipmap-anydpi-v26/
│       │   │   │   ├── ic_launcher.xml
│       │   │   │   └── ic_launcher_round.xml
│       │   │   ├── values/
│       │   │   │   ├── strings.xml
│       │   │   │   └── themes.xml          ← minimal, Compose handles all theming
│       │   │   └── xml/
│       │   │       ├── file_paths.xml
│       │   │       └── backup_rules.xml
│       │   └── java/com/luckypdf/app/
│       │       ├── LuckyPdfApplication.kt
│       │       ├── MainActivity.kt
│       │       │
│       │       ├── di/
│       │       │   ├── AppModule.kt
│       │       │   ├── DatabaseModule.kt
│       │       │   └── RepositoryModule.kt
│       │       │
│       │       ├── data/
│       │       │   ├── local/
│       │       │   │   ├── db/
│       │       │   │   │   ├── LuckyPdfDatabase.kt
│       │       │   │   │   ├── dao/
│       │       │   │   │   │   ├── DocumentDao.kt
│       │       │   │   │   │   └── FavoriteDao.kt
│       │       │   │   │   └── entity/
│       │       │   │   │       ├── DocumentEntity.kt
│       │       │   │   │       └── FavoriteEntity.kt
│       │       │   │   └── preferences/
│       │       │   │       └── UserPreferencesDataStore.kt
│       │       │   ├── repository/
│       │       │   │   ├── DocumentRepositoryImpl.kt
│       │       │   │   └── FavoriteRepositoryImpl.kt
│       │       │   └── model/
│       │       │       └── DocumentDto.kt
│       │       │
│       │       ├── domain/
│       │       │   ├── model/
│       │       │   │   ├── Document.kt
│       │       │   │   └── PdfPage.kt
│       │       │   ├── repository/
│       │       │   │   ├── DocumentRepository.kt
│       │       │   │   └── FavoriteRepository.kt
│       │       │   └── usecase/
│       │       │       ├── GetRecentDocumentsUseCase.kt
│       │       │       ├── MergePdfsUseCase.kt
│       │       │       ├── SplitPdfUseCase.kt
│       │       │       ├── CompressPdfUseCase.kt
│       │       │       ├── AddWatermarkUseCase.kt
│       │       │       ├── AddSignatureUseCase.kt
│       │       │       ├── ReorderPagesUseCase.kt
│       │       │       ├── RemovePagesUseCase.kt
│       │       │       ├── SearchDocumentsUseCase.kt
│       │       │       └── ExportDocumentUseCase.kt
│       │       │
│       │       ├── ui/
│       │       │   ├── navigation/
│       │       │   │   ├── NavGraph.kt
│       │       │   │   ├── NavRoutes.kt
│       │       │   │   └── NavTransitions.kt
│       │       │   │
│       │       │   ├── theme/
│       │       │   │   ├── Color.kt
│       │       │   │   ├── Type.kt
│       │       │   │   ├── Shape.kt
│       │       │   │   ├── Dimens.kt
│       │       │   │   └── LuckyPdfTheme.kt
│       │       │   │
│       │       │   ├── components/
│       │       │   │   ├── LuckyBottomNav.kt
│       │       │   │   ├── LuckyTopBar.kt
│       │       │   │   ├── LuckyFab.kt
│       │       │   │   ├── ToolCard.kt
│       │       │   │   ├── DocumentCard.kt
│       │       │   │   ├── SectionHeader.kt
│       │       │   │   ├── GradientButton.kt
│       │       │   │   ├── LuckySearchBar.kt
│       │       │   │   ├── EmptyState.kt
│       │       │   │   ├── LoadingOverlay.kt
│       │       │   │   ├── ErrorBanner.kt
│       │       │   │   └── PermissionRationale.kt
│       │       │   │
│       │       │   └── screens/
│       │       │       ├── home/
│       │       │       │   ├── HomeScreen.kt
│       │       │       │   ├── HomeViewModel.kt
│       │       │       │   └── HomeUiState.kt
│       │       │       ├── camera/
│       │       │       │   ├── CameraScreen.kt
│       │       │       │   ├── CameraViewModel.kt
│       │       │       │   ├── CameraUiState.kt
│       │       │       │   └── DocumentEdgeOverlay.kt
│       │       │       ├── viewer/
│       │       │       │   ├── PdfViewerScreen.kt
│       │       │       │   ├── PdfViewerViewModel.kt
│       │       │       │   └── PdfViewerUiState.kt
│       │       │       ├── merge/
│       │       │       │   ├── MergeScreen.kt
│       │       │       │   ├── MergeViewModel.kt
│       │       │       │   └── MergeUiState.kt
│       │       │       ├── split/
│       │       │       │   ├── SplitScreen.kt
│       │       │       │   ├── SplitViewModel.kt
│       │       │       │   └── SplitUiState.kt
│       │       │       ├── compress/
│       │       │       │   ├── CompressScreen.kt
│       │       │       │   ├── CompressViewModel.kt
│       │       │       │   └── CompressUiState.kt
│       │       │       ├── watermark/
│       │       │       │   ├── WatermarkScreen.kt
│       │       │       │   ├── WatermarkViewModel.kt
│       │       │       │   └── WatermarkUiState.kt
│       │       │       ├── sign/
│       │       │       │   ├── SignScreen.kt
│       │       │       │   ├── SignatureCanvas.kt
│       │       │       │   ├── SignViewModel.kt
│       │       │       │   └── SignUiState.kt
│       │       │       ├── edit/
│       │       │       │   ├── EditScreen.kt
│       │       │       │   ├── EditViewModel.kt
│       │       │       │   └── EditUiState.kt
│       │       │       ├── organize/
│       │       │       │   ├── OrganizeScreen.kt
│       │       │       │   ├── OrganizeViewModel.kt
│       │       │       │   └── OrganizeUiState.kt
│       │       │       ├── recent/
│       │       │       │   ├── RecentScreen.kt
│       │       │       │   └── RecentViewModel.kt
│       │       │       ├── favorites/
│       │       │       │   ├── FavoritesScreen.kt
│       │       │       │   └── FavoritesViewModel.kt
│       │       │       └── search/
│       │       │           ├── SearchScreen.kt
│       │       │           └── SearchViewModel.kt
│       │       │
│       │       └── utils/
│       │           ├── FileUtils.kt
│       │           ├── PdfUtils.kt
│       │           ├── BitmapUtils.kt
│       │           ├── PermissionUtils.kt
│       │           └── DateUtils.kt
│       │
│       ├── test/
│       │   └── java/com/luckypdf/app/
│       │       ├── domain/usecase/
│       │       │   ├── MergePdfsUseCaseTest.kt
│       │       │   └── CompressPdfUseCaseTest.kt
│       │       └── ui/viewmodel/
│       │           ├── HomeViewModelTest.kt
│       │           └── PdfViewerViewModelTest.kt
│       │
│       └── androidTest/
│           └── java/com/luckypdf/app/
│               └── ui/
│                   └── HomeScreenTest.kt
│
├── build.gradle.kts                       ← root build file
├── settings.gradle.kts
└── local.properties
```

---

## 4. Gradle Configuration (Exact Files)

### 4.1 `gradle/libs.versions.toml`
```toml
[versions]
agp                  = "8.6.1"
kotlin               = "2.0.21"
ksp                  = "2.0.21-1.0.27"
compose-bom          = "2025.05.00"
activity-compose     = "1.9.3"
lifecycle            = "2.8.7"
navigation-compose   = "2.8.4"
hilt                 = "2.52"
hilt-navigation      = "1.2.0"
room                 = "2.6.1"
datastore            = "1.1.1"
camerax              = "1.4.0"
mlkit-doc            = "16.0.0"
itext7               = "7.2.6"
coil                 = "2.7.0"
coroutines           = "1.9.0"
serialization        = "1.7.3"
accompanist          = "0.36.0"
junit                = "4.13.2"
junit-ext            = "1.2.1"
espresso             = "3.6.1"
mockk                = "1.13.12"
turbine              = "1.2.0"

[libraries]
# Compose BOM
compose-bom                = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui                 = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics        = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling         = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-test-manifest   = { group = "androidx.compose.ui", name = "ui-test-manifest" }
compose-ui-test-junit4     = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-material3          = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons     = { group = "androidx.compose.material", name = "material-icons-extended" }
compose-animation          = { group = "androidx.compose.animation", name = "animation" }
compose-foundation         = { group = "androidx.compose.foundation", name = "foundation" }

# Activity
activity-compose           = { group = "androidx.activity", name = "activity-compose", version.ref = "activity-compose" }

# Lifecycle
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose   = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }

# Navigation
navigation-compose         = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }

# Hilt
hilt-android               = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler              = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose    = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation" }

# Room
room-runtime               = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx                   = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler              = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences      = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# CameraX
camerax-core               = { group = "androidx.camera", name = "camera-core", version.ref = "camerax" }
camerax-camera2            = { group = "androidx.camera", name = "camera-camera2", version.ref = "camerax" }
camerax-lifecycle          = { group = "androidx.camera", name = "camera-lifecycle", version.ref = "camerax" }
camerax-view               = { group = "androidx.camera", name = "camera-view", version.ref = "camerax" }

# ML Kit
mlkit-document-scanner     = { group = "com.google.mlkit", name = "document-scanner", version.ref = "mlkit-doc" }

# iText7 (PDF processing — AGPL; use iText Community for open source)
itext7-core                = { group = "com.itextpdf", name = "itext7-core", version.ref = "itext7" }

# Coil (image loading)
coil-compose               = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Coroutines
coroutines-android         = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
coroutines-test            = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# Serialization
serialization-json         = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }

# Accompanist (permissions)
accompanist-permissions    = { group = "com.google.accompanist", name = "accompanist-permissions", version.ref = "accompanist" }

# Testing
junit                      = { group = "junit", name = "junit", version.ref = "junit" }
junit-ext                  = { group = "androidx.test.ext", name = "junit", version.ref = "junit-ext" }
espresso-core              = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }
mockk                      = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine                    = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }

[plugins]
android-application        = { id = "com.android.application", version.ref = "agp" }
android-library            = { id = "com.android.library", version.ref = "agp" }
kotlin-android             = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose             = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization       = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt                       = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp                        = { id = "com.google.devtools.ksp", version.ref = "ksp" }

[bundles]
compose                    = ["compose-ui", "compose-ui-graphics", "compose-ui-tooling-preview",
                              "compose-material3", "compose-material-icons",
                              "compose-animation", "compose-foundation"]
camerax                    = ["camerax-core", "camerax-camera2", "camerax-lifecycle", "camerax-view"]
lifecycle                  = ["lifecycle-viewmodel-compose", "lifecycle-runtime-compose"]
room                       = ["room-runtime", "room-ktx"]
```

### 4.2 `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        google { content { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") } }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "LuckyPDF"
include(":app")
```

### 4.3 Root `build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android)      apply false
    alias(libs.plugins.kotlin.compose)      apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt)                apply false
    alias(libs.plugins.ksp)                 apply false
}
```

### 4.4 `app/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.luckypdf.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.luckypdf.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    // Compose
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Core
    implementation(libs.activity.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Database
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)

    // Camera + ML Kit
    implementation(libs.bundles.camerax)
    implementation(libs.mlkit.document.scanner)

    // PDF
    implementation(libs.itext7.core)

    // Image Loading
    implementation(libs.coil.compose)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
```

---

## 5. AndroidManifest.xml (Complete)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="29" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"
        android:minSdkVersion="33" />
    <uses-permission android:name="android.permission.READ_MEDIA_DOCUMENTS"
        android:minSdkVersion="33" />

    <!-- Camera feature -->
    <uses-feature android:name="android.hardware.camera" android:required="false" />
    <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

    <application
        android:name=".LuckyPdfApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/backup_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.LuckyPDF"
        android:enableOnBackInvokedCallback="true"
        android:hardwareAccelerated="true">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <!-- Handle incoming PDFs from other apps -->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="application/pdf" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="application/pdf" />
            </intent-filter>
        </activity>

        <!-- FileProvider for sharing files on API 24+ -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>
</manifest>
```

### `res/xml/file_paths.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path name="pdf_files" path="pdfs/" />
    <files-path name="scan_files" path="scans/" />
    <cache-path name="cache_files" path="." />
    <external-files-path name="external_files" path="." />
</paths>
```

---

## 6. Theme System (Exact Tokens)

### 6.1 `ui/theme/Color.kt`
```kotlin
package com.luckypdf.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Background layers (Discord-inspired dark palette) ──────────────────────
val BackgroundDeep    = Color(0xFF0F1117)   // deepest layer: page background
val BackgroundBase    = Color(0xFF1A1D26)   // main screen background
val BackgroundPanel   = Color(0xFF212534)   // card / panel surface
val BackgroundElevated = Color(0xFF2A2F42)  // elevated cards / bottom sheet

// ── Brand Accent ────────────────────────────────────────────────────────────
val AccentPrimary     = Color(0xFF5865F2)   // Discord Blurple — primary action
val AccentSecondary   = Color(0xFF7983F5)   // lighter blurple for gradients
val AccentTertiary    = Color(0xFF9B63F5)   // purple shift for gradient end

// ── Semantic Colors ─────────────────────────────────────────────────────────
val SuccessGreen      = Color(0xFF3BA55C)
val WarningAmber      = Color(0xFFFAA61A)
val ErrorRed          = Color(0xFFED4245)
val InfoBlue          = Color(0xFF4FADE9)

// ── Text ────────────────────────────────────────────────────────────────────
val TextPrimary       = Color(0xFFFFFFFF)
val TextSecondary     = Color(0xFFB9BBBE)
val TextMuted         = Color(0xFF72767D)
val TextOnAccent      = Color(0xFFFFFFFF)

// ── Strokes & Dividers ──────────────────────────────────────────────────────
val DividerColor      = Color(0xFF2E3144)
val StrokeColor       = Color(0xFF383C52)

// ── Gradients (use with Brush.linearGradient / Brush.radialGradient) ────────
val GradientAccent    = listOf(AccentPrimary, AccentTertiary)
val GradientSurface   = listOf(BackgroundPanel, BackgroundElevated)
val GradientScanner   = listOf(Color(0xFF00C9FF), Color(0xFF5865F2))
```

### 6.2 `ui/theme/Type.kt`
```kotlin
package com.luckypdf.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default — swap with a custom font by adding a font resource
val LuckyTypography = Typography(
    displayLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 28.sp, lineHeight = 34.sp),
    displaySmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 24.sp, lineHeight = 30.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineMedium= TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    titleLarge    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
```

### 6.3 `ui/theme/Shape.kt`
```kotlin
package com.luckypdf.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val LuckyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
```

### 6.4 `ui/theme/Dimens.kt`
```kotlin
package com.luckypdf.app.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Dimens {
    // Spacing scale (8-point grid)
    val SpaceXs   = 4.dp
    val SpaceSm   = 8.dp
    val SpaceMd   = 12.dp
    val SpaceLg   = 16.dp
    val SpaceXl   = 24.dp
    val Space2xl  = 32.dp
    val Space3xl  = 48.dp
    val Space4xl  = 64.dp

    // Component sizes
    val ToolCardHeight      = 100.dp
    val ToolCardWidth       = 160.dp
    val DocumentCardHeight  = 80.dp
    val BottomNavHeight     = 64.dp
    val TopBarHeight        = 56.dp
    val FabSize             = 56.dp
    val IconSizeSm          = 20.dp
    val IconSizeMd          = 24.dp
    val IconSizeLg          = 32.dp
    val IconSizeXl          = 48.dp

    // Corner radius (use LuckyShapes instead when possible)
    val RadiusSm = 8.dp
    val RadiusMd = 12.dp
    val RadiusLg = 16.dp
    val RadiusXl = 24.dp
    val RadiusFull = 999.dp

    // Elevation
    val ElevationLow  = 2.dp
    val ElevationMid  = 4.dp
    val ElevationHigh = 8.dp

    // Screen
    val ScreenPaddingH = 16.dp
    val ScreenPaddingV = 16.dp
}
```

### 6.5 `ui/theme/LuckyPdfTheme.kt`
```kotlin
package com.luckypdf.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = AccentPrimary,
    onPrimary        = TextOnAccent,
    primaryContainer = AccentSecondary,
    onPrimaryContainer = TextPrimary,
    secondary        = AccentTertiary,
    onSecondary      = TextOnAccent,
    background       = BackgroundBase,
    onBackground     = TextPrimary,
    surface          = BackgroundPanel,
    onSurface        = TextPrimary,
    surfaceVariant   = BackgroundElevated,
    onSurfaceVariant = TextSecondary,
    error            = ErrorRed,
    onError          = TextOnAccent,
    outline          = StrokeColor,
    outlineVariant   = DividerColor,
)

@Composable
fun LuckyPdfTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = LuckyTypography,
        shapes      = LuckyShapes,
        content     = content,
    )
}
```

---

## 7. Architecture Pattern (MVVM + Clean Architecture)

### 7.1 Layer Rules

```
UI Layer         → Composables + ViewModels
Domain Layer     → UseCases + Domain Models + Repository Interfaces
Data Layer       → Repository Implementations + Room DAOs + DataStore
```

- **Composables** call ViewModel functions and observe `uiState: StateFlow<XxxUiState>`.  
- **ViewModels** call UseCases. Never touch DAOs directly from a ViewModel.  
- **UseCases** call Repository interfaces. One public `invoke()` / `execute()` function each.  
- **Repositories** (impl) call DAOs or external sources and map to domain models.  
- Domain models **never** import Android or Data layer classes.

### 7.2 UiState Pattern (Required for Every Screen)

```kotlin
// Every screen uses this sealed class pattern:
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val recentDocuments: List<Document>,
        val favoriteDocuments: List<Document>,
        val storageUsedBytes: Long,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// ViewModel:
class HomeViewModel @Inject constructor(
    private val getRecentDocuments: GetRecentDocumentsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            runCatching { getRecentDocuments() }
                .onSuccess { docs -> _uiState.value = HomeUiState.Success(docs, emptyList(), 0L) }
                .onFailure { e -> _uiState.value = HomeUiState.Error(e.message ?: "Unknown error") }
        }
    }
}

// Screen:
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToViewer: (documentId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState) {
        is HomeUiState.Loading -> LoadingOverlay()
        is HomeUiState.Error   -> ErrorBanner(message = (uiState as HomeUiState.Error).message)
        is HomeUiState.Success -> HomeContent(
            state = uiState as HomeUiState.Success,
            onDocumentClick = onNavigateToViewer,
        )
    }
}
```

### 7.3 UseCase Template

```kotlin
// domain/usecase/GetRecentDocumentsUseCase.kt
class GetRecentDocumentsUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
) {
    /** Returns the 20 most recently opened documents, sorted by lastOpened desc. */
    suspend operator fun invoke(): List<Document> =
        documentRepository.getRecentDocuments(limit = 20)
}
```

### 7.4 Repository Template

```kotlin
// domain/repository/DocumentRepository.kt  (interface — in domain layer)
interface DocumentRepository {
    suspend fun getRecentDocuments(limit: Int): List<Document>
    suspend fun getDocumentById(id: String): Document?
    suspend fun saveDocument(document: Document): String
    suspend fun deleteDocument(id: String)
    fun searchDocuments(query: String): Flow<List<Document>>
}

// data/repository/DocumentRepositoryImpl.kt  (implementation — in data layer)
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
) : DocumentRepository {
    override suspend fun getRecentDocuments(limit: Int): List<Document> =
        documentDao.getRecent(limit).map { it.toDomain() }
    // ... rest of implementations
}
```

---

## 8. Navigation System

### 8.1 `NavRoutes.kt`
```kotlin
package com.luckypdf.app.ui.navigation

import kotlinx.serialization.Serializable

// Type-safe Navigation using Kotlin Serialization
@Serializable object HomeRoute
@Serializable object CameraRoute
@Serializable object RecentRoute
@Serializable object FavoritesRoute
@Serializable object SearchRoute
@Serializable data class PdfViewerRoute(val documentId: String, val documentUri: String)
@Serializable data class MergeRoute(val documentIds: List<String> = emptyList())
@Serializable data class SplitRoute(val documentId: String)
@Serializable data class CompressRoute(val documentId: String)
@Serializable data class WatermarkRoute(val documentId: String)
@Serializable data class SignRoute(val documentId: String)
@Serializable data class EditRoute(val documentId: String)
@Serializable data class OrganizeRoute(val documentId: String)

// Bottom nav items
enum class BottomNavItem(val route: Any, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home(HomeRoute, R.string.nav_home, Icons.Rounded.Home),
    Recent(RecentRoute, R.string.nav_recent, Icons.Rounded.History),
    Camera(CameraRoute, R.string.nav_scan, Icons.Rounded.CameraAlt),
    Favorites(FavoritesRoute, R.string.nav_favorites, Icons.Rounded.BookmarkBorder),
    Search(SearchRoute, R.string.nav_search, Icons.Rounded.Search),
}
```

### 8.2 `NavGraph.kt`
```kotlin
package com.luckypdf.app.ui.navigation

@Composable
fun LuckyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = { NavTransitions.slideInFromRight },
        exitTransition  = { NavTransitions.slideOutToLeft },
        popEnterTransition  = { NavTransitions.slideInFromLeft },
        popExitTransition   = { NavTransitions.slideOutToRight },
    ) {
        composable<HomeRoute>     { HomeScreen(onNavigateToViewer = { id -> navController.navigate(PdfViewerRoute(id, "")) }) }
        composable<CameraRoute>   { CameraScreen(onDocumentScanned = { uri -> navController.navigate(PdfViewerRoute("", uri)) }) }
        composable<RecentRoute>   { RecentScreen(onDocumentClick = { id -> navController.navigate(PdfViewerRoute(id, "")) }) }
        composable<FavoritesRoute>{ FavoritesScreen(onDocumentClick = { id -> navController.navigate(PdfViewerRoute(id, "")) }) }
        composable<SearchRoute>   { SearchScreen(onDocumentClick = { id -> navController.navigate(PdfViewerRoute(id, "")) }) }
        composable<PdfViewerRoute>{ PdfViewerScreen(onNavigateBack = navController::navigateUp) }
        composable<MergeRoute>    { MergeScreen(onNavigateBack = navController::navigateUp) }
        composable<SplitRoute>    { SplitScreen(onNavigateBack = navController::navigateUp) }
        composable<CompressRoute> { CompressScreen(onNavigateBack = navController::navigateUp) }
        composable<WatermarkRoute>{ WatermarkScreen(onNavigateBack = navController::navigateUp) }
        composable<SignRoute>     { SignScreen(onNavigateBack = navController::navigateUp) }
        composable<EditRoute>     { EditScreen(onNavigateBack = navController::navigateUp) }
        composable<OrganizeRoute> { OrganizeScreen(onNavigateBack = navController::navigateUp) }
    }
}
```

### 8.3 `NavTransitions.kt`
```kotlin
package com.luckypdf.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object NavTransitions {
    private const val DURATION = 350

    val slideInFromRight = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(DURATION),
    ) + fadeIn(animationSpec = tween(DURATION))

    val slideOutToLeft = slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(DURATION),
    ) + fadeOut(animationSpec = tween(DURATION))

    val slideInFromLeft = slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(DURATION),
    ) + fadeIn(animationSpec = tween(DURATION))

    val slideOutToRight = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(DURATION),
    ) + fadeOut(animationSpec = tween(DURATION))

    val fadeIn = fadeIn(animationSpec = tween(DURATION))
    val fadeOut = fadeOut(animationSpec = tween(DURATION))
}
```

---

## 9. Reusable Component Library

### 9.1 `GradientButton.kt` (Primary Action Button)
```kotlin
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "button_scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(
                brush = if (enabled) Brush.linearGradient(GradientAccent)
                        else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray)),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = Dimens.SpaceXl, vertical = Dimens.SpaceMd),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
            icon?.let { Icon(imageVector = it, contentDescription = null, tint = TextOnAccent, modifier = Modifier.size(Dimens.IconSizeMd)) }
            Text(text = text, style = MaterialTheme.typography.labelLarge, color = TextOnAccent)
        }
    }
}
```

### 9.2 `ToolCard.kt` (Home Screen Tool Grid)
```kotlin
@Composable
fun ToolCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = GradientAccent,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) Dimens.ElevationLow else Dimens.ElevationMid,
        label = "card_elevation",
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_scale",
    )

    Card(
        onClick = onClick,
        modifier = modifier.scale(scale).size(width = Dimens.ToolCardWidth, height = Dimens.ToolCardHeight),
        shape = RoundedCornerShape(Dimens.RadiusLg),
        colors = CardDefaults.cardColors(containerColor = BackgroundPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        interactionSource = interactionSource,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(Dimens.SpaceMd),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.IconSizeXl)
                    .clip(RoundedCornerShape(Dimens.RadiusSm))
                    .background(Brush.radialGradient(gradientColors, radius = 120f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(Dimens.IconSizeLg))
            }
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = TextPrimary, maxLines = 2)
        }
    }
}
```

### 9.3 `DocumentCard.kt`
```kotlin
@Composable
fun DocumentCard(
    document: Document,
    onClick: () -> Unit,
    onMenuClick: (DocumentAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(Dimens.DocumentCardHeight),
        shape = RoundedCornerShape(Dimens.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = BackgroundPanel),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
            // PDF thumbnail
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(Dimens.RadiusSm))
                    .background(BackgroundElevated),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PictureAsPdf, null, tint = AccentPrimary, modifier = Modifier.size(28.dp))
            }
            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(document.name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${document.formattedSize} • ${document.formattedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }
            // Context menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More options", tint = TextSecondary)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DocumentAction.entries.forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action.label) },
                            leadingIcon = { Icon(action.icon, contentDescription = null) },
                            onClick = { showMenu = false; onMenuClick(action) },
                        )
                    }
                }
            }
        }
    }
}

enum class DocumentAction(val label: String, val icon: ImageVector) {
    Share("Share", Icons.Rounded.Share),
    Rename("Rename", Icons.Rounded.DriveFileRenameOutline),
    Favorite("Favorite", Icons.Rounded.BookmarkAdd),
    Delete("Delete", Icons.Rounded.Delete),
}
```

### 9.4 `LoadingOverlay.kt`
```kotlin
@Composable
fun LoadingOverlay(message: String = "Loading...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg)) {
            CircularProgressIndicator(color = AccentPrimary, trackColor = BackgroundElevated)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}
```

### 9.5 `EmptyState.kt`
```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Dimens.SpaceXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(Dimens.SpaceLg))
        Text(title, style = MaterialTheme.typography.titleMedium, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Dimens.SpaceSm))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMuted, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(Dimens.SpaceXl))
            GradientButton(text = actionLabel, onClick = onAction)
        }
    }
}
```

---

## 10. Camera & Document Scanning

### 10.1 CameraScreen Strategy
- Use `ML Kit Document Scanner` API for the entire scanning flow.
- Fallback to `CameraX` preview + manual crop for advanced control.
- Do NOT use deprecated `Camera1` APIs.

### 10.2 ML Kit Document Scanner Pattern
```kotlin
// In CameraViewModel:
class CameraViewModel @Inject constructor() : ViewModel() {
    fun buildDocumentScanner(): GmsDocumentScanner {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(20)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .setScannerMode(SCANNER_MODE_FULL)
            .build()
        return GmsDocumentScanning.getClient(options)
    }
}

// In CameraScreen:
@Composable
fun CameraScreen(onDocumentScanned: (String) -> Unit) {
    val viewModel: CameraViewModel = hiltViewModel()
    val context = LocalContext.current
    val scanner = remember { viewModel.buildDocumentScanner() }

    val scanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanResult?.pdf?.uri?.let { uri -> onDocumentScanned(uri.toString()) }
        }
    }

    LaunchedEffect(Unit) {
        scanner.getStartScanIntent(context as Activity)
            .addOnSuccessListener { intentSender ->
                scanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { /* handle error */ }
    }
}
```

### 10.3 CameraX Preview (for custom UI)
```kotlin
@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                cameraProviderFuture.addListener({
                    cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier,
    )

    DisposableEffect(Unit) { onDispose { cameraProvider?.unbindAll() } }
}
```

---

## 11. PDF Processing (iText7)

### 11.1 Utility Object Pattern
```kotlin
// utils/PdfUtils.kt
object PdfUtils {
    /**
     * Merges multiple PDF files into a single output PDF.
     * @param inputPaths List of absolute file paths to merge.
     * @param outputPath Absolute path for the merged PDF.
     */
    suspend fun mergePdfs(inputPaths: List<String>, outputPath: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val merger = PdfMerger(PdfWriter(outputPath))
                inputPaths.forEach { path ->
                    val reader = PdfReader(path)
                    val srcDoc = PdfDocument(reader)
                    merger.merge(srcDoc, 1, srcDoc.numberOfPages)
                    srcDoc.close()
                }
                merger.close()
                outputPath
            }
        }

    /** Splits a PDF at the given page numbers (1-indexed). */
    suspend fun splitPdf(inputPath: String, outputDir: String, splitAt: List<Int>): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val outputPaths = mutableListOf<String>()
                PdfDocument(PdfReader(inputPath)).use { source ->
                    val pageGroups = buildPageGroups(splitAt, source.numberOfPages)
                    pageGroups.forEachIndexed { index, pages ->
                        val outPath = "$outputDir/split_${index + 1}.pdf"
                        PdfDocument(PdfWriter(outPath)).use { dest ->
                            source.copyPagesTo(pages, dest)
                        }
                        outputPaths.add(outPath)
                    }
                }
                outputPaths
            }
        }

    /** Adds a diagonal text watermark to every page. */
    suspend fun addWatermark(inputPath: String, outputPath: String, text: String, opacity: Float = 0.3f): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                PdfDocument(PdfReader(inputPath), PdfWriter(outputPath)).use { pdf ->
                    val doc = Document(pdf)
                    val font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                    for (i in 1..pdf.numberOfPages) {
                        val page = pdf.getPage(i)
                        val canvas = PdfCanvas(page.newContentStreamBefore(), page.resources, pdf)
                        canvas.saveState()
                            .setExtGState(PdfExtGState().setFillOpacity(opacity))
                            .beginText()
                            .setFontAndSize(font, 60f)
                            .setFillColorRgb(0.7f, 0.7f, 0.7f)
                            .setTextMatrix(AffineTransform.getRotateInstance(Math.toRadians(45.0), 0.0, 0.0))
                            .showText(text)
                            .endText()
                            .restoreState()
                    }
                }
                outputPath
            }
        }

    /** Compresses a PDF by downsampling images. */
    suspend fun compressPdf(inputPath: String, outputPath: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val readerProps = ReaderProperties().setPasswordIfEncrypted(null)
                PdfDocument(PdfReader(inputPath, readerProps), PdfWriter(outputPath, WriterProperties().setCompressionLevel(9))).use { /* just re-write with max compression */ }
                outputPath
            }
        }

    private fun buildPageGroups(splitAt: List<Int>, totalPages: Int): List<List<Int>> {
        val sorted = (splitAt + listOf(totalPages + 1)).sorted()
        var start = 1
        return sorted.map { end ->
            (start until end).toList().also { start = end }
        }.filter { it.isNotEmpty() }
    }
}
```

---

## 12. Room Database

### 12.1 Entity
```kotlin
// data/local/db/entity/DocumentEntity.kt
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val filePath: String,
    val sizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val thumbnailPath: String? = null,
)

fun DocumentEntity.toDomain() = Document(
    id = id, name = name, filePath = filePath,
    sizeBytes = sizeBytes, pageCount = pageCount,
    createdAt = Instant.ofEpochMilli(createdAt),
    lastOpenedAt = Instant.ofEpochMilli(lastOpenedAt),
    isFavorite = isFavorite, thumbnailPath = thumbnailPath,
)
```

### 12.2 DAO
```kotlin
// data/local/db/dao/DocumentDao.kt
@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY lastOpenedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE name LIKE '%' || :query || '%' OR filePath LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: String): DocumentEntity?

    @Upsert
    suspend fun upsert(entity: DocumentEntity)

    @Delete
    suspend fun delete(entity: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE documents SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE documents SET lastOpenedAt = :timestamp WHERE id = :id")
    suspend fun updateLastOpened(id: String, timestamp: Long = System.currentTimeMillis())
}
```

### 12.3 Database
```kotlin
// data/local/db/LuckyPdfDatabase.kt
@Database(entities = [DocumentEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class LuckyPdfDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    companion object {
        const val DATABASE_NAME = "lucky_pdf.db"
    }
}
```

---

## 13. Permissions Handling

### 13.1 Pattern (use Accompanist)
```kotlin
// Always group related permissions together
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionWrapper(content: @Composable () -> Unit) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        cameraPermission.status.isGranted -> content()
        cameraPermission.status.shouldShowRationale -> {
            PermissionRationale(
                icon = Icons.Rounded.CameraAlt,
                title = stringResource(R.string.permission_camera_title),
                rationale = stringResource(R.string.permission_camera_rationale),
                onGrantClick = cameraPermission::launchPermissionRequest,
            )
        }
        else -> LaunchedEffect(Unit) { cameraPermission.launchPermissionRequest() }
    }
}

// Storage permissions (version-aware)
fun getStoragePermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
} else {
    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}
```

---

## 14. Animation Guidelines

| Interaction | API | Spec |
|---|---|---|
| Button press scale | `animateFloatAsState` | `0.96f`, `Spring.StiffnessMediumLow` |
| Card press scale | `animateFloatAsState` | `0.97f`, `Spring.StiffnessMedium` |
| Card elevation | `animateDpAsState` | `2dp → 4dp`, default tween |
| Screen transitions | `slideIn/Out + fadeIn/Out` | `tween(350ms)` |
| Content appearance | `AnimatedVisibility` | `fadeIn + expandVertically` |
| Loading shimmer | `InfiniteTransition` | brush alpha `0.3f → 0.9f`, 1200ms |
| FAB entrance | `AnimatedVisibility` | `scaleIn + fadeIn` |
| Bottom sheet | default `ModalBottomSheet` | no overrides needed |

### Shimmer Example
```kotlin
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "shimmer_alpha",
    )
    Box(modifier = modifier.clip(RoundedCornerShape(Dimens.RadiusSm)).background(BackgroundElevated.copy(alpha = alpha)))
}
```

---

## 15. Hilt Dependency Injection

### 15.1 Application
```kotlin
@HiltAndroidApp
class LuckyPdfApplication : Application()
```

### 15.2 AppModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): LuckyPdfDatabase =
        Room.databaseBuilder(ctx, LuckyPdfDatabase::class.java, LuckyPdfDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideDocumentDao(db: LuckyPdfDatabase): DocumentDao = db.documentDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository
}
```

### 15.3 MainActivity
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuckyPdfTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { LuckyBottomNav(navController) },
                    contentWindowInsets = WindowInsets.safeDrawing,
                ) { innerPadding ->
                    LuckyNavGraph(navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
```

---

## 16. Domain Models

```kotlin
// domain/model/Document.kt
data class Document(
    val id: String,
    val name: String,
    val filePath: String,
    val sizeBytes: Long,
    val pageCount: Int,
    val createdAt: Instant,
    val lastOpenedAt: Instant,
    val isFavorite: Boolean = false,
    val thumbnailPath: String? = null,
) {
    val formattedSize: String get() = when {
        sizeBytes < 1024       -> "${sizeBytes} B"
        sizeBytes < 1024 * 1024 -> "${sizeBytes / 1024} KB"
        else                   -> "${"%.1f".format(sizeBytes / 1024.0 / 1024.0)} MB"
    }
    val formattedDate: String get() = DateUtils.formatRelative(lastOpenedAt)
}
```

---

## 17. Testing Standards

### 17.1 ViewModel Unit Test Template
```kotlin
class HomeViewModelTest {
    @get:Rule val coroutineRule = MainCoroutineRule()

    private val getRecentDocuments = mockk<GetRecentDocumentsUseCase>()
    private lateinit var viewModel: HomeViewModel

    @Before fun setUp() {
        coEvery { getRecentDocuments() } returns emptyList()
        viewModel = HomeViewModel(getRecentDocuments)
    }

    @Test fun `initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertIs<HomeUiState.Loading>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun `success state has documents`() = runTest {
        val docs = listOf(DocumentFixtures.sampleDocument())
        coEvery { getRecentDocuments() } returns docs
        viewModel.loadData()
        viewModel.uiState.test {
            skipItems(1) // skip Loading
            val state = awaitItem()
            assertIs<HomeUiState.Success>(state)
            assertEquals(docs, state.recentDocuments)
        }
    }
}
```

### 17.2 UseCase Unit Test Template
```kotlin
class MergePdfsUseCaseTest {
    private val repository = mockk<DocumentRepository>()
    private val useCase = MergePdfsUseCase(repository)

    @Test fun `merging empty list throws exception`() = runTest {
        assertThrows<IllegalArgumentException> { useCase(emptyList(), "/output/merged.pdf") }
    }
}
```

---

## 18. Security & Privacy Rules

- All PDF processing (merge, split, compress, watermark, sign) must execute on the device. Never send file bytes to an external server unless the user explicitly enables a cloud feature.
- Use `FileProvider` for all URI sharing between apps. Never expose raw `file://` URIs.
- Store user files only in `Context.filesDir` or `Context.getExternalFilesDir()` (app-scoped storage). Never write to `Environment.getExternalStorageDirectory()` directly.
- Encrypt sensitive metadata (file paths, names) in DataStore using `EncryptedDataStore` if the feature warrants it.
- Request permissions at the point of use (runtime), not at app launch.
- Never log file contents, paths, or names to Logcat in release builds.
- Use `BuildConfig.DEBUG` guards around all debug logging.

---

## 19. File Utility Functions

```kotlin
// utils/FileUtils.kt
object FileUtils {

    /** Creates a new PDF file in the app's private pdf directory. */
    fun Context.createPdfFile(name: String): File {
        val dir = File(filesDir, "pdfs").apply { mkdirs() }
        return File(dir, "${name}_${System.currentTimeMillis()}.pdf")
    }

    /** Creates a new scan image file in the scans directory. */
    fun Context.createScanFile(): File {
        val dir = File(filesDir, "scans").apply { mkdirs() }
        return File(dir, "scan_${System.currentTimeMillis()}.jpg")
    }

    /** Copies a content:// URI to a local file and returns the File. */
    suspend fun Context.copyUriToFile(uri: Uri, destination: File): File =
        withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)!!.use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            destination
        }

    /** Returns a shareable URI via FileProvider. */
    fun Context.getShareUri(file: File): Uri =
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

    /** Returns human-readable file size string. */
    fun Long.toReadableSize(): String = when {
        this < 1_024L        -> "$this B"
        this < 1_048_576L    -> "${this / 1_024} KB"
        else                 -> "${"%.1f".format(this / 1_048_576.0)} MB"
    }
}
```

---

## 20. CI/CD (GitHub Actions)

### `.github/workflows/ci.yml`
```yaml
name: LuckyPDF CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'microsoft'

      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts', '**/libs.versions.toml') }}

      - name: Make gradlew executable
        run: chmod +x ./gradlew

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Run lint
        run: ./gradlew lintDebug

      - name: Assemble debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: luckypdf-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

---

## 21. Home Screen Feature Specification

### Tool Grid Layout (HomeScreen)
Tools appear in a horizontally scrolling row at the top, then a vertical list of recent documents below.

| Tool | Icon | Gradient End Colors | Navigation |
|---|---|---|---|
| Scan | `CameraAlt` | `[#5865F2, #9B63F5]` | `CameraRoute` |
| Merge | `MergeType` | `[#5865F2, #00B4D8]` | `MergeRoute` |
| Split | `CallSplit` | `[#F25858, #F2A158]` | `SplitRoute` |
| Compress | `Compress` | `[#3BA55C, #5865F2]` | `CompressRoute` |
| Watermark | `Branding Watermark` | `[#FAA61A, #F25858]` | `WatermarkRoute` |
| Sign | `Draw` | `[#9B63F5, #5865F2]` | `SignRoute` |
| Edit | `Edit` | `[#00B4D8, #3BA55C]` | `EditRoute` |
| Organize | `Dashboard` | `[#FAA61A, #9B63F5]` | `OrganizeRoute` |

---

## 22. Copilot Chat Prompt Examples

Use these prompts in GitHub Copilot Chat (Codespaces) for maximum precision:

```
# Generate a new screen
"Generate a CompressScreen.kt, CompressViewModel.kt, and CompressUiState.kt 
following the exact MVVM pattern in HomeScreen.kt. The screen should let users 
pick a PDF, show compression progress, and display before/after file sizes."

# Add a new composable
"Add a PdfPageThumbnail composable to components/ that accepts a filePath: String 
and pageNumber: Int, renders the page as a Bitmap using PdfRenderer, and 
displays it with a shimmer loading state using our ShimmerBox pattern."

# Fix a bug
"The MergeViewModel crashes when merging more than 10 PDFs. Look at MergeViewModel.kt 
and PdfUtils.mergePdfs(). The issue is likely memory — fix it by processing files 
in batches of 3 on Dispatchers.IO."

# Add animation
"Add entrance animations to the ToolCard grid in HomeScreen. Each card should 
appear with a staggered fadeIn + slideUp, 80ms delay between cards, using 
AnimatedVisibility and LaunchedEffect."

# Theme question
"What is the correct color token for muted secondary text in LuckyPDF? Show 
how to apply it in a Text composable."
```

---

## 23. Common Mistakes — Never Do These

| ❌ Wrong | ✅ Correct |
|---|---|
| `LiveData<UiState>` in ViewModel | `StateFlow<UiState>` |
| `GlobalScope.launch { }` | `viewModelScope.launch { }` |
| XML layout with `inflate()` | Jetpack Compose composable |
| `Groovy build.gradle` | `Kotlin build.gradle.kts` |
| Hardcoded `Color(0xFFAABBCC)` in composable | Use theme token from `Color.kt` |
| `16.dp` magic number | `Dimens.SpaceLg` |
| `Thread.sleep()` or blocking I/O on main | `withContext(Dispatchers.IO) { }` |
| Direct DAO call from Screen/ViewModel | Call UseCase from ViewModel |
| `file://` URI for sharing | `FileProvider.getUriForFile(...)` |
| `bitmap.compress()` on main thread | `withContext(Dispatchers.Default) { }` |
| `remember { mutableStateOf(...) }` in ViewModel | `MutableStateFlow` in ViewModel, `remember` only in Composables |
| Using `@Preview` without `LuckyPdfTheme {}` wrapper | Always wrap `@Preview` in `LuckyPdfTheme {}` |

---

## 24. Proguard Rules (`proguard-rules.pro`)

```
# iText7
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.luckypdf.app.**$$serializer { *; }
-keepclassmembers class com.luckypdf.app.** {
    *** Companion;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
```

---

*End of LuckyPDF Copilot Skill v2.0*
