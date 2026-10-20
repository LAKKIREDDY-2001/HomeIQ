# HomeIQ Accessibility Compliance Documentation (WCAG AAA) ♿

> *"Smart homes should be accessible to everyone."*

HomeIQ was engineered from the ground up to follow **WCAG 2.2 AAA** guidelines and Android TV accessibility standards, ensuring elderly residents and individuals with visual, auditory, or motor impairments can independently manage their home energy and safety.

---

## 🌟 Key Accessibility Pillars

### 1. 🗣️ TalkBack Screen Reader Integration
* Every interactive composable—from room tabs to energy graphs, steppers, and AI coach responses—is equipped with explicit `contentDescription` tags.
* Dynamic state changes (e.g. hazard detection overlay, circuit breaker trips) post accessibility announcement live events to TalkBack.

### 2. 👁️ High Contrast Mode (WCAG AAA Compliant)
* Adheres to a minimum **7:1 contrast ratio**:
  - Deep Midnight Canvas: `#070B16`
  - Crisp Snow Text: `#F8FAFC`
  - High-visibility Neon Focus Rings: `#38BDF8` with a 3dp stroke.
* Text elements and critical hazard icons are visually legible from standard 10-foot couch distances.

### 3. 🔍 Large Text Mode (1.3x Scaling for Seniors)
* Specifically tailored for seniors like **Grandma Savithri (72)** living independently:
  - Typography dynamically scales up to 1.3x without clipping or truncation.
  - Card layouts gracefully reflow into spacious containers on 1080p and 4K displays.

### 4. 🎮 D-Pad Navigation & Focus Predictability
* **Oversized Focus Targets**: All remote-navigable touch targets exceed 48dp × 48dp.
* **1.05x Dynamic Scale Animation**: Focused cards smoothly scale up with an illuminated cyan glow border.
* **Zero Accidental Pre-Highlights**: Focus cursor states are strictly decoupled from persistent device state flags.

### 5. 🌐 Multi-Language Localization
* Available in:
  - **English** (Default)
  - **Hindi (हिन्दी)**: *"परिवार रक्षक"*, *"शुभ प्रभात"*, *"सब सुरक्षित"*
  - **Telugu (తెలుగు)**: *"కుటుంబ సంరక్షకుడు"*, *"శుభోదయం"*, *"అంతా సురక్షితం"*
* Supports cultural inclusivity across diverse multi-generational households.

---

*Certified for the Amazon Fire TV & Alexa Hackathon 2026.*
