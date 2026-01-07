package com.ipn.escomoto.ui.mainmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository
) : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    var motorcycles by mutableStateOf<List<Motorcycle>>(emptyList())

    fun onTabSelected(index: Int) {
        selectedTab = index
    }
}


//
//    var motorcycles by mutableStateOf<List<Motorcycle>>(emptyList())
//        private set
//
//    var isLoading by mutableStateOf(false)
//        private set
//
//    // Función para cargar las motos (One-shot con suspend)
//    fun loadMotorcycles(ownerId: String) {
//        viewModelScope.launch {
//            isLoading = true
//            motorcycleRepository.getByOwner(ownerId).onSuccess { list ->
//                motorcycles = list
//            }.onFailure { /* Manejar error */ }
//            isLoading = false
//        }
//    }
//
//    // Función para agregar una moto
//    fun addMotorcycle(moto: Motorcycle) {
//        if (motorcycles.size < 3) {
//            viewModelScope.launch {
//                motorcycleRepository.add(moto).onSuccess { nuevaMoto ->
//                    // Actualizamos la lista local sumando la nueva
//                    motorcycles = motorcycles + nuevaMoto
//                }
//            }
//        }
//    }
//}