package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChordResultScreen
import com.example.ui.screens.CollectionScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HeritageScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LayeringStudioScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PerfumeDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ParchmentFaint
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.ParchmentWhite
import com.example.ui.viewmodel.OlfactoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OlfactoryApp()
            }
        }
    }
}

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val HOME = "home"
    const val COLLECTION = "collection"
    const val LAYER = "layer"
    const val DISCOVER = "discover"
    const val PROFILE = "profile"
    const val DETAIL = "detail"
    const val CHORD_RESULT = "chord_result"
    const val HERITAGE = "heritage"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun OlfactoryApp() {
    val navController = rememberNavController()
    val viewModel: OlfactoryViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(AppRoutes.HOME, "Atelier", Icons.Default.Home, "nav_home"),
        BottomNavItem(AppRoutes.COLLECTION, "Wardrobe", Icons.Default.Bookmark, "nav_collection"),
        BottomNavItem(AppRoutes.LAYER, "Studio", Icons.Default.Science, "nav_layer"),
        BottomNavItem(AppRoutes.DISCOVER, "Discover", Icons.Default.Search, "nav_discover"),
        BottomNavItem(AppRoutes.PROFILE, "DNA", Icons.Default.Person, "nav_profile")
    )

    val showBottomBar = currentRoute in listOf(
        AppRoutes.HOME,
        AppRoutes.COLLECTION,
        AppRoutes.LAYER,
        AppRoutes.DISCOVER,
        AppRoutes.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBlack,
        bottomBar = {
            if (showBottomBar) {
                OlfactoryBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(AppRoutes.SPLASH) {
                val authState by viewModel.authState.collectAsState()
                SplashScreen(
                    onSplashComplete = {
                        val destination = when (authState) {
                            is com.example.data.auth.AuthState.Authenticated,
                            is com.example.data.auth.AuthState.Guest -> AppRoutes.HOME
                            else -> AppRoutes.ONBOARDING
                        }
                        navController.navigate(destination) {
                            popUpTo(AppRoutes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoutes.ONBOARDING) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(AppRoutes.AUTH) {
                            popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoutes.AUTH) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.AUTH) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoutes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLayeringStudio = {
                        navController.navigate(AppRoutes.LAYER)
                    },
                    onNavigateToDetail = {
                        navController.navigate(AppRoutes.DETAIL)
                    },
                    onNavigateToHeritage = {
                        navController.navigate(AppRoutes.HERITAGE)
                    }
                )
            }

            composable(AppRoutes.COLLECTION) {
                CollectionScreen(
                    viewModel = viewModel,
                    onNavigateToLayeringStudio = {
                        navController.navigate(AppRoutes.LAYER)
                    },
                    onNavigateToDetail = {
                        navController.navigate(AppRoutes.DETAIL)
                    },
                    onNavigateToDiscover = {
                        navController.navigate(AppRoutes.DISCOVER)
                    }
                )
            }

            composable(AppRoutes.LAYER) {
                LayeringStudioScreen(
                    viewModel = viewModel,
                    onNavigateToChordResult = {
                        navController.navigate(AppRoutes.CHORD_RESULT)
                    }
                )
            }

            composable(AppRoutes.DISCOVER) {
                DiscoverScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {
                        navController.navigate(AppRoutes.DETAIL)
                    },
                    onNavigateToLayeringStudio = {
                        navController.navigate(AppRoutes.LAYER)
                    }
                )
            }

            composable(AppRoutes.PROFILE) {
                ProfileScreen(
                    viewModel = viewModel,
                    onSignOut = {
                        viewModel.signOut {
                            navController.navigate(AppRoutes.AUTH) {
                                popUpTo(AppRoutes.HOME) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(AppRoutes.DETAIL) {
                val fragrance = viewModel.detailFragrance.collectAsState().value
                if (fragrance != null) {
                    PerfumeDetailScreen(
                        viewModel = viewModel,
                        fragrance = fragrance,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToLayeringStudio = {
                            navController.navigate(AppRoutes.LAYER)
                        },
                        onPartnerClick = { partner ->
                            viewModel.selectFragrances(fragrance, partner)
                            navController.navigate(AppRoutes.LAYER)
                        },
                        onExploreHeritage = {
                            navController.navigate(AppRoutes.HERITAGE)
                        }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(AppRoutes.CHORD_RESULT) {
                val result = viewModel.activeChordDetail.collectAsState().value
                if (result != null) {
                    ChordResultScreen(
                        viewModel = viewModel,
                        result = result,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(AppRoutes.HERITAGE) {
                HeritageScreen(
                    onBackClick = { navController.popBackStack() },
                    onExploreAttarsClick = {
                        viewModel.setSelectedOriginFilter("Attars & Oils")
                        navController.navigate(AppRoutes.DISCOVER)
                    }
                )
            }
        }
    }
}

@Composable
fun OlfactoryBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianBlack)
            .navigationBarsPadding()
    ) {
        // Subtle gold divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            GoldPrimary.copy(alpha = 0.4f),
                            GoldBright.copy(alpha = 0.6f),
                            GoldPrimary.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val isSignatureLayer = item.route == AppRoutes.LAYER
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onNavigate(item.route) }
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isSignatureLayer) {
                        // Elevated circular golden alembic icon for signature layering capability
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        Brush.linearGradient(listOf(GoldPrimary, GoldBright))
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                ObsidianElevated,
                                                ObsidianCard
                                            )
                                        )
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) GoldBright else GoldPrimary.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) ObsidianBlack else GoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.15f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) GoldBright else ParchmentMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = if (isSelected || isSignatureLayer) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) GoldBright else if (isSignatureLayer) GoldPrimary else ParchmentFaint
                    )
                }
            }
        }
    }
}
