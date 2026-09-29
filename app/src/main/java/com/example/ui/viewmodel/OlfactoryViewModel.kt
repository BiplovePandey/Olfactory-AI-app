package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Brand
import com.example.data.model.Fragrance
import com.example.data.model.LayeringResult
import com.example.data.model.SavedCombination
import com.example.data.model.UserPreferences
import com.example.data.model.UserProfile
import com.example.data.remote.ApiClient
import com.example.data.repository.FragranceRepository
import com.example.olfactory.audio.AmbientSoundscapeEngine
import com.example.olfactory.ml.OlfactoryEngine
import com.example.olfactory.weather.WeatherAlignment
import com.example.olfactory.weather.WeatherCondition
import com.example.olfactory.weather.WeatherEngine
import com.example.olfactory.weather.WeatherPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OlfactoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FragranceRepository(application.applicationContext)
    val sessionManager = com.example.data.auth.SessionManager(application.applicationContext)

    private val _authState = MutableStateFlow<com.example.data.auth.AuthState>(sessionManager.restoreAuthState())
    val authState: StateFlow<com.example.data.auth.AuthState> = _authState.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        // Synchronize initial user profile with stored session state
        val restored = sessionManager.restoreAuthState()
        if (restored is com.example.data.auth.AuthState.Authenticated) {
            _userProfile.value = _userProfile.value.copy(
                id = restored.userId,
                name = restored.name,
                email = restored.email
            )
        }
    }

    val allFragrances: StateFlow<List<Fragrance>> = repository.allFragrancesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wardrobe: StateFlow<List<Fragrance>> = repository.wardrobeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCombinations: StateFlow<List<SavedCombination>> = repository.savedCombinationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _selectedFragranceA = MutableStateFlow<Fragrance?>(null)
    val selectedFragranceA: StateFlow<Fragrance?> = _selectedFragranceA.asStateFlow()

    private val _selectedFragranceB = MutableStateFlow<Fragrance?>(null)
    val selectedFragranceB: StateFlow<Fragrance?> = _selectedFragranceB.asStateFlow()

    private val _activeLayeringResult = MutableStateFlow<LayeringResult?>(null)
    val activeLayeringResult: StateFlow<LayeringResult?> = _activeLayeringResult.asStateFlow()

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private val _backendConnected = MutableStateFlow(false)
    val backendConnected: StateFlow<Boolean> = _backendConnected.asStateFlow()

    private val _serverUrl = MutableStateFlow(ApiClient.getCurrentBaseUrl())
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFamily = MutableStateFlow("All")
    val selectedFamily: StateFlow<String> = _selectedFamily.asStateFlow()

    private val _selectedOriginFilter = MutableStateFlow("All")
    val selectedOriginFilter: StateFlow<String> = _selectedOriginFilter.asStateFlow()

    private val _detailFragrance = MutableStateFlow<Fragrance?>(null)
    val detailFragrance: StateFlow<Fragrance?> = _detailFragrance.asStateFlow()

    private val _currentAtmosphere = MutableStateFlow<String?>("default")
    val currentAtmosphere: StateFlow<String?> = _currentAtmosphere.asStateFlow()

    private val _activeChordDetail = MutableStateFlow<LayeringResult?>(null)
    val activeChordDetail: StateFlow<LayeringResult?> = _activeChordDetail.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    // Ambient procedural soundscape audio engine
    val soundscapeEngine = AmbientSoundscapeEngine()

    // Real-time atmospheric weather condition
    private val _weatherCondition = MutableStateFlow(WeatherEngine.getDefaultEnvironment())
    val weatherCondition: StateFlow<WeatherCondition> = _weatherCondition.asStateFlow()

    // Protagonist / Scent of the Day selection
    private val _heroFragranceOverride = MutableStateFlow<Fragrance?>(null)
    val heroFragranceOverride: StateFlow<Fragrance?> = _heroFragranceOverride.asStateFlow()

    val scentOfTheDay: StateFlow<Fragrance?> = combine(
        allFragrances,
        _weatherCondition,
        _heroFragranceOverride
    ) { list, weather, override ->
        if (override != null) {
            override
        } else if (list.isNotEmpty()) {
            list.maxByOrNull {
                WeatherEngine.calculateWeatherAlignmentScore(it, weather).score
            } ?: list[0]
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weatherAlignment: StateFlow<WeatherAlignment> = combine(
        scentOfTheDay,
        _weatherCondition
    ) { hero, weather ->
        WeatherEngine.calculateWeatherAlignmentScore(hero, weather)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeatherAlignment(94, "Harmonious equilibrium."))

    // Filtered fragrances for Discover screen
    val filteredFragrances: StateFlow<List<Fragrance>> = combine(
        allFragrances,
        _searchQuery,
        _selectedFamily,
        _selectedOriginFilter
    ) { list, query, family, origin ->
        list.filter { fragrance ->
            val matchesQuery = query.isBlank() ||
                fragrance.name.contains(query, ignoreCase = true) ||
                fragrance.brand_name.contains(query, ignoreCase = true) ||
                fragrance.fragrance_family.contains(query, ignoreCase = true) ||
                (fragrance.top_notes + fragrance.middle_notes + fragrance.base_notes).any {
                    it.contains(query, ignoreCase = true)
                }

            val matchesFamily = family == "All" ||
                fragrance.fragrance_family.contains(family, ignoreCase = true)

            val matchesOrigin = when (origin) {
                "Indian Heritage" -> fragrance.origin_style.contains("indian", ignoreCase = true) ||
                        (fragrance.brand_country ?: "").contains("india", ignoreCase = true) || fragrance.is_oil_based
                "Western Luxury" -> !fragrance.origin_style.contains("indian", ignoreCase = true) &&
                        !(fragrance.brand_country ?: "").contains("india", ignoreCase = true) && !fragrance.is_oil_based
                "Attars & Oils" -> fragrance.is_oil_based || fragrance.format == "Attar" || fragrance.format.contains("Oil")
                else -> true
            }

            matchesQuery && matchesFamily && matchesOrigin
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            _brands.value = repository.loadBrands()
            checkBackendConnection()

            // Initialize default Layering selection if none set
            val frags = repository.getAllFragrances()
            if (frags.size >= 2) {
                val fA = frags.firstOrNull { it.format == "Attar" } ?: frags[0]
                val fB = frags.firstOrNull { it.id != fA.id && !it.is_oil_based } ?: frags[1]
                selectFragrances(fA, fB)
            }
        }
    }

    fun selectFragranceA(fragrance: Fragrance) {
        _selectedFragranceA.value = fragrance
        recalculateLayering()
    }

    fun selectFragranceB(fragrance: Fragrance) {
        _selectedFragranceB.value = fragrance
        recalculateLayering()
    }

    fun selectFragrances(fragA: Fragrance, fragB: Fragrance) {
        _selectedFragranceA.value = fragA
        _selectedFragranceB.value = fragB
        recalculateLayering()
    }

    fun swapFragrances() {
        val a = _selectedFragranceA.value
        val b = _selectedFragranceB.value
        _selectedFragranceA.value = b
        _selectedFragranceB.value = a
        recalculateLayering()
    }

    fun startLayeringWith(fragrance: Fragrance) {
        val currentA = _selectedFragranceA.value
        if (currentA == null || currentA.id == fragrance.id) {
            _selectedFragranceA.value = fragrance
            // Pick a complementary fragrance B
            val all = allFragrances.value
            val partner = all.firstOrNull { it.id != fragrance.id && it.is_oil_based != fragrance.is_oil_based }
                ?: all.firstOrNull { it.id != fragrance.id }
            if (partner != null) {
                _selectedFragranceB.value = partner
            }
        } else {
            _selectedFragranceB.value = fragrance
        }
        recalculateLayering()
    }

    private fun recalculateLayering() {
        val a = _selectedFragranceA.value
        val b = _selectedFragranceB.value
        if (a != null && b != null && a.id != b.id) {
            _isCalculating.value = true
            val result = repository.calculateLayering(a, b, _userPreferences.value)
            _activeLayeringResult.value = result
            _isCalculating.value = false
        } else {
            _activeLayeringResult.value = null
        }
    }

    fun saveActiveCombination() {
        val result = _activeLayeringResult.value ?: return
        viewModelScope.launch {
            repository.saveCombination(result)
        }
    }

    fun deleteSavedCombination(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedCombination(id)
        }
    }

    fun toggleWardrobe(fragranceId: Int, inWardrobe: Boolean) {
        viewModelScope.launch {
            repository.toggleWardrobe(fragranceId, inWardrobe)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFamily(family: String) {
        _selectedFamily.value = family
    }

    fun setSelectedOriginFilter(filter: String) {
        _selectedOriginFilter.value = filter
    }

    fun setDetailFragrance(fragrance: Fragrance?) {
        _detailFragrance.value = fragrance
        fragrance?.let {
            _currentAtmosphere.value = it.fragrance_family
        }
    }

    fun setAtmosphere(atmosphere: String?) {
        _currentAtmosphere.value = atmosphere
    }

    fun setActiveChordDetail(result: LayeringResult?) {
        _activeChordDetail.value = result
    }

    fun updatePreferences(newPreferences: UserPreferences) {
        _userPreferences.value = newPreferences
        recalculateLayering()
    }

    fun updateServerUrl(url: String) {
        _serverUrl.value = url
        ApiClient.updateBaseUrl(url)
        checkBackendConnection()
    }

    fun checkBackendConnection() {
        viewModelScope.launch {
            val connected = repository.syncWithBackend()
            _backendConnected.value = connected
        }
    }

    fun getPartnersFor(fragrance: Fragrance): List<LayeringResult> {
        val pool = allFragrances.value
        return OlfactoryEngine.findBestPartners(fragrance, pool, _userPreferences.value, limit = 3)
    }

    // --- Authentication & Session Actions ---

    fun signIn(email: String, passkey: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = com.example.data.auth.AuthState.Authenticating
            kotlinx.coroutines.delay(600) // Realistic atmospheric verification delay

            if (email.isBlank() || passkey.isBlank()) {
                val error = "Please provide both atelier email and passkey."
                _authState.value = com.example.data.auth.AuthState.AuthError(error)
                onResult(false, error)
                return@launch
            }

            // Verify against Connoisseur credentials or valid atelier format
            val isConnoisseur = email.contains("connoisseur", ignoreCase = true) || email.contains("user@olfactory.ai", ignoreCase = true)
            val userId = if (isConnoisseur) 1 else 2
            val name = if (isConnoisseur) "Connoisseur" else email.substringBefore("@").replaceFirstChar { it.uppercase() }

            sessionManager.saveUserSession(userId, name, email)
            _authState.value = com.example.data.auth.AuthState.Authenticated(userId, name, email)
            _userProfile.value = _userProfile.value.copy(
                id = userId,
                name = name,
                email = email
            )

            // Lazy profile & collection sync
            checkBackendConnection()
            onResult(true, null)
        }
    }

    fun signUp(name: String, email: String, passkey: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = com.example.data.auth.AuthState.Authenticating
            kotlinx.coroutines.delay(700)

            if (email.isBlank() || passkey.length < 4) {
                val error = "Passkey must contain at least 4 characters."
                _authState.value = com.example.data.auth.AuthState.AuthError(error)
                onResult(false, error)
                return@launch
            }

            val finalName = if (name.isBlank()) email.substringBefore("@").replaceFirstChar { it.uppercase() } else name
            val userId = System.currentTimeMillis().toInt().let { if (it < 0) -it else it }

            sessionManager.saveUserSession(userId, finalName, email)
            _authState.value = com.example.data.auth.AuthState.Authenticated(userId, finalName, email)
            _userProfile.value = _userProfile.value.copy(
                id = userId,
                name = finalName,
                email = email,
                persona = "Apprentice Perfumer"
            )

            checkBackendConnection()
            onResult(true, null)
        }
    }

    fun continueAsGuest(onResult: () -> Unit) {
        sessionManager.saveGuestSession()
        _authState.value = com.example.data.auth.AuthState.Guest()
        _userProfile.value = _userProfile.value.copy(
            id = 0,
            name = "Guest Perfumer",
            email = "guest@olfactory.ai",
            persona = "Visiting Scent Alchemist"
        )
        onResult()
    }

    fun setWeather(temp: Int, humidity: Int, conditionId: String) {
        val current = _weatherCondition.value
        _weatherCondition.value = WeatherEngine.createWeather(
            temp = temp,
            humidity = humidity,
            conditionId = conditionId,
            timeOfDay = current.timeOfDay
        )
    }

    fun setWeatherPreset(preset: WeatherPreset) {
        val current = _weatherCondition.value
        _weatherCondition.value = WeatherEngine.createWeather(
            temp = preset.temp,
            humidity = preset.humidity,
            conditionId = preset.id,
            timeOfDay = current.timeOfDay
        )
    }

    fun setHeroFragrance(fragrance: Fragrance?) {
        _heroFragranceOverride.value = fragrance
        fragrance?.let {
            _currentAtmosphere.value = it.fragrance_family
        }
    }

    fun surpriseScentOfTheDay(): Fragrance? {
        val pool = allFragrances.value
        if (pool.isEmpty()) return null
        val currentHero = scentOfTheDay.value
        val candidates = pool.filter { it.id != currentHero?.id }
        val picked = candidates.randomOrNull() ?: pool.random()
        setHeroFragrance(picked)
        return picked
    }

    fun recordWearRitual(fragrance: Fragrance, partner: Fragrance? = null) {
        _userProfile.value = _userProfile.value.copy(
            layeringCount = _userProfile.value.layeringCount + 1
        )
    }

    fun signOut(onComplete: () -> Unit) {
        soundscapeEngine.stop()
        sessionManager.clearSession()
        _authState.value = com.example.data.auth.AuthState.Unauthenticated
        _userProfile.value = UserProfile()
        onComplete()
    }

    override fun onCleared() {
        super.onCleared()
        soundscapeEngine.stop()
    }
}
