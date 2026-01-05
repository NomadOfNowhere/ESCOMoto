package com.ipn.escomoto.ui.mainmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ipn.escomoto.domain.model.User

class MainMenuViewModel : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    fun onTabSelected(index: Int) {
        selectedTab = index
    }
}