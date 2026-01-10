package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import android.net.http.UrlRequest.Status
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.AccessRepository
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import com.ipn.escomoto.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.AccessType
import com.ipn.escomoto.domain.model.StatusType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.collectAsState
import com.ipn.escomoto.domain.model.HistoryFilter

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository,
    private val accessRepository: AccessRepository
) : ViewModel() {
    // Estados de la UI
    var selectedTab by mutableIntStateOf(0)
        private set
    var motorcycles by mutableStateOf<List<Motorcycle>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isUserInside by mutableStateOf(false)
        private set
    var updatingMotorcycleId by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var showMotoSelector by mutableStateOf(false)
        private set
    var currentAccessRequest by mutableStateOf<AccessRequest?>(null)
        private set
    var historyLogs by mutableStateOf<List<AccessRequest>>(emptyList())
        private set

    // Flujo para supervisor
    val pendingRequests = accessRepository.getPendingRequests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private var currentEntryMotorcycleId: String? = null

    // Lógica de estados
    fun onTabSelected(index: Int) {
        selectedTab = index
    }

    fun onCheckInTap() {
        if (isUserInside) {
            errorMessage = "Ya estás dentro. Debes hacer Check-out primero."
            return
        }
        prepareRequest()
    }

    fun onMotoSelectedForCheckIn(moto: Motorcycle) {
        showMotoSelector = false
        createRequest(moto, AccessType.ENTRY)
    }

    private fun prepareRequest() {
        if (motorcycles.isEmpty()) {
            errorMessage = "Registra una moto para ingresar al estacionamiento"
            return
        }

        if (motorcycles.size == 1) {
            createRequest(motorcycles[0], AccessType.ENTRY)
        }
        else {
            showMotoSelector = true
        }
    }

    fun onCheckOutTap() {
        if (!isUserInside) {
            errorMessage = "No has registrado Check-in."
            return
        }

        val motoInside = motorcycles.find { it.id == currentEntryMotorcycleId }
        if (motoInside != null) {
            createRequest(motoInside, AccessType.EXIT)
        }
        else {
            errorMessage = "No se encuentra la moto registrada en la entrada."
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun dismissMotoSelector() {
        showMotoSelector = false
    }

    // Check-in
    private fun createRequest(moto: Motorcycle, actionType: AccessType) {

        viewModelScope.launch {
            isLoading = true
            try {
                val request = AccessRequest(
                    userId = moto.ownerId,
                    userName = moto.ownerName,
                    motorcycleId = moto.id,
                    motorcyclePlate = moto.licensePlate,
                    motorcycleImgUrl = moto.imageUrl,
                    type = actionType
                )

                accessRepository.createAccessRequest(request)
                    .onSuccess { reqId ->
                        listenToMyRequest(reqId)
                    }
                    .onFailure { e ->
                        errorMessage = "Error al solicitar acceso: ${e.message}"
                    }
            } catch (e: Exception) {
                errorMessage = "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun cancelCurrentRequest() {
        val currentRequest = currentAccessRequest ?: return // Si no hay solicitud, no hacemos nada

        viewModelScope.launch {
            try {
                // Enviamos la actualización a Firebase
                accessRepository.updateRequestStatus(currentRequest.id, StatusType.CANCELLED)

                Log.d("CHECK_IN", "Solicitud cancelada por el usuario")
            } catch (e: Exception) {
                errorMessage = "Error al cancelar: ${e.message}"
            }
        }
    }

    // Escuchar cambios
    private fun listenToMyRequest(requestId: String) {
        viewModelScope.launch {
            accessRepository.getAccessRequestFlow(requestId).collect { request ->
                currentAccessRequest = request

                // SI SE APRUEBA: CAMBIAMOS EL ESTADO DEL USUARIO
                if (request.status == StatusType.APPROVED) {
                    // Opcional: Vibrar, sonido, notificación
                    if (request.type == AccessType.ENTRY) {
                        isUserInside = true
                        currentEntryMotorcycleId = request.motorcycleId  // Guardamos moto con la que entró
                    }
                    else if (request.type == AccessType.EXIT) {
                        isUserInside = false
                        currentEntryMotorcycleId = null     // salida, limpiamos estado
                    }
                }
            }
        }
    }

    fun clearCurrentRequest() {
        currentAccessRequest = null
    }

    // Funciones de supervisor
    fun approveRequest(request: AccessRequest) {
        viewModelScope.launch {
            accessRepository.updateRequestStatus(request.id, StatusType.APPROVED)
        }
    }

    fun rejectRequest(request: AccessRequest) {
        viewModelScope.launch {
            accessRepository.updateRequestStatus(request.id, StatusType.REJECTED)
        }
    }

    // CRUD Motorcycles
    // Función para cargar las motos
    fun loadMotorcycles(ownerId: String) {
        Log.d("DEBUG_MOTO", "Buscando motos para el ID: $ownerId")
        viewModelScope.launch {
            isLoading = true
            motorcycleRepository.getByOwner(ownerId).onSuccess { list ->
                motorcycles = list

            }.onFailure { error ->
                Log.d("DEBUG_MOTO", "Error en el repositorio: ${error.message}")
            }

            // Cargar último acceso para saber si está dentro y CON QUÉ MOTO
            accessRepository.getLastAccess(ownerId).onSuccess { lastLog ->
                // Verificamos que sea Entry y que esté Aprobado
                if (lastLog != null && lastLog.type == AccessType.ENTRY && lastLog.status == StatusType.APPROVED) {
                    isUserInside = true
                    currentEntryMotorcycleId = lastLog.motorcycleId // Recuperamos el ID
                } else {
                    isUserInside = false
                    currentEntryMotorcycleId = null
                }
            }

            // Buscamos si hay solicitudes pendientes
            accessRepository.getPendingRequestByUserId(ownerId).onSuccess { pendingRequest ->
                if (pendingRequest != null) {
                    Log.d("CLEANUP", "Se encontró una solicitud huérfana (ID: ${pendingRequest.id}). Cancelando...")

                    // La cancelamos en Firebase para limpiar la base de datos
                    accessRepository.updateRequestStatus(pendingRequest.id, StatusType.CANCELLED)

                    // Avisamos al usuario
                    errorMessage = "Se canceló una solicitud pendiente anterior."
                }
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
            if(newImageUri != null) {
                updatingMotorcycleId = motoOriginal.id
            }

            val newMoto = motoOriginal.copy(
                brand = newBrand,
                model = newModel,
                licensePlate = newPlate
            )

            // Actualizamos elementos (excepto URL vieja)
            motorcycleRepository.update(newMoto, newImageUri != null).onSuccess {
                updateLocalList(newMoto)

                // Si el usuario seleccionó una nueva imagen, actualizamos
                if(newImageUri != null) {
                    motorcycleRepository.uploadImage(newImageUri).onSuccess { url ->
                        motorcycleRepository.updateImageUrl(newMoto.id, url).onSuccess {
                            motorcycles = motorcycles.map { existingMoto ->
                                if (existingMoto.id == newMoto.id) {
                                    existingMoto.copy(imageUrl = url) // Parcheamos solo la URL local
                                } else {
                                    existingMoto
                                }
                            }
                            updatingMotorcycleId = null
                        }
                    }.onFailure { updatingMotorcycleId = null }
                }
            }.onFailure {  }
        }
    }

    fun deleteMotorcycle(motoId: String) {
        if (isUserInside && motoId == currentEntryMotorcycleId) {
            errorMessage = "No puedes eliminar la motocicleta mientras está dentro del estacionamiento. Realiza Check-out primero."
            return
        }

        viewModelScope.launch {
            updatingMotorcycleId = motoId
            motorcycleRepository.remove(motoId).onSuccess {
                // Actualizamos la lista local filtrando la moto borrada
                motorcycles = motorcycles.filter { it.id != motoId }
                Log.d("DEBUG_MOTO", "Moto eliminada correctamente")
                updatingMotorcycleId = null
            }.onFailure { e ->
                Log.e("DEBUG_MOTO", "Error al eliminar", e)
                updatingMotorcycleId = null
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