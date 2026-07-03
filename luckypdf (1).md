# LuckyPDF — Production-Ready Copilot Project Prompt
> **Platform:** Native Android · **Language:** Kotlin · **UI:** Jetpack Compose + Material 3
> **Theme:** Discord-Inspired Dark · **Output:** Signed, release-ready APK

---

## 0. Copilot Prime Directive

You are the lead Android engineer on **LuckyPDF**. Every piece of code you write must be:

- **Kotlin-only.** No Java, no Groovy, no XML layouts.
- **Compose-only.** No `inflate()`, no `Fragment`, no `View`.
- **Production-ready.** No `TODO()`, no `println()`, no `Thread.sleep()`.
- **Type-safe.** No `!!` force-unwraps, no unchecked casts, no `Any` parameters.
- **Themed.** Every color references the Discord token system defined below — no hardcoded hex values inside composables.
- **Accessible.** Every interactive element has a `contentDescription`. Touch targets ≥ 48dp.
- **Error-proof.** Every suspend function is wrapped in `runCatching` / `Result`. Every UiState has an Error variant.
- **Performant.** No blocking calls on the main thread. No memory leaks. No unnecessary recompositions.

When in doubt: write less, make it correct, make it readable.

---

## 1. Project Identity

```
App Name        : LuckyPDF
Package         : com.luckypdf.app
Version Name    : 1.0.0
Version Code    : 1
Min SDK         : 26  (Android 8.0 Oreo)
Target SDK      : 35
Compile SDK     : 35
Kotlin          : 2.0.21
Compose BOM     : 2025.05.00
AGP             : 8.6.1
Build System    : Gradle 8.9 — Kotlin DSL only
Architecture    : MVVM + Clean Architecture (3 layers)
DI              : Hilt 2.52
Database        : Room 2.6.1
JDK             : 17
```

---

## 2. Discord-Inspired Color System (Complete Token Set)

Every color in the entire app must reference one of these tokens. **Never** write a hex value directly inside a composable.

### 2.1 Background Layers

| Token | Hex | Usage |
|---|---|---|
| `BG_DEEPEST` | `#0E0F13` | Splash, fullscreen video, camera preview behind chrome |
| `BG_BASE` | `#1A1D26` | Default screen background (`MaterialTheme.colorScheme.background`) |
| `BG_PANEL` | `#212534` | Cards, list items, bottom sheets surface |
| `BG_ELEVATED` | `#2B2F42` | Elevated cards, dialog backgrounds, tooltips |
| `BG_OVERLAY` | `#1A1D2699` | Scrim behind modals (60% alpha) |
| `BG_SCAN_OVERLAY` | `#0000007A` | Camera screen dark vignette outside document |

### 2.2 Brand Accent (Primary Palette)

| Token | Hex | Usage |
|---|---|---|
| `ACCENT_PRIMARY` | `#5865F2` | Primary buttons, active nav icons, focus rings, FAB |
| `ACCENT_SECONDARY` | `#7983F5` | Gradient midpoint, secondary actions, chips |
| `ACCENT_TERTIARY` | `#9B63F5` | Gradient end, scanner corner brackets, badge fill |
| `ACCENT_GLOW` | `#5865F240` | Soft glow behind accent elements (25% alpha) |
| `ACCENT_ON` | `#FFFFFF` | Text/icons on top of any accent surface |

### 2.3 Gradient Definitions

```kotlin
// Declare in Color.kt as Brush values
val GradientBrand   = Brush.linearGradient(listOf(Color(0xFF5865F2), Color(0xFF9B63F5)))
val GradientCool    = Brush.linearGradient(listOf(Color(0xFF5865F2), Color(0xFF00B4D8)))
val GradientWarm    = Brush.linearGradient(listOf(Color(0xFFF25858), Color(0xFFF2A158)))
val GradientSuccess = Brush.linearGradient(listOf(Color(0xFF3BA55C), Color(0xFF5865F2)))
val GradientScan    = Brush.linearGradient(listOf(Color(0xFF00C9FF), Color(0xFF5865F2)))
val GradientSunset  = Brush.linearGradient(listOf(Color(0xFF9B63F5), Color(0xFFFAA61A)))
val GradientOcean   = Brush.linearGradient(listOf(Color(0xFF00B4D8), Color(0xFF3BA55C)))
```

### 2.4 Text Colors

| Token | Hex | Usage |
|---|---|---|
| `TEXT_PRIMARY` | `#FFFFFF` | Headings, primary body text |
| `TEXT_SECONDARY` | `#B9BBBE` | Subtitles, metadata, labels |
| `TEXT_MUTED` | `#72767D` | Placeholders, disabled states, empty state body |
| `TEXT_LINK` | `#5865F2` | Tappable links, "See all" actions |
| `TEXT_ON_ACCENT` | `#FFFFFF` | Text rendered on top of accent-colored surfaces |
| `TEXT_ERROR` | `#ED4245` | Error messages, destructive action labels |
| `TEXT_SUCCESS` | `#3BA55C` | Success feedback, "Done" states |
| `TEXT_WARNING` | `#FAA61A` | Warnings, caution indicators |

### 2.5 Semantic Colors

| Token | Hex | Usage |
|---|---|---|
| `SEMANTIC_ERROR` | `#ED4245` | Error icons, border rings, snackbar fill |
| `SEMANTIC_SUCCESS` | `#3BA55C` | Success icons, check animations |
| `SEMANTIC_WARNING` | `#FAA61A` | Warning banners, permission nudges |
| `SEMANTIC_INFO` | `#4FADE9` | Info tooltips, "beta" badges |

### 2.6 Structural Colors

| Token | Hex | Usage |
|---|---|---|
| `DIVIDER` | `#2E3144` | `HorizontalDivider`, section separators |
| `STROKE` | `#383C52` | Card borders, input field outlines |
| `STROKE_FOCUS` | `#5865F2` | Focused input border, selected card ring |
| `SHADOW` | `#00000050` | Box shadows (via `drawBehind` with blur) |
| `SCAN_BRACKET` | `#5865F2` | Corner bracket lines of the document finder overlay |
| `SCAN_LINE` | `#00C9FF` | Animated scanning line color |

### 2.7 Material 3 Color Scheme Mapping

```kotlin
darkColorScheme(
    primary            = Color(0xFF5865F2),   // ACCENT_PRIMARY
    onPrimary          = Color(0xFFFFFFFF),   // ACCENT_ON
    primaryContainer   = Color(0xFF7983F5),   // ACCENT_SECONDARY
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary          = Color(0xFF9B63F5),   // ACCENT_TERTIARY
    onSecondary        = Color(0xFFFFFFFF),
    background         = Color(0xFF1A1D26),   // BG_BASE
    onBackground       = Color(0xFFFFFFFF),   // TEXT_PRIMARY
    surface            = Color(0xFF212534),   // BG_PANEL
    onSurface          = Color(0xFFFFFFFF),
    surfaceVariant     = Color(0xFF2B2F42),   // BG_ELEVATED
    onSurfaceVariant   = Color(0xFFB9BBBE),   // TEXT_SECONDARY
    error              = Color(0xFFED4245),   // SEMANTIC_ERROR
    onError            = Color(0xFFFFFFFF),
    errorContainer     = Color(0xFF4A1C1D),
    outline            = Color(0xFF383C52),   // STROKE
    outlineVariant     = Color(0xFF2E3144),   // DIVIDER
    scrim              = Color(0xFF0E0F13),   // BG_DEEPEST
    inverseSurface     = Color(0xFFE0E0E0),
    inverseOnSurface   = Color(0xFF1A1D26),
)
```

---

## 3. Typography System

Use the Inter font family (or system sans-serif fallback). All sizes in sp.

```
Display Large   : 34sp · Bold        · tracking -0.25sp  → App name on splash
Display Medium  : 28sp · Bold        · tracking 0         → Section hero titles
Headline Large  : 24sp · SemiBold    · tracking 0         → Screen titles
Headline Medium : 20sp · SemiBold    · tracking 0         → Card headings
Headline Small  : 18sp · Medium      · tracking 0         → Tool card titles
Title Large     : 20sp · SemiBold    · tracking 0         → Toolbar titles
Title Medium    : 16sp · SemiBold    · tracking 0.15sp    → List item titles
Title Small     : 14sp · Medium      · tracking 0.1sp     → Chip labels
Body Large      : 16sp · Regular     · tracking 0.5sp     → Body paragraphs
Body Medium     : 14sp · Regular     · tracking 0.25sp    → Descriptions
Body Small      : 12sp · Regular     · tracking 0.4sp     → Metadata, timestamps
Label Large     : 14sp · SemiBold    · tracking 1.25sp    → Button labels
Label Medium    : 12sp · SemiBold    · tracking 1.25sp    → Badge text
Label Small     : 11sp · SemiBold    · tracking 1.5sp     → Caption text, nav labels
```

---

## 4. Spacing & Shape Token System

### Spacing Scale (8-point grid)
```
XS   = 4.dp    → micro gaps between icon and label
SM   = 8.dp    → within-card padding, chip inner
MD   = 12.dp   → card inner vertical padding
LG   = 16.dp   → screen horizontal margin, card padding
XL   = 24.dp   → section spacing, large card padding
2XL  = 32.dp   → hero section margin
3XL  = 48.dp   → between major sections
4XL  = 64.dp   → splash logo offset from center
```

### Corner Radius Scale
```
RADIUS_XS   = 4.dp    → badges, chips indicator pill
RADIUS_SM   = 8.dp    → small icon buttons, image thumbnails
RADIUS_MD   = 12.dp   → list cards, input fields, tool icons
RADIUS_LG   = 16.dp   → main content cards, bottom sheets
RADIUS_XL   = 24.dp   → FAB, hero cards, modal cards
RADIUS_FULL = 9999.dp → circular buttons, avatar, pill chips
```

### Elevation Scale
```
EL_LOW  = 2.dp   → resting card state
EL_MID  = 4.dp   → hover/focus card state
EL_HIGH = 8.dp   → pressed FAB, active modals
EL_MAX  = 16.dp  → sheets fully open
```

---

## 5. Animation System

Every animation in the app must use one of these pre-defined specs. Never use raw numbers inside `tween()` or `spring()` inline — declare them as constants.

### 5.1 Animation Constants
```kotlin
object Anim {
    // Durations
    const val FAST     = 150   // ms — button micro-interactions
    const val NORMAL   = 300   // ms — screen elements appearing
    const val SLOW     = 500   // ms — hero reveals, splash
    const val XSLOW    = 800   // ms — onboarding transitions

    // Spring configs
    val SpringSnappy = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    val SpringSmooth = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy,     stiffness = Spring.StiffnessMediumLow)
    val SpringBouncy = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy,    stiffness = Spring.StiffnessLow)

    // Easing
    val EaseOut = tween<Float>(durationMillis = NORMAL, easing = FastOutSlowInEasing)
    val EaseIn  = tween<Float>(durationMillis = FAST,   easing = LinearOutSlowInEasing)

    // Stagger delay between list items
    const val STAGGER = 60 // ms between each list card appearing
}
```

### 5.2 Screen Transition Specs
```
Enter  : slideInHorizontally(initialOffsetX = { fullWidth }) + fadeIn    · 350ms FastOutSlowIn
Exit   : slideOutHorizontally(targetOffsetX = { -fullWidth/3 }) + fadeOut · 350ms LinearOutSlowIn
PopEnter  : slideInHorizontally(initialOffsetX = { -fullWidth/3 }) + fadeIn · 350ms
PopExit   : slideOutHorizontally(targetOffsetX = { fullWidth }) + fadeOut   · 350ms
```

### 5.3 Interaction Animation Specs

| Element | Trigger | Animation | Spec |
|---|---|---|---|
| Primary Button | Press | Scale 1.0 → 0.95 | SpringSnappy |
| Tool Card | Press | Scale 1.0 → 0.97 + elevation drop | SpringSmooth |
| Document Card | Press | Scale 1.0 → 0.98 | SpringSmooth |
| FAB | Appear | ScaleIn(0f → 1f) + FadeIn | 300ms bounce |
| FAB | Disappear | ScaleOut + FadeOut | 200ms |
| Nav Icon | Select | Scale 1.0 → 1.15 + color transition | SpringSnappy |
| Bottom Sheet | Open | SlideUp + FadeIn | 400ms EaseOut |
| Tool Icon bg | Idle | Subtle pulse glow (InfiniteTransition, 3s) | Repeating |
| Scanner Line | Scanning | translateY top-to-bottom loop | 2000ms Linear Infinite |
| Scanner Brackets | Idle | Scale 0.95 → 1.0 loop | 1500ms ease in-out Infinite |
| Card list | First load | Staggered FadeIn + SlideUp each 60ms | STAGGER delay |
| Shimmer | Loading | Alpha 0.3 → 0.9 ping-pong | 1200ms Infinite |
| Progress arc | Operation | Stroke sweep 0 → target | 600ms EaseOut |
| Checkmark | Success | Stroke draw-on animation | 400ms |
| Error shake | Validation fail | translateX ±8dp, 3 cycles | 400ms total |

---

## 6. Screen-by-Screen Specifications

---

### 6.1 Splash Screen

**File:** `ui/screens/splash/SplashScreen.kt`

**Background:** `BG_DEEPEST` (#0E0F13) — full bleed, edge-to-edge.

**Layout (centered vertically + horizontally):**
```
┌─────────────────────────────┐
│                             │
│                             │
│         [APP ICON]          │  ← 96×96dp, animated scale-in from 0.6→1.0, 500ms SpringBouncy
│      LuckyPDF               │  ← Display Large, TEXT_PRIMARY, fadeIn 300ms, delay 200ms
│   Your PDF companion        │  ← Body Medium, TEXT_MUTED, fadeIn 300ms, delay 400ms
│                             │
│   [gradient accent bar]     │  ← 4dp tall, GradientBrand, width 120dp, fadeIn delay 600ms
│                             │
│                             │
│   [circular progress ring]  │  ← 32dp, AccentPrimary, centered bottom 80dp from bottom
└─────────────────────────────┘
```

**Behavior:**
- `SplashViewModel` checks: first launch (→ Onboarding) vs returning user (→ Home).
- Minimum display time: 1800ms (so the animation plays fully).
- Navigate using `LaunchedEffect` after `delay(1800)`.
- Use `SplashScreen API` (AndroidX) to suppress the system white flash.

**UiState:**
```kotlin
sealed interface SplashUiState {
    data object Checking : SplashUiState
    data object NavigateToOnboarding : SplashUiState
    data object NavigateToHome : SplashUiState
}
```

---

### 6.2 Onboarding Screen

**File:** `ui/screens/onboarding/OnboardingScreen.kt`

**Layout:** Horizontal `HorizontalPager` (3 pages). Fixed `BG_BASE` background.

**Page Indicator:** Row of 3 dots — inactive: `STROKE` 8dp circle, active: `ACCENT_PRIMARY` 24dp pill, animated width expand.

**CTA Button:** Full-width `GradientButton` — "Get Started" on last page, "Next →" on others.

**Skip Button:** `TextButton` top-right, `TEXT_SECONDARY`, hidden on last page.

#### Onboarding Page 1 — Scan Anything
```
Illustration : Large centered icon — document with camera (96dp, GradientBrand fill)
Headline     : "Scan Any Document"          (Display Medium, TEXT_PRIMARY)
Body         : "Point your camera and let LuckyPDF automatically detect edges, 
               correct perspective, and produce a crisp, clean PDF instantly."
               (Body Large, TEXT_SECONDARY, centered, max 3 lines)
```

#### Onboarding Page 2 — Powerful Tools
```
Illustration : Wrench/tools icon with PDF symbol (96dp, GradientCool fill)
Headline     : "Edit Like a Pro"
Body         : "Merge, split, compress, watermark, annotate, sign — 
               every tool you need to master your documents, 
               all in one place."
```

#### Onboarding Page 3 — Privacy First
```
Illustration : Shield with lock icon (96dp, GradientSuccess fill)
Headline     : "100% Private & Offline"
Body         : "Everything runs on your device. 
               Your files never leave your phone unless you share them yourself."
```

**State Management:**
```kotlin
sealed interface OnboardingUiState {
    data class Active(val currentPage: Int, val totalPages: Int = 3) : OnboardingUiState
    data object Finished : OnboardingUiState
}
```

---

### 6.3 Main Dashboard (Home Screen)

**File:** `ui/screens/home/HomeScreen.kt`

**Background:** `BG_BASE`

**Top Bar:**
```
Left  : "LuckyPDF" logo mark (28dp icon) + "LuckyPDF" (Title Large, TEXT_PRIMARY)
Right : Search icon (IconButton, 48dp tap target) + Avatar/Settings icon
```

**Storage Banner** (below top bar):
```
Card (BG_PANEL, RADIUS_LG, full width, 72dp tall):
  Left  : Storage icon (24dp, ACCENT_PRIMARY)
  Center: "Storage: 128 MB used" (Title Small, TEXT_PRIMARY)
          LinearProgressIndicator(progress, GradientBrand, BG_ELEVATED)
  Right : "Manage" TextButton (TEXT_LINK)
```

**Quick Tools Section** (horizontally scrolling row):
```
Section header: "Quick Tools" (Headline Small, TEXT_PRIMARY)
                "See all" (Label Medium, TEXT_LINK, right-aligned)

Scrollable Row, spacing = LG:
  [ToolCard] × 8 visible, rest scroll horizontally
```

Each `ToolCard` specification:
```
Size     : 100×100dp
Shape    : RADIUS_LG
BG       : BG_PANEL
Border   : 1dp STROKE
Padding  : MD

Top      : 44×44dp gradient icon box (RADIUS_MD, gradient per tool)
           24dp icon centered (tinted WHITE)
Bottom   : Tool name (Label Large, TEXT_PRIMARY, max 2 lines)

Press    : Scale 0.97 (SpringSmooth) + elevation drop EL_LOW→0
```

| # | Tool Name | Icon | Gradient |
|---|---|---|---|
| 1 | Scan | `CameraAlt` | GradientBrand |
| 2 | Merge | `MergeType` | GradientCool |
| 3 | Split | `CallSplit` | GradientWarm |
| 4 | Compress | `Compress` | GradientSuccess |
| 5 | Watermark | `BrandingWatermark` | GradientSunset |
| 6 | Sign | `Draw` | GradientBrand |
| 7 | Annotate | `EditNote` | GradientOcean |
| 8 | Organize | `Dashboard` | GradientSunset |

**Recent Files Section**:
```
Section header: "Recent Files" (Headline Small, TEXT_PRIMARY)
                "See all" (Label Medium, TEXT_LINK)

LazyColumn of DocumentCard items
  → staggered entrance: each item fades in + slides up 16dp, 60ms stagger
  → EmptyState if list is empty
```

**FAB** (bottom-right, offset from bottom nav):
```
Extended FAB: GradientBrand fill, 56dp, RADIUS_FULL
Icon  : Add (24dp, WHITE)
Label : "New PDF"
Scroll: FAB collapses to icon-only when list is scrolled down 2+ items
```

**Bottom Navigation Bar**:
```
BG_PANEL, top border 1dp DIVIDER, height 64dp

5 items: Home | Recent | [Scan — center, accent] | Favorites | Settings

Center (Scan):
  Raised circle 56dp, GradientBrand fill, RADIUS_FULL
  Slight upward offset (-16dp translateY into nav bar)
  Camera icon 28dp, WHITE

Other items:
  Icon 24dp: unselected TEXT_MUTED, selected ACCENT_PRIMARY
  Label 10sp: unselected TEXT_MUTED, selected ACCENT_PRIMARY
  Scale animation on select: 1.0→1.15 (SpringSnappy)
```

**UiState:**
```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val recentDocuments: List<Document>,
        val favoriteDocuments: List<Document>,
        val storageUsedBytes: Long,
        val storageTotalBytes: Long,
    ) : HomeUiState
    data class Error(val message: String, val canRetry: Boolean = true) : HomeUiState
    data object Empty : HomeUiState
}
```

---

### 6.4 Camera / Scan Screen

**File:** `ui/screens/camera/CameraScreen.kt`

**Background:** `BG_DEEPEST` — full bleed, edge-to-edge, no status bar padding.

**CameraX Preview** fills 100% of screen using `AndroidView { PreviewView }`.

**Scanner Overlay** (drawn on top of camera preview using `Canvas` in `drawWithContent`):

```
Outer vignette : BG_SCAN_OVERLAY fills area OUTSIDE the document bounding box
Document box   : Clear (transparent) — 80% screen width, 16:9 aspect ratio
Corner brackets: 32dp arms, 3dp stroke width, SCAN_BRACKET color
                 4 corners only (not full rectangle border)
Scanner line   : Horizontal cyan line (SCAN_LINE color), 2dp tall
                 Animates top→bottom of document box → repeats Infinitely
                 Pauses 400ms at each end before reversing
```

**Top Controls**:
```
← Back button (44dp tap target, WHITE icon, shadow)
Title: "Scan Document" (Title Large, WHITE)
Flash toggle icon (44dp, WHITE)
```

**Bottom Controls** (above safe area):
```
BG_DEEPEST panel 100dp tall, fade gradient upward
[Gallery Pick]   [CAPTURE BUTTON]   [Batch counter]
  48dp circle      72dp circle        48dp circle
  BG_ELEVATED      GradientBrand      BG_ELEVATED
  Image icon       Camera icon        Badge "3/10"
```

**Auto-detect behavior:**
- When ML Kit detects document edges → animate corner brackets to snap to corners with SpringBouncy animation.
- Show green SCAN_BRACKET tint + subtle success sound (haptic only if sound off).
- Auto-capture after 1500ms of stable detection (user can toggle this in settings).

**Permission Gate:** if `CAMERA` not granted → show `PermissionRationale` screen instead.

**UiState:**
```kotlin
sealed interface CameraUiState {
    data object PermissionRequired : CameraUiState
    data object Ready : CameraUiState
    data class Detecting(val cornersFound: Boolean) : CameraUiState
    data object Capturing : CameraUiState
    data class Processing(val progress: Float) : CameraUiState
    data class Success(val outputPath: String) : CameraUiState
    data class Error(val message: String) : CameraUiState
}
```

---

### 6.5 PDF Viewer Screen

**File:** `ui/screens/viewer/PdfViewerScreen.kt`

**Arguments received:** `documentId: String`, `documentUri: String`

**Background:** `BG_DEEPEST`

**Top Bar** (hides on scroll-down, re-appears on scroll-up — animate slide in/out with tween 200ms):
```
← Back    [filename, max 1 line ellipsis]    [Search] [More⋮]
```

**More menu items:**
```
Share · Print · Add Watermark · Add Signature · Compress · Document Info
```

**Page View:** `LazyColumn` of `PdfPageItem` composables.
- Each page rendered via `PdfRenderer` (Android built-in) → `Bitmap` → displayed with `AsyncImage (Coil)`.
- Render only ±2 pages around visible pages (`RememberLazyListState` + derived calculation).
- Show `ShimmerBox` while page renders.

**Page Thumbnail Sidebar** (right edge, visible when > 5 pages):
```
Draggable vertical scrollbar, 40dp wide
Shows page number label that follows thumb: "12/48" (Label Medium, TEXT_PRIMARY, BG_ELEVATED bubble)
```

**Bottom Action Bar** (always visible above safe area):
```
BG_PANEL, RADIUS_XL top corners, elevation EL_HIGH
[Annotate] [Sign] [Watermark] [Organize] [Share]
Icon + label, ACCENT_PRIMARY tint, Label Small below each
```

**Zoom:** `detectTransformGestures` — pinch to zoom 1x → 5x, double-tap reset.

**UiState:**
```kotlin
sealed interface PdfViewerUiState {
    data object Loading : PdfViewerUiState
    data class Ready(
        val document: Document,
        val currentPage: Int,
        val totalPages: Int,
        val zoom: Float = 1f,
        val isToolBarVisible: Boolean = true,
    ) : PdfViewerUiState
    data class Error(val message: String) : PdfViewerUiState
}
```

---

### 6.6 Merge PDFs Screen

**File:** `ui/screens/merge/MergeScreen.kt`

**Layout:**
```
Top bar    : ← "Merge PDFs"              [Merge →] gradient action button right
Body       : LazyColumn — selected PDF list (reorderable via drag handle)
Empty area : EmptyState → "Add PDFs to merge" + "Add Files" GradientButton
Footer     : Fixed bar — "X files selected · Total: Y MB" + [Add More] + [Merge]
```

**DraggableItem component:** 
- Each row shows: drag handle (`DragHandle` icon, TEXT_MUTED, left) + PDF icon (24dp) + file name + page count + size + remove "✕" button.
- Drag to reorder using `LazyColumn` with `ReorderableLazyColumn` pattern.
- Removal: swipe-left reveals red delete strip (200ms animation).

**Merge button disabled** when < 2 files selected.

**Progress Dialog** (shown during merge operation):
```
Modal bottom sheet, not dismissable during processing
[Gradient animated progress arc, 72dp ring]
"Merging 3 files..."  (Title Medium, TEXT_PRIMARY)
"File 2 of 3"         (Body Medium, TEXT_SECONDARY)
[Cancel] TextButton   (TEXT_ERROR)
```

**Post-merge:** navigate to PdfViewer with the merged output file.

**UiState:**
```kotlin
sealed interface MergeUiState {
    data class Selecting(val selectedFiles: List<Document>) : MergeUiState
    data class Processing(val current: Int, val total: Int, val progress: Float) : MergeUiState
    data class Success(val outputPath: String) : MergeUiState
    data class Error(val message: String) : MergeUiState
}
```

---

### 6.7 Split PDF Screen

**File:** `ui/screens/split/SplitScreen.kt`

**Layout:**
```
Top bar: ← "Split PDF"
Body   : Horizontal thumbnail pager of all pages
         Below: Range selector OR individual page checkboxes
         Split Mode toggle: [Range Mode] [Page Mode] (segmented control, ACCENT_PRIMARY selected fill)
Footer : [Preview Split] [Split PDF] gradient button
```

**Range Mode:** Two sliders (from/to page) with `RangeSlider`. Show "Pages 3 – 7" preview.

**Page Mode:** Grid of page thumbnails (3-column), each tappable with checkmark overlay (ACCENT_PRIMARY circle, WHITE checkmark).

**Split Result Dialog:**
```
"Split into N files" (Title Medium)
[Document Card] × N — each showing page range + estimated size
[Save All] [Share All] [Cancel]
```

**UiState:**
```kotlin
sealed interface SplitUiState {
    data class Ready(
        val document: Document,
        val totalPages: Int,
        val mode: SplitMode,
        val selectedPages: Set<Int>,
        val rangeStart: Int,
        val rangeEnd: Int,
    ) : SplitUiState
    data class Processing(val progress: Float) : SplitUiState
    data class Success(val outputPaths: List<String>) : SplitUiState
    data class Error(val message: String) : SplitUiState
}
enum class SplitMode { RANGE, INDIVIDUAL }
```

---

### 6.8 Compress PDF Screen

**File:** `ui/screens/compress/CompressScreen.kt`

**Layout:**
```
Top bar  : ← "Compress PDF"
Hero card: Original file preview (file name, page count, original size in large text)
           Original: 4.2 MB  (Display Small, TEXT_PRIMARY)
Slider   : Compression level — Low | Medium | High | Maximum
           Slider thumb is ACCENT_PRIMARY, track fill GradientBrand
Estimate : "Estimated output: ~1.8 MB  (57% smaller)"  (Body Medium, TEXT_SECONDARY)
           Updates live as slider moves
Preview  : Side-by-side quality indicator (page 1 thumbnail comparison)
Footer   : [Compress] GradientButton full-width
```

**After compression:**
```
Result Card (BG_PANEL, RADIUS_XL, padding XL):
  ✓ Checkmark animated (SEMANTIC_SUCCESS, stroke draw-on 400ms)
  "Compressed successfully!"  (Headline Medium)
  Before: 4.2 MB → After: 1.8 MB  (57% saved)  (Body Large, TEXT_SECONDARY)
  [Save] [Share] [Open] buttons row
```

---

### 6.9 Watermark Screen

**File:** `ui/screens/watermark/WatermarkScreen.kt`

**Layout — 3-step flow:**

**Step 1: Choose Type** (bottom sheet picks from):
```
[Text Watermark]  [Image Watermark]
Icon cards, RADIUS_LG, GradientBrand / GradientCool icon backgrounds
```

**Step 2: Configure**

For Text watermark:
```
TextField: "Watermark text" (BG_ELEVATED input, STROKE border, ACCENT_PRIMARY focus ring)
Font size slider : 20sp – 120sp
Opacity slider   : 0% – 100% (default 30%)
Rotation slider  : 0° – 90° (default 45°)
Color picker     : Row of preset colors + custom option
Position toggle  : Diagonal | Horizontal | Custom
```

For Image watermark:
```
[Pick Image from Gallery] card (RADIUS_LG, dashed border STROKE)
Size slider   : 10% – 100% of page width
Opacity slider: 0% – 100%
Position grid : 9-point grid selector (TL, TC, TR, ML, MC, MR, BL, BC, BR)
```

**Step 3: Preview** (live-rendered thumbnail of page 1 with watermark):
```
Card (BG_PANEL) containing AsyncImage of the preview page
"Apply to all pages" toggle (SwitchDefaults with ACCENT_PRIMARY thumb)
Or: "Select pages" chips row (scrollable)
[Apply Watermark] GradientButton
```

---

### 6.10 Sign / E-Signature Screen

**File:** `ui/screens/sign/SignScreen.kt`

**Tab Row** (3 tabs — Material 3 PrimaryTabRow):
```
[Draw]  [Type]  [Image]
ACCENT_PRIMARY active indicator underline, 3dp thick
```

**Draw Tab:**
```
Canvas area: BG_ELEVATED, RADIUS_LG, 100% width × 200dp tall
             Thin STROKE border (1dp), dashed when empty, solid when drawing
Brush picker: Color dots row (Black, Blue, Red, Purple, Green + custom)
Pen size    : 3 size buttons (S / M / L) → strokeWidth 2dp / 4dp / 8dp
[Clear] outlined button (TEXT_ERROR border/label)
[Done] GradientButton
```

**Type Tab:**
```
TextField: "Your Name" placeholder
Font picker: Horizontal scroll of 5 handwriting-style fonts
Color dots: same as Draw
Preview    : Renders the typed signature with selected font
```

**Image Tab:**
```
[Pick signature image] or [Take photo of signature]
Auto background remove toggle (strips white background)
```

**Placement Flow** (after signature is ready):
- Show PDF page with draggable + resizable signature overlay.
- Drag to position, pinch-resize.
- Confirm placement → apply to PDF.

---

### 6.11 Organize Pages Screen

**File:** `ui/screens/organize/OrganizeScreen.kt`

**Layout:**
```
Top bar: ← "Organize Pages"   [Select All] [Done ✓]

Grid: LazyVerticalGrid (2 columns, spacing MD)
  Each cell: page thumbnail card (BG_PANEL, RADIUS_MD)
    - Page thumbnail (renders via PdfRenderer)
    - Page number badge (bottom-right, ACCENT_PRIMARY, RADIUS_FULL, Label Small)
    - Long-press: enters selection mode (checkbox appears, ACCENT_PRIMARY ring around card)
    - Drag handle appears top-right on drag

Selection mode action bar (slides up from bottom, BG_PANEL, EL_HIGH):
  [Delete] [Move to...] [Duplicate] [Rotate 90°]
  Icons with Label Small labels, TEXT_ERROR for Delete
```

**Reorder:** Long-press + drag card to new position. Placeholder shown as dashed BG_ELEVATED box.

---

### 6.12 Recent Files Screen

**File:** `ui/screens/recent/RecentScreen.kt`

**Top area:**
```
Search bar (LuckySearchBar component, full width, BG_PANEL bg)
Sort chip row (scrollable horizontal): [Date ▼] [Name] [Size] [Type]
View toggle: [List] [Grid]  (two icon buttons, right-aligned)
```

**List view:** `DocumentCard` items (full width).

**Grid view:** 2-column `LazyVerticalGrid` of `DocumentGridCard` items.

**DocumentCard specification:**
```
Height  : 80dp
BG      : BG_PANEL
Shape   : RADIUS_MD
Padding : horizontal LG, vertical MD

Left  : 48×48dp thumbnail (BG_ELEVATED bg, RADIUS_SM, PDF icon ACCENT_PRIMARY 28dp)
Center: [File name] (Title Medium, TEXT_PRIMARY, 1 line ellipsis)
        [Page count · File size · Date modified] (Body Small, TEXT_MUTED)
Right : ⋮ menu icon (IconButton, TEXT_MUTED)
        Dropdown: Open | Share | Rename | Favorite | Delete
```

**Swipe actions:**
- Swipe left → reveal red Delete strip + Trash icon (200ms spring).
- Swipe right → reveal gold Favorite strip + Bookmark icon.

**Empty State:**
```
[Empty inbox illustration — 96dp, TEXT_MUTED]
"No recent files"              (Headline Medium, TEXT_SECONDARY)
"Files you open or create      (Body Medium, TEXT_MUTED, centered)
 will appear here."
[Scan a Document] GradientButton
```

---

### 6.13 Favorites Screen

**File:** `ui/screens/favorites/FavoritesScreen.kt`

Identical layout to Recent Files but filtered to `isFavorite = true` documents.

Empty state:
```
[Bookmark illustration]
"No favorites yet"
"Tap ⋮ on any file and choose Favorite to pin it here."
```

---

### 6.14 Search Screen

**File:** `ui/screens/search/SearchScreen.kt`

**Activated by:** tapping the search icon in Home top bar OR the Search bottom nav item.

**Layout:**
```
Animated SearchBar (expands from icon, Material 3 SearchBar component)
  BG_PANEL, STROKE border, ACCENT_PRIMARY focus ring
  [← Back]  [Search field]  [✕ Clear]

Below (before typing): "Recent searches" chips + "Suggested" section
During typing         : Live results list (DocumentCard items, filtered by name)
No results            : EmptyState "No results for 'query'"
```

**Search debounce:** 300ms after last keystroke using `debounce` on a `MutableStateFlow<String>`.

---

### 6.15 Settings Screen

**File:** `ui/screens/settings/SettingsScreen.kt`

**Layout:** `LazyColumn` of grouped sections with `HorizontalDivider` between groups.

**Section: Scan Settings**
```
Auto-detect document          [Toggle — default ON]
Auto-capture on detect        [Toggle — default OFF]
Image quality: Low/Med/High   [SegmentedButton]
Default save folder           [Value row → folder picker]
```

**Section: PDF Defaults**
```
Default compression level     [Slider — Medium]
Default watermark text        [TextField row]
Signature ink color           [Color dot row]
PDF author name               [TextField row]
```

**Section: Appearance**
```
Theme                         [Row — Dark only for now, disabled]
App icon variant              [3 options — Default, Minimal, Color]
```

**Section: Storage**
```
Storage used                  [Progress bar + "128 MB / 8 GB"]
Clear cache                   [Action row, TEXT_ERROR label]
Delete all files              [Action row, TEXT_ERROR label, confirm dialog]
```

**Section: About**
```
Version                       [Value row — "1.0.0 (1)"]
Privacy Policy                [Link row]
Licenses                      [Link row → OSS screen]
Rate LuckyPDF                 [Link row → Play Store]
```

**Destructive action dialogs** must use:
```kotlin
AlertDialog(
    containerColor = BG_ELEVATED,
    titleContentColor = TEXT_PRIMARY,
    textContentColor = TEXT_SECONDARY,
    confirmButton = GradientButton text = "Delete", gradient = GradientWarm,
    dismissButton = OutlinedButton text = "Cancel", borderColor = STROKE,
)
```

---

## 7. Reusable Component Library (Full Specifications)

### 7.1 `GradientButton`
```
Props   : text, onClick, modifier, enabled, icon?, gradient (default GradientBrand)
Size    : fillMaxWidth by default, height 52dp, RADIUS_MD
Content : Row(icon? + Text), icon 20dp WHITE, Label Large WHITE
Disabled: Brush.linearGradient([Color.Gray, Color.DarkGray]) 40% alpha
Press   : Scale 0.95 (SpringSnappy)
```

### 7.2 `OutlinedLuckyButton`
```
Props   : text, onClick, modifier, enabled, borderColor (default STROKE)
Size    : fillMaxWidth, height 52dp, RADIUS_MD
Border  : 1dp solid borderColor
Content : Label Large, TEXT_PRIMARY
Press   : Scale 0.96 (SpringSmooth)
```

### 7.3 `LuckyTopBar`
```
Props   : title, onBack?, actions: List<TopBarAction>
Left    : IconButton ← (only if onBack != null)
Center  : title (Title Large, TEXT_PRIMARY)
Right   : up to 3 action IconButtons (24dp icons, TEXT_SECONDARY)
BG      : BG_BASE or BG_PANEL based on screen context
Divider : 1dp DIVIDER at bottom
```

### 7.4 `LuckyBottomNav`
```
Items   : List<BottomNavItem> — icon + label + route
BG      : BG_PANEL
Border  : 1dp DIVIDER top
Height  : 64dp
Center item elevated: wrapped in 56dp GradientBrand circle, offset -16dp upward
Indicator: none (rely on color + scale for selection state)
```

### 7.5 `LuckySearchBar`
```
Props   : query, onQueryChange, onSearch, placeholder, modifier
BG      : BG_PANEL
Border  : 1dp STROKE (default), 1dp STROKE_FOCUS (when focused, animated color transition)
Height  : 48dp, RADIUS_FULL
Left    : Search icon 20dp TEXT_MUTED
Content : BasicTextField, Body Large, TEXT_PRIMARY, cursor ACCENT_PRIMARY
Clear   : ✕ IconButton (appears when query not empty, animated fadeIn)
```

### 7.6 `DocumentCard`
*(Already specified in section 6.12 — reuse for all screens)*

### 7.7 `ShimmerBox`
```
Props   : modifier (required — caller sets size)
Content : BG_ELEVATED filled box, alpha animates 0.3→0.9 Infinite ping-pong 1200ms
Shape   : caller applies via modifier.clip(...)
Usage   : Replace any image/card while loading
```

### 7.8 `EmptyState`
```
Props   : icon, title, subtitle, actionLabel?, onAction?
Layout  : Column centered, full screen
Icon    : 72dp, TEXT_MUTED
Title   : Headline Medium, TEXT_SECONDARY
Subtitle: Body Medium, TEXT_MUTED, centered
Button  : GradientButton (only if actionLabel != null)
```

### 7.9 `ErrorBanner`
```
Props   : message, onRetry?
Layout  : Full-width card (BG_ELEVATED, 1dp SEMANTIC_ERROR border, RADIUS_MD)
          Row: Error icon (20dp SEMANTIC_ERROR) + message (Body Medium TEXT_PRIMARY) + [Retry?]
```

### 7.10 `PermissionRationale`
```
Props   : icon, title, rationale, onGrantClick, onDenyClick?
Layout  : Centered Column, full screen, BG_BASE
          Icon (72dp, ACCENT_PRIMARY), Title (Headline Medium),
          Rationale (Body Large, TEXT_SECONDARY, centered, padding XL horizontal),
          [Grant Permission] GradientButton,
          [Not Now] TextButton TEXT_MUTED (optional)
```

### 7.11 `ProgressDialog`
```
Props   : title, subtitle?, progress (0f–1f or -1f for indeterminate), onCancel?
Layout  : ModalBottomSheet (non-dismissable during processing)
          Animated circular arc (72dp, ACCENT_PRIMARY)
          title (Title Medium, TEXT_PRIMARY, center)
          subtitle? (Body Medium, TEXT_SECONDARY, center)
          [Cancel] TextButton TEXT_ERROR (only if onCancel != null)
```

### 7.12 `SectionHeader`
```
Props   : title, actionLabel?, onAction?
Layout  : Row, full width, vertical center
          title (Headline Small, TEXT_PRIMARY, weight(1f))
          actionLabel? (Label Medium, TEXT_LINK, clickable)
Bottom  : 0dp padding (spacing handled by parent LazyColumn item spacing)
```

---

## 8. App Icon Specification

**Adaptive Icon** (API 26+): foreground + background layers.

**Foreground** (`ic_launcher_foreground.xml`):
```
108×108dp canvas (safe zone: 72×72dp center)
Shape    : Document silhouette (folded top-right corner)
Fill     : GradientBrand (diagonal top-left to bottom-right)
Accent   : "P" letter in bold on document face — WHITE, 32dp, SemiBold
Corner   : Folded triangle top-right, WHITE, semi-transparent
Shadow   : Subtle drop shadow offset 2dp below
```

**Background** (`ic_launcher_background.xml`):
```xml
<shape xmlns:android="...">
    <gradient android:startColor="#1A1D26" android:endColor="#212534"
              android:angle="135" android:type="linear"/>
</shape>
```

**Monochrome layer** (`ic_launcher_monochrome.xml`):
```
Same document shape but single-color (for themed icons on Android 13+)
```

**Notification icon** (`ic_notification.xml`):
```
24×24dp simplified "P" document mark, single WHITE path
```

---

## 9. Production-Ready Error Handling

Every feature must handle these error scenarios:

### 9.1 PDF Processing Errors
```kotlin
sealed class PdfError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class FileNotFound(path: String) : PdfError("File not found: $path")
    class FileTooLarge(sizeBytes: Long) : PdfError("File too large: ${sizeBytes / 1_048_576}MB (max 500MB)")
    class PasswordProtected : PdfError("This PDF is password protected")
    class CorruptFile(path: String) : PdfError("Cannot read PDF: file may be corrupt")
    class InsufficientStorage(needed: Long) : PdfError("Not enough storage: ${needed / 1_048_576}MB needed")
    class OperationFailed(op: String, cause: Throwable) : PdfError("$op failed", cause)
}
```

### 9.2 Error → User Message Mapping
```kotlin
fun PdfError.toUserMessage(): String = when (this) {
    is PdfError.FileNotFound        -> "We couldn't find that file. It may have been moved or deleted."
    is PdfError.FileTooLarge        -> "This file is too large to process (max 500 MB)."
    is PdfError.PasswordProtected   -> "This PDF is password-protected. Please unlock it first."
    is PdfError.CorruptFile         -> "This PDF appears to be damaged and can't be opened."
    is PdfError.InsufficientStorage -> "Not enough free storage to complete this operation."
    is PdfError.OperationFailed     -> "Something went wrong. Please try again."
}
```

### 9.3 Error Display Rules
- **Minor errors** (single file fails in a batch) → `Snackbar` (duration `Long`, action "Dismiss").
- **Blocking errors** (can't complete the operation) → `ErrorBanner` in-screen.
- **Critical errors** (app state corrupted) → `AlertDialog` with retry/dismiss.
- **Permission errors** → `PermissionRationale` full-screen replacement.
- **Storage full** → `AlertDialog` with "Free Up Space" deep link to system storage settings.

---

## 10. Performance Requirements

| Metric | Requirement | Implementation |
|---|---|---|
| App cold start | ≤ 800ms to splash visible | SplashScreen API, Hilt lazy init |
| PDF page render | ≤ 200ms per page | PdfRenderer on `Dispatchers.Default`, pre-render ±2 pages |
| Home screen load | ≤ 300ms to first list item | Pre-warm DB query in ViewModel init |
| Camera preview | 60fps, no stutter | CameraX, hardware accelerated PreviewView |
| Merge (10 files, 100 pages) | ≤ 8s | iText7 streaming writer, progress callbacks |
| Compress (5MB) | ≤ 3s | Dispatchers.Default, image down-sampling |
| Scroll performance | 0 jank frames | `key { }` in LazyColumn, `@Stable` on data classes |
| Memory | ≤ 200MB peak | Bitmap recycling, Coil memory cache 15% heap |

### Recomposition Guards
- Annotate all domain model data classes with `@Immutable`.
- Use `key(document.id) { DocumentCard(...) }` inside `LazyColumn`.
- Never pass lambdas directly — wrap in `remember { }` or use `::method` references.
- Use `derivedStateOf` for any computed state that shouldn't trigger full recomposition.

---

## 11. Accessibility Checklist

Every screen must pass these checks before being considered done:

- [ ] All `Icon` calls have meaningful `contentDescription` (null only for purely decorative icons that have a sibling text label).
- [ ] All `IconButton` calls have `contentDescription` on the Button, not the Icon inside.
- [ ] All interactive elements have minimum 48×48dp touch target (`minimumInteractiveComponentEnforcement` enabled).
- [ ] Color is never the sole indicator of state (always pair with icon or text).
- [ ] Text contrast ratio ≥ 4.5:1 against their background (TEXT_PRIMARY on BG_PANEL passes; TEXT_MUTED on BG_PANEL = 3.1:1 — only acceptable for non-essential metadata).
- [ ] `semantics { role = Role.Button }` applied to any custom clickable non-Button elements.
- [ ] Screens have logical focus order (top-left to bottom-right by default in Compose).
- [ ] All input fields have `label` set (not just placeholder).
- [ ] Dialogs trap focus while open (`FocusRequester` on first input).

---

## 12. File & Storage Rules

```kotlin
// ALWAYS use app-scoped directories — never raw external storage
Context.filesDir               → app private storage (not accessible to other apps)
Context.cacheDir               → temporary files (can be cleared by system)
Context.getExternalFilesDir()  → user-accessible, app-scoped (survives uninstall only with MANAGE_EXTERNAL_STORAGE)

// Output path conventions
val pdfDir    = File(filesDir, "pdfs").also  { it.mkdirs() }
val scanDir   = File(filesDir, "scans").also { it.mkdirs() }
val cacheDir  = cacheDir  // Coil and PdfRenderer bitmaps

// Naming convention
fun outputFileName(prefix: String) = "${prefix}_${System.currentTimeMillis()}.pdf"
// Examples: merge_1719918000000.pdf, scan_1719918000000.pdf, compressed_1719918000000.pdf

// Sharing — ALWAYS use FileProvider
FileProvider.getUriForFile(context, "${packageName}.fileprovider", file)
```

---

## 13. Navigation Flow Diagram

```
[SPLASH]
  ├─ first launch ──► [ONBOARDING] ──► [HOME]
  └─ returning    ──────────────────► [HOME]
                                         │
              ┌──────────────────────────┤────────────────────────┐
              │                          │                        │
           [RECENT]               [CAMERA/SCAN]             [SETTINGS]
              │                          │
           [SEARCH]               [VIEWER] ◄─── [HOME card tap]
           [FAVORITES]                   │
                                   ┌─────┴──────────────────────┐
                                [MERGE] [SPLIT] [COMPRESS] [WATERMARK]
                                [SIGN]  [EDIT]  [ORGANIZE]
```

---

## 14. Development Order (Strict Build Sequence)

Follow this exact order. Do not skip ahead. Each step must compile without errors before moving to the next.

### Phase 1 — Foundation (Steps 1–3)
```
Step 1 : Create Android project (com.luckypdf.app, minSdk 26, Kotlin, Compose)
Step 2 : Set up libs.versions.toml with all dependencies from the skill.md
Step 3 : Set up LuckyPdfTheme — Color.kt, Type.kt, Shape.kt, Dimens.kt, LuckyPdfTheme.kt
         ✓ VERIFY: App launches with BG_BASE background, no crash
```

### Phase 2 — Structure (Steps 4–6)
```
Step 4 : Set up Hilt — LuckyPdfApplication, AppModule, MainActivity @AndroidEntryPoint
Step 5 : Set up Room — DocumentEntity, DocumentDao, LuckyPdfDatabase, RepositoryModule
Step 6 : Set up Navigation — NavRoutes, NavGraph (stub screens), LuckyBottomNav
         ✓ VERIFY: Bottom nav works, each tab shows placeholder Text("Screen")
```

### Phase 3 — Screens (Steps 7–15)
```
Step 7  : SplashScreen + OnboardingScreen (with DataStore first-launch flag)
Step 8  : HomeScreen — top bar, storage banner, tool grid, recent list, FAB
Step 9  : RecentScreen + FavoritesScreen + SearchScreen
Step 10 : CameraScreen — CameraX preview + ML Kit scanner overlay
Step 11 : PdfViewerScreen — PdfRenderer page rendering, zoom, bottom action bar
Step 12 : MergeScreen — file picker, drag-reorder list, merge operation
Step 13 : SplitScreen — page thumbnails, range/page mode, split operation
Step 14 : CompressScreen — quality slider, live estimate, compress operation
Step 15 : WatermarkScreen + SignScreen (canvas drawing)
          ✓ VERIFY: All screens reachable, no empty state crashes
```

### Phase 4 — Tools & Polish (Steps 16–20)
```
Step 16 : OrganizeScreen — grid, multi-select, drag reorder, delete
Step 17 : SettingsScreen — all preference groups wired to DataStore
Step 18 : Complete all animations (transitions, card springs, FAB, scanner line)
Step 19 : Wire all error states, empty states, loading states
Step 20 : Final APK build — assembleRelease with ProGuard, verify file size < 30MB
          ✓ VERIFY: APK installs and runs on API 26 emulator without crash
```

---

## 15. APK Build Checklist

Before declaring the build done, verify every item:

- [ ] `./gradlew clean assembleDebug` completes with `BUILD SUCCESSFUL`
- [ ] `./gradlew testDebugUnitTest` — 0 failures
- [ ] `./gradlew lintDebug` — 0 errors (warnings acceptable)
- [ ] Debug APK installs on API 26 emulator (Android 8.0)
- [ ] Debug APK installs on API 35 device/emulator (Android 15)
- [ ] All screens reachable from navigation
- [ ] Camera permission flow works (grant + deny)
- [ ] Storage permission flow works (grant + deny)
- [ ] At least one PDF can be scanned end-to-end (scan → view → share)
- [ ] Merge with 2 PDFs works end-to-end
- [ ] App does not crash when back button pressed from any screen
- [ ] App survives process death and restores correctly (test with: `adb shell am kill com.luckypdf.app`)
- [ ] No `StrictMode` violations in debug build
- [ ] Release APK: `./gradlew assembleRelease` — size ≤ 30MB
- [ ] ProGuard rules do not strip iText7, ML Kit, Room entities

---

## 16. Strings Reference (`res/values/strings.xml`)

Every user-visible string must be in this file. Copilot must never hardcode display strings.

```xml
<!-- App -->
<string name="app_name">LuckyPDF</string>
<string name="app_tagline">Your PDF companion</string>

<!-- Navigation -->
<string name="nav_home">Home</string>
<string name="nav_recent">Recent</string>
<string name="nav_scan">Scan</string>
<string name="nav_favorites">Favorites</string>
<string name="nav_settings">Settings</string>

<!-- Home -->
<string name="home_quick_tools">Quick Tools</string>
<string name="home_see_all">See all</string>
<string name="home_recent_files">Recent Files</string>
<string name="home_storage_used">%s used</string>
<string name="home_fab_new">New PDF</string>
<string name="home_manage_storage">Manage</string>

<!-- Tools -->
<string name="tool_scan">Scan</string>
<string name="tool_merge">Merge</string>
<string name="tool_split">Split</string>
<string name="tool_compress">Compress</string>
<string name="tool_watermark">Watermark</string>
<string name="tool_sign">Sign</string>
<string name="tool_annotate">Annotate</string>
<string name="tool_organize">Organize</string>

<!-- Camera -->
<string name="camera_title">Scan Document</string>
<string name="camera_capture">Capture</string>
<string name="camera_gallery">Gallery</string>
<string name="camera_flash_on">Flash on</string>
<string name="camera_flash_off">Flash off</string>
<string name="camera_detecting">Document detected</string>

<!-- Permissions -->
<string name="permission_camera_title">Camera Access Needed</string>
<string name="permission_camera_rationale">LuckyPDF needs camera access to scan documents. Your camera is never used in the background.</string>
<string name="permission_storage_title">Storage Access Needed</string>
<string name="permission_storage_rationale">LuckyPDF needs storage access to open and save PDF files on your device.</string>
<string name="permission_grant">Grant Permission</string>
<string name="permission_not_now">Not Now</string>

<!-- Empty States -->
<string name="empty_recent_title">No recent files</string>
<string name="empty_recent_subtitle">Files you open or create will appear here.</string>
<string name="empty_favorites_title">No favorites yet</string>
<string name="empty_favorites_subtitle">Tap ⋮ on any file and choose Favorite to pin it here.</string>
<string name="empty_search_title">No results for "%s"</string>
<string name="empty_search_subtitle">Try a different file name or keyword.</string>

<!-- Actions -->
<string name="action_open">Open</string>
<string name="action_share">Share</string>
<string name="action_rename">Rename</string>
<string name="action_favorite">Favorite</string>
<string name="action_unfavorite">Unfavorite</string>
<string name="action_delete">Delete</string>
<string name="action_cancel">Cancel</string>
<string name="action_save">Save</string>
<string name="action_done">Done</string>
<string name="action_retry">Retry</string>
<string name="action_add_files">Add Files</string>
<string name="action_get_started">Get Started</string>
<string name="action_next">Next</string>
<string name="action_skip">Skip</string>

<!-- Merge -->
<string name="merge_title">Merge PDFs</string>
<string name="merge_button">Merge</string>
<string name="merge_progress">Merging %1$d of %2$d files…</string>
<string name="merge_success">Merged successfully!</string>
<string name="merge_hint">Add at least 2 PDF files to merge</string>

<!-- Split -->
<string name="split_title">Split PDF</string>
<string name="split_button">Split PDF</string>
<string name="split_range_mode">Range</string>
<string name="split_page_mode">Pages</string>
<string name="split_success">Split into %d files</string>

<!-- Compress -->
<string name="compress_title">Compress PDF</string>
<string name="compress_button">Compress</string>
<string name="compress_estimate">Estimated output: ~%s (%d%% smaller)</string>
<string name="compress_quality_low">Low</string>
<string name="compress_quality_medium">Medium</string>
<string name="compress_quality_high">High</string>
<string name="compress_quality_maximum">Maximum</string>
<string name="compress_success_title">Compressed!</string>
<string name="compress_success_detail">%1$s → %2$s  (%3$d%% saved)</string>

<!-- Errors -->
<string name="error_file_not_found">We couldn\'t find that file. It may have been moved or deleted.</string>
<string name="error_file_too_large">This file is too large to process (max 500 MB).</string>
<string name="error_password_protected">This PDF is password-protected. Please unlock it first.</string>
<string name="error_corrupt_file">This PDF appears to be damaged and can\'t be opened.</string>
<string name="error_no_storage">Not enough free storage to complete this operation.</string>
<string name="error_generic">Something went wrong. Please try again.</string>
<string name="error_dismiss">Dismiss</string>
```

---

## 17. What Must Never Be Done

| ❌ NEVER | ✅ ALWAYS |
|---|---|
| Hardcode any hex color in a composable | Use tokens from `Color.kt` |
| Write a non-null assertion `!!` | Use `?.let {}`, `?: return`, or `Result` |
| Call blocking I/O on the main thread | `withContext(Dispatchers.IO)` |
| Use `GlobalScope` | `viewModelScope` or `lifecycleScope` |
| Use `LiveData` | `StateFlow` + `collectAsStateWithLifecycle()` |
| Create XML layout files | Compose only |
| Write strings inline in composables | `stringResource(R.string.key)` |
| Use `16.dp` magic numbers | `Dimens.SpaceLG` tokens |
| Access DAO from ViewModel directly | UseCase → Repository → DAO |
| Use `file://` URIs for sharing | `FileProvider.getUriForFile()` |
| Create `Fragment` subclasses | Compose screens only |
| Store sensitive data in SharedPreferences | `EncryptedSharedPreferences` or `DataStore` |
| Catch `Exception` silently | Log + propagate or map to `PdfError` |
| Skip the `Error` variant in a `UiState` | Always include `Error` case |
| Put business logic inside a Composable | ViewModel or UseCase only |
| Use `remember { mutableStateOf() }` in ViewModel | `MutableStateFlow` in VM, `remember` in Composable |
| Run PDF operations synchronously | Always async with progress callbacks |

---

## 18. Final Quality Bar

The LuckyPDF app is done when:

1. Every screen matches the Discord color palette — no off-brand colors visible.
2. All 8 tool screens are functional end-to-end (not stub/placeholder).
3. The camera scanner shows the live edge-detection overlay with animated brackets.
4. PDF rendering works (pages visible, zoomable, scrollable).
5. All loading states show the shimmer animation.
6. All empty states show the icon + message + action button.
7. All error states show the error banner with retry.
8. Screen transitions animate (slide + fade).
9. Every button press has a scale animation response.
10. The FAB collapses on scroll and expands on scroll-back.
11. The debug APK installs and runs without crash on API 26 and API 35.
12. `./gradlew assembleRelease` completes under 5 minutes with `BUILD SUCCESSFUL`.
13. The app icon looks sharp and premium on the device launcher.
14. No `TODO`, `FIXME`, `println`, or `Thread.sleep` anywhere in production code.

---

*LuckyPDF Copilot Project Prompt — v2.0 Production-Ready*
*All specifications are final. When in conflict, this document overrides all other instructions.*
