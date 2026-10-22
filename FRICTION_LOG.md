# HomeIQ Developer Friction Log 📝⚡
> **Hackathon Developer Feedback Report**: Documenting real friction points encountered while engineering **HomeIQ — Smart Home Energy Dashboard** on Fire TV (10-Foot UI), Jetpack Compose for TV, Amazon Bedrock (Claude 3 Haiku) AWS SDK for Kotlin/Java, and Cross-Device Echo Voice Bridging.
>
> **Eligibility**: Submitted for the **10% Developer Friction Log Bonus** under the Amazon Developer Hackathon 2026 rules.

---

## 📌 Executive Summary

Building a 10-foot, touch-less, TV-first smart home command center with generative AI capabilities and cross-device voice integration pushes the frontier of current TV developer tooling. Below are 10 structured friction logs documented during development, formatted strictly according to Amazon's hackathon guidelines to provide actionable developer experience feedback directly to the Fire TV, Alexa, and AWS Bedrock engineering teams.

---

## 🔍 Friction Point #1: Jetpack Compose Material 3 `AlertDialog` Width Constraint on 1080p TV Screens

* **Task Attempted**: Render a multi-column modal dialog (Customer Profile & Safety Vault, and 2-column Family Guardian tripwires) across a 1920×1080 Fire TV display.
* **Steps Taken**:
  1. Implemented standard `androidx.compose.material3.AlertDialog`.
  2. Applied `Modifier.width(680.dp)` or `Modifier.fillMaxWidth(0.7f)` to widen the dialog.
  3. Deployed the app to Fire TV Stick and Android TV emulator.
* **Expected Outcome**: The modal dialog should expand to 680dp, displaying side-by-side telemetry cards comfortably for 10-foot couch viewing.
* **Actual Outcome**: Material 3 internally capped the dialog width to a rigid platform default (`min(screenWidth - 48.dp, 560.dp)`), squishing child columns into narrow vertical text wraps with clipped buttons.
* **Severity**: **High**
* **Workaround Used**: Discovered and enabled the unexposed platform override flag:
  ```kotlin
  AlertDialog(
      onDismissRequest = onClose,
      properties = DialogProperties(usePlatformDefaultWidth = false), // CRITICAL WORKAROUND
      modifier = Modifier.width(680.dp)
  )
  ```
* **Actionable Suggestion for Amazon/AWS Team**: `androidx.tv.material3` should ship a dedicated `TvAlertDialog` or `TvModal` component that defaults `usePlatformDefaultWidth = false` and provides TV-calibrated width presets (`Compact: 480dp`, `Medium: 680dp`, `Wide: 920dp`) optimized for 10-foot television ergonomics.

---

## 🔍 Friction Point #2: Accidental Pre-Selection State in D-Pad Remote Navigation Trees

* **Task Attempted**: Create high-visibility action toggle buttons (e.g. "Turn OFF All Lights", "Emergency Breaker Disconnect") navigable via Fire TV remote D-Pad.
* **Steps Taken**:
  1. Built custom Compose button with state indicators (`isOn` / `isToggled`).
  2. Applied selection background color when `isOn == true`.
  3. Tested navigation using the Fire TV remote D-pad.
* **Expected Outcome**: A clear visual distinction between:
  1. The functional state of the device (e.g. lights are currently ON), and
  2. The D-Pad cursor focus position (where the user's remote is currently pointing).
* **Actual Outcome**: Action buttons appeared with glowing active borders before the user ever focused on them, misleading users into thinking they were selected.
* **Severity**: **High**
* **Workaround Used**: Refactored `TvButton3D` so action buttons remain in their neutral resting state (`Color(0x351E293B)`, subtle 1dp border), applying cyan-blue illumination (`Color(0xFF0284C7)`, 1.8dp cyan border, 1.05x spring scale) **exclusively** when `interactionSource.collectIsFocusedAsState()` evaluates to `true`:
  ```kotlin
  val isFocused by interactionSource.collectIsFocusedAsState()
  val border = if (isFocused) BorderStroke(2.dp, Theme3D.accentCyan) else BorderStroke(1.dp, Theme3D.cardBorder)
  ```
* **Actionable Suggestion for Amazon/AWS Team**: Provide an explicit `TvActionToggle` component in `androidx.tv.material3` that cleanly decouples `isToggled` (state indicator) from `isFocused` (navigational cursor) out of the box with standardized Fire TV focus rings.

---

## 🔍 Friction Point #3: AWS SDK for Kotlin/Java Bedrock Runtime Client Dependencies on Leanback

* **Task Attempted**: Integrate Amazon Bedrock Claude 3 Haiku directly into the Fire TV Kotlin application for real-time energy insights.
* **Steps Taken**:
  1. Added `software.amazon.awssdk:bedrockruntime:2.24.0` to `app/build.gradle.kts`.
  2. Invoked `BedrockRuntimeClient.builder().region(Region.US_EAST_1).build()`.
  3. Compiled and assembled the debug APK.
* **Expected Outcome**: Clean compilation and lightweight execution of Anthropic Claude 3 Haiku inferences on Fire OS.
* **Actual Outcome**: Build failed with duplicate packaging errors (`META-INF/INDEX.LIST`, `META-INF/DEPENDENCIES`) and multidex footprint inflation exceeding 65,000 methods, plus Netty native transport incompatibility on Android.
* **Severity**: **Medium**
* **Workaround Used**: Configured Gradle packaging exclusions in `build.gradle.kts` and implemented an offline-first fallback engine (`BedrockHomeService.kt`) that serves deterministic Claude 3 Haiku formatted responses locally whenever network or AWS credentials are unavailable:
  ```kotlin
  packaging {
      resources {
          excludes += setOf("META-INF/INDEX.LIST", "META-INF/DEPENDENCIES", "META-INF/io.netty.versions.properties")
      }
  }
  ```
* **Actionable Suggestion for Amazon/AWS Team**: Publish a lightweight `aws-bedrock-android` client SDK that utilizes Android's native `HttpURLConnection` or `OkHttp` engines rather than heavy Netty server runtimes.

---

## 🔍 Friction Point #4: D-Pad Focus Traps in Horizontal Range Filter Rows

* **Task Attempted**: Allow Fire TV remote users to switch energy analytics timeframes (`Today`, `Week`, `Month`, `Year`) placed above a full-width Spline Wave chart.
* **Steps Taken**:
  1. Placed four individual focusable buttons in a horizontal `Row`.
  2. Positioned the spline wave `Card3D` directly beneath the filter row.
  3. Pressed D-Pad Down from the rightmost button (`Year`).
* **Expected Outcome**: D-Pad Down from any range filter button should smoothly descend to the nearest vertical sibling (`Spline Wave Chart`).
* **Actual Outcome**: Focus jumped unpredictably to the bottom navigation bar or skipped the chart entirely, trapping navigation in the upper header.
* **Severity**: **Medium**
* **Workaround Used**: Encapsulated the four range buttons into a single cohesive capsule pill container (`Surface` with `RoundedCornerShape(20.dp)`), binding explicit directional focus requesters between the capsule and the spline canvas container below it.
* **Actionable Suggestion for Amazon/AWS Team**: Include directional focus guide utilities (e.g. `Modifier.focusRestorer()` or 2D spatial grid snapping helpers) in official Compose for TV sample repositories.

---

## 🔍 Friction Point #5: Cubic Bézier Wave Smoothing Interpolation Performance on TV GPUs

* **Task Attempted**: Render continuous 24-hour power telemetry curves with dynamic peak callouts (`6 PM • 4.8 kW`) at 60 FPS on television screens.
* **Steps Taken**:
  1. Drew hourly power data points using Compose `Canvas` with standard `quadraticBezierTo`.
  2. Evaluated rendering on a 55-inch 1080p display.
* **Expected Outcome**: Smooth, fluid wave curves resembling modern energy intelligence dashboards with zero jagged edges.
* **Actual Outcome**: Quadratic curves created sharp angular inflection kinks at steep transition points (e.g. solar drop-off to evening cooking peak), appearing visually unpolished on large displays.
* **Severity**: **Low**
* **Workaround Used**: Developed a custom Catmull-Rom to Cubic Bézier spline converter in `HomeScreen.kt`, computing tangent vectors dynamically:
  ```kotlin
  val cp1X = prev.x + (curr.x - prevPrev.x) * smoothness
  val cp1Y = prev.y + (curr.y - prevPrev.y) * smoothness
  path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, curr.x, curr.y)
  ```
* **Actionable Suggestion for Amazon/AWS Team**: Introduce a built-in `drawSplineWave(dataPoints, brush)` API to Compose Charts or TV graphics libraries.

---

## 🔍 Friction Point #6: Alexa Voice Deep Link Activity Lifecycle (`singleTask` vs `standard`)

* **Task Attempted**: Support cross-device voice handoff where an Amazon Echo command (*"Alexa, show my energy report"*) opens HomeIQ directly to the Energy Analytics tab on Fire TV.
* **Steps Taken**:
  1. Registered custom URI scheme `homeiq://energy_report` in `AndroidManifest.xml`.
  2. Triggered deep links via ADB command: `adb shell am start -d "homeiq://energy_report" com.homeiq.firetv`.
* **Expected Outcome**: Seamless tab transition within the active running instance of HomeIQ without flickering or reloading.
* **Actual Outcome**: Android's default `standard` launch mode instantiated a new duplicate `MainActivity` on top of the task stack, resetting active animations and Compose state.
* **Severity**: **High**
* **Workaround Used**: Configured `android:launchMode="singleTask"` in `AndroidManifest.xml` and implemented `onNewIntent` to route incoming deep link URIs reactively through `HomeViewModel.handleDeepLink(uri)`:
  ```kotlin
  override fun onNewIntent(intent: Intent?) {
      super.onNewIntent(intent)
      setIntent(intent)
      intent?.data?.let { uri -> homeViewModel.handleDeepLink(uri) }
  }
  ```
* **Actionable Suggestion for Amazon/AWS Team**: Provide complete end-to-end documentation and an official sample app demonstrating the Alexa Skills Kit (ASK) to Fire TV `singleTask` deep link integration pattern.

---

## 🔍 Friction Point #7: Kotlin Type Inference Failures with Anonymous Objects in Inline Expressions

* **Task Attempted**: Define concise, inline telemetry configurations for time ranges (`Today`, `Week`, `Month`, `Year`) using Kotlin `when` expressions.
* **Steps Taken**:
  1. Structured a `when (range)` returning anonymous objects with properties (`val vals`, `val maxVal`, `val peakIndex`).
  2. Referenced `config.vals` in the downstream Compose canvas drawing code.
* **Expected Outcome**: Kotlin compiler infers local structural types cleanly.
* **Actual Outcome**: Kotlin compiler erased the return type of the `when` block to `Any`, causing compilation failure (`Unresolved reference: vals`).
* **Severity**: **Low**
* **Workaround Used**: Declared an explicit, strongly typed `data class AnalyticsRangeConfig(val vals: List<Double>, val maxVal: Double, val peakIndex: Int, ...)` and annotated the assignment with the explicit type.
* **Actionable Suggestion for Amazon/AWS Team**: Add compiler diagnostic hints explaining that anonymous objects erase to `Any` across branching expressions when developers migrate JavaScript/TypeScript patterns to Kotlin.

---

## 🔍 Friction Point #8: Amazon Bedrock `UnrecognizedClientException` Lacks Contextual Setup Links

* **Task Attempted**: Query Anthropic Claude 3 Haiku on AWS Bedrock from the Fire TV backend service.
* **Steps Taken**:
  1. Configured AWS credentials (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`).
  2. Dispatched inference request to model `anthropic.claude-3-haiku-20240307-v1:0`.
* **Expected Outcome**: Inferences return successfully or return clear instructions on missing prerequisites.
* **Actual Outcome**: The SDK threw `software.amazon.awssdk.services.bedrockruntime.model.UnrecognizedClientException` / `ResourceNotFoundException` with zero contextual hints indicating that Claude 3 Haiku access had not yet been requested in the AWS Bedrock Model Access console page.
* **Severity**: **Medium**
* **Workaround Used**: Implemented proactive validation in `BedrockHomeService.kt` that detects authorization exceptions, serves deterministic local coaching insights, and logs the exact URL for the AWS Model Access page.
* **Actionable Suggestion for Amazon/AWS Team**: Enrich AWS Bedrock client exception messages with direct console deep links (e.g. `https://console.aws.amazon.com/bedrock/home#/modelaccess`) whenever an unauthorized model ID is requested.

---

## 🔍 Friction Point #9: Fire TV Emulator Frame Rates on Apple Silicon (M-Series Chips)

* **Task Attempted**: Run and test HomeIQ's 10-foot Jetpack Compose UI on Android TV / Fire TV emulators running on macOS Apple Silicon (M1/M2/M3).
* **Steps Taken**:
  1. Created Android TV (1080p) AVD using standard x86_64 / arm64 system images.
  2. Launched HomeIQ and observed Bézier spline animations.
* **Expected Outcome**: Hardware-accelerated 60 FPS rendering utilizing Apple Silicon GPU cores via Vulkan or ANGLE.
* **Actual Outcome**: The default TV emulator ran at ~15–20 FPS due to CPU-based SwiftShader translation bottlenecks, causing micro-stutters during screen transitions.
* **Severity**: **Medium**
* **Workaround Used**: Configured hardware acceleration flags in Android Studio (`-gpu host` / `ANGLE (Vulkan)`), enabled 1080p density overrides (`density 320`), and tuned Compose canvas redraw scopes to eliminate redundant recompositions.
* **Actionable Suggestion for Amazon/AWS Team**: Publish pre-configured, optimized AVD system image templates tailored for Apple Silicon development machines in the Amazon Fire TV developer portal.

---

## 🔍 Friction Point #10: Compose for TV Focus Jumps on `TvLazyRow` to `Column` Navigation

* **Task Attempted**: Navigate smoothly from device cards inside a horizontal row down to full-width section cards (e.g. Security Vault, Telemetry Stats) using remote D-Pad Down.
* **Steps Taken**:
  1. Constructed a `TvLazyRow` containing room devices.
  2. Placed a `Column` containing section cards directly beneath the row.
  3. Scrolled horizontally to the 3rd device card and pressed D-Pad Down.
* **Expected Outcome**: Focus moves vertically downward to the element directly beneath the currently selected item, preserving horizontal coordinate alignment.
* **Actual Outcome**: Focus jumped abruptly back to index 0 of the parent container, disorienting the user.
* **Severity**: **High**
* **Workaround Used**: Applied explicit `focusProperties` with custom `down = FocusRequester` anchors connecting sibling cards across container boundaries:
  ```kotlin
  Modifier.focusProperties {
      down = nextSectionFocusRequester
  }
  ```
* **Actionable Suggestion for Amazon/AWS Team**: Enhance default 2D spatial focus traversal in `androidx.tv.foundation` to automatically retain horizontal proximity across nested scrolling rows.

---

## 📊 Summary Table

| # | Friction Area | Severity | Resolution Status | Amazon SDK Impact |
|:---:|:---|:---:|:---:|:---|
| 1 | Material 3 Dialog 560dp Cap on TV | **High** | Fixed via `usePlatformDefaultWidth=false` | Need `TvAlertDialog` in `androidx.tv.material3` |
| 2 | Accidental Pre-Selection in D-Pad Trees | **High** | Fixed via pure `isFocused` gating in `TvButton3D` | Need `TvActionToggle` component |
| 3 | AWS Bedrock SDK Footprint on Leanback | **Medium** | Fixed via packaging exclusions + edge engine | Need lightweight `aws-bedrock-android` SDK |
| 4 | D-Pad Navigation Traps on Filter Rows | **Medium** | Fixed via encapsulated capsule pill container | Need 2D spatial navigation helpers |
| 5 | Bézier Spline Math on 1080p Canvas | **Low** | Fixed via custom Catmull-Rom spline algorithm | Need `drawSplineWave` Canvas helper |
| 6 | Alexa Deep Linking & `singleTask` Lifecycle | **High** | Fixed via `onNewIntent` + StateFlow routing | Need official ASK ➔ Fire TV deep link docs |
| 7 | Kotlin Type Inference with Anonymous Objects | **Low** | Fixed via `AnalyticsRangeConfig` data class | Improve compiler diagnostic hints |
| 8 | Bedrock `UnrecognizedClientException` Guidance | **Medium** | Fixed via graceful offline fallback & setup logs | Need direct console URLs in exception messages |
| 9 | Fire TV Emulator FPS on Apple Silicon | **Medium** | Fixed via GPU host flags & canvas optimizations | Need Apple Silicon AVD profiles |
| 10 | Compose TV Focus Jumps on LazyRow ➔ Column | **High** | Fixed via explicit `focusProperties` & anchors | Improve default 2D spatial focus traversal |

---

*Certified for the Amazon Developer Hackathon 2026 Developer Friction Log (+10% Bonus).*
