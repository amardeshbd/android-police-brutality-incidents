package com.blacklivesmatter.policebrutality.ui.incident

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blacklivesmatter.policebrutality.analytics.Analytics
import com.blacklivesmatter.policebrutality.config.LATEST_INCIDENT_LIMIT
import com.blacklivesmatter.policebrutality.config.PREF_KEY_SHARE_CAPABILITY_REMINDER_SHOWN
import com.blacklivesmatter.policebrutality.data.IncidentRepository
import com.blacklivesmatter.policebrutality.data.model.Incident
import com.blacklivesmatter.policebrutality.ui.extensions.LiveEvent
import com.blacklivesmatter.policebrutality.ui.incident.arg.FilterType
import timber.log.Timber

class IncidentViewModel @ViewModelInject constructor(
    private val analytics: Analytics,
    private val incidentRepository: IncidentRepository,
    private val preferences: SharedPreferences
) : ViewModel() {
    internal val selectedState = ObservableField("")

    private val _incidents = MediatorLiveData<List<Incident>>()
    val incidents: LiveData<List<Incident>> = _incidents

    private val _shareIncident = LiveEvent<Incident>()
    val shareIncident: LiveData<Incident> = _shareIncident

    private val _shouldShowShareCapabilityMessage = MutableLiveData<Unit>()
    val shouldShowShareCapabilityMessage: LiveData<Unit> = _shouldShowShareCapabilityMessage

    fun onShareIncidentClicked(incident: Incident) {
        Timber.d("User clicked on share incident")
        analytics.logShare(Analytics.CONTENT_TYPE_INCIDENT_SHARE, incident.incident_id ?: "---")
        _shareIncident.value = incident
    }

    fun setArgs(navArgs: IncidentsFragmentArgs) {
        when (navArgs.filterArgs.type) {
            FilterType.STATE -> selectedSate(navArgs.filterArgs.stateName!!)
            FilterType.DATE -> selectedTimestamp(navArgs.filterArgs.timestamp!!)
            FilterType.LATEST -> selectedMostRecentIncidents()
        }

        val isMessageShown = preferences.getBoolean(PREF_KEY_SHARE_CAPABILITY_REMINDER_SHOWN, false)
        if (isMessageShown.not()) {
            Timber.d("User has launched app for first time, show share capability message.")
            preferences.edit { putBoolean(PREF_KEY_SHARE_CAPABILITY_REMINDER_SHOWN, true) }
            _shouldShowShareCapabilityMessage.value = Unit
        } else {
            Timber.d("Not first time app launch, don't show share capability message.")
        }
    }

    private fun selectedSate(stateName: String) {
        selectedState.set(stateName)

        _incidents.addSource(incidentRepository.getStateIncidents(stateName)) {
            Timber.d("Incidents Updated ")
            _incidents.value = it
        }
    }

    private fun selectedTimestamp(timestamp: Long) {
        _incidents.addSource(incidentRepository.getIncidentsByDate(timestamp)) {
            Timber.d("Incidents Updated ")
            _incidents.value = it
        }
    }

    private fun selectedMostRecentIncidents() {
        _incidents.addSource(incidentRepository.getIncidentsRecentFirst(limit = LATEST_INCIDENT_LIMIT)) {
            Timber.d("Incidents Updated ")
            _incidents.value = it
        }
    }
}
