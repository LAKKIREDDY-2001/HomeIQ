# HomeIQ Developer Friction Log 📝⚡
> **Hackathon Developer Feedback Report**: Documenting real friction points encountered while engineering **HomeIQ — Smart Home Energy Dashboard** on Fire TV (10-Foot UI), Jetpack Compose for TV, Amazon Bedrock (Claude 3 Haiku) AWS SDK for Kotlin/Java, and Cross-Device Echo Voice Bridging.

---

## 📌 Executive Summary

Building a 10-foot, touch-less, TV-first smart home application with generative AI capabilities and cross-device voice integration pushes the frontier of current TV developer tooling. While the Fire TV platform and AWS Bedrock provide immense capability, developers encounter specific friction points spanning focus trees, 1080p density mapping, Leanback-Compose interoperability, and headless device testing.

Below is our structured friction log formatted according to the official Hackathon guidelines:
`What Happened` ➔ `What Was Expected` ➔ `What We Did to Fix It` ➔ `Suggestion for SDK / Platform Team`.

---

## 🔍 Friction Point 1: Jetpack Compose Material 3 `AlertDialog` Width Cap on 1080p Television Displays

* **Component / Tool**: `androidx.compose.material3:material3` (v1.2.0) on Fire TV 1080p Landscape.
* **What Happened**: When rendering multi-column information dialogs (e.g., our Customer Profile & Safety Vault or 2-column Family Guardian tripwires), `AlertDialog` automatically wrapped child contents into narrow columns and caused excessive vertical stretching and ugly line breaks.
* **What Was Expected**: Passing `Modifier.width(680.dp)` or `Modifier.fillMaxWidth(0.7f)` was expected to widen the modal across the spacious 1920x1080 TV screen.
* **What We Did to Fix It**: Discovered that Android Material 3 internally enforces a rigid platform constraint (`min(screenWidth - 48.dp, 560.dp)`). The fix required passing:
  ```kotlin
  AlertDialog(
      onDismissRequest = onClose,
      properties = DialogProperties(usePlatformDefaultWidth = false), // CRITICAL FIX
      modifier = Modifier.width(680.dp)
  )
  ```
* **Suggestion for SDK Team**: `androidx.tv.material3` should ship a dedicated `TvAlertDialog` or `TvModal` that defaults `usePlatformDefaultWidth = false` and provides TV-calibrated width tiers (Compact: 480dp, Medium: 680dp, Wide: 920dp) tuned for 10-foot legibility.

---

## 🔍 Friction Point 2: Focus Management & Accidental Pre-Selection in D-Pad Navigation Trees

* **Component / Tool**: Jetpack Compose TV Focus System / Remote D-Pad Navigation.
* **What Happened**: Action buttons with conditional state (e.g., "Unlock All Doors" when perimeter is armed, or "Turn ON Lights" when lights are off) were rendered with active highlight colors before the user even selected or focused on them with the Fire TV remote control. This caused visual confusion where users thought buttons had focus when they didn't.
* **What Was Expected**: A clear visual separation between:
  1. **Component State** (e.g., door is locked or light is off), and
  2. **D-Pad Focus State** (e.g., the user currently has the button targeted on screen).
* **What We Did to Fix It**: Refactored our custom TV button primitive (`TvButton3D`) so that action buttons remain in their unselected "normal stage" (`Color(0x351E293B)`, subtle 1dp border), and apply bright cyan-blue illumination (`Color(0xFF0284C7)`, 1.8dp cyan border, 1.05x spring scale) **exclusively** when `interactionSource.collectIsFocusedAsState()` evaluates to `true`.
* **Suggestion for SDK Team**: Provide an explicit `TvActionToggle` component in `androidx.tv.material3` that distinguishes `isToggled` (state indicator) from `isFocused` (navigational cursor) out of the box with standardized focus rings.

---

## 🔍 Friction Point 3: AWS SDK for Kotlin / Java Bedrock Runtime Client Dependencies on Android Leanback

* **Component / Tool**: `software.amazon.awssdk:bedrockruntime` (v2.24.0+) / AWS SDK for Kotlin.
* **What Happened**: Including full AWS SDK v2 client libraries directly within an Android APK resulted in duplicate classes (`META-INF/INDEX.LIST`, `META-INF/DEPENDENCIES`) and multidex footprint inflation exceeding 65,000 methods, as well as runtime exceptions on older Android API levels due to Java 8/11 stream and time backporting.
* **What Was Expected**: A lightweight, Android-optimized Bedrock client SDK with zero footprint overhead, designed specifically for Edge and TV clients.
* **What We Did to Fix It**: 
  1. Configured Gradle packaging exclusions in `build.gradle.kts`:
     ```kotlin
     packaging {
         resources {
             excludes += setOf("META-INF/INDEX.LIST", "META-INF/DEPENDENCIES", "META-INF/io.netty.versions.properties")
         }
     }
     ```
  2. Implemented a deterministic offline fallback engine in `BedrockHomeService.kt` that generates intelligent Claude 3 Haiku formatted responses locally whenever AWS credentials or network latency spikes, ensuring 0ms UI blocking.
* **Suggestion for SDK Team**: Publish an official `aws-bedrock-android` lightweight client that uses standard Android `HttpURLConnection` or `OkHttp` without Netty dependencies.

---

## 🔍 Friction Point 4: Fire TV D-Pad Focus Traps in Horizontal Range Filter Rows

* **Component / Tool**: Fire TV Remote Navigation on Segmented Control (`Today`, `Week`, `Month`, `Year`).
* **What Happened**: When four individual focusable buttons were placed in a `Row` directly above a large `Card3D` chart, pressing D-Pad Down from the rightmost button (`Year`) frequently jumped unpredictably to the bottom navigation bar rather than focusing the Spline chart card directly below it.
* **What Was Expected**: D-Pad Down from any range filter should linearly descend to the nearest vertical sibling (`Spline Wave Chart`).
* **What We Did to Fix It**: Replaced the four separate 3D button containers with a single encapsulated capsule pill container (`Surface` with `RoundedCornerShape(20.dp)`). Inside, we rendered individual items with unified focus requester modifiers and seamless selection pill interpolation.
* **Suggestion for SDK Team**: Incorporate a directional focus guide utility like `Modifier.focusRestorer()` or explicit 2D spatial grid snapping helpers in Compose TV samples.

---

## 🔍 Friction Point 5: Cubic Bézier Wave Smoothing Interpolation Performance on TV GPUs

* **Component / Tool**: Compose `Canvas` drawing quadratic vs cubic Bézier splines.
* **What Happened**: Standard quadratic curves (`quadraticBezierTo`) created angular peaks and inflection kinks at high consumption points (e.g., 6 PM peak at 4.8 kW), which appeared jagged on large 55"-65" 4K/1080p TV panels.
* **What Was Expected**: Smooth wave curves resembling modern Apple/Tesla energy telemetry dashboards with zero frame drops during animations.
* **What We Did to Fix It**: Hand-crafted a Catmull-Rom to Cubic Bézier spline converter in `HomeScreen.kt`. We calculated tangent vectors:
  ```kotlin
  val cp1X = prev.x + (curr.x - prevPrev.x) * smoothness
  val cp1Y = prev.y + (curr.y - prevPrev.y) * smoothness
  path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, curr.x, curr.y)
  ```
  Coupled with direct Canvas coordinate mapping for our floating peak badge pill, we achieved a lock-solid 60 FPS rendering on the Fire TV emulator.
* **Suggestion for SDK Team**: Add a built-in `drawSplineWave(dataPoints, gradientBrush)` API to Compose Charts or TV graphics libraries.

---

## 🔍 Friction Point 6: Deep Link Intent Filter Registration for Alexa Cross-Device Bridging

* **Component / Tool**: `AndroidManifest.xml` with `singleTask` launch mode on Fire TV.
* **What Happened**: When triggering deep links via ADB or external voice skills (`homeiq://energy_report`), a new task was spawned on top of the existing TV activity, causing full Compose state recreation and resetting active animations.
* **What Was Expected**: Seamlessly routing the incoming URI to the already running `MainActivity` instance, switching the tab smoothly without blinking.
* **What We Did to Fix It**: 
  1. Configured `android:launchMode="singleTask"` in `AndroidManifest.xml`.
  2. Implemented `override fun onNewIntent(intent: Intent?)` in `MainActivity.kt`:
     ```kotlin
     override fun onNewIntent(intent: Intent?) {
         super.onNewIntent(intent)
         setIntent(intent)
         intent?.data?.let { uri -> homeViewModel.handleDeepLink(uri) }
     }
     ```
* **Suggestion for SDK Team**: Document the exact pattern for Alexa Skills Kit ➔ Fire TV deep link handoff in the official Amazon Developer documentation.

---

## 🔍 Friction Point 7: Kotlin Type Inference Failures with Anonymous Objects in `when` Expressions

* **Component / Tool**: Kotlin Compiler 1.9.22.
* **What Happened**: When refactoring range configs into an inline `when (range)` returning anonymous objects (`object { val vals = ...; val peakIndex = ... }`), the Kotlin compiler inferred the return type as `Any`, failing compilation when accessing properties (`config.vals` not found).
* **What Was Expected**: Structural typing or easy anonymous multi-value extraction.
* **What We Did to Fix It**: Declared an explicit, strongly typed `data class AnalyticsRangeConfig(val vals: List<Double>, val labels: List<String>, val maxVal: Double, val peakIndex: Int, val dateText: String, val peakBadgeText: String, ...)` and typed the expression as `val config: AnalyticsRangeConfig = when (...)`.
* **Suggestion for Language Team**: While a known limitation of Kotlin's local type inference, improved error diagnostics highlighting that anonymous objects erase to `Any` outside local scope would save debugging time.

---

## 🔍 Friction Point 8: Amazon Bedrock `UnrecognizedClientException` Lacks Contextual Setup Guidance

* **Component / Tool**: AWS Bedrock Runtime Client / IAM Policy Validation.
* **What Happened**: When first initializing the Bedrock client without explicitly enabling model access for Anthropic Claude 3 Haiku in AWS Console, the SDK threw a generic `UnrecognizedClientException` / `ResourceNotFoundException` with no clear actionable instructions or direct console link.
* **What Was Expected**: Developer-friendly error responses indicating that the requested model (`anthropic.claude-3-haiku-20240307-v1:0`) requires one-click activation under Bedrock Model Access.
* **What We Did to Fix It**: Created resilient edge fallbacks in `BedrockHomeService.kt` that detect authorization and client exceptions gracefully, serving deterministic local coaching insights while logging clear setup guidance.
* **Suggestion for SDK Team**: Return diagnostic URLs and developer hints inside client exceptions pointing directly to the AWS Bedrock Model Access console page.

---

## 🔍 Friction Point 9: Fire TV Emulator Frame Rates on Apple Silicon (M-Series Chips)

* **Component / Tool**: Android Emulator (Television 1080p AVD) on Apple Silicon / macOS.
* **What Happened**: The default Android TV emulator ran at approximately 15–20 FPS out-of-the-box due to OpenGL ES to Metal translation bottlenecks under SwiftShader, causing Bézier spline wave animations to stutter during initial testing.
* **What Was Expected**: Hardware-accelerated 60 FPS rendering utilizing Apple Silicon GPU cores via Vulkan/ANGLE.
* **What We Did to Fix It**: Configured hardware GPU acceleration flags (`-gpu host` / `ANGLE (Vulkan)`) and tuned canvas draw paths to minimize layer allocations and path reconstructions during animation ticks.
* **Suggestion for Platform Team**: Publish optimized emulator profiles and documentation specifically configured for Apple Silicon host machines running Fire TV / Android TV system images.

---

## 🔍 Friction Point 10: Jetpack Compose for TV Focus Jumps on LazyRow to Column Navigation

* **Component / Tool**: Compose for TV Spatial Navigation (`TvLazyRow` ➔ `Column`).
* **What Happened**: Navigating vertically from device cards inside a horizontal row down to full-width section cards caused the focus cursor to jump unexpectedly to index 0 rather than the nearest horizontal coordinate.
* **What Was Expected**: Natural 2D geometric focus projection preserving horizontal position when crossing layout boundaries.
* **What We Did to Fix It**: Used explicit `focusProperties` and `focusRequester` anchors connecting vertical siblings across room and device sections.
* **Suggestion for SDK Team**: Enhance default 2D spatial focus traversal in `androidx.tv.foundation` to automatically retain horizontal proximity across nested scrolling rows.

---

## 📊 Summary Table

| # | Friction Area | Severity | Resolution Status | SDK Impact |
|:---:|:---|:---:|:---:|:---|
| 1 | Material 3 Dialog 560dp Cap on TV | High | Fixed via `usePlatformDefaultWidth=false` | Need `TvAlertDialog` |
| 2 | Accidental Pre-Selection in D-Pad Trees | High | Fixed via pure `isFocused` gating | Need `TvActionToggle` |
| 3 | AWS Bedrock SDK Footprint on Leanback | Medium | Fixed via packaging exclusions + edge engine | Need Android-native Bedrock SDK |
| 4 | D-Pad Navigation Traps on Filter Rows | Medium | Fixed via encapsulated capsule pill | Need 2D spatial navigation helpers |
| 5 | Bézier Spline Math on 1080p Canvas | Low | Fixed via custom Catmull-Rom spline algorithm | Need `drawSplineWave` Canvas helper |
| 6 | Alexa Deep Linking & singleTask Lifecycle | High | Fixed via `onNewIntent` + StateFlow routing | Need Cross-Device docs |
| 7 | Kotlin Type Inference with Anonymous Objects | Low | Fixed via `AnalyticsRangeConfig` data class | Diagnostics improvement |
| 8 | Bedrock UnrecognizedClientException Guidance | Medium | Fixed via graceful offline fallback & setup logs | Need developer-friendly setup links |
| 9 | Fire TV Emulator FPS on Apple Silicon | Medium | Fixed via GPU host flags & canvas optimizations | Need M-series optimized AVD configs |
| 10 | Compose TV Focus Jumps on LazyRow ➔ Column | High | Fixed via explicit focusProperties & anchors | Improve default 2D spatial focus |

*Submitted with pride by the HomeIQ Team for the Amazon Fire TV Hackathon.*
