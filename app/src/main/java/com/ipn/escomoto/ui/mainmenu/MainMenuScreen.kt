package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.ui.mainmenu.components.BottomNavigationBar
import com.ipn.escomoto.ui.mainmenu.components.SelectMotorcycleDialog

@Composable
fun MainMenuScreen(
    user: User?,
    onLogout: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    val selectedTab = viewModel.selectedTab
    var showMotoSelection by remember { mutableStateOf(false) }

    if (showMotoSelection) {
        SelectMotorcycleDialog(
            motorcycles = viewModel.motorcycles,
            onDismiss = { showMotoSelection = false },
            onMotoSelected = { moto ->
//                viewModel.performCheckIn(moto, user!!)
                showMotoSelection = false
            }
        )
    }

    LaunchedEffect(user?.id) {
        user?.id?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.loadMotorcycles(id)
            }
        }
    }

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
                    modifier = contentModifier,
                    motorcycles = viewModel.motorcycles,
                    isLoading = viewModel.isLoading,
                    // --- NUEVOS PARÁMETROS ---
                    isUserInside = viewModel.isUserInside,
                    onCheckInTap = {
                        if (viewModel.motorcycles.isNotEmpty()) {
                            showMotoSelection = true
                        }
                    },
                    onCheckOutTap = {
//                        viewModel.performCheckOut(user!!)
                    },
                    // -------------------------
                    onRefresh = { viewModel.loadMotorcycles(user?.id ?: "") },
                    onAddMotorcycle = { brand, model, plate, imageUri ->
                        viewModel.addMotorcycle(
                            Motorcycle(
                                id = "",
                                ownerId = user?.id ?: "",
                                ownerName = user?.name ?: "",
                                brand = brand,
                                model = model,
                                licensePlate = plate,
                                imageUrl = ""
                            ), imageUri = imageUri
                        )
                    }
                )
                /*
                0 -> HomeScreen(
                    name = user?.name ?: "Usuario",
                    escomId = user?.escomId ?: "",
                    userType = user?.userType ?: "Visitante",
                    modifier = contentModifier,
                    motorcycles = viewModel.motorcycles,
                    isLoading = viewModel.isLoading,
                    onRefresh = { viewModel.loadMotorcycles(user?.id ?: "") },
                    onAddMotorcycle = { brand, model, plate, imageUri ->
                        viewModel.addMotorcycle(
                            Motorcycle(
                                id = "",
                                ownerId = user?.id ?: "",
                                ownerName = user?.name ?: "",
                                brand = brand,
                                model = model,
                                licensePlate = plate,
                                imageUrl = ""
                            ), imageUri = imageUri
                        )
                    }
                ) */
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



/*

package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import android.util.Log
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.ui.mainmenu.components.BottomNavigationBar

@Composable
fun MainMenuScreen(
    user: User?,
    onLogout: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    val selectedTab = viewModel.selectedTab

    LaunchedEffect(user?.id) {
        user?.id?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.loadMotorcycles(id)
            }
        }
    }

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
                    modifier = contentModifier,
                    motorcycles = viewModel.motorcycles,
                    isLoading = viewModel.isLoading,
                    onRefresh = { viewModel.loadMotorcycles(user?.id ?: "") },
                    onAddMotorcycle = { brand, model, plate, imageUri ->
                        viewModel.addMotorcycle(
                            Motorcycle(
                                id = "",
                                ownerId = user?.id ?: "",
                                ownerName = user?.name ?: "",
                                brand = brand,
                                model = model,
                                licensePlate = plate,
                                imageUrl = ""
                            ), imageUri = imageUri
                        )
                    }
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


 */