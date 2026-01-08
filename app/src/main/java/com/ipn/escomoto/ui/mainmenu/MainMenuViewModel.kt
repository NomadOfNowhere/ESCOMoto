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
import com.ipn.escomoto.domain.repository.AccessRepository
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository,
//    private val accessRepository: AccessRepository
) : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    var motorcycles by mutableStateOf<List<Motorcycle>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isUserInside by mutableStateOf(false)
        private set

    var currentMotoIdInside by mutableStateOf<String?>(null)
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

    fun updateMotorcycle(motoOriginal: Motorcycle, newBrand: String, newModel: String, newPlate: String, newImageUri: Uri?) {
        viewModelScope.launch {
            isLoading = true

            // 1. Objeto con los nuevos datos de texto
            // Mantenemos la imagen original por ahora
            val motoActualizada = motoOriginal.copy(
                brand = newBrand,
                model = newModel,
                licensePlate = newPlate
            )

            // 2. ¿El usuario seleccionó una NUEVA imagen?
            if (newImageUri != null) {
                // CASO A: Actualizar con nueva foto
                motorcycleRepository.uploadImage(newImageUri).onSuccess { url ->
                    val motoFinal = motoActualizada.copy(imageUrl = url)

                    // Actualizamos todo en BD
                    motorcycleRepository.update(motoFinal).onSuccess {
                        updateLocalList(motoFinal)
                        isLoading = false
                        Log.d("DEBUG_MOTO", "Moto actualizada con nueva foto")
                    }
                }.onFailure {
                    isLoading = false
                    Log.e("DEBUG_MOTO", "Error subiendo nueva foto", it)
                }
            } else {
                // CASO B: Solo actualizar textos (Mantiene la URL vieja)
                motorcycleRepository.update(motoActualizada).onSuccess {
                    updateLocalList(motoActualizada)
                    isLoading = false
                    Log.d("DEBUG_MOTO", "Moto actualizada (solo texto)")
                }.onFailure {
                    isLoading = false
                }
            }
        }
    }

    fun deleteMotorcycle(motoId: String) {
        viewModelScope.launch {
            isLoading = true
            motorcycleRepository.remove(motoId).onSuccess {
                // Actualizamos la lista local filtrando la moto borrada
                motorcycles = motorcycles.filter { it.id != motoId }
                Log.d("DEBUG_MOTO", "Moto eliminada correctamente")
                isLoading = false
            }.onFailure { e ->
                Log.e("DEBUG_MOTO", "Error al eliminar", e)
                isLoading = false
            }
        }
    }

    // Función auxiliar para refrescar la lista localmente
    private fun updateLocalList(updatedMoto: Motorcycle) {
        motorcycles = motorcycles.map {
            if (it.id == updatedMoto.id) updatedMoto else it
        }
    }
}