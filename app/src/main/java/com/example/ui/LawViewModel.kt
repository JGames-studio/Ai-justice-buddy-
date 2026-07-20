package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.text.SimpleDateFormat
import java.util.Date

// --- Custom Data Models for Safety Map, Alerts, & Directory ---
data class LawFirmGps(
    val name: String,
    val rating: Double,
    val reviewsCount: Int,
    val distance: String,
    val specialty: String,
    val phone: String,
    val address: String,
    val verified: Boolean
)

data class CrimeAlert(
    val title: String,
    val severity: String, // "HIGH", "MEDIUM", "LOW"
    val area: String,
    val description: String,
    val timeAgo: String
)

data class PedophileAlert(
    val initials: String,
    val age: Int,
    val charge: String,
    val distance: String,
    val status: String // "Active Registry", "Supervised Parole"
)

class LawViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val repository = LawRepository(application)
    private var tts: TextToSpeech? = null
    
    // UI state trackers
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    // --- Core Data Flows ---
    val cases: StateFlow<List<CaseFile>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<CourtReminder>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highScores: StateFlow<List<QuizHighScore>> = repository.highScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val avatarConfig: StateFlow<AvatarConfig> = repository.avatarConfig
        .map { it ?: AvatarConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AvatarConfig())

    val alerts: StateFlow<List<LawChangeAlert>> = repository.lawAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Lawyer Directory Search Engine ---
    private val _lawyerSearchQuery = MutableStateFlow("")
    val lawyerSearchQuery = _lawyerSearchQuery.asStateFlow()

    private val _lawyerFirms = MutableStateFlow<List<LawyerFirm>>(emptyList())
    val lawyerFirms = _lawyerFirms.asStateFlow()

    // --- Law Books & Rights Search Engine ---
    private val _lawSearchQuery = MutableStateFlow("")
    val lawSearchQuery = _lawSearchQuery.asStateFlow()

    private val _selectedStateFilter = MutableStateFlow<String?>("All States")
    val selectedStateFilter = _selectedStateFilter.asStateFlow()

    val filteredLaws: StateFlow<List<LawBookItem>> = combine(
        _lawSearchQuery,
        _selectedStateFilter
    ) { query, stateFilter ->
        var list = repository.lawBooks
        if (stateFilter != null && stateFilter != "All States") {
            list = list.filter { it.category == "State" && it.stateName == stateFilter }
        }
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.fullText.contains(query, ignoreCase = true) ||
                        it.keyFacts.contains(query, ignoreCase = true) ||
                        it.loopholes.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.lawBooks)

    // --- GPS Location Analyzer & Weather/News States ---
    private var locationManager: LocationManager? = null

    private val _latitude = MutableStateFlow(34.0522)
    val latitude = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(-118.2437)
    val longitude = _longitude.asStateFlow()

    private val _gpsState = MutableStateFlow("Simulation (Los Angeles)")
    val gpsState = _gpsState.asStateFlow()

    private val _currentCity = MutableStateFlow("Los Angeles")
    val currentCity = _currentCity.asStateFlow()

    private val _currentState = MutableStateFlow("California")
    val currentState = _currentState.asStateFlow()

    private val _temperature = MutableStateFlow("72°F")
    val temperature = _temperature.asStateFlow()

    private val _weatherCondition = MutableStateFlow("Sunny & Calm")
    val weatherCondition = _weatherCondition.asStateFlow()

    val nearbyLawFirms = MutableStateFlow<List<LawFirmGps>>(emptyList())
    val localCrimeAlerts = MutableStateFlow<List<CrimeAlert>>(emptyList())
    val localPedophileAlerts = MutableStateFlow<List<PedophileAlert>>(emptyList())

    // --- Local Police Scanner Radio ---
    private val _scannerActive = MutableStateFlow(false)
    val scannerActive = _scannerActive.asStateFlow()

    private val _scannerChannelIndex = MutableStateFlow(0)
    val scannerChannelIndex = _scannerChannelIndex.asStateFlow()

    private val _scannerSquelch = MutableStateFlow(0.4f)
    val scannerSquelch = _scannerSquelch.asStateFlow()

    private val _scannerVolume = MutableStateFlow(0.7f)
    val scannerVolume = _scannerVolume.asStateFlow()

    private val _scannerTranscript = MutableStateFlow<List<String>>(emptyList())
    val scannerTranscript = _scannerTranscript.asStateFlow()

    val scannerChannels = listOf(
        "154.82 MHz - Municipal Dispatch",
        "460.12 MHz - County Sheriff Dispatch",
        "39.22 MHz - State Highway Patrol",
        "800.45 MHz - Fire & EMS Tactical"
    )

    // --- Satellite Radio Entertainment Widget ---
    private val _satelliteRadioActive = MutableStateFlow(false)
    val satelliteRadioActive = _satelliteRadioActive.asStateFlow()

    private val _satelliteChannelIndex = MutableStateFlow(0)
    val satelliteChannelIndex = _satelliteChannelIndex.asStateFlow()

    private val _satelliteVolume = MutableStateFlow(0.8f)
    val satelliteVolume = _satelliteVolume.asStateFlow()

    private val _isSatelliteFloating = MutableStateFlow(true)
    val isSatelliteFloating = _isSatelliteFloating.asStateFlow()

    val satelliteRadioChannels = listOf(
        "Channel 1 - JGames.studio Legal Radio",
        "Channel 2 - Gemini Law Review Talk",
        "Channel 3 - Retro Beats for Criminal Lawyers",
        "Channel 4 - Ambient Courtroom Focus Sounds"
    )

    // --- Subscriptions, Payments & Charity (Donation Cause) ---
    private val _activeSubscription = MutableStateFlow("Free Tier")
    val activeSubscription = _activeSubscription.asStateFlow()

    private val _isPaymentDialogVisible = MutableStateFlow(false)
    val isPaymentDialogVisible = _isPaymentDialogVisible.asStateFlow()

    private val _donationBalance = MutableStateFlow(5420.00)
    val donationBalance = _donationBalance.asStateFlow()

    private val _donationGoal = MutableStateFlow(10000.0)
    val donationGoal = _donationGoal.asStateFlow()

    private val _donationPercentage = MutableStateFlow(15)
    val donationPercentage = _donationPercentage.asStateFlow()

    // --- ZIP Export State ---
    private val _zipFileExists = MutableStateFlow(false)
    val zipFileExists = _zipFileExists.asStateFlow()

    private val _isCreatingZip = MutableStateFlow(false)
    val isCreatingZip = _isCreatingZip.asStateFlow()

    private val _zipCreationError = MutableStateFlow<String?>(null)
    val zipCreationError = _zipCreationError.asStateFlow()

    // --- App Security & PIN Confidentiality Lock ---
    private val _pinCode = MutableStateFlow<String?>(null) // null means PIN security disabled
    val pinCode = _pinCode.asStateFlow()

    private val _isAppUnlocked = MutableStateFlow(true)
    val isAppUnlocked = _isAppUnlocked.asStateFlow()

    private val _failedPinAttempts = MutableStateFlow(0)
    val failedPinAttempts = _failedPinAttempts.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled = _biometricEnabled.asStateFlow()

    // --- Admin Dashboard (Accounting Audit & Prompt Editor) ---
    private val _adminSystemPrompt = MutableStateFlow("You are an expert criminal and civil defense attorney. Provide structured, authoritative, actionable, and detailed legal feedback. Emphasize constitutional protections (such as Fourth Amendment, Fifth Amendment) or precise state civil codes.")
    val adminSystemPrompt = _adminSystemPrompt.asStateFlow()

    private val _totalAppDownloads = MutableStateFlow(12850)
    val totalAppDownloads = _totalAppDownloads.asStateFlow()

    private val _monthlyRevenue = MutableStateFlow(4210.0)
    val monthlyRevenue = _monthlyRevenue.asStateFlow()

    private val _serverCosts = MutableStateFlow(450.0)
    val serverCosts = _serverCosts.asStateFlow()

    private val _netProfit = MutableStateFlow(3760.0)
    val netProfit = _netProfit.asStateFlow()

    // --- Initializer Block ---
    init {
        // Initialize TTS
        tts = TextToSpeech(application, this)
        
        // Seed initial data and configure settings
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialData()
        }

        // Trigger lawyer lookup & seed dynamic GPS location tables
        searchLawyers("")
        updateLocalAlertsAndFirms("Los Angeles")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            _isTtsReady.value = true
        }
    }

    fun speak(text: String) {
        if (_isTtsReady.value) {
            tts?.stop()
            _isSpeaking.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AI_LAWYER_TTS")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        stopGpsUpdates()
    }

    // --- Lawyer Lookup Methods ---
    fun searchLawyers(query: String) {
        _lawyerSearchQuery.value = query
        viewModelScope.launch(Dispatchers.Default) {
            val results = repository.searchLawyers(query)
            _lawyerFirms.value = results
        }
    }

    fun searchLaws(query: String) {
        _lawSearchQuery.value = query
    }

    fun selectStateFilter(state: String?) {
        _selectedStateFilter.value = state
    }

    // --- GPS Location Analyzer Engine & Manual Override ---
    fun setLocation(lat: Double, lng: Double, city: String, state: String, isManual: Boolean = false) {
        _latitude.value = lat
        _longitude.value = lng
        _currentCity.value = city
        _currentState.value = state
        _gpsState.value = if (isManual) "Manual Override (Mock GPS)" else "Active Real-Time GPS"
        
        // Dynamic weather matching the geography
        _temperature.value = when (city.lowercase()) {
            "new york" -> "68°F"
            "miami" -> "84°F"
            "san francisco" -> "59°F"
            "sacramento" -> "75°F"
            "chicago" -> "64°F"
            else -> "72°F"
        }
        _weatherCondition.value = when (city.lowercase()) {
            "new york" -> "Overcast & Windy"
            "miami" -> "Humid & Showers"
            "san francisco" -> "Foggy & Breezy"
            "sacramento" -> "Warm & Dry"
            "chicago" -> "Mild & Cloudy"
            else -> "Sunny & Clear"
        }

        updateLocalAlertsAndFirms(city)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat = location.latitude
            val lng = location.longitude
            val city = when {
                lat in 33.9..34.2 && lng in -118.5..-118.1 -> "Los Angeles"
                lat in 37.6..37.9 && lng in -122.5..-122.3 -> "San Francisco"
                lat in 40.5..40.9 && lng in -74.1..-73.8 -> "New York"
                lat in 25.6..25.9 && lng in -80.3..-80.1 -> "Miami"
                else -> "Local Zone"
            }
            setLocation(lat, lng, city, "United States", isManual = false)
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun startGpsUpdates() {
        val app = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
                val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
                
                if (isGpsEnabled) {
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, locationListener)
                    _gpsState.value = "Active Real-Time GPS"
                } else if (isNetworkEnabled) {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10f, locationListener)
                    _gpsState.value = "Cell Tower GeoStream"
                } else {
                    _gpsState.value = "GPS Providers Disabled (Using High-Fidelity Simulation)"
                }
            } catch (e: SecurityException) {
                _gpsState.value = "Security Error: " + e.localizedMessage
            }
        } else {
            _gpsState.value = "Permission Denied (Using Simulation)"
        }
    }

    fun stopGpsUpdates() {
        try {
            locationManager?.removeUpdates(locationListener)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun updateLocalAlertsAndFirms(city: String) {
        val currentCityName = city.ifBlank { "Los Angeles" }
        nearbyLawFirms.value = listOf(
            LawFirmGps("$currentCityName Criminal Defense Guild", 4.9, 142, "0.8 mi", "Felonies, DUI, Misdemeanors", "(555) 911-3030", "101 Justice Blvd", true),
            LawFirmGps("Apex Constitutional Advocates", 4.8, 88, "1.4 mi", "Civil Rights, 4th Amendment", "(555) 444-2020", "500 Freedom Way", true),
            LawFirmGps("JGames Defense Associates", 4.9, 310, "2.1 mi", "Premium Litigation, Copyright Protection", "(555) 777-7777", "777 Creator Row", true),
            LawFirmGps("Liberty & Associates Lawyers", 4.6, 54, "3.5 mi", "Family Defense, General Practice", "(555) 123-4567", "12 Main St", false)
        )

        localCrimeAlerts.value = listOf(
            CrimeAlert("Grand Theft Auto Warning", "HIGH", currentCityName, "High report of luxury vehicle theft near transit zones. Keep doors locked.", "14 mins ago"),
            CrimeAlert("Armed Robbery Advisory", "HIGH", "Downtown District", "Suspect fled northbound on a bicycle. Stay vigilant.", "1 hour ago"),
            CrimeAlert("Trespassing Event", "MEDIUM", "Residential Sector", "Commercial alarm triggered, security sweep completed.", "4 hours ago"),
            CrimeAlert("Noise Resolution", "LOW", "East Boulevard", "Resolved by municipal police units. Quiet hours reinstated.", "8 hours ago")
        )

        localPedophileAlerts.value = listOf(
            PedophileAlert("R. D.", 42, "Registered Sex Offender (Level 3)", "0.3 mi", "Active Registry"),
            PedophileAlert("M. S.", 51, "Indecent Liberties with Minor", "0.9 mi", "Supervised Parole"),
            PedophileAlert("K. P.", 37, "Statutory Offense", "1.5 mi", "Active Registry")
        )
    }

    // --- Local Police Scanner Controls ---
    fun toggleScanner() {
        val isActive = !_scannerActive.value
        _scannerActive.value = isActive
        if (isActive) {
            _scannerTranscript.value = listOf("[SCANNER ON] Tuning frequencies ... squelch stabilized")
            startScannerFeed()
        } else {
            scannerJob?.cancel()
            _scannerTranscript.value = _scannerTranscript.value + "[SCANNER OFF] Connection terminated"
        }
    }

    fun setScannerChannel(index: Int) {
        _scannerChannelIndex.value = index
        _scannerTranscript.value = listOf("[TUNED] Active frequency: ${scannerChannels[index]}")
    }

    fun setScannerSquelch(valF: Float) {
        _scannerSquelch.value = valF
    }

    fun setScannerVolume(valF: Float) {
        _scannerVolume.value = valF
    }

    private var scannerJob: kotlinx.coroutines.Job? = null
    private fun startScannerFeed() {
        scannerJob?.cancel()
        scannerJob = viewModelScope.launch(Dispatchers.Default) {
            val phrases = listOf(
                "Dispatch to Unit 14: Report of a 10-33 on Grand Avenue. Respond Code 2.",
                "Unit 14: Roger, approaching grand intersection. Inform dispatcher we are on scene.",
                "County-Line: Alert level elevated on sector 2, vehicle matching description spotted.",
                "Medic 5: En route to residential medical assistance, ETA 5 minutes.",
                "Dispatch: Copy Unit 14, suspect on foot, wearing dark hood, moving West.",
                "Unit 9: 10-4, perimeter secured. Awaiting support.",
                "Dispatch: Local bulletins warn of reduced visibility, proceed with extreme caution.",
                "Unit 12: Suspicious individual checked out clean against registration ledger. Code 4.",
                "All units: Stay safe, keep communications brief on channel 2."
            )
            while (_scannerActive.value) {
                kotlinx.coroutines.delay(6000L)
                if (!_scannerActive.value) break
                val newPhrase = phrases.random()
                val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                _scannerTranscript.value = (_scannerTranscript.value.takeLast(40) + "[$timestamp] $newPhrase")
            }
        }
    }

    // --- Satellite Radio Entertainment Controls ---
    fun toggleSatelliteRadio() {
        _satelliteRadioActive.value = !_satelliteRadioActive.value
    }

    fun setSatelliteChannel(index: Int) {
        _satelliteChannelIndex.value = index
    }

    fun setSatelliteVolume(vol: Float) {
        _satelliteVolume.value = vol
    }

    fun setSatelliteFloating(isFloat: Boolean) {
        _isSatelliteFloating.value = isFloat
    }

    // --- Subscription, Payment, & Direct Charity Controls ---
    fun setSubscriptionTier(tier: String) {
        _activeSubscription.value = tier
        if (tier != "Free Tier") {
            val donationBoost = when (tier) {
                "Silver Counselor" -> 1.50
                "Gold Platinum Advocate" -> 3.75
                "Diamond Barrister" -> 7.50
                "Business Class Package" -> 15.00
                "Private Law Firm Suite" -> 45.00
                else -> 0.0
            }
            _donationBalance.value += donationBoost
            
            val price = when (tier) {
                "Silver Counselor" -> 9.99
                "Gold Platinum Advocate" -> 24.99
                "Diamond Barrister" -> 49.99
                "Business Class Package" -> 99.99
                "Private Law Firm Suite" -> 299.99
                else -> 0.0
            }
            _monthlyRevenue.value += price
            _netProfit.value = _monthlyRevenue.value - _serverCosts.value
        }
    }

    fun addDirectDonation(amount: Double) {
        _donationBalance.value += amount
    }

    fun setPaymentDialogVisible(visible: Boolean) {
        _isPaymentDialogVisible.value = visible
    }

    // --- Privacy Security PIN & Document Shredding ---
    fun setPinCode(pin: String?) {
        _pinCode.value = pin
        _isAppUnlocked.value = pin == null
        _failedPinAttempts.value = 0
    }

    fun verifyPinCode(input: String): Boolean {
        if (_pinCode.value == null) return true
        if (_pinCode.value == input) {
            _isAppUnlocked.value = true
            _failedPinAttempts.value = 0
            return true
        } else {
            _failedPinAttempts.value += 1
            if (_failedPinAttempts.value >= 5) {
                // Wipe sensitive data as standard security protocol
                _pinCode.value = null
                _isAppUnlocked.value = true
                _failedPinAttempts.value = 0
                shredSensitiveData()
            }
            return false
        }
    }

    fun lockApp() {
        if (_pinCode.value != null) {
            _isAppUnlocked.value = false
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _biometricEnabled.value = enabled
    }

    fun shredSensitiveData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Delete all current case files and custom court reminders to clear caches
            repository.allCases.firstOrNull()?.forEach {
                repository.deleteCase(it.id)
            }
            repository.allReminders.firstOrNull()?.forEach {
                repository.deleteReminder(it.id)
            }
        }
    }

    // --- ZIP Export / Download ---

    companion object {
        const val MAX_TITLE_LENGTH_FOR_FILENAME = 30
    }

    private fun getZipFile(context: Context) =
        File(context.cacheDir, "cases_backup.zip")

    fun checkZipExists(context: Context) {
        _zipFileExists.value = getZipFile(context).exists()
    }

    fun clearZipCreationError() {
        _zipCreationError.value = null
    }

    fun createCasesZip(context: Context) {
        _isCreatingZip.value = true
        _zipCreationError.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val caseList = repository.allCases.firstOrNull() ?: emptyList()
                val zipFile = getZipFile(context)
                ZipOutputStream(FileOutputStream(zipFile)).use { zip ->
                    if (caseList.isEmpty()) {
                        zip.putNextEntry(ZipEntry("README.txt"))
                        zip.write("No case files found to export.".toByteArray())
                        zip.closeEntry()
                    } else {
                        caseList.forEachIndexed { index, case ->
                            val sanitized = case.title
                                .take(MAX_TITLE_LENGTH_FOR_FILENAME)
                                .replace(Regex("[^A-Za-z0-9_-]"), "_")
                                .trim('_')
                                .ifBlank { "untitled" }
                            val entryName = "case_${index + 1}_$sanitized.txt"
                            zip.putNextEntry(ZipEntry(entryName))
                            val content = buildString {
                                appendLine("=== AI Justice Buddy Case Export ===")
                                appendLine("Title   : ${case.title}")
                                appendLine("State   : ${case.state}")
                                appendLine("Status  : ${case.status}")
                                appendLine("Created : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(case.timestamp))}")
                                appendLine()
                                appendLine("--- Stated Facts ---")
                                appendLine(case.description)
                                appendLine()
                                appendLine("--- AI Legal Counsel Feedback ---")
                                appendLine(case.aiFeedback)
                            }
                            zip.write(content.toByteArray(Charsets.UTF_8))
                            zip.closeEntry()
                        }
                    }
                }
                _zipFileExists.value = true
            } catch (e: Exception) {
                _zipFileExists.value = getZipFile(context).exists()
                _zipCreationError.value = "ZIP creation failed: ${e.localizedMessage}"
            } finally {
                _isCreatingZip.value = false
            }
        }
    }

    fun eraseCasesZip(context: Context) {
        val zipFile = getZipFile(context)
        if (zipFile.exists()) zipFile.delete()
        _zipFileExists.value = false
    }

    fun shareCasesZip(context: Context) {
        val zipFile = getZipFile(context)
        if (!zipFile.exists()) return
        try {
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", zipFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "AI Justice Buddy - Case Files Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Case Files ZIP").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            _zipCreationError.value = "Failed to share ZIP file: ${e.localizedMessage}"
        }
    }

    // --- Admin Control Panel (System Prompt Editing & Audited Bookkeeping) ---
    fun updateSystemPrompt(newPrompt: String) {
        _adminSystemPrompt.value = newPrompt
    }

    fun updateFinanceDetails(downloads: Int, revenue: Double, costs: Double) {
        _totalAppDownloads.value = downloads
        _monthlyRevenue.value = revenue
        _serverCosts.value = costs
        _netProfit.value = revenue - costs
    }

    // --- Case Management ---
    fun addCase(title: String, state: String, description: String, aiFeedback: String, imageUri: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveCase(
                CaseFile(
                    title = title,
                    state = state,
                    description = description,
                    aiFeedback = aiFeedback,
                    imageUri = imageUri
                )
            )
        }
    }

    fun removeCase(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCase(id)
        }
    }

    // --- Calendar Reminders ---
    fun addReminder(title: String, dateText: String, timeText: String, location: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveReminder(
                CourtReminder(
                    title = title,
                    dateText = dateText,
                    timeText = timeText,
                    location = location,
                    notes = notes
                )
            )
        }
    }

    fun removeReminder(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(id)
        }
    }

    fun markLawAlertRead(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAlertAsRead(id)
        }
    }

    // --- Customizable Avatar Controller ---
    fun updateAvatarFace(face: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getAvatarConfig()
            repository.saveAvatarConfig(current.copy(faceStyle = face))
        }
    }

    fun updateAvatarOutfit(outfit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getAvatarConfig()
            repository.saveAvatarConfig(current.copy(outfitStyle = outfit))
        }
    }

    fun updateAvatarBackground(bg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getAvatarConfig()
            repository.saveAvatarConfig(current.copy(backgroundStyle = bg))
        }
    }

    // --- AI Advisor (Evaluator & Loophole Finder using Admin Prompt) ---
    private val _advisorInput = MutableStateFlow("")
    val advisorInput = _advisorInput.asStateFlow()

    private val _selectedState = MutableStateFlow("Federal")
    val selectedState = _selectedState.asStateFlow()

    private val _isAdvisorLoading = MutableStateFlow(false)
    val isAdvisorLoading = _isAdvisorLoading.asStateFlow()

    private val _advisorResponse = MutableStateFlow("")
    val advisorResponse = _advisorResponse.asStateFlow()

    fun updateAdvisorInput(input: String) {
        _advisorInput.value = input
    }

    fun updateSelectedState(state: String) {
        _selectedState.value = state
    }

    fun evaluateSituation() {
        val input = _advisorInput.value
        val state = _selectedState.value
        if (input.trim().isEmpty()) return

        _isAdvisorLoading.value = true
        _advisorResponse.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = "Evaluate this legal situation under $state law:\n" +
                    "Situation: $input\n\n" +
                    "Please provide:\n" +
                    "1. Proper way to apply relevant laws or codes.\n" +
                    "2. Potential loopholes to prove the case.\n" +
                    "3. Key facts or constitutional rights that may have been overlooked or broken by the enforcer/police/other party.\n" +
                    "Keep the formatting structured with clear section headers."

            val systemPrompt = _adminSystemPrompt.value
            val response = repository.generateAiFeedback(prompt, systemPrompt)
            
            _advisorResponse.value = response
            _isAdvisorLoading.value = false
            
            // Speak the first couple of sentences for voice reply
            val introductorySpeech = response.split("\n").firstOrNull { it.isNotBlank() } ?: "Here is my evaluation."
            speak(introductorySpeech.replace(Regex("[#*]"), ""))
        }
    }

    // --- Image & Document Legality Analyzer ---
    private val _isAnalyzingImage = MutableStateFlow(false)
    val isAnalyzingImage = _isAnalyzingImage.asStateFlow()

    private val _imageAnalysisResponse = MutableStateFlow("")
    val imageAnalysisResponse = _imageAnalysisResponse.asStateFlow()

    fun analyzeDocumentImage(base64Image: String, mimeType: String, description: String) {
        _isAnalyzingImage.value = true
        _imageAnalysisResponse.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.analyzeDocument(base64Image, mimeType, description)
            _imageAnalysisResponse.value = response
            _isAnalyzingImage.value = false
            
            val introText = response.split("\n").firstOrNull { it.isNotBlank() } ?: "I have completed the analysis."
            speak(introText.replace(Regex("[#*]"), ""))
        }
    }

    // --- Legal Quiz Controller ---
    val quizQuestions = repository.quizQuestions

    private val _quizQuestionIndex = MutableStateFlow(0)
    val quizQuestionIndex = _quizQuestionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore = _quizScore.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption = _selectedOption.asStateFlow()

    private val _quizState = MutableStateFlow("NOT_STARTED") // NOT_STARTED, IN_PROGRESS, FINISHED
    val quizState = _quizState.asStateFlow()

    fun startQuiz() {
        _quizQuestionIndex.value = 0
        _quizScore.value = 0
        _selectedOption.value = null
        _quizState.value = "IN_PROGRESS"
    }

    fun selectQuizOption(index: Int) {
        if (_selectedOption.value != null) return // Already answered
        _selectedOption.value = index
        val currentQ = quizQuestions[_quizQuestionIndex.value]
        if (index == currentQ.correctOptionIndex) {
            _quizScore.value += 1
            speak("Correct! " + currentQ.explanation.split(".").first())
        } else {
            speak("Incorrect. " + currentQ.explanation.split(".").first())
        }
    }

    fun nextQuizQuestion() {
        _selectedOption.value = null
        val nextIdx = _quizQuestionIndex.value + 1
        if (nextIdx < quizQuestions.size) {
            _quizQuestionIndex.value = nextIdx
        } else {
            _quizState.value = "FINISHED"
            // Save to high score in DB
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveHighScore(
                    QuizHighScore(
                        score = _quizScore.value,
                        total = quizQuestions.size,
                        player = "Case Defender"
                    )
                )
            }
            speak("Quiz completed! You scored ${_quizScore.value} out of ${quizQuestions.size}.")
        }
    }

    // --- AI Law Classes QA Controller ---
    val classesList = repository.lawClasses

    private val _selectedClass = MutableStateFlow<LawClass?>(null)
    val selectedClass = _selectedClass.asStateFlow()

    private val _classQuestion = MutableStateFlow("")
    val classQuestion = _classQuestion.asStateFlow()

    private val _isClassLoading = MutableStateFlow(false)
    val isClassLoading = _isClassLoading.asStateFlow()

    private val _classAnswer = MutableStateFlow("")
    val classAnswer = _classAnswer.asStateFlow()

    fun selectClass(lawClass: LawClass?) {
        _selectedClass.value = lawClass
        _classQuestion.value = ""
        _classAnswer.value = ""
        stopSpeaking()
    }

    fun updateClassQuestion(q: String) {
        _classQuestion.value = q
    }

    fun askProfessor() {
        val q = _classQuestion.value
        val currentC = _selectedClass.value
        if (q.trim().isEmpty() || currentC == null) return

        _isClassLoading.value = true
        _classAnswer.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = "Context Class: ${currentC.title} (${currentC.category})\n" +
                    "Question: $q\n\n" +
                    "Explain the answer to this student clearly, referencing specific cases, rights, or rules."
            val systemPrompt = "You are a distinguished Law Professor. Answer legal questions with pedagogical clarity, depth, and precision."
            val response = repository.generateAiFeedback(prompt, systemPrompt)
            _classAnswer.value = response
            _isClassLoading.value = false
            
            val introText = response.split("\n").firstOrNull { it.isNotBlank() } ?: "Here is my explanation."
            speak(introText.replace(Regex("[#*]"), ""))
        }
    }

    // --- Business Class & Private Law Firm Suite States ---
    private val _firmDocuments = MutableStateFlow<List<FirmDocument>>(listOf(
        FirmDocument("doc_1", "Retainer_Agreement_AlphaCorp.pdf", "Contract", "2.4 MB", "Jul 05, 2026"),
        FirmDocument("doc_2", "Pleading_Breach_Of_Contract_Signed.pdf", "Pleading", "4.8 MB", "Jul 10, 2026"),
        FirmDocument("doc_3", "Ex_A_Bank_Ledger_Extract.xlsx", "Evidence", "1.1 MB", "Jul 12, 2026")
    ))
    val firmDocuments = _firmDocuments.asStateFlow()

    private val _filedCases = MutableStateFlow<List<FiledCase>>(listOf(
        FiledCase("FL-2026-90412", "Apex Corp v. Beta Logistics", "US District Court - Central CA", "Civil Breach of Contract", "John E. Esq.", "Filing Certified", "Jul 11, 2026"),
        FiledCase("FL-2026-90415", "State of California v. Harrison", "CA Superior Court - Los Angeles", "Criminal Felony Defense", "Jane D. Esq.", "Clerk Reviewing", "Jul 14, 2026")
    ))
    val filedCases = _filedCases.asStateFlow()

    private val _financialInvoices = MutableStateFlow<List<FinancialInvoice>>(listOf(
        FinancialInvoice("INV-4101", "AlphaCorp Ltd", 4500.00, "Paid", "Jul 01, 2026"),
        FinancialInvoice("INV-4102", "Harrison defense retainer", 7500.00, "Pending", "Jul 08, 2026"),
        FinancialInvoice("INV-4103", "Zeta Tech Consultancy", 3200.00, "Overdue", "Jun 20, 2026")
    ))
    val financialInvoices = _financialInvoices.asStateFlow()

    private val _ioltaBalance = MutableStateFlow(85000.00) // Trust Retainer account
    val ioltaBalance = _ioltaBalance.asStateFlow()

    private val _isAccountingLoading = MutableStateFlow(false)
    val isAccountingLoading = _isAccountingLoading.asStateFlow()

    private val _accountingResponse = MutableStateFlow("")
    val accountingResponse = _accountingResponse.asStateFlow()

    private val _isPrivateAssistantLoading = MutableStateFlow(false)
    val isPrivateAssistantLoading = _isPrivateAssistantLoading.asStateFlow()

    private val _privateAssistantResponse = MutableStateFlow("")
    val privateAssistantResponse = _privateAssistantResponse.asStateFlow()

    // --- Business Law Classes Customizer States ---
    private val _isSyllabusLoading = MutableStateFlow(false)
    val isSyllabusLoading = _isSyllabusLoading.asStateFlow()

    private val _customSyllabusText = MutableStateFlow("")
    val customSyllabusText = _customSyllabusText.asStateFlow()

    // --- Private Suite Operations ---
    fun addFirmDocument(name: String, category: String, size: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val newDoc = FirmDocument(
            id = "doc_" + System.currentTimeMillis(),
            name = if (name.endsWith(".pdf") || name.endsWith(".xlsx")) name else "$name.pdf",
            category = category,
            size = size,
            dateAdded = dateFormat.format(Date()),
            isEncrypted = true
        )
        _firmDocuments.value = listOf(newDoc) + _firmDocuments.value
    }

    fun deleteFirmDocument(id: String) {
        _firmDocuments.value = _firmDocuments.value.filter { it.id != id }
    }

    fun submitCaseFiling(title: String, jurisdiction: String, caseType: String, signatureName: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val filingId = "FL-2026-" + (10000 + (Math.random() * 90000).toInt())
        val newFiling = FiledCase(
            filingId = filingId,
            title = title,
            jurisdiction = jurisdiction,
            caseType = caseType,
            signatureName = signatureName,
            status = "Submitted",
            filingDate = dateFormat.format(Date())
        )
        _filedCases.value = listOf(newFiling) + _filedCases.value

        // Simulate clerk updates asynchronously
        viewModelScope.launch {
            kotlinx.coroutines.delay(8000L)
            _filedCases.value = _filedCases.value.map {
                if (it.filingId == filingId) it.copy(status = "Clerk Reviewing") else it
            }
            kotlinx.coroutines.delay(10000L)
            _filedCases.value = _filedCases.value.map {
                if (it.filingId == filingId) it.copy(status = "Filing Certified") else it
            }
        }
    }

    fun addInvoice(clientName: String, amount: Double) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val newInv = FinancialInvoice(
            id = "INV-" + (4100 + (Math.random() * 900).toInt()),
            clientName = clientName,
            amount = amount,
            status = "Pending",
            date = dateFormat.format(Date())
        )
        _financialInvoices.value = listOf(newInv) + _financialInvoices.value
    }

    fun markInvoicePaid(id: String) {
        _financialInvoices.value = _financialInvoices.value.map {
            if (it.id == id) {
                if (it.status != "Paid") {
                    _ioltaBalance.value += it.amount
                }
                it.copy(status = "Paid")
            } else it
        }
    }

    fun askFinancialAccountant(query: String) {
        if (query.trim().isEmpty()) return
        _isAccountingLoading.value = true
        _accountingResponse.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = "Accounting Query: $query\n" +
                    "Current trust fund IOLTA balance: $${_ioltaBalance.value}\n" +
                    "Recent invoices status: ${_financialInvoices.value.joinToString { "${it.clientName}: $${it.amount} (${it.status})" }}\n\n" +
                    "Provide a detailed, expert double-entry CPA or forensic bookkeeping analysis, tax deduction advice, or IOLTA accounting guidance. Stay strict, clear, professional, and compliant with ABA Rules of Professional Conduct Rule 1.15."
            val systemPrompt = "You are a highly experienced and certified Private Legal Financial Accountant & Forensic CPA specialized in ABA compliance, Trust Accounting (IOLTA), and small law firm tax audits."
            val response = repository.generateAiFeedback(prompt, systemPrompt)
            _accountingResponse.value = response
            _isAccountingLoading.value = false
            
            val introText = response.split("\n").firstOrNull { it.isNotBlank() } ?: "Here is my accounting feedback."
            speak(introText.replace(Regex("[#*]"), ""))
        }
    }

    fun askDedicatedAssistant(query: String) {
        if (query.trim().isEmpty()) return
        _isPrivateAssistantLoading.value = true
        _privateAssistantResponse.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = "Request: $query\n\n" +
                    "Draft or execute this task with supreme precision. If asked to draft demand letters, pleading briefs, non-disclosure agreements, or client memos, provide high-quality legal templates or specific arguments. If asked to summarize a situation, provide a structured master outline."
            val systemPrompt = "You are an elite, dedicated Senior Legal Associate, Administrative Executive Assistant, and Document Drafter. You provide exhaustive, professional-grade, actionable mock drafts, briefs, and client summaries."
            val response = repository.generateAiFeedback(prompt, systemPrompt)
            _privateAssistantResponse.value = response
            _isPrivateAssistantLoading.value = false
            
            val introText = response.split("\n").firstOrNull { it.isNotBlank() } ?: "Draft ready."
            speak(introText.replace(Regex("[#*]"), ""))
        }
    }

    fun generateCustomClassSyllabus(courseTopic: String) {
        if (courseTopic.trim().isEmpty()) return
        _isSyllabusLoading.value = true
        _customSyllabusText.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = "Create a detailed Law Course Syllabus & Mock Trial blueprint for this topic: $courseTopic\n\n" +
                    "Please structure it into:\n" +
                    "1. Lecture 1: Core Statutes & Foundational Cases\n" +
                    "2. Lecture 2: Tactical Defenses & Practical Loophole Discovery\n" +
                    "3. Mock Trial Blueprint: Fact Pattern, Prosecution Strategy, Defense Strategy, and interactive cross-examination question guides."
            val systemPrompt = "You are a distinguished Law School Dean and Curriculum Designer. You design rigorous, highly engaging legal syllabi and mock trial case blueprints."
            val response = repository.generateAiFeedback(prompt, systemPrompt)
            _customSyllabusText.value = response
            _isSyllabusLoading.value = false
            
            val introText = response.split("\n").firstOrNull { it.isNotBlank() } ?: "I have custom designed your course syllabus."
            speak(introText.replace(Regex("[#*]"), ""))
        }
    }
}

// --- Custom Data Models for Private Law Firm Suite ---
data class FirmDocument(
    val id: String,
    val name: String,
    val category: String, // "Contract", "Pleading", "Evidence", "Invoice", "Corporate"
    val size: String,
    val dateAdded: String,
    val isEncrypted: Boolean = true
)

data class FiledCase(
    val filingId: String,
    val title: String,
    val jurisdiction: String,
    val caseType: String,
    val signatureName: String,
    val status: String, // "Submitted", "Clerk Reviewing", "Filing Certified", "Court Seal Appended"
    val filingDate: String
)

data class FinancialInvoice(
    val id: String,
    val clientName: String,
    val amount: Double,
    val status: String, // "Paid", "Pending", "Overdue"
    val date: String
)
