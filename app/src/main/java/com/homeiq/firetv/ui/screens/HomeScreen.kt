package com.homeiq.firetv.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.homeiq.firetv.model.*
import com.homeiq.firetv.viewmodel.HomeViewModel


// ─── Theme Colors Matching the Reference UI/UX Mockup ───
object Theme3D {
    val bgPrimary = Color(0xFF070B16)
    val cardBg = Color(0x35142850)
    val cardBorder = Color(0x3038BDF8)
    val cardBorderSubtle = Color(0x18FFFFFF)
    val accentCyan = Color(0xFF38BDF8)
    val accentBlue = Color(0xFF2563EB)
    val accentGlow = Color(0x5538BDF8)
    val successGreen = Color(0xFF10B981)
    val warningAmber = Color(0xFFF59E0B)
    val dangerRed = Color(0xFFEF4444)
    val purpleAccent = Color(0xFFA855F7)
    val pinkAccent = Color(0xFFEC4899)
    val textPrimary = Color(0xFFF8FAFC)
    val textSecondary = Color(0xFF94A3B8)
    val textMuted = Color(0xFF64748B)
}

// ─── Dynamic Ambient Glowing Background ───
@Composable
fun Dynamic3DBackground(modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = modifier.background(Theme3D.bgPrimary)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(500.dp)
                .offset(x = (-140).dp, y = (-140).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x350284C7).copy(alpha = pulse * 0.5f), Color.Transparent),
                        center = Offset(250f, 250f),
                        radius = 500f
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(450.dp)
                .offset(x = 100.dp, y = 100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x253B82F6).copy(alpha = pulse * 0.4f), Color.Transparent),
                        center = Offset(225f, 225f),
                        radius = 450f
                    )
                )
        )
    }
}

// ─── Glassmorphism Card ───
@Composable
fun Card3D(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = Theme3D.cardBorderSubtle,
    containerColor: Color = Theme3D.cardBg,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.025f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f),
        label = "scale"
    )

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }
    } else {
        Modifier.focusable(interactionSource = interactionSource)
    }

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = if (isFocused)
                    Brush.verticalGradient(listOf(Color(0x450284C7), Color(0x25142850)))
                else
                    Brush.verticalGradient(listOf(containerColor, Color(0x15091224))),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Theme3D.accentCyan else borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = if (isFocused) 16.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isFocused) Theme3D.accentGlow else Color.Transparent,
                spotColor = if (isFocused) Theme3D.accentGlow else Color.Transparent
            )
            .then(clickableModifier)
            .padding(12.dp),
        content = content
    )
}

// ─── Focusable TV Button ───
@Composable
fun TvButton3D(
    text: String,
    isSelected: Boolean = false,
    isDanger: Boolean = false,
    fontSize: Int = 12,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 350f),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = when {
            isFocused -> Color(0xFF0284C7)
            isSelected -> Color(0xFF2563EB)
            isDanger -> Color(0x30EF4444)
            else -> Color(0x351E293B)
        },
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isFocused || isSelected) 1.8.dp else 1.dp,
                color = if (isFocused || isSelected) Theme3D.accentCyan else Theme3D.cardBorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.SemiBold,
            color = Theme3D.textPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

// ─── Prominent, High-Contrast TV Back Button for Every Page ───
@Composable
fun TvBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Back to Dashboard"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 350f),
        label = "backBtnScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        color = if (isFocused) Color(0xFF0284C7) else Color(0x601E293B),
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isFocused) 2.dp else 1.2.dp,
                color = if (isFocused) Theme3D.accentCyan else Color(0x6038BDF8),
                shape = RoundedCornerShape(20.dp)
            )
            .shadow(
                elevation = if (isFocused) 12.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isFocused) Theme3D.accentCyan else Color.Transparent
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isFocused) Color.White.copy(alpha = 0.25f) else Color(0x4038BDF8)),
                contentAlignment = Alignment.Center
            ) {
                Text("❮", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = if (isFocused) Color.White else Theme3D.accentCyan)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ─── High-Contrast TV Stepper Button (+ and -) ───
@Composable
fun TvStepperBtn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    fontSize: Int = 16,
    accentColor: Color = Theme3D.accentCyan
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 350f),
        label = "stepperScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(8.dp),
        color = if (isFocused) Color(0xFF0284C7) else Color(0xFF1E293B),
        modifier = modifier
            .size(size)
            .scale(scale)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Theme3D.accentCyan else Color(0x4038BDF8),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isFocused) Color.White else accentColor
            )
        }
    }
}


// ─── Interactive Slider Bar with Glowing Knob ───
@Composable
fun GlowSlider(
    value: Float, // 0..1
    color: Color = Theme3D.accentCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(CircleShape)
            .background(Color(0x30FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(value)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(listOf(color.copy(alpha = 0.6f), color))
                )
        )
    }
}

// ─── Glowing Donut / Ring Gauge ───
@Composable
fun DonutGauge3D(
    progress: Float, // 0..1
    size: Dp = 90.dp,
    strokeWidth: Dp = 9.dp,
    primaryColor: Color = Theme3D.accentCyan,
    trackColor: Color = Color(0x20FFFFFF)
) {
    Canvas(modifier = Modifier.size(size)) {
        val sw = strokeWidth.toPx()
        val arcSize = Size(this.size.width - sw, this.size.height - sw)
        val topLeft = Offset(sw / 2, sw / 2)

        // Background Track
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = sw, cap = StrokeCap.Round),
            topLeft = topLeft,
            size = arcSize
        )

        // Active Arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(primaryColor.copy(alpha = 0.5f), primaryColor),
                center = Offset(this.size.width / 2, this.size.height / 2)
            ),
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = sw, cap = StrokeCap.Round),
            topLeft = topLeft,
            size = arcSize
        )
    }
}

// ─── MAIN APP CONTAINER ───
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val selectedRoom by viewModel.selectedRoom.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Global Hardware Back Handler for Fire TV Remote and Mobile Phone
    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    val darkBg = when (state.activeTheme) {
        "OLED_BLACK" -> Color.Black
        "NEON_CYBER" -> Color(0xFF030712)
        else -> Theme3D.bgPrimary
    }

    Box(modifier = Modifier.fillMaxSize().background(darkBg)) {
        Dynamic3DBackground(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ═══ SCREEN CONTENT (Exact match to 5 screens of the Mockup) ═══
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopStart) {
                    when (selectedTab) {
                        0 -> Screen1MainDashboard(state = state, viewModel = viewModel)
                        1 -> Screen2RoomDetailView(state = state, selectedRoom = selectedRoom, viewModel = viewModel)
                        2 -> Screen3EnergyAnalytics(state = state, viewModel = viewModel)
                        3 -> Screen4SecurityAndCameras(state = state, viewModel = viewModel)
                        4 -> Screen5AiAssistantVoice(state = state, viewModel = viewModel)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ═══ BOTTOM SCREEN SELECTOR (Pattern-matched to the 5 Mockup Screens) ═══
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x30101A30))
                        .border(1.dp, Color(0x2038BDF8), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val patternScreens = listOf(
                        "1. MAIN DASHBOARD",
                        "2. ROOM DETAIL VIEW",
                        "3. ENERGY ANALYTICS",
                        "4. SECURITY & CAMERAS",
                        "5. AI ASSISTANT / VOICE COMMAND"
                    )
                    patternScreens.forEachIndexed { index, label ->
                        TvButton3D(
                            text = label,
                            isSelected = selectedTab == index,
                            fontSize = 10,
                            onClick = { viewModel.selectTab(index) }
                        )
                    }
                }
            }
        }

        // ─── ENHANCEMENT 2: Family Guardian Anomaly Slide-in Notification Overlay ───
        AnimatedVisibility(
            visible = state.activeAnomaly != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -80 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -80 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            state.activeAnomaly?.let { anom ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xF21C0B0B),
                    modifier = Modifier
                        .width(760.dp)
                        .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                        .shadow(20.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFEF4444))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x35EF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚠", fontSize = 18.sp, color = Color(0xFFEF4444))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(anom.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x30EF4444),
                                        modifier = Modifier.border(1.dp, Color(0x60EF4444), RoundedCornerShape(6.dp))
                                    ) {
                                        Text("FAMILY GUARDIAN ALERT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(anom.description, fontSize = 10.sp, color = Theme3D.textSecondary)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            TvButton3D(
                                text = "🛑 Auto-Shutoff (60s)",
                                isDanger = true,
                                fontSize = 10,
                                onClick = { viewModel.triggerStoveAutoShutoff() }
                            )
                            TvButton3D(
                                text = "✅ Dismiss",
                                fontSize = 10,
                                onClick = { viewModel.dismissActiveAnomaly() }
                            )
                        }
                    }
                }
            }
        }

        // Floating Animated HUD Status Banner (Instant Feedback for Every Button Click)
        AnimatedVisibility(
            visible = state.hudNotification != null && state.activeAnomaly == null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -50 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -50 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
        ) {
            state.hudNotification?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xF20F172A),
                    modifier = Modifier
                        .border(1.5.dp, Theme3D.accentCyan, RoundedCornerShape(24.dp))
                        .shadow(18.dp, RoundedCornerShape(24.dp), spotColor = Theme3D.accentCyan)
                        .clickable { viewModel.dismissHudNotification() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Theme3D.accentCyan))
                        Spacer(Modifier.width(10.dp))
                        Text(msg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.width(14.dp))
                        Text("✕", fontSize = 11.sp, color = Theme3D.textMuted)
                    }
                }
            }
        }


        // Search Overlay
        if (state.isSearchOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleSearchDialog() },
                title = { Text("🔍 Search HomeIQ Devices", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search appliances, lights...", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Theme3D.accentCyan
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TvButton3D(text = "Close", onClick = { viewModel.toggleSearchDialog() })
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        // Settings Dialog
        if (state.isSettingsOpen) {
            SettingsDialog(
                settings = state.settings,
                wearable = state.wearable,
                onRateChange = { viewModel.updateRatePerKwh(it) },
                onToggleUnit = { viewModel.toggleTempUnit() },
                onToggleAutoArm = { viewModel.toggleAutoArming() },
                onToggleContrast = { viewModel.toggleHighContrast() },
                onToggleWearableHaptics = { viewModel.toggleWearableHaptics() },
                onStartPairingPioneer = { viewModel.startPairingPioneer() },
                onUnpairPioneer = { viewModel.unpairPioneer() },
                onClose = { viewModel.toggleSettingsDialog() }
            )
        }

        // Multi-Device & Amazon Bee Pioneer Connectivity Hub
        if (state.isConnectHubOpen) {
            ConnectHubDialog(
                state = state,
                viewModel = viewModel,
                onClose = { viewModel.toggleConnectHubDialog() }
            )
        }

        // Customer Profile & Family Guardian Safety Vault Dialog
        if (state.isProfileOpen || state.isFamilyGuardianOpen) {
            CustomerProfileSecurityDialog(
                state = state,
                viewModel = viewModel,
                onClose = {
                    if (state.isProfileOpen) viewModel.toggleProfileDialog()
                    if (state.isFamilyGuardianOpen) viewModel.toggleFamilyGuardianDialog()
                }
            )
        }

        // Gamification & Eco Challenges Dialog
        if (state.isGamificationOpen) {
            GamificationDialog(
                state = state,
                viewModel = viewModel,
                onClose = { viewModel.toggleGamificationDialog() }
            )
        }
    }
}

// ─── BRAND LOGO COMPONENT ───
@Composable
fun HomeIqBrandLogo(onClick: (() -> Unit)? = null) {
    val modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text("Home", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text("IQ", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Theme3D.accentCyan)
    }
}

// ─── SCREEN 1: MAIN DASHBOARD ───
@Composable
fun Screen1MainDashboard(
    state: HomeState,
    viewModel: HomeViewModel
) {
    val activeRoom = viewModel.selectedRoom.collectAsState().value ?: "living"
    val currentRoom = state.rooms.find { it.id == activeRoom } ?: state.rooms.firstOrNull() ?: state.rooms[0]

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HomeIqBrandLogo()
                Spacer(Modifier.width(12.dp))
                // LIVE Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x2010B981),
                    modifier = Modifier.border(1.dp, Color(0x4010B981), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Theme3D.successGreen))
                        Spacer(Modifier.width(5.dp))
                        Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                    }
                }
            }

            // Right Info
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Mobile Phone & Bee Pioneer Hub Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x300284C7),
                    modifier = Modifier
                        .border(1.dp, Theme3D.accentCyan, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleConnectHubDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📱", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Mobile & 🐝 Pioneer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }

                // Gamification & Eco Challenges Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x3010B981),
                    modifier = Modifier
                        .border(1.dp, Color(0x6010B981), RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleGamificationDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏆", fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Challenges 🔥 ${state.ecoStreak.days}d", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                    }
                }

                // Theme Switcher Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x351E293B),
                    modifier = Modifier
                        .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(12.dp))
                        .clickable { viewModel.cycleTheme() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎨", fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            when (state.activeTheme) {
                                "OLED_BLACK" -> "OLED"
                                "NEON_CYBER" -> "Neon"
                                else -> "Navy"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Text(state.liveDateTimeString, fontSize = 11.sp, color = Theme3D.textSecondary)
                Text("🔍", fontSize = 14.sp, modifier = Modifier.clickable { viewModel.toggleSearchDialog() })
                Text("⚙️", fontSize = 14.sp, modifier = Modifier.clickable { viewModel.toggleSettingsDialog() })
                UserProfileAuthBadge(state = state, viewModel = viewModel)
            }
        }

        Spacer(Modifier.height(10.dp))

        // MAIN 2-COLUMN LAYOUT
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // LEFT COLUMN (67%)
            Column(
                modifier = Modifier.weight(0.67f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. AI Recommendation Banner (Normal stage, highlights on focus)
                Card3D(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.selectTab(4) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Robot Avatar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(Theme3D.accentCyan, Color(0xFF1E3A8A)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🤖", fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("IQ • ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                    Text("Your AC uses ", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                    Text("42%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                    Text(" of total energy.", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                }
                                Spacer(Modifier.height(2.dp))
                                Text("Setting to 26°C could save ₹85/month. Tap to talk to IQ ➔", fontSize = 11.sp, color = Theme3D.textSecondary)
                            }
                        }
                        Text(">", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }

                // 2. Three Metric Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // TODAY Card
                    Card3D(modifier = Modifier.weight(1f), onClick = { viewModel.selectTab(2) }) {
                        Text("⚡ TODAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("18.4", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("kWh", fontSize = 11.sp, color = Theme3D.textSecondary, modifier = Modifier.padding(bottom = 2.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        // Mini sparkline bars
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.height(18.dp)) {
                            listOf(6, 10, 14, 18, 12, 8, 16).forEach { h ->
                                Box(modifier = Modifier.weight(1f).height(h.dp).clip(RoundedCornerShape(1.dp)).background(Theme3D.accentCyan))
                            }
                        }
                    }

                    // WEEK Card
                    Card3D(modifier = Modifier.weight(1f), onClick = { viewModel.selectTab(2) }) {
                        Text("📊 WEEK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("112.3", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("kWh", fontSize = 11.sp, color = Theme3D.textSecondary, modifier = Modifier.padding(bottom = 2.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("↓ 12%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                            Spacer(Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.height(14.dp)) {
                                listOf(12, 14, 10, 8, 6).forEach { h ->
                                    Box(modifier = Modifier.width(3.dp).height(h.dp).clip(RoundedCornerShape(1.dp)).background(Theme3D.successGreen))
                                }
                            }
                        }
                    }

                    // MONTH Card
                    Card3D(modifier = Modifier.weight(1f), onClick = { viewModel.selectTab(2) }) {
                        Text("📅 MONTH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("485.6", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("kWh", fontSize = 11.sp, color = Theme3D.textSecondary, modifier = Modifier.padding(bottom = 2.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹33.12", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                            Spacer(Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.height(14.dp)) {
                                listOf(8, 12, 16, 14, 18).forEach { h ->
                                    Box(modifier = Modifier.width(3.dp).height(h.dp).clip(RoundedCornerShape(1.dp)).background(Theme3D.warningAmber))
                                }
                            }
                        }
                    }
                }

                // 3. Room Selector Pills Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val rooms = listOf(
                            "living" to "🛋️ Living Room",
                            "kitchen" to "🍳 Kitchen",
                            "bedroom" to "🛏️ Bedroom",
                            "garage" to "🚗 Garage"
                        )
                        rooms.forEach { (id, label) ->
                            val isSel = (activeRoom == id)
                            TvButton3D(
                                text = label,
                                isSelected = isSel,
                                onClick = {
                                    viewModel.selectRoom(id)
                                }
                            )
                        }
                    }

                    // Direct Room Detail Action Button
                    TvButton3D(
                        text = "View Room Detail ➔",
                        fontSize = 11,
                        onClick = {
                            viewModel.selectRoom(activeRoom)
                            viewModel.selectTab(1)
                        }
                    )
                }

                // 4. Device Rows List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(currentRoom.devices, key = { it.id }) { dev ->
                        Card3D(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.toggleDevice(dev.id) }
                                ) {
                                    Text(
                                        when (dev.type) {
                                            DeviceType.TV -> "📺"
                                            DeviceType.LIGHT -> "💡"
                                            DeviceType.AC -> "❄️"
                                            DeviceType.SPEAKER -> "🔊"
                                            DeviceType.FAN -> "🌀"
                                            DeviceType.CAMERA -> "📷"
                                            DeviceType.LOCK -> "🔒"
                                            DeviceType.DOOR -> "🚪"
                                            DeviceType.PLUG -> "🔌"
                                            else -> "⚡"
                                        },
                                        fontSize = 18.sp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(dev.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(
                                            when (dev.type) {
                                                DeviceType.TV -> "Samsung Smart TV"
                                                DeviceType.LIGHT -> "Ceiling Light"
                                                DeviceType.AC -> "Daikin Inverter"
                                                DeviceType.SPEAKER -> "Sonos Play"
                                                DeviceType.PLUG -> "Smart Plug 16A"
                                                DeviceType.FAN -> "Havells Inverter Fan"
                                                DeviceType.CAMERA -> "1080p Smart Cam"
                                                DeviceType.LOCK -> "Smart Deadbolt"
                                                else -> "Connected Appliance"
                                            },
                                            fontSize = 10.sp,
                                            color = Theme3D.textMuted
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Quick Stepper Buttons (+ / -) for TV, AC, Light, Speaker
                                    when (dev.type) {
                                        DeviceType.AC -> {
                                            val temp = dev.temperature?.toInt() ?: 24
                                            TvStepperBtn(text = "—", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustTemperature(dev.id, -1.0) })
                                            Text("$temp°C", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            TvStepperBtn(text = "+", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustTemperature(dev.id, 1.0) })
                                        }
                                        DeviceType.TV, DeviceType.LIGHT -> {
                                            val bright = dev.brightness ?: 80
                                            TvStepperBtn(text = "—", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustBrightness(dev.id, -10) })
                                            Text("$bright%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            TvStepperBtn(text = "+", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustBrightness(dev.id, 10) })
                                        }
                                        DeviceType.SPEAKER -> {
                                            val vol = dev.volume ?: 60
                                            TvStepperBtn(text = "—", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustVolume(dev.id, -10) })
                                            Text("$vol%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            TvStepperBtn(text = "+", size = 32.dp, fontSize = 15, onClick = { viewModel.adjustVolume(dev.id, 10) })
                                        }
                                        else -> {}
                                    }

                                    // Power Watts
                                    Text("${dev.powerWatts.toInt()} W", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Theme3D.textSecondary)

                                    // ON / OFF Toggle Pill
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (dev.isOn) Color(0xFF16A34A) else Color(0xFF334155),
                                        modifier = Modifier.clickable { viewModel.toggleDevice(dev.id) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (dev.isOn) Color.White else Color(0xFF94A3B8)))
                                            Spacer(Modifier.width(4.dp))
                                            Text(if (dev.isOn) "ON" else "OFF", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    // Details Chevron Button
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0x301E293B),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                viewModel.selectRoom(currentRoom.id)
                                                viewModel.selectTab(1)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("›", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RIGHT COLUMN (33%)
            Column(
                modifier = Modifier.weight(0.33f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Daily Energy Goal Card
                Card3D(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.toggleEcoMode() }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Daily Energy Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                        Spacer(Modifier.height(8.dp))
                        Box(contentAlignment = Alignment.Center) {
                            DonutGauge3D(progress = 0.61f, size = 85.dp, strokeWidth = 9.dp)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("61%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("of 30 kWh", fontSize = 9.sp, color = Theme3D.textSecondary)
                            }
                        }
                    }
                }

                // 2. 24-Hour Usage (kWh) Histogram
                Card3D(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.selectTab(2) }) {
                    Column {
                        Text("24-Hour Usage (kWh)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth().height(55.dp), verticalAlignment = Alignment.Bottom) {
                            Column(modifier = Modifier.padding(end = 4.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Text("3", fontSize = 8.sp, color = Theme3D.textMuted)
                                Text("2", fontSize = 8.sp, color = Theme3D.textMuted)
                                Text("1", fontSize = 8.sp, color = Theme3D.textMuted)
                                Text("0", fontSize = 8.sp, color = Theme3D.textMuted)
                            }
                            Row(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val heights = listOf(8, 10, 6, 8, 12, 16, 22, 28, 34, 45, 52, 48, 36, 26, 18, 14, 10)
                                heights.forEachIndexed { idx, h ->
                                    val isPeak = idx == 10
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(h.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isPeak) Theme3D.accentCyan else Color(0xFF1D4ED8))
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(start = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("12AM", fontSize = 8.sp, color = Theme3D.textMuted)
                            Text("6AM", fontSize = 8.sp, color = Theme3D.textMuted)
                            Text("12PM", fontSize = 8.sp, color = Theme3D.textMuted)
                            Text("6PM", fontSize = 8.sp, color = Theme3D.textMuted)
                            Text("12AM", fontSize = 8.sp, color = Theme3D.textMuted)
                        }
                    }
                }

                // 3. HOME MODE Card
                Card3D(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Theme3D.successGreen,
                    onClick = { viewModel.selectTab(3) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16A34A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔒", fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("HOME MODE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("2 cameras • 3 doors", fontSize = 10.sp, color = Theme3D.textSecondary)
                            }
                        }
                        Text(">", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Theme3D.textSecondary)
                    }
                }

                // 4. Weather Card with Dynamic Location Switching
                Card3D(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.cycleLocation() }) {
                    val currentCity = state.availableCities.getOrNull(state.selectedCityIndex) ?: state.availableCities[0]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentCity.icon, fontSize = 26.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${state.weather.temp}°C", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(Modifier.width(6.dp))
                                    Text("• ${currentCity.name}, ${currentCity.region}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                }
                                Text("${state.weather.condition}, ${state.weather.humidity}% humidity", fontSize = 10.sp, color = Theme3D.textSecondary)
                            }
                        }
                        Text("⇄", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }
            }
        }
    }
}

// ─── SCREEN 2: ROOM DETAIL VIEW ───
@Composable
fun Screen2RoomDetailView(
    state: HomeState,
    selectedRoom: String?,
    viewModel: HomeViewModel
) {
    val activeRoomId = selectedRoom ?: "living"
    val room = state.rooms.find { it.id == activeRoomId } ?: state.rooms.firstOrNull() ?: state.rooms[0]

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER: Back Button + Room Title + Devices Count + Clock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Prominent TV Back Button
                TvBackButton(onClick = { viewModel.selectTab(0) })

                Spacer(Modifier.width(14.dp))
                Text(room.icon, fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(room.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(10.dp))
                // Active Devices Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x2010B981),
                    modifier = Modifier.border(1.dp, Color(0x4010B981), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Theme3D.successGreen))
                        Spacer(Modifier.width(5.dp))
                        Text("${room.devices.count { it.isOn }} devices active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x300284C7),
                    modifier = Modifier
                        .border(1.dp, Theme3D.accentCyan, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleConnectHubDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📱", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Mobile & 🐝 Pioneer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }
                Text(state.liveDateTimeString, fontSize = 11.sp, color = Theme3D.textSecondary)
                UserProfileAuthBadge(state = state, viewModel = viewModel)
            }
        }

        Spacer(Modifier.height(10.dp))

        // 2x2 DEVICE CONTROL CARDS GRID
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            // LEFT COLUMN (TV & AC)
            Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1: TV
                val tvDev = state.rooms.flatMap { it.devices }.find { it.id == "tv1" }
                val tvBright = tvDev?.brightness ?: 80
                Card3D(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 3D TV Frame Graphic
                            Box(
                                modifier = Modifier
                                    .size(54.dp, 36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6))))
                                    .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📺", fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("TV", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (tvDev?.isOn == true) Color(0xFF16A34A) else Color(0xFF475569),
                                    modifier = Modifier.clickable { viewModel.toggleDevice("tv1") }
                                ) {
                                    Text(if (tvDev?.isOn == true) "ON" else "OFF", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("⚡ ${tvDev?.powerWatts?.toInt() ?: 120} W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                    }

                    Spacer(Modifier.weight(1f))

                    // TV Brightness Controls with Increasing (+) and Decreasing (-) Buttons
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Brightness", fontSize = 11.sp, color = Theme3D.textSecondary)
                        Spacer(Modifier.width(10.dp))
                        TvStepperBtn(text = "—", onClick = { viewModel.adjustBrightness("tv1", -10) })
                        Spacer(Modifier.width(8.dp))
                        GlowSlider(value = tvBright / 100f, color = Theme3D.accentCyan, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        TvStepperBtn(text = "+", onClick = { viewModel.adjustBrightness("tv1", 10) })
                        Spacer(Modifier.width(10.dp))
                        Text("$tvBright%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Card 3: AC
                val acDev = state.rooms.flatMap { it.devices }.find { it.id == "ac1" }
                val acTemp = acDev?.temperature?.toInt() ?: 24
                Card3D(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // AC Graphic Unit
                            Box(
                                modifier = Modifier
                                    .size(54.dp, 28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("❄️", fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("AC", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (acDev?.isOn == true) Color(0xFF16A34A) else Color(0xFF475569),
                                    modifier = Modifier.clickable { viewModel.toggleDevice("ac1") }
                                ) {
                                    Text(if (acDev?.isOn == true) "ON" else "OFF", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("⚡ ${acDev?.powerWatts?.toInt() ?: 1500} W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                    }

                    Spacer(Modifier.weight(1f))

                    // AC Temperature Increasing (+) and Decreasing (-) Buttons
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("❄️", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("Temperature", fontSize = 11.sp, color = Theme3D.textSecondary)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TvStepperBtn(text = "—", onClick = { viewModel.adjustTemperature("ac1", -1.0) })
                            Text("$acTemp°C", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            TvStepperBtn(text = "+", onClick = { viewModel.adjustTemperature("ac1", 1.0) })
                        }
                    }
                }
            }

            // RIGHT COLUMN (Ceiling Light & Speaker)
            Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 2: Ceiling Light
                val lightDev = state.rooms.flatMap { it.devices }.find { it.id == "light1" }
                val lightBright = lightDev?.brightness ?: 80
                Card3D(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Glowing Lamp Graphic
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x30F59E0B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡", fontSize = 24.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Ceiling Light", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (lightDev?.isOn == true) Color(0xFF16A34A) else Color(0xFF475569),
                                    modifier = Modifier.clickable { viewModel.toggleDevice("light1") }
                                ) {
                                    Text(if (lightDev?.isOn == true) "ON" else "OFF", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("⚡ ${lightDev?.powerWatts?.toInt() ?: 45} W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                    }

                    Spacer(Modifier.weight(1f))

                    // Light Brightness Controls with Increasing (+) and Decreasing (-) Buttons
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Brightness", fontSize = 11.sp, color = Theme3D.textSecondary)
                        Spacer(Modifier.width(10.dp))
                        TvStepperBtn(text = "—", onClick = { viewModel.adjustBrightness("light1", -10) })
                        Spacer(Modifier.width(8.dp))
                        GlowSlider(value = lightBright / 100f, color = Theme3D.warningAmber, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        TvStepperBtn(text = "+", onClick = { viewModel.adjustBrightness("light1", 10) })
                        Spacer(Modifier.width(10.dp))
                        Text("$lightBright%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Card 4: Speaker
                val spkDev = state.rooms.flatMap { it.devices }.find { it.id == "spk1" }
                val spkVol = spkDev?.volume ?: 60
                Card3D(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Cylindrical Speaker Graphic
                            Box(
                                modifier = Modifier
                                    .size(36.dp, 46.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF334155))
                                    .border(1.5.dp, Color(0xFF64748B), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔊", fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Speaker", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (spkDev?.isOn == true) Color(0xFF16A34A) else Color(0xFF475569),
                                    modifier = Modifier.clickable { viewModel.toggleDevice("spk1") }
                                ) {
                                    Text(if (spkDev?.isOn == true) "ON" else "OFF", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("⚡ ${spkDev?.powerWatts?.toInt() ?: 30} W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                    }

                    Spacer(Modifier.weight(1f))

                    // Volume Controls with Increasing (+) and Decreasing (-) Buttons
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("🔊 Volume", fontSize = 11.sp, color = Theme3D.textSecondary)
                        Spacer(Modifier.width(10.dp))
                        TvStepperBtn(text = "—", onClick = { viewModel.adjustVolume("spk1", -10) })
                        Spacer(Modifier.width(8.dp))
                        GlowSlider(value = spkVol / 100f, color = Theme3D.purpleAccent, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        TvStepperBtn(text = "+", onClick = { viewModel.adjustVolume("spk1", 10) })
                        Spacer(Modifier.width(10.dp))
                        Text("$spkVol%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // BOTTOM BAR: ROOM ENERGY BREAKDOWN
        Card3D(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Room Energy:  1,745 W total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))

                    // Segmented Color Bar
                    Row(modifier = Modifier.fillMaxWidth(0.95f).height(8.dp).clip(CircleShape)) {
                        Box(modifier = Modifier.weight(0.86f).fillMaxHeight().background(Color(0xFF3B82F6)))
                        Box(modifier = Modifier.weight(0.07f).fillMaxHeight().background(Color(0xFF8B5CF6)))
                        Box(modifier = Modifier.weight(0.03f).fillMaxHeight().background(Color(0xFFF59E0B)))
                        Box(modifier = Modifier.weight(0.04f).fillMaxHeight().background(Color(0xFFEC4899)))
                    }

                    Spacer(Modifier.height(8.dp))

                    // Color Legend
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
                            Spacer(Modifier.width(5.dp))
                            Text("AC  86%", fontSize = 10.sp, color = Theme3D.textSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF8B5CF6)))
                            Spacer(Modifier.width(5.dp))
                            Text("TV  7%", fontSize = 10.sp, color = Theme3D.textSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Spacer(Modifier.width(5.dp))
                            Text("Light  3%", fontSize = 10.sp, color = Theme3D.textSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFEC4899)))
                            Spacer(Modifier.width(5.dp))
                            Text("Speaker  2%", fontSize = 10.sp, color = Theme3D.textSecondary)
                        }
                    }
                }

                // Donut Gauge Right
                Box(contentAlignment = Alignment.Center) {
                    DonutGauge3D(progress = 0.86f, size = 68.dp, strokeWidth = 7.dp, primaryColor = Theme3D.accentCyan)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1,745 W", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Total", fontSize = 8.sp, color = Theme3D.textMuted)
                    }
                }
            }
        }
    }
}

data class AnalyticsRangeConfig(
    val dateText: String,
    val summaryTitle: String,
    val yLabels: List<String>,
    val gridVals: List<Float>,
    val maxVal: Float,
    val vals: List<Float>,
    val xLabels: List<String>,
    val peakIndex: Int,
    val peakPillText: String,
    val totalEnergy: String,
    val totalDelta: String,
    val estCost: String,
    val costDelta: String,
    val avgMetricLabel: String,
    val avgMetricVal: String,
    val avgMetricSub: String,
    val peakHours: String,
    val tierBadge: String
)

// ─── SCREEN 3: ENERGY ANALYTICS ───
@Composable
fun Screen3EnergyAnalytics(
    state: HomeState,
    viewModel: HomeViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER: Back Button + Logo + Title + Range Pills on Left, Hub Button + Clock on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TvBackButton(onClick = { viewModel.selectTab(0) })
                Spacer(Modifier.width(12.dp))
                HomeIqBrandLogo(onClick = { viewModel.selectTab(0) })
                Spacer(Modifier.width(10.dp))
                Text("📊 Energy Insights", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(14.dp))
                // Segmented Range Enclosed Capsule (Exact match to reference mockup)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x35101A30),
                    modifier = Modifier.border(1.dp, Color(0x3038BDF8), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Today", "Week", "Month", "Year").forEach { r ->
                            val isSel = (state.selectedAnalyticsRange == r)
                            Surface(
                                onClick = { viewModel.setAnalyticsRange(r) },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSel) Color(0xFF0284C7) else Color.Transparent,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = r,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else Theme3D.textSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x300284C7),
                    modifier = Modifier
                        .border(1.dp, Theme3D.accentCyan, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleConnectHubDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📱", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Mobile & 🐝 Pioneer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }
                Text(state.liveDateTimeString, fontSize = 11.sp, color = Theme3D.textSecondary, maxLines = 1)
                UserProfileAuthBadge(state = state, viewModel = viewModel)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Dynamic Range Data Configuration
        val config: AnalyticsRangeConfig = when (state.selectedAnalyticsRange) {
            "Today" -> AnalyticsRangeConfig(
                dateText = "📅 Today • Hourly Breakdown",
                summaryTitle = "Today's Summary",
                yLabels = listOf("4.5", "3.0", "1.5", "0.0"),
                gridVals = listOf(0f, 1.5f, 3.0f, 4.5f),
                maxVal = 4.5f,
                vals = listOf(0.6f, 0.9f, 1.4f, 2.8f, 3.8f, 2.9f, 1.8f, 1.1f),
                xLabels = listOf("12AM", "3AM", "6AM", "9AM", "12PM", "3PM", "6PM", "9PM"),
                peakIndex = 4,
                peakPillText = "6 PM: 3.8 kWh",
                totalEnergy = "18.4 kWh",
                totalDelta = "↓ 12% vs yest",
                estCost = "₹147.20",
                costDelta = "-₹18 saved",
                avgMetricLabel = "Hourly Average",
                avgMetricVal = "1.2 kWh / hr",
                avgMetricSub = "⚡ Target 1.5",
                peakHours = "6 PM – 9 PM",
                tierBadge = "Tier 1"
            )
            "Month" -> AnalyticsRangeConfig(
                dateText = "📅 Oct 1 – Oct 31, 2026",
                summaryTitle = "Monthly Summary",
                yLabels = listOf("160", "120", "80", "40", "0"),
                gridVals = listOf(0f, 40f, 80f, 120f, 160f),
                maxVal = 160.0f,
                vals = listOf(98.2f, 138.4f, 115.0f, 134.0f),
                xLabels = listOf("Week 1", "Week 2", "Week 3", "Week 4"),
                peakIndex = 1,
                peakPillText = "Wk 2: 138.4 kWh",
                totalEnergy = "485.6 kWh",
                totalDelta = "↓ 5% vs last mo",
                estCost = "₹3,884.80",
                costDelta = "-₹320 saved",
                avgMetricLabel = "Weekly Average",
                avgMetricVal = "121.4 kWh / wk",
                avgMetricSub = "⚡ Target 125.0",
                peakHours = "5 PM – 9 PM",
                tierBadge = "Tier 2"
            )
            "Year" -> AnalyticsRangeConfig(
                dateText = "📅 Jan 2026 – Dec 2026",
                summaryTitle = "Annual Summary",
                yLabels = listOf("800", "600", "400", "200", "0"),
                gridVals = listOf(0f, 200f, 400f, 600f, 800f),
                maxVal = 800.0f,
                vals = listOf(420f, 390f, 450f, 520f, 720f, 680f, 590f, 510f, 485f, 430f, 400f),
                xLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov"),
                peakIndex = 4,
                peakPillText = "Jul: 720 kWh",
                totalEnergy = "5,420 kWh",
                totalDelta = "↓ 11% YoY",
                estCost = "₹43,360",
                costDelta = "-₹4,800 saved",
                avgMetricLabel = "Monthly Average",
                avgMetricVal = "451.6 kWh / mo",
                avgMetricSub = "⚡ Target 480.0",
                peakHours = "Summer High Tier",
                tierBadge = "Tier 3"
            )
            else -> AnalyticsRangeConfig( // "Week"
                dateText = "📅 Oct 9 – Oct 15, 2026",
                summaryTitle = "Weekly Summary",
                yLabels = listOf("6", "4", "2", "0"),
                gridVals = listOf(0f, 2f, 4f, 6f),
                maxVal = 6.0f,
                vals = listOf(2.8f, 4.2f, 3.1f, 3.8f, 2.9f, 3.4f, 1.8f),
                xLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                peakIndex = 1,
                peakPillText = "Tue: 4.2 kWh",
                totalEnergy = "103.4 kWh",
                totalDelta = "↓ 8% vs last wk",
                estCost = "₹827.20",
                costDelta = "-₹96 saved",
                avgMetricLabel = "Daily Average",
                avgMetricVal = "14.8 kWh / day",
                avgMetricSub = "⚡ Target 15.0",
                peakHours = "4 PM – 8 PM",
                tierBadge = "Tier 2"
            )
        }

        // MAIN MIDDLE ROW: Left 68% Spline Chart + Right 32% Summary Metrics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // LEFT CARD: Spline Wave Line Chart (Normal stage, highlights on focus)
            Card3D(
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight()
            ) {
                // Top Row inside Card: Title + Date Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Energy Usage (kWh)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(config.dateText, fontSize = 11.sp, color = Theme3D.textSecondary)
                }

                Spacer(Modifier.height(4.dp))

                // Chart Canvas with Y-Axis Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Y-axis labels
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(26.dp)
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        config.yLabels.forEach { label ->
                            Text(label, fontSize = 9.sp, color = Theme3D.textMuted)
                        }
                    }

                    // Canvas Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val padX = 16.dp.toPx()
                            val padTop = 26.dp.toPx()
                            val padBottom = 8.dp.toPx()
                            val w = size.width - (padX * 2)
                            val h = size.height - padTop - padBottom
                            val maxVal = config.maxVal

                            // Horizontal dashed gridlines
                            config.gridVals.forEach { gVal ->
                                val y = padTop + (1f - (gVal / maxVal)) * h
                                drawLine(
                                    color = Color(0x18FFFFFF),
                                    start = Offset(padX, y),
                                    end = Offset(size.width - padX, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                                )
                            }

                            val coords = config.vals.mapIndexed { idx, v ->
                                val px = padX + (idx.toFloat() / (config.vals.size - 1).coerceAtLeast(1)) * w
                                val py = padTop + (1f - (v / maxVal)) * h
                                Offset(px, py)
                            }

                            if (coords.isNotEmpty()) {
                                val strokePath = Path().apply {
                                    moveTo(coords[0].x, coords[0].y)
                                    for (i in 0 until coords.size - 1) {
                                        val p0 = coords[i]
                                        val p1 = coords[i + 1]
                                        val cpx1 = p0.x + (p1.x - p0.x) / 2f
                                        val cpy1 = p0.y
                                        val cpx2 = p0.x + (p1.x - p0.x) / 2f
                                        val cpy2 = p1.y
                                        cubicTo(cpx1, cpy1, cpx2, cpy2, p1.x, p1.y)
                                    }
                                }

                                val fillPath = Path().apply {
                                    addPath(strokePath)
                                    lineTo(coords.last().x, size.height - padBottom)
                                    lineTo(coords.first().x, size.height - padBottom)
                                    close()
                                }

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0x600284C7), Color(0x050284C7)),
                                        startY = padTop,
                                        endY = size.height - padBottom
                                    )
                                )

                                drawPath(
                                    path = strokePath,
                                    color = Color(0xFF38BDF8),
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )

                                // Data Points
                                coords.forEachIndexed { i, pt ->
                                    if (i == config.peakIndex) {
                                        drawCircle(color = Color(0x400284C7), radius = 10.dp.toPx(), center = pt)
                                        drawCircle(color = Color(0xFF38BDF8), radius = 5.dp.toPx(), center = pt)
                                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pt)
                                    } else {
                                        drawCircle(color = Color(0xFF0F172A), radius = 4.dp.toPx(), center = pt)
                                        drawCircle(color = Color(0xFF38BDF8), radius = 2.5.dp.toPx(), center = pt)
                                    }
                                }

                                // Draw Dynamic Peak Pill directly in Canvas
                                if (config.peakIndex in coords.indices) {
                                    val peakPt = coords[config.peakIndex]
                                    val pillW = 88.dp.toPx()
                                    val pillH = 18.dp.toPx()
                                    val pillLeft = (peakPt.x - pillW / 2).coerceIn(padX, size.width - padX - pillW)
                                    val pillTop = (peakPt.y - pillH - 6.dp.toPx()).coerceAtLeast(2.dp.toPx())

                                    drawRoundRect(
                                        color = Color(0xFF0284C7),
                                        topLeft = Offset(pillLeft, pillTop),
                                        size = androidx.compose.ui.geometry.Size(pillW, pillH),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )

                                    drawIntoCanvas { nativeCanvas ->
                                        val paint = android.graphics.Paint().apply {
                                            color = android.graphics.Color.WHITE
                                            textSize = 9.sp.toPx()
                                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                                            textAlign = android.graphics.Paint.Align.CENTER
                                            isAntiAlias = true
                                        }
                                        nativeCanvas.nativeCanvas.drawText(
                                            config.peakPillText,
                                            pillLeft + pillW / 2,
                                            pillTop + pillH - 5.dp.toPx(),
                                            paint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // X-axis day labels below chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    config.xLabels.forEachIndexed { i, d ->
                        Text(
                            text = d,
                            fontSize = if (config.xLabels.size > 8) 8.sp else 10.sp,
                            fontWeight = if (i == config.peakIndex) FontWeight.Bold else FontWeight.Normal,
                            color = if (i == config.peakIndex) Theme3D.accentCyan else Theme3D.textSecondary
                        )
                    }
                }
            }

            // RIGHT CARD: Summary Metrics
            Card3D(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight(),
                borderColor = Theme3D.cardBorder
            ) {
                Text(config.summaryTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Spacer(Modifier.weight(1f))

                // Metric 1: Total Energy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Energy", fontSize = 10.sp, color = Theme3D.textSecondary)
                        Text(config.totalEnergy, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2010B981)
                    ) {
                        Text(
                            config.totalDelta,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme3D.successGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Spacer(Modifier.weight(1f))

                // Metric 2: Estimated Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Est. Cost", fontSize = 10.sp, color = Theme3D.textSecondary)
                        Text(config.estCost, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2010B981)
                    ) {
                        Text(
                            config.costDelta,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme3D.successGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Spacer(Modifier.weight(1f))

                // Metric 3: Average
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(config.avgMetricLabel, fontSize = 10.sp, color = Theme3D.textSecondary)
                        Text(config.avgMetricVal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Text(config.avgMetricSub, fontSize = 9.sp, color = Theme3D.textMuted)
                }

                Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Spacer(Modifier.weight(1f))

                // Metric 4: Peak Hours
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Peak Hours", fontSize = 10.sp, color = Theme3D.textSecondary)
                        Text(config.peakHours, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Theme3D.warningAmber)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x25F59E0B)
                    ) {
                        Text(
                            config.tierBadge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme3D.warningAmber,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // BOTTOM 3 CARDS ROW (Exact match to Mockup Screen 3)
        Row(
            modifier = Modifier.fillMaxWidth().height(66.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: AC accounts for 42%
            Card3D(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { viewModel.selectTab(4) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("❄️", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("AC accounts for 42% of usage", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlowSlider(value = 0.42f, color = Theme3D.accentCyan, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(6.dp))
                            Text("42%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                        }
                    }
                }
            }

            // Card 2: Sunday was efficient
            Card3D(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { viewModel.refreshData() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(30.dp).clip(CircleShape).background(Color(0x3010B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍃", fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Sunday was your most efficient day.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                        Spacer(Modifier.height(1.dp))
                        Text("Only 12.1 kWh used.", fontSize = 10.sp, color = Theme3D.textSecondary)
                    }
                }
            }

            // Card 3: Suggestion
            Card3D(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { viewModel.loadAiInsight() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(Color(0x30F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡", fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Suggestion", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                            Spacer(Modifier.height(1.dp))
                            Text("Shift laundry to 10AM (off-peak).", fontSize = 11.sp, color = Color.White)
                        }
                    }
                    Text("›", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                }
            }
        }
    }
}

// ─── SCREEN 4: SECURITY & CAMERAS ───
@Composable
fun Screen4SecurityAndCameras(
    state: HomeState,
    viewModel: HomeViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER: Back Button + Title + Status Pill + Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TvBackButton(onClick = { viewModel.selectTab(0) })
                Spacer(Modifier.width(12.dp))
                HomeIqBrandLogo(onClick = { viewModel.selectTab(0) })
                Spacer(Modifier.width(12.dp))
                Text("🛡️ Security Center", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(12.dp))
                // Mode Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.security.isArmed) Color(0xFF16A34A) else Color(0xFFDC2626),
                    modifier = Modifier.clickable { viewModel.toggleSecurityMode() }
                ) {
                    Text(
                        if (state.security.isArmed) "🛡️ ARMED - ${state.security.mode}" else "🔓 DISARMED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x300284C7),
                    modifier = Modifier
                        .border(1.dp, Theme3D.accentCyan, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleConnectHubDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📱", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Mobile & 🐝 Pioneer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }
                Text(state.liveDateTimeString, fontSize = 11.sp, color = Theme3D.textSecondary)
                Text("🔍", fontSize = 13.sp, modifier = Modifier.clickable { viewModel.toggleSearchDialog() })
                Text("⚙️", fontSize = 13.sp, modifier = Modifier.clickable { viewModel.toggleSettingsDialog() })
                UserProfileAuthBadge(state = state, viewModel = viewModel)
            }
        }

        Spacer(Modifier.height(10.dp))

        // 2-COLUMN LAYOUT: 4 CAMERAS (65%) + PERIMETER CHECKLIST (35%)
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left: 4 Live Cameras (2x2 Grid)
            Column(modifier = Modifier.weight(0.65f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CameraFeedTile("Front Door", "14:32", Color(0xFF1E293B), modifier = Modifier.weight(1f).fillMaxHeight())
                    CameraFeedTile("Backyard", "14:32", Color(0xFF1E293B), modifier = Modifier.weight(1f).fillMaxHeight())
                }
                Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CameraFeedTile("Garage", "14:31", Color(0xFF1E293B), modifier = Modifier.weight(1f).fillMaxHeight())
                    CameraFeedTile("Living Room", "14:32", Color(0xFF1E293B), modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }

            // Right: Checklist & Logs
            Column(modifier = Modifier.weight(0.35f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Doors & Windows Card
                Card3D(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.lockAllDoors() }) {
                    Text("Doors & Windows", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🚪 Front Door", fontSize = 11.sp, color = Color.White)
                        Text("🔒 Locked", fontSize = 11.sp, color = Theme3D.successGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🚪 Back Door", fontSize = 11.sp, color = Color.White)
                        Text("🔒 Locked", fontSize = 11.sp, color = Theme3D.successGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🚗 Garage", fontSize = 11.sp, color = Color.White)
                        Text("🔒 Closed", fontSize = 11.sp, color = Theme3D.successGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("✅ 5/5 Windows Closed", fontSize = 11.sp, color = Theme3D.successGreen, fontWeight = FontWeight.Bold)
                }

                // Motion Sensors Card
                Card3D(modifier = Modifier.fillMaxWidth()) {
                    Text("Motion Sensors", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                    Spacer(Modifier.height(4.dp))
                    Text("🟢 All clear", fontSize = 12.sp, color = Theme3D.successGreen, fontWeight = FontWeight.Bold)
                }

                // Recent Events Card
                Card3D(modifier = Modifier.weight(1f), onClick = { viewModel.refreshData() }) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recent Events", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.textMuted)
                        Text("View All", fontSize = 10.sp, color = Theme3D.accentCyan)
                    }
                    Spacer(Modifier.height(6.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf(
                            "🔴 14:32  Front door locked",
                            "🔴 13:15  Motion detected: Backyard (package)",
                            "🔴 08:00  System armed"
                        )) { evt ->
                            Text(evt, fontSize = 10.sp, color = Theme3D.textSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraFeedTile(name: String, time: String, bgColor: Color, modifier: Modifier = Modifier) {
    Card3D(modifier = modifier, containerColor = bgColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Simulated Camera Stream Graphic Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (name) {
                        "Front Door" -> "🚪 Porch Cam View"
                        "Backyard" -> "🌿 Garden Cam View"
                        "Garage" -> "🚗 Driveway Cam View"
                        else -> "🛋️ Living Interior View"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }

            // RED LIVE BADGE TOP LEFT
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
                Spacer(Modifier.width(4.dp))
                Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Red)
            }

            // NAME & TIMESTAMP BOTTOM
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(time, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

// ─── SCREEN 5: AI ASSISTANT / VOICE COMMAND ───
@Composable
fun Screen5AiAssistantVoice(
    state: HomeState,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP HEADER: Back Button + Logo + Right Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TvBackButton(onClick = { viewModel.selectTab(0) })
                Spacer(Modifier.width(14.dp))
                HomeIqBrandLogo(onClick = { viewModel.selectTab(0) })
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x300284C7),
                    modifier = Modifier
                        .border(1.dp, Theme3D.accentCyan, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleConnectHubDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📱", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Mobile & 🐝 Pioneer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                    }
                }
                Text(state.liveDateTimeString, fontSize = 11.sp, color = Theme3D.textSecondary)
                UserProfileAuthBadge(state = state, viewModel = viewModel)
            }
        }

        Spacer(Modifier.height(4.dp))

        // Glowing 3D AI Robot Avatar
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF1E3A8A))))
                .border(2.dp, Theme3D.accentCyan, CircleShape)
                .shadow(16.dp, CircleShape, spotColor = Theme3D.accentGlow)
                .clickable { viewModel.loadAiInsight() },
            contentAlignment = Alignment.Center
        ) {
            Text("🤖", fontSize = 28.sp)
        }

        Spacer(Modifier.height(2.dp))
        Text("HomeIQ Bedrock AI (Claude 3 Haiku)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

        Spacer(Modifier.height(4.dp))

        // Dynamic AI Speech Bubble Card (Reflects current state.aiInsight)
        Card3D(
            modifier = Modifier.width(620.dp),
            onClick = { viewModel.loadAiInsight() }
        ) {
            Text(
                if (state.isAiLoading) "⚡ IQ (Claude 3 Haiku) analyzing real-time home telemetry..." else "\"${state.aiInsight}\"",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Spacer(Modifier.height(6.dp))

        // ─── AI MESSAGE WRITING PROMPT INPUT BLOCK ───
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier
                .width(620.dp)
                .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (state.aiInputText.isEmpty()) {
                            Text(
                                "Ask IQ (e.g. 'How to save ₹500 on bill?')...",
                                fontSize = 10.sp,
                                color = Theme3D.textMuted
                            )
                        }
                        BasicTextField(
                            value = state.aiInputText,
                            onValueChange = { viewModel.setAiInputText(it) },
                            textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                            cursorBrush = SolidColor(Theme3D.accentCyan),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    TvButton3D(
                        text = if (state.isAiLoading) "⏳ Thinking..." else "➤ Send",
                        fontSize = 10,
                        onClick = { viewModel.submitCustomAiPrompt() }
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Quick Prompt Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "How to save ₹500 on bill?",
                        "Optimize AC sleep curve",
                        "Which device uses most power?",
                        "Solar generation schedule"
                    ).forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x251E293B),
                            modifier = Modifier
                                .border(1.dp, Color(0x2538BDF8), RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setAiInputText(chip)
                                    viewModel.submitCustomAiPrompt(chip)
                                }
                        ) {
                            Text(
                                chip,
                                fontSize = 8.sp,
                                color = Theme3D.accentCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // QUICK ACTION PILLS ROW 1: DYNAMIC TWO-WAY TOGGLES (Normal unselected stage, highlights on focus)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Light On / Off 2-Way Toggle
            TvButton3D(
                text = if (state.areLightsAllOff) "💡 Turn ON Lights" else "💡 Turn OFF Lights",
                fontSize = 10,
                onClick = { viewModel.toggleAllLightsAi() }
            )

            // Door Lock / Unlock 2-Way Toggle
            TvButton3D(
                text = if (state.security.isArmed) "🔓 Unlock All Doors" else "🔒 Lock All Doors",
                fontSize = 10,
                onClick = { viewModel.toggleSecurityLockAi() }
            )

            // Night / Day Mode 2-Way Toggle
            TvButton3D(
                text = if (state.isNightModeActive) "☀️ Day Mode" else "🌙 Night Mode",
                fontSize = 10,
                onClick = { viewModel.toggleNightModeAi() }
            )

            // Eco Cooling / Max Cooling 2-Way Toggle
            TvButton3D(
                text = if (state.isEcoCoolingActive) "❄️ Max Cooling (20°)" else "🌱 Eco Saver (26°)",
                fontSize = 10,
                onClick = { viewModel.toggleEcoCoolingAi() }
            )

            // Theme Switcher
            TvButton3D(
                text = when (state.activeTheme) {
                    "OLED_BLACK" -> "🖤 OLED"
                    "NEON_CYBER" -> "⚡ Cyber"
                    else -> "🌌 Navy"
                },
                fontSize = 10,
                onClick = { viewModel.cycleTheme() }
            )
        }

        Spacer(Modifier.height(5.dp))

        // AUDIO WAVEFORM VISUALIZER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val waveHeights = listOf(4, 9, 14, 20, 11, 17, 22, 14, 19, 11, 15, 8, 4)
            waveHeights.forEach { h ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Theme3D.accentCyan)
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text("Listening for voice commands on Amazon Bee Pioneer Wearable... 🎙️", fontSize = 9.sp, color = Theme3D.accentCyan)

        Spacer(Modifier.height(4.dp))

        // INTERACTIVE VOICE COMMAND SIMULATION PILLS
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TvButton3D(
                text = if (state.areLightsAllOff) "🎙️ \"Turn on lights\"" else "🎙️ \"Turn off lights\"",
                fontSize = 10,
                onClick = { viewModel.toggleAllLightsAi() }
            )
            TvButton3D(
                text = "🎙️ \"Peak shave load\"",
                fontSize = 10,
                onClick = {
                    viewModel.triggerScenePreset("PEAK_SHAVE")
                    viewModel.showHudNotification("🎙️ Bee Pioneer Voice: Peak tariff load shed activated!")
                }
            )
            TvButton3D(
                text = "📳 \"Test Pioneer Haptic\"",
                fontSize = 10,
                onClick = { viewModel.triggerHapticPulse() }
            )
            TvButton3D(
                text = "📱 Multi-Device Hub ➔",
                fontSize = 10,
                onClick = { viewModel.toggleConnectHubDialog() }
            )
        }
    }
}

// ─── SYSTEM SETTINGS DIALOG ───
@Composable
fun SettingsDialog(
    settings: UserSettings,
    wearable: WearableDevice,
    onRateChange: (Double) -> Unit,
    onToggleUnit: () -> Unit,
    onToggleAutoArm: () -> Unit,
    onToggleContrast: () -> Unit,
    onToggleWearableHaptics: () -> Unit,
    onStartPairingPioneer: () -> Unit,
    onUnpairPioneer: () -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("⚙️ System & Energy Settings", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Electricity Tariff Rate", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("₹${settings.ratePerKwh} / kWh", fontSize = 11.sp, color = Theme3D.textSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TvButton3D(text = "- ₹0.5", onClick = { onRateChange(-0.5) })
                        TvButton3D(text = "+ ₹0.5", onClick = { onRateChange(0.5) })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Temperature Unit (${settings.tempUnit})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    TvButton3D(text = "Toggle Unit", onClick = onToggleUnit)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Arm Security at Night", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Switch(
                        checked = settings.autoArmSecurity,
                        onCheckedChange = { onToggleAutoArm() },
                        colors = SwitchDefaults.colors(checkedTrackColor = Theme3D.successGreen)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OLED High Contrast Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Switch(
                        checked = settings.highContrastMode,
                        onCheckedChange = { onToggleContrast() },
                        colors = SwitchDefaults.colors(checkedTrackColor = Theme3D.successGreen)
                    )
                }
            }
        },
        confirmButton = {
            TvButton3D(text = "Close", onClick = onClose)
        },
        containerColor = Color(0xFF1E293B)
    )
}

// ─── MULTI-DEVICE & AMAZON BEE PIONEER CONNECTIVITY HUB ───
@Composable
fun ConnectHubDialog(
    state: HomeState,
    viewModel: HomeViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Mobile Phone, 1: Bee Pioneer Wearable

    fun triggerPhysicalVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(180)
            }
        } catch (_: Exception) {}
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📡 HomeIQ Connectivity & Multi-Device Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            Column(
                modifier = Modifier.width(620.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Switcher (Phone Companion vs Bee Pioneer Wearable)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TvButton3D(
                        text = "📱 Mobile Phone Companion",
                        isSelected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 }
                    )
                    TvButton3D(
                        text = "🐝 Amazon Bee Pioneer Wearable",
                        isSelected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 }
                    )
                }

                if (selectedSubTab == 0) {
                    // ═══ MOBILE PHONE COMPANION SECTION ═══
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Authentic Canvas Generated QR Code
                        Card3D(
                            modifier = Modifier.size(160.dp),
                            containerColor = Color.White
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                val w = size.width
                                val h = size.height
                                val blockW = w / 12f
                                val blockH = h / 12f

                                fun drawFinder(x: Float, y: Float) {
                                    drawRect(Color.Black, topLeft = Offset(x, y), size = Size(blockW * 3.5f, blockH * 3.5f))
                                    drawRect(Color.White, topLeft = Offset(x + blockW * 0.7f, y + blockH * 0.7f), size = Size(blockW * 2.1f, blockH * 2.1f))
                                    drawRect(Color.Black, topLeft = Offset(x + blockW * 1.2f, y + blockH * 1.2f), size = Size(blockW * 1.1f, blockH * 1.1f))
                                }

                                drawFinder(0f, 0f)
                                drawFinder(w - blockW * 3.5f, 0f)
                                drawFinder(0f, h - blockH * 3.5f)

                                // Data pattern blocks
                                val patternCoords = listOf(
                                    Pair(4, 1), Pair(5, 1), Pair(6, 2), Pair(4, 4), Pair(5, 5), Pair(6, 4),
                                    Pair(7, 4), Pair(8, 2), Pair(9, 5), Pair(4, 7), Pair(6, 8), Pair(8, 7),
                                    Pair(9, 8), Pair(5, 10), Pair(7, 10), Pair(8, 11), Pair(10, 10), Pair(11, 8)
                                )
                                patternCoords.forEach { (bx, by) ->
                                    drawRect(Color.Black, topLeft = Offset(bx * blockW, by * blockH), size = Size(blockW * 0.9f, blockH * 0.9f))
                                }
                            }
                        }

                        // Mobile Sync Info & Telemetry
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Theme3D.successGreen))
                                Spacer(Modifier.width(6.dp))
                                Text(state.phoneCompanion.deviceName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text("Pairing Code: ${state.phoneCompanion.syncToken}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Theme3D.accentCyan)
                            Text("Local Subnet: http://${state.phoneCompanion.ipAddress}", fontSize = 11.sp, color = Theme3D.textSecondary)
                            Text("Sync Latency: 6 ms • Protocol: WebSockets Bi-directional", fontSize = 10.sp, color = Theme3D.textMuted)

                            Spacer(Modifier.height(4.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TvButton3D(
                                    text = "🔄 Sync Phone Data",
                                    fontSize = 11,
                                    onClick = {
                                        viewModel.syncPhoneCompanion()
                                        triggerPhysicalVibration()
                                    }
                                )
                                TvButton3D(
                                    text = "🔔 Push Tariff Alert",
                                    fontSize = 11,
                                    onClick = {
                                        viewModel.showHudNotification("📱 Sent Push Notification to ${state.phoneCompanion.deviceName}: Peak tariff surge at 6 PM!")
                                        triggerPhysicalVibration()
                                    }
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x301E293B),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0x2038BDF8), RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            "📲 Universal Companion APK: Same app binary is engineered for Android Phones (touch orientation) and Fire TV (D-pad 1080p). Install via `adb install app-debug.apk` on mobile.",
                            fontSize = 10.sp,
                            color = Theme3D.textSecondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    // ═══ AMAZON BEE PIONEER WEARABLE SECTION ═══
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card3D(modifier = Modifier.fillMaxWidth(), borderColor = Theme3D.warningAmber) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0x30F59E0B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🐝", fontSize = 24.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Amazon Bee Pioneer Wearable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Connected via Bluetooth Low Energy 5.3", fontSize = 11.sp, color = Theme3D.successGreen)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("🔋 94%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Docked / Active", fontSize = 10.sp, color = Theme3D.textMuted)
                                }
                            }
                        }

                        Text("Interactive Wearable Telemetry & Tests:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.textSecondary)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Haptic Test Button
                            TvButton3D(
                                text = "📳 Test Haptic Vibration",
                                fontSize = 11,
                                onClick = {
                                    triggerPhysicalVibration()
                                    viewModel.triggerHapticPulse()
                                }
                            )

                            // Mic Audio Test Button
                            TvButton3D(
                                text = "🎙️ Test Voice Mic",
                                fontSize = 11,
                                onClick = {
                                    viewModel.processVoiceCommand("Eco cooling mode activated via Bee Pioneer wearable voice")
                                    viewModel.showHudNotification("🐝 Pioneer Voice Captured: \"Eco cooling mode activated\"")
                                    triggerPhysicalVibration()
                                }
                            )

                            // Wrist Flick Gesture
                            TvButton3D(
                                text = "⌚ Flick Wrist Gesture",
                                fontSize = 11,
                                onClick = {
                                    viewModel.toggleDevice("light1")
                                    viewModel.showHudNotification("⌚ Bee Pioneer IMU Gesture: Toggled Living Room Light")
                                    triggerPhysicalVibration()
                                }
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x301E293B),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0x2038BDF8), RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                "🐝 Bee Pioneer Wearable Integration: Supports voice telemetry streaming, dual-haptic motor alerts for peak tariffs, and wrist flick gesture controls to toggle lighting seamlessly.",
                                fontSize = 10.sp,
                                color = Theme3D.textSecondary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TvButton3D(text = "Done / Close", onClick = onClose)
        },
        containerColor = Color(0xFF0F172A)
    )
}

// ─── AUTHENTICATION INPUT FIELD ───
@Composable
fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Theme3D.textSecondary)
        Spacer(Modifier.height(3.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x351E293B),
            border = BorderStroke(1.dp, Color(0x5038BDF8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(placeholder, fontSize = 10.sp, color = Theme3D.textMuted)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(Theme3D.accentCyan),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ─── QUICK DEMO PROFILE CARD ───
@Composable
fun DemoProfileCard(
    avatar: String,
    name: String,
    role: String,
    email: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isFocused) Color(0xFF2563EB) else Color(0x251E293B),
        border = BorderStroke(1.dp, if (isFocused) Color(0xFF38BDF8) else Color(0x30FFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(avatar, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(role, fontSize = 8.sp, color = Theme3D.textSecondary)
                }
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0x3010B981)
            ) {
                Text(
                    "Sign In ➔",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme3D.successGreen,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ─── USER PROFILE AUTH BADGE (HEADER) ───
@Composable
fun UserProfileAuthBadge(
    state: HomeState,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val isLoggedIn = state.userProfile.isLoggedIn

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
            isFocused -> Color(0xFF2563EB)
            isLoggedIn -> Color(0x351E293B)
            else -> Color(0x40D97706)
        },
        border = BorderStroke(
            1.dp,
            when {
                isFocused -> Color(0xFF38BDF8)
                isLoggedIn -> Color(0x5038BDF8)
                else -> Color(0xFFF59E0B)
            }
        ),
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .clickable {
                viewModel.openAuthDialog(if (isLoggedIn) "PROFILE" else "LOGIN")
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isLoggedIn) Color(0xFF3B82F6) else Color(0xFFF59E0B)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isLoggedIn) "👤" else "🔑", fontSize = 10.sp)
            }
            Spacer(Modifier.width(5.dp))
            if (isLoggedIn) {
                Text(
                    state.userProfile.name.split(" ").firstOrNull() ?: "Venkat",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Theme3D.successGreen)
                )
            } else {
                Text(
                    "Login / Sign Up",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDE68A)
                )
            }
        }
    }
}

// ─── CUSTOMER PROFILE, SIGNUP, LOGIN & SAFETY VAULT DIALOG ───
@Composable
fun CustomerProfileSecurityDialog(
    state: HomeState,
    viewModel: HomeViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    var loginEmail by remember { mutableStateOf(state.userProfile.email) }
    var loginPin by remember { mutableStateOf("8824") }
    var remember2Fa by remember { mutableStateOf(true) }

    var signUpName by remember { mutableStateOf(state.userProfile.name) }
    var signUpEmail by remember { mutableStateOf(state.userProfile.email) }
    var signUpPhone by remember { mutableStateOf(state.userProfile.phone) }
    var signUpHomeId by remember { mutableStateOf(state.userProfile.smartHomeId) }
    var signUpPin by remember { mutableStateOf("1234") }
    var signUpRole by remember { mutableStateOf("Home Administrator") }

    fun triggerPhysicalVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(180)
            }
        } catch (_: Exception) {}
    }

    AlertDialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    when (state.authMode) {
                                        "LOGIN" -> listOf(Color(0xFF38BDF8), Color(0xFF1E3A8A))
                                        "SIGNUP" -> listOf(Color(0xFF10B981), Color(0xFF065F46))
                                        else -> listOf(Color(0xFF818CF8), Color(0xFF312E81))
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            when (state.authMode) {
                                "LOGIN" -> "🔐"
                                "SIGNUP" -> "✨"
                                else -> "👤"
                            },
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            when (state.authMode) {
                                "LOGIN" -> "Resident Login & Authentication"
                                "SIGNUP" -> "Create Resident Account (Sign Up)"
                                else -> "Customer Profile & Safety Vault"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            when (state.authMode) {
                                "LOGIN" -> "Sign in with Resident ID or Select Quick Demo Profile"
                                "SIGNUP" -> "Register Resident, Master Security PIN & Smart Home ID"
                                else -> "Encrypted Smart Home ID: ${state.userProfile.smartHomeId}"
                            },
                            fontSize = 10.sp,
                            color = Theme3D.textSecondary
                        )
                    }
                }

                // Top Segmented Tab Switcher (Profile | Login | Sign Up)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x351E293B),
                    border = BorderStroke(1.dp, Color(0x3038BDF8))
                ) {
                    Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            Pair("PROFILE", "👤 Profile"),
                            Pair("LOGIN", "🔐 Login"),
                            Pair("SIGNUP", "✨ Sign Up")
                        ).forEach { (mode, label) ->
                            val isSelected = state.authMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                                modifier = Modifier.clickable {
                                    viewModel.setAuthMode(mode)
                                }
                            ) {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Theme3D.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        text = {
            when (state.authMode) {
                // ═══════════════ TAB 1: LOGIN ═══════════════
                "LOGIN" -> {
                    Row(
                        modifier = Modifier
                            .width(740.dp)
                            .height(345.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // LEFT: Login Form
                        Card3D(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            borderColor = Theme3D.accentCyan
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("🔐 Resident Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                    Spacer(Modifier.height(8.dp))

                                    AuthInputField(
                                        label = "RESIDENT EMAIL / SMART HOME ID",
                                        value = loginEmail,
                                        onValueChange = { loginEmail = it },
                                        placeholder = "lakkireddy@homeiq.ai"
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    AuthInputField(
                                        label = "SECURITY PIN / PASSWORD",
                                        value = loginPin,
                                        onValueChange = { loginPin = it },
                                        placeholder = "••••",
                                        isPassword = true
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = remember2Fa,
                                            onCheckedChange = { remember2Fa = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Theme3D.accentCyan,
                                                checkmarkColor = Color.Black
                                            )
                                        )
                                        Text("Enable 256-Bit Hardware Vault & 2FA", fontSize = 9.sp, color = Theme3D.textSecondary)
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TvButton3D(
                                        text = "🔓 Sign In to HomeIQ",
                                        fontSize = 11,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            viewModel.loginUser(loginEmail.ifBlank { "lakkireddy@homeiq.ai" })
                                            triggerPhysicalVibration()
                                        }
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Don't have an account? ", fontSize = 9.sp, color = Theme3D.textMuted)
                                        Text(
                                            "Tap 'Sign Up' above",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Theme3D.accentCyan,
                                            modifier = Modifier.clickable { viewModel.setAuthMode("SIGNUP") }
                                        )
                                    }
                                }
                            }
                        }

                        // RIGHT: Fast Demo Profiles (Fire TV 1-click login)
                        Card3D(
                            modifier = Modifier.weight(1.05f).fillMaxHeight(),
                            borderColor = Theme3D.purpleAccent
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⚡ Quick-Login Resident Profiles", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.purpleAccent)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0x3010B981)
                                        ) {
                                            Text("1-CLICK DEMO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text("Select any profile below to instantly authenticate without remote typing:", fontSize = 9.sp, color = Theme3D.textMuted)
                                    Spacer(Modifier.height(8.dp))

                                    DemoProfileCard(
                                        avatar = "👑",
                                        name = "Venkat Reddy",
                                        role = "Home Administrator • HQ-8824-FIRETV",
                                        email = "lakkireddy@homeiq.ai",
                                        onClick = {
                                            loginEmail = "lakkireddy@homeiq.ai"
                                            loginPin = "8824"
                                            viewModel.loginUser("lakkireddy@homeiq.ai", "Venkat Reddy")
                                            triggerPhysicalVibration()
                                        }
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    DemoProfileCard(
                                        avatar = "🏠",
                                        name = "Alex Johnson",
                                        role = "Family Resident • Living & Kitchen Hub",
                                        email = "alex@homeiq.ai",
                                        onClick = {
                                            loginEmail = "alex@homeiq.ai"
                                            loginPin = "4412"
                                            viewModel.loginUser("alex@homeiq.ai", "Alex Johnson")
                                            triggerPhysicalVibration()
                                        }
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    DemoProfileCard(
                                        avatar = "👵",
                                        name = "Priya Reddy",
                                        role = "Elder Care Caretaker • Safety Tripwires",
                                        email = "priya@homeiq.ai",
                                        onClick = {
                                            loginEmail = "priya@homeiq.ai"
                                            loginPin = "2209"
                                            viewModel.loginUser("priya@homeiq.ai", "Priya Reddy")
                                            triggerPhysicalVibration()
                                        }
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2038BDF8),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "🛡️ Enterprise AES-256 cloud session active on Fire TV",
                                        fontSize = 8.sp,
                                        color = Theme3D.accentCyan,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ═══════════════ TAB 2: SIGN UP ═══════════════
                "SIGNUP" -> {
                    Row(
                        modifier = Modifier
                            .width(740.dp)
                            .height(345.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // LEFT: Sign Up Fields
                        Card3D(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            borderColor = Theme3D.successGreen
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("✨ Create Resident Account", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                                    Spacer(Modifier.height(6.dp))

                                    AuthInputField(
                                        label = "FULL NAME",
                                        value = signUpName,
                                        onValueChange = { signUpName = it },
                                        placeholder = "e.g. Venkat Reddy"
                                    )

                                    Spacer(Modifier.height(5.dp))

                                    AuthInputField(
                                        label = "EMAIL ADDRESS",
                                        value = signUpEmail,
                                        onValueChange = { signUpEmail = it },
                                        placeholder = "e.g. user@homeiq.ai"
                                    )

                                    Spacer(Modifier.height(5.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        AuthInputField(
                                            label = "PHONE NUMBER",
                                            value = signUpPhone,
                                            onValueChange = { signUpPhone = it },
                                            placeholder = "+91 98765 43210",
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        AuthInputField(
                                            label = "MASTER PIN",
                                            value = signUpPin,
                                            onValueChange = { signUpPin = it },
                                            placeholder = "1234",
                                            isPassword = true,
                                            modifier = Modifier.weight(0.8f)
                                        )
                                    }
                                }

                                TvButton3D(
                                    text = "✨ Complete Registration & Protect Home",
                                    fontSize = 11,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        viewModel.signUpUser(
                                            name = signUpName.ifBlank { "Venkat Reddy" },
                                            email = signUpEmail.ifBlank { "user@homeiq.ai" },
                                            phone = signUpPhone.ifBlank { "+91 98765 43210" },
                                            smartHomeId = signUpHomeId.ifBlank { "HQ-8824-FIRETV" },
                                            role = signUpRole
                                        )
                                        triggerPhysicalVibration()
                                    }
                                )
                            }
                        }

                        // RIGHT: Smart Home & Role Setup
                        Card3D(
                            modifier = Modifier.weight(1.05f).fillMaxHeight(),
                            borderColor = Theme3D.accentCyan
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("🏡 Smart Home & Role Assignment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                    Spacer(Modifier.height(6.dp))

                                    AuthInputField(
                                        label = "SMART HOME VILLA ID",
                                        value = signUpHomeId,
                                        onValueChange = { signUpHomeId = it },
                                        placeholder = "HQ-8824-FIRETV"
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text("ASSIGN RESIDENT ROLE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Theme3D.textSecondary)
                                    Spacer(Modifier.height(3.dp))

                                    listOf(
                                        Pair("Home Administrator", "👑 Full Control & Safety Breaker Override"),
                                        Pair("Family Resident", "🏠 Multi-Room Device & AC Controls"),
                                        Pair("Elder Care Guest", "👵 Anomaly Tripwires & Caretaker Telemetry")
                                    ).forEach { (role, desc) ->
                                        val isSelected = signUpRole == role
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) Color(0x352563EB) else Color(0x201E293B),
                                            border = BorderStroke(1.dp, if (isSelected) Theme3D.accentCyan else Color(0x20FFFFFF)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .clickable { signUpRole = role }
                                        ) {
                                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                                                Text(role, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Theme3D.accentCyan else Color.White)
                                                Text(desc, fontSize = 8.sp, color = Theme3D.textMuted)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Already registered? ", fontSize = 9.sp, color = Theme3D.textMuted)
                                    Text(
                                        "Switch to Sign In",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Theme3D.accentCyan,
                                        modifier = Modifier.clickable { viewModel.setAuthMode("LOGIN") }
                                    )
                                }
                            }
                        }
                    }
                }

                // ═══════════════ TAB 3: PROFILE & FAMILY GUARDIAN VAULT ═══════════════
                else -> {
                    Row(
                        modifier = Modifier
                            .width(740.dp)
                            .height(345.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // LEFT COLUMN: Resident Profile & Authentication
                        Card3D(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            borderColor = Theme3D.accentCyan
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Resident & Elder Care", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0x3010B981)
                                        ) {
                                            Text("4 MEMBERS SAFE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }

                                    // Active User Card
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0x251E3A8A),
                                        border = BorderStroke(1.dp, Color(0x4038BDF8)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF2563EB)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("👑", fontSize = 11.sp)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Column {
                                                Text(state.userProfile.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text("${state.userProfile.role} • ${state.userProfile.email}", fontSize = 8.sp, color = Theme3D.textSecondary)
                                            }
                                        }
                                    }

                                    // Family Members Quick Telemetry
                                    state.familyMembers.forEach { member ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(member.avatar, fontSize = 11.sp)
                                                Spacer(Modifier.width(6.dp))
                                                Column {
                                                    Text(member.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    Text(member.role, fontSize = 7.5.sp, color = Theme3D.textSecondary)
                                                }
                                            }
                                            Text(member.status, fontSize = 8.sp, color = if (member.isHome) Theme3D.successGreen else Theme3D.accentCyan)
                                        }
                                    }
                                }

                                // Switch / Re-sync Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TvButton3D(
                                        text = "🔄 Sync Vault",
                                        fontSize = 9,
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 5.dp),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.showHudNotification("🔐 Cloud Auth Vault synchronized: 256-bit token refreshed!")
                                            triggerPhysicalVibration()
                                        }
                                    )
                                    TvButton3D(
                                        text = "👵 Elder Care",
                                        fontSize = 9,
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 5.dp),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.toggleElderCareTripwire()
                                        }
                                    )
                                    TvButton3D(
                                        text = "➕ Add Member",
                                        fontSize = 9,
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 5.dp),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.setAuthMode("SIGNUP")
                                        }
                                    )
                                }
                            }
                        }

                        // RIGHT COLUMN: Home Safety Tripwires & Hardware Protection
                        Card3D(
                            modifier = Modifier
                                .weight(1.05f)
                                .fillMaxHeight(),
                            borderColor = Theme3D.warningAmber
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Safety Tripwires & Circuit Protection", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                                        Text("ACTIVE 🛡️", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                                    }
                                    Spacer(Modifier.height(6.dp))

                                    // Tripwire 1: Elder Care Stove Auto-Shutoff
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("👵 Elder Care Stove Rule", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("180 min timer with no motion", fontSize = 8.sp, color = Theme3D.textMuted)
                                        }
                                        Text(if (state.familyGuardianSettings.isElderCareActive) "ARMED ✅" else "PAUSED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (state.familyGuardianSettings.isElderCareActive) Theme3D.successGreen else Color.Gray)
                                    }

                                    Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp))

                                    // Tripwire 2: Gas & CO
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("🧯 Gas & CO Monitor", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("0.0 ppm • Clean Air", fontSize = 8.sp, color = Theme3D.textMuted)
                                        }
                                        Text("NORMAL ✅", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Theme3D.successGreen)
                                    }

                                    Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp))

                                    // Tripwire 3: Child Safety / Perimeter
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("🚨 Child Safety / Perimeter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("8/8 Beams • School hours lock", fontSize = 8.sp, color = Theme3D.textMuted)
                                        }
                                        Text("ARMED 🔒", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                                    }
                                }

                                // EMERGENCY TRIPWIRE DISCONNECT
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x35DC2626),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.emergencyShutdown()
                                            viewModel.showHudNotification("🚨 EMERGENCY CIRCUIT BREAKER TRIPPED: All heavy loads killed!")
                                            triggerPhysicalVibration()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🚨", fontSize = 12.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Emergency Breaker: Trip All Mains (10ms)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.userProfile.isLoggedIn) {
                    TvButton3D(
                        text = "🚪 Log Out",
                        fontSize = 10,
                        onClick = {
                            viewModel.logoutUser()
                            triggerPhysicalVibration()
                        }
                    )
                } else {
                    TvButton3D(
                        text = "🔑 Switch to Login",
                        fontSize = 10,
                        onClick = {
                            viewModel.setAuthMode("LOGIN")
                            triggerPhysicalVibration()
                        }
                    )
                }
                TvButton3D(
                    text = "Close Vault",
                    fontSize = 10,
                    onClick = onClose
                )
            }
        },
        containerColor = Color(0xFF0F172A)
    )
}

// ─── ENHANCEMENT 4: GAMIFICATION & CHALLENGES DIALOG ───
@Composable
fun GamificationDialog(
    state: HomeState,
    viewModel: HomeViewModel,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF10B981), Color(0xFF065F46)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("HomeIQ Eco Challenges & Leaderboard", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Daily Engagement & Energy Habit Building", fontSize = 10.sp, color = Theme3D.textSecondary)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x30F59E0B),
                    modifier = Modifier.border(1.dp, Color(0x60F59E0B), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        "🔥 ${state.ecoStreak.days}-DAY STREAK (Saved ₹${state.ecoStreak.weeklySavingsRupees.toInt()})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Theme3D.warningAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Row(
                modifier = Modifier
                    .width(680.dp)
                    .height(290.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT COLUMN: Daily Challenges
                Card3D(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Active Eco Challenges", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.accentCyan)
                            Spacer(Modifier.height(6.dp))

                            state.challenges.forEach { ch ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x201E293B),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).border(1.dp, Color(0x1538BDF8), RoundedCornerShape(8.dp))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(ch.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(if (ch.isCompleted) "COMPLETED ✅" else "+${ch.rewardPoints} Pts", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (ch.isCompleted) Theme3D.successGreen else Theme3D.warningAmber)
                                        }
                                        Text(ch.description, fontSize = 8.sp, color = Theme3D.textSecondary)
                                        Spacer(Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color(0x30FFFFFF))
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth(ch.progress).fillMaxHeight().background(if (ch.isCompleted) Theme3D.successGreen else Theme3D.accentCyan))
                                        }
                                    }
                                }
                            }
                        }

                        TvButton3D(
                            text = "⚡ Claim Daily Bonus Points",
                            fontSize = 10,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.completeChallenge("CHALLENGE_01")
                            }
                        )
                    }
                }

                // RIGHT COLUMN: Family Leaderboard & Achievements
                Card3D(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Weekly Family Leaderboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Theme3D.warningAmber)
                            Spacer(Modifier.height(6.dp))

                            state.leaderboard.forEach { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            when (entry.rank) {
                                                1 -> "🥇"
                                                2 -> "🥈"
                                                3 -> "🥉"
                                                else -> "4."
                                            },
                                            fontSize = 11.sp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(entry.member.name.split(" ").first(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${entry.savingsKwh} kWh saved", fontSize = 9.sp, color = Theme3D.successGreen)
                                        Spacer(Modifier.width(6.dp))
                                        Text("${entry.scorePoints} pts", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Divider(color = Color(0x18FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                            Text("Unlocked Achievement Badges:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.achievements.filter { it.isUnlocked }.forEach { ach ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x301E293B),
                                        modifier = Modifier.border(1.dp, Color(0x3010B981), RoundedCornerShape(6.dp))
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(ach.icon, fontSize = 10.sp)
                                            Spacer(Modifier.width(3.dp))
                                            Text(ach.title, fontSize = 8.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        Text("Target: Save ₹500 this month • Progress: 68%", fontSize = 9.sp, color = Theme3D.accentCyan)
                    }
                }
            }
        },
        confirmButton = {
            TvButton3D(text = "Close Challenges", onClick = onClose)
        },
        containerColor = Color(0xFF0F172A)
    )
}


