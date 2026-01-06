package com.ipn.escomoto.ui.mainmenu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.ui.mainmenu.components.BottomNavigationBar

@Composable
fun MainMenuScreen(
    user: User?,
    onLogout: () -> Unit,
    viewModel: MainMenuViewModel = viewModel()
) {
    val selectedTab = viewModel.selectedTab

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) },
                userType = user?.userType ?: "Visitante"
            )
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding)

        // Animación de transición entre tabs
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { if (targetState > initialState) 300 else -300 }
                        ) togetherWith
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { if (targetState > initialState) -300 else 300 }
                        )
            },
            label = "tab_animation"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    name = user?.name ?: "Usuario",
                    escomId = user?.escomId ?: "",
                    userType = user?.userType ?: "Visitante",
                    modifier = contentModifier
                )
                1 -> ActivitiesScreen(user?.userType ?: "Visitante", contentModifier)
                2 -> ServicesScreen(user?.userType ?: "Visitante", contentModifier)
                3 -> ProfileScreen(
                    name = user?.name ?: "Usuario",
                    escomId = user?.escomId ?: "",
                    modifier = contentModifier,
                    onLogout = onLogout
                )
            }
        }
    }
}
