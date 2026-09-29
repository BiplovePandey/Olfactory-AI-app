package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Fragrance
import com.example.data.model.UserPreferences
import com.example.olfactory.ml.OlfactoryEngine
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryAtelierTopBar
import com.example.ui.components.OlfactoryFlaconCard
import com.example.ui.components.OlfactoryScoreBadge
import com.example.ui.components.getFamilyColor
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IndianRose
import com.example.ui.theme.KannaujKhus
import com.example.ui.theme.MittiClay
import com.example.ui.theme.MysoreSandal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.OudDark
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// DATA DEFINITIONS FROM ORIGINAL MAIN BRANCH
// ---------------------------------------------------------------------------

data class DiscoverMoodPreset(
    val id: String,
    val title: String,
    val emoji: String,
    val tagline: String,
    val description: String,
    val preferredNotes: List<String>,
    val targetFamily: String,
    val sweetness: Int,
    val freshness: Int,
    val intensity: Int,
    val originFilter: String,
    val accentColor: Color
)

val ORIGINAL_DISCOVER_MOODS = listOf(
    DiscoverMoodPreset(
        id = "fresh_energetic",
        title = "Fresh & Energetic",
        emoji = "🌞",
        tagline = "Luminous citrus sparkle & invigorating green herbs",
        description = "Crisp bergamot, Sicilian lemon, and green leaves paired with radiant light woods. Ideal for an early morning boost.",
        preferredNotes = listOf("Bergamot", "Lemon", "Neroli", "Mint", "Vetiver"),
        targetFamily = "Citrus",
        sweetness = 3,
        freshness = 9,
        intensity = 5,
        originFilter = "All",
        accentColor = Color(0xFFEAB308)
    ),
    DiscoverMoodPreset(
        id = "warm_seductive",
        title = "Warm & Seductive",
        emoji = "🌙",
        tagline = "Opulent amber, toasted spices & sultry vanilla",
        description = "An intoxicating blend of golden amber resins, bourbon vanilla, cinnamon, and cashmere musk for evening allure.",
        preferredNotes = listOf("Amber", "Vanilla", "Cardamom", "Cinnamon", "Tonka Bean"),
        targetFamily = "Amber",
        sweetness = 8,
        freshness = 3,
        intensity = 9,
        originFilter = "All",
        accentColor = Color(0xFFC58B45)
    ),
    DiscoverMoodPreset(
        id = "clean_calm",
        title = "Clean & Calm",
        emoji = "🌿",
        tagline = "Crisp white musk, cedar shavings & tranquil green tea",
        description = "A soothing, second-skin aura that calms the senses. Airy musks, delicate iris, and fresh dewy leaves create a sanctuary.",
        preferredNotes = listOf("White Musk", "Cedarwood", "Iris", "Lavender", "Green Tea"),
        targetFamily = "Woody",
        sweetness = 4,
        freshness = 8,
        intensity = 4,
        originFilter = "All",
        accentColor = Color(0xFF10B981)
    ),
    DiscoverMoodPreset(
        id = "romantic_soft",
        title = "Romantic & Soft",
        emoji = "🌹",
        tagline = "Velvety Damascena rose, jasmine sambac & powdery peach",
        description = "Tender floral petals unfurling in morning mist, sweetened with juicy stone fruit and resting on delicate sandalwood.",
        preferredNotes = listOf("Rose", "Jasmine", "Mogra", "Peach", "Sandalwood"),
        targetFamily = "Floral",
        sweetness = 7,
        freshness = 6,
        intensity = 6,
        originFilter = "All",
        accentColor = Color(0xFFC86D74)
    ),
    DiscoverMoodPreset(
        id = "dark_woody",
        title = "Dark & Woody",
        emoji = "🪵",
        tagline = "Smoky Assam agarwood, aged vetiver & midnight leather",
        description = "Deep, resinous and brooding. Pure oudh, earthy khus roots, burnt incense, and smoked birch woods.",
        preferredNotes = listOf("Oud", "Agarwood", "Vetiver", "Patchouli", "Leather"),
        targetFamily = "Woody",
        sweetness = 2,
        freshness = 4,
        intensity = 10,
        originFilter = "All",
        accentColor = Color(0xFF78350F)
    ),
    DiscoverMoodPreset(
        id = "indian_soul",
        title = "Indian Soul",
        emoji = "🇮🇳",
        tagline = "Petrichor mitti attar, pure ruh khus, saffron & royal sandalwood",
        description = "The soul of Indian perfumery: rain-soaked parched earth (geeli mitti), Kashmiri zafran, wild vetiver, and sacred temple woods.",
        preferredNotes = listOf("Mitti Attar", "Ruh Khus", "Saffron", "Sandalwood", "Kewra"),
        targetFamily = "Earthy",
        sweetness = 5,
        freshness = 7,
        intensity = 8,
        originFilter = "Indian Heritage",
        accentColor = Color(0xFFD6AA62)
    ),
    DiscoverMoodPreset(
        id = "surprise_me",
        title = "Surprise Me",
        emoji = "✨",
        tagline = "An adventurous cross-cultural chord pairing curated for you",
        description = "An unexpected harmonic dialogue between opposing worlds: an Indian distilled botanical attar married to an avant-garde extrait.",
        preferredNotes = listOf("Cardamom", "Bergamot", "Oud", "Vanilla", "Rose"),
        targetFamily = "Spicy",
        sweetness = 6,
        freshness = 6,
        intensity = 7,
        originFilter = "All",
        accentColor = Color(0xFF8B5CF6)
    )
)

data class VibeTasteCard(
    val id: Int,
    val emoji: String,
    val name: String,
    val family: String,
    val tags: List<String>,
    val accentColor: Color,
    val targetFamily: String,
    val targetNote: String,
    val sweetness: Int,
    val freshness: Int,
    val intensity: Int
)

val ORIGINAL_TINDER_VIBE_CARDS = listOf(
    VibeTasteCard(
        id = 1,
        emoji = "🌹",
        name = "Rose & Petals",
        family = "Floral",
        tags = listOf("Romantic", "Soft & Velvet", "Elegant Sillage"),
        accentColor = Color(0xFFE86A92),
        targetFamily = "Floral",
        targetNote = "Rose",
        sweetness = 6,
        freshness = 6,
        intensity = 6
    ),
    VibeTasteCard(
        id = 2,
        emoji = "🌧️",
        name = "Mitti & Petrichor",
        family = "Earthy",
        tags = listOf("First Monsoon Rain", "Baked Clay", "Meditative"),
        accentColor = Color(0xFFD95D39),
        targetFamily = "Earthy",
        targetNote = "Mitti Attar",
        sweetness = 4,
        freshness = 7,
        intensity = 7
    ),
    VibeTasteCard(
        id = 3,
        emoji = "🪵",
        name = "Mysore Sandalwood",
        family = "Woody",
        tags = listOf("Creamy Wood", "Warm Second-Skin", "Sacred Comfort"),
        accentColor = Color(0xFFB58A58),
        targetFamily = "Woody",
        targetNote = "Sandalwood",
        sweetness = 5,
        freshness = 5,
        intensity = 7
    ),
    VibeTasteCard(
        id = 4,
        emoji = "🍋",
        name = "Bergamot & Mint",
        family = "Fresh",
        tags = listOf("Crisp Zest", "Clean Morning", "Invigorating Breeze"),
        accentColor = Color(0xFF55BFA3),
        targetFamily = "Citrus",
        targetNote = "Bergamot",
        sweetness = 3,
        freshness = 9,
        intensity = 5
    ),
    VibeTasteCard(
        id = 5,
        emoji = "🍯",
        name = "Amber & Spiced Vanilla",
        family = "Sweet",
        tags = listOf("Decadent Gourmand", "Cozy Warmth", "Evening Glamour"),
        accentColor = Color(0xFFF2A65A),
        targetFamily = "Amber",
        targetNote = "Vanilla",
        sweetness = 8,
        freshness = 3,
        intensity = 8
    )
)

data class InteractiveNoteBubble(
    val id: String,
    val name: String,
    val hindiName: String,
    val emoji: String,
    val family: String,
    val vibe: String,
    val description: String,
    val culturalOrigin: String,
    val pairsBestWith: List<String>,
    val accentColor: Color
)

val ORIGINAL_POPULAR_NOTES = listOf(
    InteractiveNoteBubble(
        id = "rose",
        name = "Damascena Rose",
        hindiName = "Gulab",
        emoji = "🌹",
        family = "Floral",
        vibe = "Romantic • Soft • Velvety",
        description = "Fresh dewy dawn-picked petals distilled in copper degs. Tender, honeyed, and uplifting with natural powdery elegance.",
        culturalOrigin = "Kannauj & Grasse perfumery",
        pairsBestWith = listOf("Sandalwood", "Bourbon Vanilla", "Oud", "Cardamom"),
        accentColor = Color(0xFFE86A92)
    ),
    InteractiveNoteBubble(
        id = "sandalwood",
        name = "Mysore Sandalwood",
        hindiName = "Chandan",
        emoji = "🪵",
        family = "Woody",
        vibe = "Creamy • Meditative • Sacred",
        description = "The golden crown of Indian perfumery. Buttery, milky, warm heartwood that acts as the ultimate olfactory harmonizer.",
        culturalOrigin = "Southern India & Vedic rituals",
        pairsBestWith = listOf("Damascena Rose", "Amber", "Bergamot", "Jasmine"),
        accentColor = Color(0xFFB58A58)
    ),
    InteractiveNoteBubble(
        id = "mitti",
        name = "Baked Earth / Petrichor",
        hindiName = "Mitti Attar",
        emoji = "🌧️",
        family = "Earthy",
        vibe = "Calm • Nostalgic • Rain-soaked",
        description = "The poetic fragrance of first summer rain falling upon sun-baked alluvial clay, hydro-distilled into a sandalwood base.",
        culturalOrigin = "Kannauj, Uttar Pradesh heritage",
        pairsBestWith = listOf("Vetiver (Khus)", "Rose", "Citrus", "Cedarwood"),
        accentColor = Color(0xFFD95D39)
    ),
    InteractiveNoteBubble(
        id = "vanilla",
        name = "Bourbon Vanilla",
        hindiName = "Vanilla",
        emoji = "🍦",
        family = "Gourmand",
        vibe = "Delicious • Cozy • Sensual",
        description = "Dark, caramelized pods infused with creamy balsam and smoky tonka nuances. Instantly adds softness and comfort.",
        culturalOrigin = "Madagascar & French Haute Parfumerie",
        pairsBestWith = listOf("Oud", "Coffee", "Lavender", "Tobacco"),
        accentColor = Color(0xFFF2A65A)
    ),
    InteractiveNoteBubble(
        id = "bergamot",
        name = "Calabrian Bergamot",
        hindiName = "Citrus Zest",
        emoji = "🍋",
        family = "Citrus",
        vibe = "Sparkling • Crisp • Energizing",
        description = "Sun-drenched, aromatic citrus with a delicate floral undertone. Illuminates any dark or heavy base note.",
        culturalOrigin = "Mediterranean & Classic Colognes",
        pairsBestWith = listOf("Tea", "Vetiver", "Patchouli", "Musk"),
        accentColor = Color(0xFF55BFA3)
    ),
    InteractiveNoteBubble(
        id = "khus",
        name = "Wild Ruh Khus (Vetiver)",
        hindiName = "Khus / Usira",
        emoji = "🌿",
        family = "Earthy",
        vibe = "Cooling • Grassy • Forest Earth",
        description = "Distilled from deep tangled roots of wild vetiver grass. Nature's ancient cooling agent, deeply green and smoky.",
        culturalOrigin = "North Indian riverbeds & Ayurvedic coolness",
        pairsBestWith = listOf("Grapefruit", "Mitti", "Cardamom", "Cedar"),
        accentColor = Color(0xFF3DA388)
    ),
    InteractiveNoteBubble(
        id = "mogra",
        name = "Sambac Jasmine",
        hindiName = "Mogra / Chameli",
        emoji = "🌼",
        family = "Floral",
        vibe = "Festive • Intoxicating • Opulent",
        description = "Night-blooming royal white blossoms worn in festive hair garlands. Intensely indolic, narcotic, and joyful.",
        culturalOrigin = "Madurai & Traditional temple garlands",
        pairsBestWith = listOf("Sandalwood", "Green Tea", "Saffron", "Amber"),
        accentColor = Color(0xFFD97706)
    ),
    InteractiveNoteBubble(
        id = "kesar",
        name = "Kashmiri Saffron",
        hindiName = "Kesar / Zafran",
        emoji = "✨",
        family = "Spicy",
        vibe = "Regal • Golden • Warm Leather",
        description = "The world's most precious spice thread. Bitter-sweet, metallic, warm golden nuance wrapping fragrances in royal luxury.",
        culturalOrigin = "Pampore, Kashmir & Royal Mughal courts",
        pairsBestWith = listOf("Rose", "Oud", "Amber", "Cardamom"),
        accentColor = Color(0xFFEA580C)
    ),
    InteractiveNoteBubble(
        id = "oud",
        name = "Assam Agarwood",
        hindiName = "Oudh",
        emoji = "🪵",
        family = "Woody",
        vibe = "Enigmatic • Resinous • Powerful",
        description = "Dark resinous heartwood naturally aged over decades. Complex, balsamic, deeply animalic, and remarkably persistent.",
        culturalOrigin = "Northeastern India & Middle Eastern royal scents",
        pairsBestWith = listOf("Rose", "Vanilla", "Amber", "Bergamot"),
        accentColor = Color(0xFF7B3F98)
    )
)

data class IndianHeritageAccord(
    val name: String,
    val english: String,
    val emoji: String,
    val origin: String,
    val description: String,
    val filterKeyword: String,
    val accentColor: Color
)

val ORIGINAL_HERITAGE_ACCORDS = listOf(
    IndianHeritageAccord(
        name = "Mitti Attar",
        english = "Petrichor / Baked Earth",
        emoji = "🌧️",
        origin = "Kannauj, Uttar Pradesh",
        description = "The intoxicating scent of parched alluvial clay receiving the first monsoon raindrops, distilled into Mysore sandalwood.",
        filterKeyword = "Mitti",
        accentColor = MittiClay
    ),
    IndianHeritageAccord(
        name = "Ruh Gulab",
        english = "Pure Damascena Rose",
        emoji = "🌹",
        origin = "Aligarh & Hasayan",
        description = "Hydro-distilled in traditional deg-bhapka copper stills before sunrise. Lush, deeply sweet, and celestial.",
        filterKeyword = "Rose",
        accentColor = IndianRose
    ),
    IndianHeritageAccord(
        name = "Mysore Chandan",
        english = "Sandalwood Heartwood",
        emoji = "🪵",
        origin = "Karnataka",
        description = "The ancient foundation of all royal Indian perfumery. Milky, serene, balsamic, and sacred.",
        filterKeyword = "Sandalwood",
        accentColor = MysoreSandal
    ),
    IndianHeritageAccord(
        name = "Ruh Khus",
        english = "Wild Riverbed Vetiver",
        emoji = "🌿",
        origin = "Rajasthan & Uttar Pradesh",
        description = "Wild harvested vetiver roots imparting an emerald green, cooling petrichor with profound longevity.",
        filterKeyword = "Khus",
        accentColor = KannaujKhus
    ),
    IndianHeritageAccord(
        name = "Mogra & Chameli",
        english = "Jasmine Sambac",
        emoji = "🌼",
        origin = "Madurai, Tamil Nadu",
        description = "Night-blooming blossoms radiating white floral intensity, worn in festive ceremonies and royal courtyards.",
        filterKeyword = "Jasmine",
        accentColor = AmberAccent
    ),
    IndianHeritageAccord(
        name = "Assam Dehn Al Oud",
        english = "Pure Agarwood Oil",
        emoji = "🪵",
        origin = "Upper Assam Rainforests",
        description = "Aged wild resinous wood yielding complex, smoky, animalic, and deeply meditative aura.",
        filterKeyword = "Oud",
        accentColor = OudDark
    )
)

// ---------------------------------------------------------------------------
// MAIN DISCOVER SCREEN
// ---------------------------------------------------------------------------

@Composable
fun DiscoverScreen(
    viewModel: OlfactoryViewModel,
    onNavigateToDetail: (Fragrance) -> Unit,
    onNavigateToLayeringStudio: () -> Unit
) {
    val allFragrances by viewModel.allFragrances.collectAsState()
    val filteredFragrances by viewModel.filteredFragrances.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFamily by viewModel.selectedFamily.collectAsState()
    val selectedOrigin by viewModel.selectedOriginFilter.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val wardrobe by viewModel.wardrobe.collectAsState()
    val wardrobeIds = remember(wardrobe) { wardrobe.map { it.id }.toSet() }

    // Active Discovery Mode (matching the original sub-nav in DiscoverView.tsx)
    var activeMode by remember { mutableStateOf<DiscoverMode>(DiscoverMode.VIBE_DECK) }

    // Selected Note detail popover
    var inspectedNote by remember { mutableStateOf<InteractiveNoteBubble?>(null) }

    // Active Indian Accord filter
    var activeIndianFilter by remember { mutableStateOf<String?>(null) }

    // Active Selected Mood
    var activeMoodId by remember { mutableStateOf<String?>(null) }

    val families = listOf("All", "Woody", "Floral", "Earthy", "Spicy", "Citrus", "Gourmand", "Amber")
    val origins = listOf("All", "Indian Heritage", "Western Luxury", "Attars & Oils")

    val indianFragrances = remember(allFragrances, activeIndianFilter) {
        val base = allFragrances.filter {
            it.origin_style.contains("indian", ignoreCase = true) ||
            (it.brand_country ?: "").contains("india", ignoreCase = true) ||
            it.is_oil_based
        }
        if (activeIndianFilter.isNullOrBlank()) {
            base
        } else {
            base.filter { frag ->
                val allNotes = (frag.top_notes + frag.middle_notes + frag.base_notes).joinToString(" ").lowercase()
                allNotes.contains(activeIndianFilter!!.lowercase()) ||
                frag.name.contains(activeIndianFilter!!, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Atmospheric dynamic background canvas
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = if (selectedFamily != "All") selectedFamily else null
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("discover_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Top Bar
            item {
                OlfactoryAtelierTopBar(
                    title = "Interactive Discovery",
                    subtitle = "Living Accords, Vibe Decks & Indian Soul"
                )
            }

            // Discovery Mode Navigation (Vibes, Battling, Catalog, Indian Soul)
            item {
                DiscoverModeTabs(
                    activeMode = activeMode,
                    onSelectMode = { activeMode = it }
                )
            }

            when (activeMode) {
                DiscoverMode.VIBE_DECK -> {
                    // 1. TINDER VIBE DECK / SENSORY CALIBRATION
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER I",
                            title = "SENSORY ACCORD CALIBRATION",
                            subtitle = "Swipe instinctually to calibrate your personal 8D Olfactory signature"
                        )
                    }

                    item {
                        NativeTinderVibeDeck(
                            onCompleteVibeCheck = { newPrefs ->
                                viewModel.updatePreferences(newPrefs)
                            }
                        )
                    }

                    // 2. DAILY MOOD ACCORDS
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER II",
                            title = "HOW DO YOU WANT TO SMELL TODAY?",
                            subtitle = "Harmonize your daily state of mind with bespoke volatility accords"
                        )
                    }

                    item {
                        NativeDailyMoodPicker(
                            moods = ORIGINAL_DISCOVER_MOODS,
                            selectedMoodId = activeMoodId,
                            onSelectMood = { mood ->
                                if (activeMoodId == mood.id) {
                                    activeMoodId = null
                                    viewModel.setSelectedFamily("All")
                                } else {
                                    activeMoodId = mood.id
                                    viewModel.setSelectedFamily(mood.targetFamily)
                                    if (mood.originFilter != "All") {
                                        viewModel.setSelectedOriginFilter(mood.originFilter)
                                    }
                                }
                            }
                        )
                    }

                    // 3. INTERACTIVE FLOATING NOTE BUBBLES
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER III",
                            title = "EXPLORE LIVING NOTE BUBBLES",
                            subtitle = "Tap tactile botanical notes to unlock their heritage stories and pairing harmonies"
                        )
                    }

                    item {
                        NativeFloatingNoteBubbles(
                            notes = ORIGINAL_POPULAR_NOTES,
                            onNoteClick = { inspectedNote = it }
                        )
                    }

                    // 4. INDIAN SOUL: CRADLE OF BOTANICAL ATTARS
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER IV",
                            title = "INDIAN SOUL: CRADLE OF ATTARS",
                            subtitle = "400-year-old copper deg-bhapka hydro-distillations, petrichor & sacred woods"
                        )
                    }

                    item {
                        NativeIndianSoulSection(
                            accords = ORIGINAL_HERITAGE_ACCORDS,
                            indianFragrances = indianFragrances,
                            activeFilter = activeIndianFilter,
                            onSelectAccordFilter = { accord ->
                                activeIndianFilter = if (activeIndianFilter == accord.filterKeyword) null else accord.filterKeyword
                            },
                            wardrobeIds = wardrobeIds,
                            onFragranceClick = {
                                viewModel.setDetailFragrance(it)
                                onNavigateToDetail(it)
                            },
                            onWardrobeToggle = { frag ->
                                viewModel.toggleWardrobe(frag.id, !wardrobeIds.contains(frag.id))
                            },
                            onLayerClick = { frag ->
                                viewModel.startLayeringWith(frag)
                                onNavigateToLayeringStudio()
                            }
                        )
                    }
                }

                DiscoverMode.SCENT_BATTLES -> {
                    // FRAGRANCE CONFRONTATION DUEL
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER V",
                            title = "FRAGRANCE CONFRONTATION DUEL",
                            subtitle = "Head-to-head volatility, sillage projection, and harmonic chord duel"
                        )
                    }

                    item {
                        NativeScentBattlesSection(
                            allFragrances = allFragrances,
                            userPreferences = userPreferences,
                            onNavigateToDetail = {
                                viewModel.setDetailFragrance(it)
                                onNavigateToDetail(it)
                            },
                            onLaunchDuelInStudio = { fragA, fragB ->
                                viewModel.startLayeringWith(fragA)
                                viewModel.selectFragranceB(fragB)
                                onNavigateToLayeringStudio()
                            }
                        )
                    }
                }

                DiscoverMode.CATALOG_SEARCH -> {
                    // STANDARD SEARCH & FLACON DIRECTORY
                    item {
                        ChapterDivider(
                            chapter = "CHAMBER VI",
                            title = "CATALOG INTELLIGENCE",
                            subtitle = "Natural multi-dimensional query and format curation"
                        )
                    }

                    // Search Query Box
                    item {
                        Box(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        text = "Search by fragrance, note (Mitti, Rose, Oud), or brand...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = ParchmentFaint
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = GoldPrimary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = ParchmentMuted
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("discover_search_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianCardBorder,
                                    focusedTextColor = ParchmentWhite,
                                    unfocusedTextColor = ParchmentWhite,
                                    focusedContainerColor = ObsidianCard,
                                    unfocusedContainerColor = ObsidianCard
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Origin & Format Filter
                    item {
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = "ORIGIN & FORMAT",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GoldPrimary,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(origins) { origin ->
                                    val isSelected = selectedOrigin == origin
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isSelected) GoldPrimary else ObsidianCard)
                                            .border(
                                                0.5.dp,
                                                if (isSelected) GoldBright else ObsidianCardBorder,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.setSelectedOriginFilter(origin) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                            .testTag("origin_chip_$origin")
                                    ) {
                                        Text(
                                            text = origin,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                            color = if (isSelected) ObsidianBlack else ParchmentWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Olfactory Family Filter
                    item {
                        Column(modifier = Modifier.padding(bottom = 14.dp)) {
                            Text(
                                text = "OLFACTORY FAMILY",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = GoldPrimary,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(families) { family ->
                                    val isSelected = selectedFamily == family
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else ObsidianCard)
                                            .border(
                                                0.5.dp,
                                                if (isSelected) GoldBright else ObsidianCardBorder,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.setSelectedFamily(family) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                            .testTag("family_chip_$family")
                                    ) {
                                        Text(
                                            text = family,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                            color = if (isSelected) GoldBright else ParchmentMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Result count
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${filteredFragrances.size} FLACONS MATCHING CRITERIA",
                                style = MaterialTheme.typography.labelSmall,
                                color = ParchmentMuted
                            )
                        }
                    }

                    // Flacon cards list
                    if (filteredFragrances.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = ParchmentFaint,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No Flacons Found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ParchmentWhite
                                )
                                Text(
                                    text = "Try adjusting your search query or removing active family and origin filters.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ParchmentMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(filteredFragrances) { fragrance ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                OlfactoryFlaconCard(
                                    fragrance = fragrance,
                                    isInWardrobe = wardrobeIds.contains(fragrance.id),
                                    onCardClick = {
                                        viewModel.setDetailFragrance(fragrance)
                                        onNavigateToDetail(fragrance)
                                    },
                                    onWardrobeToggle = {
                                        viewModel.toggleWardrobe(fragrance.id, !wardrobeIds.contains(fragrance.id))
                                    },
                                    onLayerClick = {
                                        viewModel.startLayeringWith(fragrance)
                                        onNavigateToLayeringStudio()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Note Detail Dialog
        inspectedNote?.let { note ->
            NoteDetailModal(
                note = note,
                onDismiss = { inspectedNote = null },
                onFilterCatalog = {
                    inspectedNote = null
                    activeMode = DiscoverMode.CATALOG_SEARCH
                    viewModel.setSearchQuery(note.name.split(" ").first())
                },
                onApplyToPreferences = {
                    val updatedNotes = (userPreferences.preferred_notes + note.name).distinct()
                    viewModel.updatePreferences(userPreferences.copy(preferred_notes = updatedNotes))
                    inspectedNote = null
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-NAV DISCOVERY MODE SELECTOR
// ---------------------------------------------------------------------------

enum class DiscoverMode(val title: String, val icon: ImageVector) {
    VIBE_DECK("Vibes & Rituals", Icons.Default.LocalFireDepartment),
    SCENT_BATTLES("Scent Battles", Icons.Default.SportsKabaddi),
    CATALOG_SEARCH("Search Catalog", Icons.Default.Search)
}

@Composable
private fun DiscoverModeTabs(
    activeMode: DiscoverMode,
    onSelectMode: (DiscoverMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianCard)
            .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DiscoverMode.values().forEach { mode ->
            val isSelected = activeMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) GoldPrimary else Color.Transparent
                    )
                    .clickable { onSelectMode(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = null,
                        tint = if (isSelected) ObsidianBlack else ParchmentMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) ObsidianBlack else ParchmentWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 1. NATIVE TINDER VIBE DECK WITH TOUCH SWIPING
// ---------------------------------------------------------------------------

@Composable
private fun NativeTinderVibeDeck(
    onCompleteVibeCheck: (UserPreferences) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val likedCards = remember { mutableStateListOf<VibeTasteCard>() }
    val passedCards = remember { mutableStateListOf<VibeTasteCard>() }
    var isFinished by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var swipeDecision by remember { mutableStateOf<String?>(null) } // "liked" or "passed"

    val currentCard = ORIGINAL_TINDER_VIBE_CARDS.getOrNull(currentIndex)

    fun handleVote(liked: Boolean) {
        if (currentCard == null || isFinished) return
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        val card = currentCard
        coroutineScope.launch {
            val targetX = if (liked) 600f else -600f
            swipeDecision = if (liked) "liked" else "passed"
            offsetX.animateTo(targetX, tween(200, easing = FastOutSlowInEasing))
            if (liked) likedCards.add(card) else passedCards.add(card)
            offsetX.snapTo(0f)
            swipeDecision = null
            if (currentIndex + 1 >= ORIGINAL_TINDER_VIBE_CARDS.size) {
                isFinished = true
            } else {
                currentIndex += 1
            }
        }
    }

    fun handleApply() {
        val favoriteFamilies = likedCards.map { it.targetFamily }.distinct()
        val favoriteNotes = likedCards.map { it.targetNote }.distinct()
        val avgSweetness = if (likedCards.isNotEmpty()) likedCards.map { it.sweetness }.average().roundToInt() else 5
        val avgFreshness = if (likedCards.isNotEmpty()) likedCards.map { it.freshness }.average().roundToInt() else 6
        val avgIntensity = if (likedCards.isNotEmpty()) likedCards.map { it.intensity }.average().roundToInt() else 7

        onCompleteVibeCheck(
            UserPreferences(
                favorite_family = if (favoriteFamilies.isNotEmpty()) favoriteFamilies else listOf("Woody", "Amber"),
                preferred_notes = if (favoriteNotes.isNotEmpty()) favoriteNotes else listOf("Sandalwood", "Bergamot"),
                sweetness = avgSweetness,
                freshness = avgFreshness,
                intensity = avgIntensity
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.6f), ObsidianCardBorder))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "TASTE CALIBRATION RITUAL",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                        color = GoldPrimary
                    )
                }

                if (!isFinished) {
                    Text(
                        text = "${currentIndex + 1} of ${ORIGINAL_TINDER_VIBE_CARDS.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sensory Accord Deck",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                color = ParchmentWhite
            )
            Text(
                text = "Swipe right or tap Love to harmonize your 8-dimensional olfactory matrix.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isFinished && currentCard != null) {
                // Progress indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    ORIGINAL_TINDER_VIBE_CARDS.forEachIndexed { index, _ ->
                        val isDone = index < currentIndex
                        val isCurrent = index == currentIndex
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(if (isCurrent) 24.dp else 12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        isCurrent -> GoldPrimary
                                        isDone -> KannaujKhus
                                        else -> ObsidianElevated
                                    }
                                )
                        )
                    }
                }

                // Interactive Swipeable Card
                val dragModifier = Modifier
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + delta)
                            }
                        },
                        onDragStopped = { velocity ->
                            if (offsetX.value > 120f || velocity > 300f) {
                                handleVote(true)
                            } else if (offsetX.value < -120f || velocity < -300f) {
                                handleVote(false)
                            } else {
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, tween(200))
                                }
                            }
                        }
                    )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .rotate(offsetX.value * 0.04f)
                        .then(dragModifier)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(ObsidianElevated, ObsidianSurface, ObsidianBlack)
                            )
                        )
                        .border(1.dp, currentCard.accentColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Stamp feedback indicators
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (offsetX.value > 40f || swipeDecision == "liked") {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(KannaujKhus.copy(alpha = 0.25f))
                                        .border(1.dp, KannaujKhus, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("HARMONIZE ❤️", color = KannaujKhus, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            } else if (offsetX.value < -40f || swipeDecision == "passed") {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(IndianRose.copy(alpha = 0.25f))
                                        .border(1.dp, IndianRose, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("PASS 👎", color = IndianRose, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                        }

                        // Emoji Icon
                        Text(
                            text = currentCard.emoji,
                            fontSize = 64.sp
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentCard.family.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.5.sp),
                                color = currentCard.accentColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentCard.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = ParchmentWhite
                            )
                        }

                        // Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentCard.tags.take(2).forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ObsidianBlack)
                                        .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tag, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = ParchmentMuted)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Pass & Like
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { handleVote(false) },
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = IndianRose),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(IndianRose, IndianRose.copy(alpha = 0.4f))))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Pass", tint = IndianRose)
                    }

                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex -= 1
                                isFinished = false
                            }
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ParchmentMuted),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, ObsidianCardBorder)))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Undo", tint = ParchmentMuted, modifier = Modifier.size(18.dp))
                    }

                    Button(
                        onClick = { handleVote(true) },
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Love", tint = ObsidianBlack)
                    }
                }
            } else {
                // Completed Summary Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldBright, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calibration Completed!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentWhite
                    )
                    Text(
                        text = "We captured ${likedCards.size} affinities and tuned your composite vector.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ParchmentMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { handleApply() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("APPLY ACCORDS TO DNA PROFILE", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Reset Deck",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldBright),
                        modifier = Modifier
                            .clickable {
                                currentIndex = 0
                                likedCards.clear()
                                passedCards.clear()
                                isFinished = false
                            }
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. NATIVE DAILY MOOD ACCORDS PICKER
// ---------------------------------------------------------------------------

@Composable
private fun NativeDailyMoodPicker(
    moods: List<DiscoverMoodPreset>,
    selectedMoodId: String?,
    onSelectMood: (DiscoverMoodPreset) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(moods) { mood ->
                val isSelected = selectedMoodId == mood.id
                Card(
                    modifier = Modifier
                        .width(230.dp)
                        .clickable { onSelectMood(mood) }
                        .testTag("mood_card_${mood.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ObsidianElevated else ObsidianCard
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isSelected) listOf(GoldBright, mood.accentColor)
                            else listOf(ObsidianCardBorder, Color.Transparent)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mood.emoji,
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 28.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) GoldPrimary else mood.accentColor.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mood.targetFamily.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isSelected) ObsidianBlack else mood.accentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = mood.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            color = ParchmentWhite
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = mood.tagline,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = ParchmentMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Metric indicators for sweetness & freshness
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Int: ${mood.intensity}/10",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = GoldPrimary
                            )
                            Text(
                                text = "Fresh: ${mood.freshness}/10",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = KannaujKhus
                            )
                            Text(
                                text = "Sweet: ${mood.sweetness}/10",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                                color = AmberAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. INTERACTIVE LIVING NOTE BUBBLES
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NativeFloatingNoteBubbles(
    notes: List<InteractiveNoteBubble>,
    onNoteClick: (InteractiveNoteBubble) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Text(
                    text = "BOTANICAL LIVING NOTE BUBBLES",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap any note to unlock traditional distillation origin, accords & pairing harmonies.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                notes.forEach { note ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ObsidianElevated)
                            .border(1.dp, note.accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onNoteClick(note) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(note.emoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = note.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ParchmentWhite
                                )
                                Text(
                                    text = note.hindiName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp
                                    ),
                                    color = note.accentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. INDIAN SOUL SECTION: CRADLE OF ATTARS
// ---------------------------------------------------------------------------

@Composable
private fun NativeIndianSoulSection(
    accords: List<IndianHeritageAccord>,
    indianFragrances: List<Fragrance>,
    activeFilter: String?,
    onSelectAccordFilter: (IndianHeritageAccord) -> Unit,
    wardrobeIds: Set<Int>,
    onFragranceClick: (Fragrance) -> Unit,
    onWardrobeToggle: (Fragrance) -> Unit,
    onLayerClick: (Fragrance) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.5f), MittiClay.copy(alpha = 0.3f)))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Text(
                    text = "HERITAGE STILLS OF KANNAUJ & BEYOND",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pure hydro-distilled Deg-Bhapka botanical attars, ancient petrichor, and sacred sandalwood foundations.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Accord filter carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(accords) { accord ->
                    val isSelected = activeFilter == accord.filterKeyword
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) ObsidianElevated else ObsidianSurface)
                            .border(
                                1.dp,
                                if (isSelected) GoldPrimary else ObsidianCardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectAccordFilter(accord) }
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(accord.emoji, fontSize = 24.sp)
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = accord.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ParchmentWhite
                            )
                            Text(
                                text = accord.origin,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = accord.accentColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = accord.english,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ParchmentMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indian Flacons Showcase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeFilter != null) "SHOWING $activeFilter ATELIER FLACONS (${indianFragrances.size})" else "CURATED BOTANICAL ATTARS (${indianFragrances.size})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                    color = GoldBright
                )

                if (activeFilter != null) {
                    Text(
                        text = "CLEAR FILTER",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = IndianRose),
                        modifier = Modifier.clickable { onSelectAccordFilter(accords.first { it.filterKeyword == activeFilter }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                indianFragrances.take(5).forEach { fragrance ->
                    OlfactoryFlaconCard(
                        fragrance = fragrance,
                        isInWardrobe = wardrobeIds.contains(fragrance.id),
                        onCardClick = { onFragranceClick(fragrance) },
                        onWardrobeToggle = { onWardrobeToggle(fragrance) },
                        onLayerClick = { onLayerClick(fragrance) }
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. NATIVE SCENT BATTLES: HEAD-TO-HEAD CONFRONTATION DUEL
// ---------------------------------------------------------------------------

@Composable
private fun NativeScentBattlesSection(
    allFragrances: List<Fragrance>,
    userPreferences: UserPreferences,
    onNavigateToDetail: (Fragrance) -> Unit,
    onLaunchDuelInStudio: (Fragrance, Fragrance) -> Unit
) {
    if (allFragrances.size < 2) return

    var selectedIndexA by remember { mutableIntStateOf(0) }
    var selectedIndexB by remember { mutableIntStateOf(1) }
    var battleWinner by remember { mutableStateOf<Fragrance?>(null) }
    var duelAnalysis by remember { mutableStateOf<String?>(null) }

    val fragA = allFragrances.getOrNull(selectedIndexA) ?: allFragrances[0]
    val fragB = allFragrances.getOrNull(selectedIndexB) ?: allFragrances[1]

    val vectorA = remember(fragA) { OlfactoryEngine.getVector8D100(fragA) }
    val vectorB = remember(fragB) { OlfactoryEngine.getVector8D100(fragB) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(IndianRose.copy(alpha = 0.5f), GoldPrimary.copy(alpha = 0.5f)))
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = IndianRose, modifier = Modifier.size(18.dp))
                Text(
                    text = "HEAD-TO-HEAD CONFRONTATION ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = IndianRose
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pit two fragrances against each other to evaluate performance, sillage projection, and layering synergy.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ParchmentMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selectors A vs B
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Fragrance A Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, getFamilyColor(fragA.fragrance_family).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text("ALPHA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp, color = GoldPrimary))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fragA.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fragA.brand_name,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = ParchmentMuted,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Int: ${fragA.intensity}/10", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = GoldBright)
                    Text("Longevity: ${fragA.longevity}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = ParchmentMuted)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            selectedIndexA = (selectedIndexA + 1) % allFragrances.size
                            battleWinner = null
                            duelAnalysis = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("Next A", fontSize = 10.sp, color = GoldPrimary)
                    }
                }

                // VS Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianBlack)
                        .border(1.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("VS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                }

                // Fragrance B Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, getFamilyColor(fragB.fragrance_family).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text("BETA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp, color = AmberAccent))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fragB.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fragB.brand_name,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = ParchmentMuted,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Int: ${fragB.intensity}/10", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = AmberAccent)
                    Text("Longevity: ${fragB.longevity}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp), color = ParchmentMuted)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            selectedIndexB = (selectedIndexB + 1) % allFragrances.size
                            battleWinner = null
                            duelAnalysis = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("Next B", fontSize = 10.sp, color = AmberAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Run Battle Button
            Button(
                onClick = {
                    val scoreA = vectorA.intensity * 0.4 + vectorA.woody * 0.3 + (if (fragA.is_oil_based) 20 else 0)
                    val scoreB = vectorB.intensity * 0.4 + vectorB.woody * 0.3 + (if (fragB.is_oil_based) 20 else 0)
                    battleWinner = if (scoreA >= scoreB) fragA else fragB
                    duelAnalysis = if (scoreA >= scoreB) {
                        "${fragA.name} wins the longevity duel due to dense ${fragA.base_notes.take(2).joinToString(", ")} anchors."
                    } else {
                        "${fragB.name} overcomes Alpha with expansive ${fragB.fragrance_family} projection and sustained sillage."
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = ObsidianBlack)
                    Text("SIMULATE SCENT CONFRONTATION", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            }

            // Duel Outcome
            battleWinner?.let { winner ->
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("DUEL WINNER", style = MaterialTheme.typography.labelSmall.copy(color = GoldBright, letterSpacing = 1.2.sp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🏆 ${winner.name}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = duelAnalysis ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ParchmentMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onLaunchDuelInStudio(fragA, fragB) },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(GoldPrimary, AmberAccent))),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Text("HARMONIZE PAIR IN STUDIO", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// NOTE DETAIL MODAL DIALOG
// ---------------------------------------------------------------------------

@Composable
private fun NoteDetailModal(
    note: InteractiveNoteBubble,
    onDismiss: () -> Unit,
    onFilterCatalog: () -> Unit,
    onApplyToPreferences: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(note.accentColor, GoldPrimary.copy(alpha = 0.4f)))
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(note.emoji, fontSize = 32.sp)
                        Column {
                            Text(
                                text = note.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Traditional: ${note.hindiName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = note.accentColor
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ParchmentMuted)
                    }
                }

                // Vibe box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianElevated)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Vibe: ${note.vibe}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = GoldBright
                    )
                }

                // Heritage description
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ParchmentMuted
                )

                // Cultural Origin
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = note.accentColor, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Origin: ${note.culturalOrigin}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ParchmentWhite
                    )
                }

                // Best Pairs
                Column {
                    Text(
                        text = "PAIRS MAGICALLY WITH",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        note.pairsBestWith.take(3).forEach { pair ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianElevated)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(pair, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ParchmentWhite)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onFilterCatalog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary)
                    ) {
                        Text("Search Flacons", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onApplyToPreferences,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text("Add to DNA", fontSize = 11.sp, color = ObsidianBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
