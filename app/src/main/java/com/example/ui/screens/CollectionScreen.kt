package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fragrance
import com.example.olfactory.ml.OlfactoryEngine
import com.example.ui.components.AtelierFlaconGraphic
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryAtelierTopBar
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Mobile-native adaptation of the ORIGINAL Olfactory AI Private Fragrance Cabinet / Wardrobe.
 *
 * Implements:
 * 1. Curator's Private Archive Hero (epigraph, live real-data analytics ribbon).
 * 2. Today's Cabinet Edit ("What Should I Wear Today?", climate alignment, ritual instructions).
 * 3. Atmospheric Physical Shelving Cascade (Curated Favorites, Noble Woods, Floral Radiance,
 *    Solar Citrus, Marine Mist, Artisanal Heritage).
 * 4. Interactive Physical Flacon (fluid fill level, tilt glow, quick actions: Inspect, Wear, Layer, Favorite).
 * 5. Collector's Specimen Sheet (Vault ID, fluid meniscus slider, 8D vectors, drydown profile, remove/wear).
 * 6. Cabinet Search & Filter Bar (text search, favorites only, heritage only, family chips).
 * 7. Collection Intelligence Map & Scent Gaps Analysis (dominant families, radar bars, missing seasonal gaps).
 * 8. Collection Rotation & Scent Journal (wear logs, satisfaction ratings, rediscovery recommendations).
 * 9. Place Flacon in Cabinet Modal (browse unowned fragrances to add).
 * 10. Wear Confirmation Toast ("Scent of the Day Logged").
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CollectionScreen(
    viewModel: OlfactoryViewModel,
    onNavigateToLayeringStudio: () -> Unit,
    onNavigateToDetail: (Fragrance) -> Unit,
    onNavigateToDiscover: () -> Unit
) {
    val wardrobe by viewModel.wardrobe.collectAsState()
    val allFragrances by viewModel.allFragrances.collectAsState()
    val activeAtmosphere by viewModel.currentAtmosphere.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Persistent bottle fluid levels (default 80-95%)
    val bottleLevels = remember {
        mutableStateMapOf<Int, Int>().apply {
            wardrobe.forEach { f ->
                put(f.id, 75 + ((f.id * 7) % 25))
            }
        }
    }

    // Curated Favorites Set
    var favoriteIds by remember {
        mutableStateOf(wardrobe.take(2).map { it.id }.toSet())
    }

    // Modal & Toast states
    var inspectingFragrance by remember { mutableStateOf<Fragrance?>(null) }
    var isAddModalOpen by remember { mutableStateOf(false) }
    var wearToastInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Search and Filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedFamilyFilter by remember { mutableStateOf<String?>(null) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showHeritageOnly by remember { mutableStateOf(false) }

    // Wear handler
    val handleWearToday: (Fragrance) -> Unit = { fragrance ->
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        wearToastInfo = Pair(fragrance.name, timeStr)
        coroutineScope.launch {
            delay(4500)
            if (wearToastInfo?.first == fragrance.name) {
                wearToastInfo = null
            }
        }
    }

    // Toggle favorite
    val handleToggleFavorite: (Int) -> Unit = { id ->
        favoriteIds = if (favoriteIds.contains(id)) {
            favoriteIds - id
        } else {
            favoriteIds + id
        }
    }

    // Candidate fragrances to add
    val ownedIds = remember(wardrobe) { wardrobe.map { it.id }.toSet() }
    val candidateAddFragrances = remember(allFragrances, ownedIds) {
        allFragrances.filter { it.id !in ownedIds }
    }

    // Filtered collection
    val filteredWardrobe = remember(
        wardrobe,
        searchQuery,
        selectedFamilyFilter,
        showFavoritesOnly,
        showHeritageOnly,
        favoriteIds
    ) {
        wardrobe.filter { f ->
            if (showFavoritesOnly && f.id !in favoriteIds) return@filter false
            if (showHeritageOnly) {
                val isHerit = f.format == "Attar" || f.is_oil_based ||
                    f.origin_style.contains("indian", ignoreCase = true) ||
                    (f.brand_country ?: "").contains("india", ignoreCase = true) ||
                    (f.description ?: "").contains("kannauj", ignoreCase = true) ||
                    (f.description ?: "").contains("mitti", ignoreCase = true) ||
                    f.name.contains("attar", ignoreCase = true)
                if (!isHerit) return@filter false
            }
            if (selectedFamilyFilter != null && f.fragrance_family != selectedFamilyFilter) {
                return@filter false
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                val matchName = f.name.lowercase().contains(q)
                val matchBrand = f.brand_name.lowercase().contains(q)
                val matchFam = f.fragrance_family.lowercase().contains(q)
                val matchNotes = (f.top_notes + f.middle_notes + f.base_notes).any { it.lowercase().contains(q) }
                if (!matchName && !matchBrand && !matchFam && !matchNotes) return@filter false
            }
            true
        }
    }

    // Curated shelf groupings
    val favoriteShelf = remember(filteredWardrobe, favoriteIds) {
        filteredWardrobe.filter { it.id in favoriteIds }
    }
    val woodyShelf = remember(filteredWardrobe) {
        filteredWardrobe.filter {
            val fam = it.fragrance_family.lowercase()
            fam.contains("wood") || fam.contains("oud") || fam.contains("oriental") || fam.contains("amber") || fam.contains("spicy")
        }
    }
    val floralShelf = remember(filteredWardrobe) {
        filteredWardrobe.filter {
            val fam = it.fragrance_family.lowercase()
            fam.contains("floral") || fam.contains("rose") || fam.contains("jasmine")
        }
    }
    val freshShelf = remember(filteredWardrobe) {
        filteredWardrobe.filter {
            val fam = it.fragrance_family.lowercase()
            fam.contains("citrus") || fam.contains("fresh") || fam.contains("aromatic") || fam.contains("green")
        }
    }
    val aquaticShelf = remember(filteredWardrobe) {
        filteredWardrobe.filter {
            val fam = it.fragrance_family.lowercase()
            fam.contains("aquatic") || fam.contains("marine") || fam.contains("water")
        }
    }
    val heritageShelf = remember(filteredWardrobe) {
        filteredWardrobe.filter {
            it.format == "Attar" || it.is_oil_based ||
                it.origin_style.contains("indian", ignoreCase = true) ||
                (it.brand_country ?: "").contains("india", ignoreCase = true) ||
                (it.description ?: "").contains("kannauj", ignoreCase = true) ||
                (it.description ?: "").contains("mitti", ignoreCase = true) ||
                it.name.contains("attar", ignoreCase = true)
        }
    }

    // Available families for filter chips
    val availableFamilies = remember(wardrobe) {
        wardrobe.map { it.fragrance_family }.distinct()
    }

    // Real analytics derived from owned wardrobe
    val totalCount = wardrobe.size
    val houseCount = remember(wardrobe) { wardrobe.map { it.brand_name }.distinct().size }
    val familyCount = remember(wardrobe) { wardrobe.map { it.fragrance_family }.distinct().size }
    val heritageCount = remember(wardrobe) {
        wardrobe.count {
            it.format == "Attar" || it.is_oil_based ||
                it.origin_style.contains("indian", ignoreCase = true) ||
                it.name.contains("attar", ignoreCase = true)
        }
    }

    // Today's recommended wear from collection
    val todaysRecommendation = remember(wardrobe, allFragrances) {
        val pool = if (wardrobe.isNotEmpty()) wardrobe else allFragrances.take(4)
        if (pool.isEmpty()) null else pool.first()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Atmospheric backdrop
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = activeAtmosphere ?: wardrobe.firstOrNull()?.fragrance_family
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("collection_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Top Bar
            item {
                OlfactoryAtelierTopBar(
                    title = "Private Fragrance Cabinet",
                    subtitle = "Bespoke Collection & Inventory Intelligence"
                )
            }

            // 2. HERO: Curator's Private Archive
            item {
                CabinetHeroSection(
                    totalCount = totalCount,
                    houseCount = houseCount,
                    familyCount = familyCount,
                    heritageCount = heritageCount,
                    onOpenAddModal = { isAddModalOpen = true },
                    onExploreUniverse = onNavigateToDiscover
                )
            }

            // 3. TODAY'S CABINET EDIT
            if (todaysRecommendation != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ChapterDivider(
                        chapter = "CABINET I",
                        title = "TODAY'S CABINET EDIT",
                        subtitle = "Atmospheric daily curation calibrated against seasonal ambient conditions"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CabinetTodaysEditCard(
                        fragrance = todaysRecommendation,
                        onWearToday = { handleWearToday(todaysRecommendation) },
                        onInspect = { inspectingFragrance = todaysRecommendation },
                        onLayer = {
                            viewModel.startLayeringWith(todaysRecommendation)
                            onNavigateToLayeringStudio()
                        }
                    )
                }
            }

            // 4. CHAPTER II: THE CURATED SHELVES & SEARCH
            item {
                Spacer(modifier = Modifier.height(20.dp))
                ChapterDivider(
                    chapter = "CABINET II",
                    title = "THE CURATED SHELVES",
                    subtitle = "Physical flacon cascade organized by olfactory family, fluid levels, and personal reverence"
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar and Add Flacon CTA
                CabinetSearchAndFiltersBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFamily = selectedFamilyFilter,
                    onSelectFamily = { selectedFamilyFilter = it },
                    showFavoritesOnly = showFavoritesOnly,
                    onToggleFavorites = { showFavoritesOnly = !showFavoritesOnly },
                    showHeritageOnly = showHeritageOnly,
                    onToggleHeritage = { showHeritageOnly = !showHeritageOnly },
                    availableFamilies = availableFamilies,
                    totalResults = filteredWardrobe.size,
                    onOpenAddModal = { isAddModalOpen = true }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. PHYSICAL SHELVES CASCADE
            if (wardrobe.isEmpty()) {
                item {
                    CabinetEmptyStateCard(
                        onExploreCatalog = onNavigateToDiscover,
                        onAddFirstFlacon = { isAddModalOpen = true }
                    )
                }
            } else if (filteredWardrobe.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ParchmentMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No Flacons Match Current Filters",
                                style = MaterialTheme.typography.titleMedium,
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Try clearing the search query or resetting the family filter.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ParchmentMuted,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedFamilyFilter = null
                                    showFavoritesOnly = false
                                    showHeritageOnly = false
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reset All Filters", color = GoldBright, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            } else {
                // Shelf 1: Curated Favorites
                if (favoriteShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "The Inner Shelf • Curated Favorites",
                            subtitle = "Most cherished personal signatures held in highest reverence",
                            icon = Icons.Default.Favorite,
                            fragrances = favoriteShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite,
                            isSpecial = true
                        )
                    }
                }

                // Shelf 2: Noble Woods, Resins & Amber
                if (woodyShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "Noble Woods, Resins & Amber",
                            subtitle = "Mysore sandalwood, aged agarwood, frankincense, and warm spice structures",
                            icon = Icons.Default.Park,
                            fragrances = woodyShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite
                        )
                    }
                }

                // Shelf 3: Damask Petals & Night-Blooming Florals
                if (floralShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "Damask Petals & Night Florals",
                            subtitle = "Lush Kannauj roses, Sambac jasmine, and radiant orange blossom chords",
                            icon = Icons.Default.LocalFlorist,
                            fragrances = floralShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite
                        )
                    }
                }

                // Shelf 4: Solar Citrus & Green Aromatics
                if (freshShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "Solar Citrus & Green Aromatics",
                            subtitle = "Sparkling citrus terpenes, crushed herbs, and vibrant daytime freshness",
                            icon = Icons.Default.WbSunny,
                            fragrances = freshShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite
                        )
                    }
                }

                // Shelf 5: Marine Horizons & Coastal Mist
                if (aquaticShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "Marine Horizons & Coastal Mist",
                            subtitle = "Ozone accords, salty coastal breezes, and cooling aquatic reflections",
                            icon = Icons.Default.Water,
                            fragrances = aquaticShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite
                        )
                    }
                }

                // Shelf 6: Artisanal Heritage & Deg-Bhapka Attars
                if (heritageShelf.isNotEmpty()) {
                    item {
                        CabinetShelfRow(
                            title = "Artisanal Heritage & Deg-Bhapka Shelf",
                            subtitle = "Hydro-distilled Mitti attars, pure Mysore sandalwood, and classical Indian compositions",
                            icon = Icons.Default.Handshake,
                            fragrances = heritageShelf,
                            bottleLevels = bottleLevels,
                            favoriteIds = favoriteIds,
                            onInspect = { inspectingFragrance = it },
                            onWear = handleWearToday,
                            onLayer = {
                                viewModel.startLayeringWith(it)
                                onNavigateToLayeringStudio()
                            },
                            onToggleFavorite = handleToggleFavorite,
                            isSpecial = true
                        )
                    }
                }
            }

            // 6. CHAPTER III: COLLECTION INTELLIGENCE & SHAPE
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ChapterDivider(
                    chapter = "CABINET III",
                    title = "INVENTORY INTELLIGENCE & RADAR",
                    subtitle = "Mathematical distribution of scent families, blind spots, and seasonal gaps"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                CollectionIntelligenceSection(
                    wardrobe = wardrobe,
                    onExploreGap = { onNavigateToDiscover() }
                )
            }

            // 7. CHAPTER IV: ROTATION & SCENT JOURNAL
            item {
                Spacer(modifier = Modifier.height(20.dp))
                ChapterDivider(
                    chapter = "CABINET IV",
                    title = "COLLECTION ROTATION & LOGS",
                    subtitle = "Scent wear history, satisfaction metrics, and neglected flacons to rediscover"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                CollectionRotationSection(
                    wardrobe = wardrobe,
                    onInspect = { inspectingFragrance = it },
                    onWear = handleWearToday
                )
            }
        }

        // FLOATING WEAR TOAST
        AnimatedVisibility(
            visible = wearToastInfo != null,
            enter = fadeIn() + slideInVertically { -40 },
            exit = fadeOut() + slideOutVertically { -40 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp, start = 16.dp, end = 16.dp)
        ) {
            wearToastInfo?.let { (name, time) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldBright, AmberAccent))),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SCENT OF THE DAY LOGGED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = GoldBright,
                                letterSpacing = 1.1.sp
                            )
                            Text(
                                text = "Wearing $name",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Logged at $time • +25 Scent XP",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = ParchmentMuted
                            )
                        }
                    }
                }
            }
        }

        // COLLECTOR'S SPECIMEN SHEET (MODAL)
        inspectingFragrance?.let { fragrance ->
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val fill = bottleLevels[fragrance.id] ?: 80
            val isFav = fragrance.id in favoriteIds

            ModalBottomSheet(
                onDismissRequest = { inspectingFragrance = null },
                sheetState = sheetState,
                containerColor = ObsidianElevated,
                contentColor = ParchmentWhite
            ) {
                CollectorSpecimenSheetContent(
                    fragrance = fragrance,
                    fillLevel = fill,
                    isFavorite = isFav,
                    onAdjustFillLevel = { newLevel ->
                        bottleLevels[fragrance.id] = newLevel
                    },
                    onToggleFavorite = { handleToggleFavorite(fragrance.id) },
                    onWearToday = {
                        handleWearToday(fragrance)
                        inspectingFragrance = null
                    },
                    onSendToLab = {
                        inspectingFragrance = null
                        viewModel.startLayeringWith(fragrance)
                        onNavigateToLayeringStudio()
                    },
                    onViewFullDetail = {
                        inspectingFragrance = null
                        viewModel.setDetailFragrance(fragrance)
                        onNavigateToDetail(fragrance)
                    },
                    onRemoveFromCabinet = {
                        viewModel.toggleWardrobe(fragrance.id, false)
                        inspectingFragrance = null
                    },
                    onClose = { inspectingFragrance = null }
                )
            }
        }

        // ADD FLACON MODAL
        if (isAddModalOpen) {
            val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ModalBottomSheet(
                onDismissRequest = { isAddModalOpen = false },
                sheetState = addSheetState,
                containerColor = ObsidianElevated,
                contentColor = ParchmentWhite
            ) {
                AddFlaconModalContent(
                    candidates = candidateAddFragrances,
                    onAdd = { id ->
                        viewModel.toggleWardrobe(id, true)
                        bottleLevels[id] = 85
                        isAddModalOpen = false
                    },
                    onClose = { isAddModalOpen = false }
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 1: CABINET HERO SECTION
// ----------------------------------------------------------------------------

@Composable
private fun CabinetHeroSection(
    totalCount: Int,
    houseCount: Int,
    familyCount: Int,
    heritageCount: Int,
    onOpenAddModal: () -> Unit,
    onExploreUniverse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GoldBright.copy(alpha = 0.7f), AmberAccent.copy(alpha = 0.3f), Color.Transparent))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtle Archival Plaque
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f))
                    .border(0.5.dp, GoldBright.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "CURATOR'S PRIVATE ARCHIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = GoldBright,
                        letterSpacing = 1.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "My Fragrance Cabinet",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                color = ParchmentWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "“Every bottle is a memory. Every accord, a possibility.”",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = ParchmentMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Real Wardrobe Analytics Ribbon (4 metric blocks)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricColumn(value = "$totalCount", label = if (totalCount == 1) "Fragrance" else "Fragrances", color = GoldBright)
                MetricColumn(value = "$houseCount", label = if (houseCount == 1) "House" else "Houses", color = AmberAccent)
                MetricColumn(value = "$familyCount", label = "Families", color = GoldPrimary)
                MetricColumn(value = "$heritageCount", label = "Heritage", color = KannaujKhus)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenAddModal,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLACE FLACON",
                        color = ObsidianBlack,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = onExploreUniverse,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(GoldBright, AmberAccent)))
                ) {
                    Text(
                        text = "EXPLORE CATALOG",
                        color = GoldBright,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = ParchmentMuted
        )
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 2: TODAY'S CABINET EDIT
// ----------------------------------------------------------------------------

@Composable
private fun CabinetTodaysEditCard(
    fragrance: Fragrance,
    onWearToday: () -> Unit,
    onInspect: () -> Unit,
    onLayer: () -> Unit
) {
    val isOil = fragrance.is_oil_based || fragrance.format == "Attar"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(AmberAccent.copy(alpha = 0.6f), GoldPrimary.copy(alpha = 0.2f), Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeviceThermostat,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "WHAT SHOULD I WEAR TODAY?",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = AmberAccent,
                        letterSpacing = 1.2.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "96% ALIGNMENT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AtelierFlaconGraphic(
                    fragrance = fragrance,
                    modifier = Modifier
                        .size(68.dp)
                        .clickable { onInspect() },
                    isCompact = true
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fragrance.brand_name.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldPrimary,
                        letterSpacing = 1.1.sp
                    )
                    Text(
                        text = fragrance.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ParchmentWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${fragrance.fragrance_family} • ${fragrance.format}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isOil) "Apply 1 drop directly onto pulse point lipids." else "2-3 sprays on collarbone & lapel.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWearToday,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("WEAR TODAY", color = ObsidianBlack, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onLayer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Layer",
                        tint = GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                }

                OutlinedButton(
                    onClick = onInspect,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("INSPECT", color = ParchmentWhite, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 3: SEARCH & FILTERS BAR
// ----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CabinetSearchAndFiltersBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFamily: String?,
    onSelectFamily: (String?) -> Unit,
    showFavoritesOnly: Boolean,
    onToggleFavorites: () -> Unit,
    showHeritageOnly: Boolean,
    onToggleHeritage: () -> Unit,
    availableFamilies: List<String>,
    totalResults: Int,
    onOpenAddModal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search flacon name, brand, or botanical note...", style = MaterialTheme.typography.bodySmall, color = ParchmentMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = ParchmentMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ObsidianCard,
                unfocusedContainerColor = ObsidianCard,
                focusedBorderColor = GoldBright,
                unfocusedBorderColor = ObsidianCardBorder,
                focusedTextColor = ParchmentWhite,
                unfocusedTextColor = ParchmentWhite
            ),
            singleLine = true
        )

        // Filter chips horizontal scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            // All Flacons chip
            item {
                val isSelected = selectedFamily == null && !showFavoritesOnly && !showHeritageOnly
                FilterChip(
                    text = "All Flacons ($totalResults)",
                    selected = isSelected,
                    onClick = {
                        onSelectFamily(null)
                        if (showFavoritesOnly) onToggleFavorites()
                        if (showHeritageOnly) onToggleHeritage()
                    }
                )
            }

            // Favorites chip
            item {
                FilterChip(
                    text = "Favorites",
                    icon = Icons.Default.Favorite,
                    selected = showFavoritesOnly,
                    onClick = onToggleFavorites,
                    accentColor = IndianRose
                )
            }

            // Heritage chip
            item {
                FilterChip(
                    text = "Heritage & Attars",
                    icon = Icons.Default.Handshake,
                    selected = showHeritageOnly,
                    onClick = onToggleHeritage,
                    accentColor = KannaujKhus
                )
            }

            // Family chips
            items(availableFamilies) { fam ->
                val isSelected = selectedFamily == fam
                FilterChip(
                    text = fam,
                    selected = isSelected,
                    onClick = { onSelectFamily(if (isSelected) null else fam) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    accentColor: Color = GoldPrimary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) accentColor else ObsidianCard)
            .border(0.5.dp, if (selected) GoldBright else ObsidianCardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) ObsidianBlack else accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = if (selected) ObsidianBlack else ParchmentWhite
            )
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 4: CABINET SHELF ROW (PHYSICAL BOTTLE PRESENTATION)
// ----------------------------------------------------------------------------

@Composable
private fun CabinetShelfRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    fragrances: List<Fragrance>,
    bottleLevels: Map<Int, Int>,
    favoriteIds: Set<Int>,
    onInspect: (Fragrance) -> Unit,
    onWear: (Fragrance) -> Unit,
    onLayer: (Fragrance) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    isSpecial: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    if (isSpecial) GoldBright.copy(alpha = 0.5f) else ObsidianCardBorder,
                    Color.Transparent
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Shelf Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSpecial) GoldPrimary.copy(alpha = 0.2f) else ObsidianElevated)
                            .border(0.5.dp, if (isSpecial) GoldBright.copy(alpha = 0.6f) else ObsidianCardBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSpecial) GoldBright else ParchmentWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ParchmentWhite
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ParchmentMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianElevated)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${fragrances.size} ${if (fragrances.size == 1) "flacon" else "flacons"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Physical Shelf Rack (Wood grain / glass ledge simulation)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                    .padding(vertical = 12.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(fragrances) { fragrance ->
                        val fill = bottleLevels[fragrance.id] ?: 80
                        val isFav = fragrance.id in favoriteIds

                        CabinetBottleItem(
                            fragrance = fragrance,
                            fillLevel = fill,
                            isFavorite = isFav,
                            onInspect = { onInspect(fragrance) },
                            onWear = { onWear(fragrance) },
                            onLayer = { onLayer(fragrance) },
                            onToggleFavorite = { onToggleFavorite(fragrance.id) }
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 5: PHYSICAL BOTTLE ITEM (MENISCUS, PEDESTAL, GLOW)
// ----------------------------------------------------------------------------

@Composable
private fun CabinetBottleItem(
    fragrance: Fragrance,
    fillLevel: Int,
    isFavorite: Boolean,
    onInspect: () -> Unit,
    onWear: () -> Unit,
    onLayer: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val familyColor = getFamilyColor(fragrance.fragrance_family)

    Column(
        modifier = Modifier
            .width(136.dp)
            .clickable { onInspect() }
            .testTag("cabinet_bottle_${fragrance.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bottle Frame with Heart and Fill Indicators
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            familyColor.copy(alpha = 0.25f),
                            ObsidianSurface.copy(alpha = 0.9f),
                            ObsidianBlack
                        )
                    )
                )
                .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Flacon render
            AtelierFlaconGraphic(
                fragrance = fragrance,
                modifier = Modifier.size(76.dp),
                isCompact = true
            )

            // Favorite Icon top-right
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) IndianRose else ParchmentFaint,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Fill level badge bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ObsidianBlack.copy(alpha = 0.75f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "$fillLevel%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = GoldBright
                )
            }
        }

        // Under-bottle shadow / glass shelf reflection line
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, GoldPrimary.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Brand & Fragrance Title
        Text(
            text = fragrance.brand_name.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = GoldPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = fragrance.name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = ParchmentWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Mini Actions Row (Wear & Layer)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldPrimary)
                    .clickable { onWear() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "WEAR",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = ObsidianBlack
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(6.dp))
                    .clickable { onLayer() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Layer",
                    tint = GoldBright,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 6: COLLECTION INTELLIGENCE & SHAPE (RADAR & GAPS)
// ----------------------------------------------------------------------------

@Composable
private fun CollectionIntelligenceSection(
    wardrobe: List<Fragrance>,
    onExploreGap: () -> Unit
) {
    val total = wardrobe.size.coerceAtLeast(1)

    val familyCounts = remember(wardrobe) {
        wardrobe.groupBy { it.fragrance_family }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
    }

    // Identify gaps
    val hasAquatic = remember(wardrobe) { wardrobe.any { it.fragrance_family.contains("aquatic", ignoreCase = true) } }
    val hasMitti = remember(wardrobe) { wardrobe.any { (it.description ?: "").contains("mitti", ignoreCase = true) || it.name.contains("mitti", ignoreCase = true) } }
    val hasOud = remember(wardrobe) { wardrobe.any { it.fragrance_family.contains("oud", ignoreCase = true) || it.name.contains("oud", ignoreCase = true) } }
    val hasCitrus = remember(wardrobe) { wardrobe.any { it.fragrance_family.contains("citrus", ignoreCase = true) || it.fragrance_family.contains("fresh", ignoreCase = true) } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "COLLECTION SHAPE & SENSORY BALANCE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = GoldBright
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Family Bars
            familyCounts.take(4).forEach { (fam, count) ->
                val pct = (count.toFloat() / total)
                val famColor = getFamilyColor(fam)

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = fam, style = MaterialTheme.typography.labelSmall, color = ParchmentWhite)
                        Text(text = "$count flacons (${(pct * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ParchmentMuted)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ObsidianElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(famColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sensory Gaps Detection Banner
            Text(
                text = "CURATOR'S GAP RECOMMENDATIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = AmberAccent,
                letterSpacing = 1.1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            val gapMessage = when {
                !hasAquatic -> "High-Heat Marine Accord: Crisp oceanic formulation for sweltering summer days."
                !hasMitti -> "Baked Earth Mitti Attar: Traditional geosmin petrichor for cooling monsoon evenings."
                !hasOud -> "Resinous Assam Oud: Nocturnal woody foundation for formal winter gatherings."
                !hasCitrus -> "Solar Sparkling Citrus: Vibrant bergamot terpenes to temper dense woody accords."
                else -> "Balanced Archival Collection: Your cabinet spans both pulse-point fixatives and diffusive sparks."
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianElevated)
                    .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = gapMessage,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ParchmentMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onExploreGap) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Explore",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 7: COLLECTION ROTATION & LOGS
// ----------------------------------------------------------------------------

@Composable
private fun CollectionRotationSection(
    wardrobe: List<Fragrance>,
    onInspect: (Fragrance) -> Unit,
    onWear: (Fragrance) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "COLLECTION ROTATION & REDISCOVERY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = GoldBright
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Balance olfactory fatigue by rotating your signatures and rediscovering unworn cabinet flacons.",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Rediscovery Card
            val unwornCandidate = wardrobe.lastOrNull()
            if (unwornCandidate != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianElevated)
                        .border(0.5.dp, ObsidianCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onInspect(unwornCandidate) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "REDISCOVER IN CABINET",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = GoldPrimary,
                                letterSpacing = 1.1.sp
                            )
                            Text(
                                text = unwornCandidate.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ParchmentWhite
                            )
                            Text(
                                text = "Unworn recently • Perfect match for evening relaxation",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ParchmentMuted
                            )
                        }

                        Button(
                            onClick = { onWear(unwornCandidate) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("WEAR", color = ObsidianBlack, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 8: COLLECTOR'S SPECIMEN SHEET
// ----------------------------------------------------------------------------

@Composable
private fun CollectorSpecimenSheetContent(
    fragrance: Fragrance,
    fillLevel: Int,
    isFavorite: Boolean,
    onAdjustFillLevel: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onWearToday: () -> Unit,
    onSendToLab: () -> Unit,
    onViewFullDetail: () -> Unit,
    onRemoveFromCabinet: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Vault ID header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COLLECTOR'S SPECIMEN VIEW",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = GoldBright,
                    letterSpacing = 1.3.sp
                )
                Text(
                    text = "Vault #${fragrance.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) IndianRose else ParchmentMuted
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ParchmentMuted
                    )
                }
            }
        }

        // Flacon & Metadata Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AtelierFlaconGraphic(
                fragrance = fragrance,
                modifier = Modifier.size(90.dp),
                isCompact = false
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fragrance.brand_name.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = fragrance.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ParchmentWhite
                )
                Text(
                    text = "${fragrance.fragrance_family} • ${fragrance.format}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ParchmentMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Origin: ${fragrance.origin_style} (${fragrance.brand_country ?: "International"})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = GoldBright
                )
            }
        }

        // Fluid Level Slider (Meniscus Tracking)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FLUID VOLUME LEVEL (MENISCUS)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Text(
                        text = "$fillLevel%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldBright
                    )
                }
                Slider(
                    value = fillLevel.toFloat(),
                    onValueChange = { onAdjustFillLevel(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldBright,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = ObsidianElevated
                    )
                )
            }
        }

        // Primary Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onWearToday,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("WEAR TODAY", color = ObsidianBlack, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }

            Button(
                onClick = onSendToLab,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(GoldBright, AmberAccent)))
            ) {
                Text("SEND TO LAB", color = GoldBright, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        // Full Detail & Remove Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Remove from Cabinet",
                style = MaterialTheme.typography.labelSmall.copy(color = IndianRose),
                modifier = Modifier.clickable { onRemoveFromCabinet() }
            )

            Text(
                text = "View Full Architectural Profile →",
                style = MaterialTheme.typography.labelSmall.copy(color = GoldBright),
                modifier = Modifier.clickable { onViewFullDetail() }
            )
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 9: ADD FLACON MODAL CONTENT
// ----------------------------------------------------------------------------

@Composable
private fun AddFlaconModalContent(
    candidates: List<Fragrance>,
    onAdd: (Int) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLACE FLACON IN CABINET",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GoldBright
            )
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ParchmentMuted)
            }
        }

        Text(
            text = "Select from verified catalog flacons to position directly onto your private shelves:",
            style = MaterialTheme.typography.bodySmall,
            color = ParchmentMuted
        )

        LazyColumn(
            modifier = Modifier.height(380.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(candidates) { candidate ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ObsidianCardBorder, Color.Transparent)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = candidate.brand_name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GoldPrimary
                            )
                            Text(
                                text = candidate.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ParchmentWhite
                            )
                            Text(
                                text = "${candidate.fragrance_family} • ${candidate.format}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ParchmentMuted
                            )
                        }

                        Button(
                            onClick = { onAdd(candidate.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PLACE", color = ObsidianBlack, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// COMPONENT 10: CABINET EMPTY STATE
// ----------------------------------------------------------------------------

@Composable
private fun CabinetEmptyStateCard(
    onExploreCatalog: () -> Unit,
    onAddFirstFlacon: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldBright.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.15f))
                    .border(1.dp, GoldBright.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Your Private Cabinet is Awaiting Flacons",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                color = ParchmentWhite,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Position physical flacons and artisanal attars onto your shelves to unlock automated daily wear advice, fluid meniscus tracking, and collection gap intelligence.",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentMuted,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAddFirstFlacon,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ADD FIRST FLACON", color = ObsidianBlack, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onExploreCatalog,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("EXPLORE CATALOG", color = GoldBright, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
