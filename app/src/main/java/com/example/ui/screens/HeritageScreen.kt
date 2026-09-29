package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AtmosphericFragranceCanvas
import com.example.ui.components.ChapterDivider
import com.example.ui.components.OlfactoryAtelierTopBar
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
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite

@Composable
fun HeritageScreen(
    onBackClick: () -> Unit,
    onExploreAttarsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AtmosphericFragranceCanvas(
            modifier = Modifier.fillMaxSize(),
            activeAtmosphere = "khus"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("heritage_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
        // Top Bar
        item {
            OlfactoryAtelierTopBar(
                title = "Indian Heritage Atlas",
                subtitle = "Kannauj Deg-Bhapka & Sacred Botanicals",
                onBackClick = onBackClick
            )
        }

        // Chapter I: The Deg-Bhapka Living Craft
        item {
            ChapterDivider(
                chapter = "HERITAGE I",
                title = "THE DEGS OF KANNAUJ",
                subtitle = "One millennium of uninterrupted hydro-distillation on the banks of the Ganges"
            )
        }

        // Hero Story Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.8f), Color.Transparent))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "THE PERFUME CAPITAL OF INDIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        letterSpacing = 1.4.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Kannauj: 1,000 Years of Hydro-Distillation",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ParchmentWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Nestled along the banks of the sacred Ganges in Uttar Pradesh, Kannauj has preserved the ancient art of Deg-Bhapka hydro-distillation for over a millennium. Giant copper cauldrons (Degs), sealed with clay and wild cotton rope, simmer gently over wood fires. Fragrant botanical vapors travel through bent bamboo pipes (Chonga) into subterranean copper receivers (Bhapka) submerged in cold water baths, capturing volatile aromatic essences into pure sandalwood oil.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ParchmentMuted,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Heritage Botanical Pillars
        item {
            ChapterDivider(
                chapter = "HERITAGE II",
                title = "THE SACRED CORNERSTONES",
                subtitle = "Four timeless botanicals that anchor Indian classical perfumery"
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                HeritagePillarCard(
                    title = "Mitti Attar (Petrichor / The Scent of Rain)",
                    subtitle = "Baked Alluvial Clay into Sandalwood",
                    description = "Sun-baked earth and unglazed clay cakes (Kullad) extracted from dried riverbeds are distilled directly into sandalwood oil, capturing the divine, primordial aroma of the first monsoon rain upon thirsty soil.",
                    icon = Icons.Default.WaterDrop,
                    accentColor = MittiClay
                )

                Spacer(modifier = Modifier.height(10.dp))

                HeritagePillarCard(
                    title = "Ruh Gulab (Pure Damask Rose)",
                    subtitle = "Rosa Damascena Hydro-Distillation",
                    description = "Thousands of freshly plucked pink Damask roses distilled before dawn. Unlike chemical solvent absolutes, Ruh Gulab retains the living soul, honeyed green facets, and crystalline sweetness of the bloom.",
                    icon = Icons.Default.Spa,
                    accentColor = IndianRose
                )

                Spacer(modifier = Modifier.height(10.dp))

                HeritagePillarCard(
                    title = "Khus (Wild Vetiver of Bharatpur)",
                    subtitle = "Cooling Green Roots & Hydro-Essence",
                    description = "Wild vetiver grass roots harvested from Indian river plains. Distilled into a cooling, emerald-tinted attar revered for mitigating extreme summer heat and soothing mental turbulence.",
                    icon = Icons.Default.Park,
                    accentColor = KannaujKhus
                )

                Spacer(modifier = Modifier.height(10.dp))

                HeritagePillarCard(
                    title = "Mysore Chandan & Assam Agarwood Oud",
                    subtitle = "The Imperial Bases of Mughal Perfumery",
                    description = "Sacred Santalum Album sandalwood from Karnataka and ancient resinous Aquilaria wood from the rainforests of Assam form the supreme fixatives that anchor olfactory compositions for days.",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = AmberAccent
                )
            }
        }

        // Bridging Ancient & Modern AI Card
        item {
            ChapterDivider(
                chapter = "HERITAGE III",
                title = "CROSS-CULTURAL COMPOUNDING",
                subtitle = "Bridging Kannauj molecular hydro-distillates with European modern accords"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GoldBright.copy(alpha = 0.5f), Color.Transparent))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ALCHEMICAL HARMONY + MODERN AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Olfactory AI bridges these two worlds. By translating the chemical molecular volatility of pure botanical attars into our 8-Dimensional Olfactory Vector Space, we empower you to compound historical Indian essences with contemporary European haute parfumerie.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onExploreAttarsClick,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "EXPLORE INDIAN HERITAGE ATTARS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun HeritagePillarCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = ParchmentWhite
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentMuted,
                lineHeight = 19.sp
            )
        }
    }
}
