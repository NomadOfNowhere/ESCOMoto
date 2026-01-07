package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository
) : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    var motorcycles by mutableStateOf<List<Motorcycle>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onTabSelected(index: Int) {
        selectedTab = index
    }

    // Función para cargar las motos
    fun loadMotorcycles(ownerId: String) {
        Log.d("DEBUG_MOTO", "Buscando motos para el ID: $ownerId")
        viewModelScope.launch {
            isLoading = true
            motorcycleRepository.getByOwner(ownerId).onSuccess { list ->
                motorcycles = list

                // ITERACIÓN PARA DEBUG:
                if (list.isEmpty()) {
                    Log.d("DEBUG_MOTO", "La lista llegó vacía de Firebase para el ID: $ownerId")
                } else {
                    list.forEach { moto ->
                        moto.debug("XD")
                    }
                }
            }.onFailure { error ->
                Log.d("DEBUG_MOTO", "Error en el repositorio: ${error.message}")
            }
            isLoading = false
        }
    }

//    // Función para agregar una moto
//    fun addMotorcycle(moto: Motorcycle, imageUri: Uri) {
//        // Límite de 3 motos por usuario
//        if(motorcycles.size >= 3) return
//
//        viewModelScope.launch {
//            isLoading = true
//            motorcycleRepository.uploadImage(imageUri).onSuccess { url ->
//                Log.d("DEBUG_MOTO", "Imagen subida. URL: $url")
//                val motoCopy = moto.copy(imageUrl = url)
//
//                motorcycleRepository.add(motoCopy).onSuccess { newMoto ->
//                    motorcycles = motorcycles + newMoto
//                    Log.d("DEBUG_MOTO", "Moto guardada exitosamente con foto")
//                    isLoading = false
//                }.onFailure { e->
//                    Log.e("DEBUG_MOTO", "Error al guardar en Firestore", e)
//                    isLoading = false
//                }
//            }.onFailure { e ->
//                Log.e("DEBUG_MOTO", "Falló la subida de la imagen. Cancelando operación.", e)
//                isLoading = false
//            }
//        }
//    }

    // Función para agregar una moto
    fun addMotorcycle(moto: Motorcycle, imageUri: Uri) {
        // Límite de 3 motos por usuario
        if(motorcycles.size >= 3) return

        viewModelScope.launch {
            isLoading = true
            // Subir moto a la BD
            motorcycleRepository.add(moto).onSuccess { newMoto ->
                motorcycles = motorcycles + newMoto
                Log.d("DEBUG_MOTO", "Moto guardada exitosamente")
                isLoading = false

                // Subir foto al storage
                motorcycleRepository.uploadImage(imageUri).onSuccess { url ->
                    Log.d("DEBUG_MOTO", "Imagen subida: $url")

                    // Actualizar SOLO el campo de la URL en BD
                    motorcycleRepository.updateImageUrl(newMoto.id, url).onSuccess {

//                        // Actualizar UI localmente para que aparezca la foto
//                        // (Esto hace que la imagen aparezca de repente en la tarjeta existente)
                        motorcycles = motorcycles.map { existingMoto ->
                            if (existingMoto.id == newMoto.id) {
                                existingMoto.copy(imageUrl = url) // Parcheamos solo la URL local
                            } else {
                                existingMoto
                            }
                        }
                        Log.d("DEBUG_MOTO", "Foto sincronizada en BD y UI")
                    }.onFailure { e ->
                        Log.e("DEBUG_MOTO", "EERRORCINI", e)
                    }
                }

            }.onFailure { e->
                Log.e("DEBUG_MOTO", "Error al guardar en Firestore", e)
                isLoading = false
            }
        }
    }
}