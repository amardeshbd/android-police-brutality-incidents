/*
 * MIT License
 *
 * Copyright (c) 2020 Hossain Khan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.blacklivesmatter.policebrutality.ui.incident

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blacklivesmatter.policebrutality.R
import com.blacklivesmatter.policebrutality.data.model.Incident
import com.blacklivesmatter.policebrutality.databinding.DialogBottomsheetIncidentDetailsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

/**
 * Incident details fragment that allows to see map and share the incident.
 */
class IncidentDetailsBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        const val FRAGMENT_TAG = "IncidentDetailsBottomSheetFragment"
    }

    private lateinit var incidentViewModel: IncidentViewModel
    private lateinit var selectedIncident: Incident
    private lateinit var binding: DialogBottomsheetIncidentDetailsBinding

    /**
     * Sets required data for the child bottom sheet fragment.
     *
     * This fragment intentionally does not use DI, or navigation args because:
     * - Navigation args would required the [Incident] to be parcelable.
     * - If we don't make parcelable and pass incident ID, then we are waiting extra for database to get info by ID
     * - DI is not used to keep it simple as this dialog is very short lived.
     *
     * In other words! this is a HACK! and won't survive configuration change (see [onStop] for workaround ;-)
     */
    fun setData(viewModel: IncidentViewModel, incident: Incident) {
        incidentViewModel = viewModel
        selectedIncident = incident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogBottomsheetIncidentDetailsBinding.inflate(layoutInflater, null, false).apply {
            lifecycleOwner = this@IncidentDetailsBottomSheetFragment
            vm = incidentViewModel
            data = selectedIncident
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        dismissAllowingStateLoss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (selectedIncident.hasValidGeocodingData.not()) {
            Timber.d("The incident %s does not have geocoding, not showing map.", selectedIncident.incident_id)
            return
        }

        val options = GoogleMapOptions().apply {
            liteMode(true)
        }

        val mapFragment = SupportMapFragment.newInstance(options)
        childFragmentManager.beginTransaction()
            .add(R.id.incident_map_container, mapFragment, "MAP")
            .commit()

        mapFragment.getMapAsync { googleMap ->
            Timber.d("Google map is ready")
            binding.mapLoadingIndicator.apply {
                cancelAnimation()
                visibility = View.GONE
            }

            googleMap.uiSettings.apply {
                isMapToolbarEnabled = false
            }

            applyMapStyle(googleMap)

            // Add a marker for the incident and move the camera
            val incidentLocation = selectedIncident.toLatLng()
            googleMap.addMarker(MarkerOptions().position(incidentLocation).title(selectedIncident.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incidentLocation, 13f))
        }
    }

    /**
     * Styles the map in dark mode.
     * - https://developers.google.com/maps/documentation/android-sdk/styling
     * - https://mapstyle.withgoogle.com/
     */
    private fun applyMapStyle(map: GoogleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success: Boolean = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.google_maps_style
                )
            )
            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "Can't find style. Error: ")
        }
    }

    /**
     * Extension to convert geocoding data to Google's [LatLng].
     * We can use `!!` because data has been validated in [Incident.hasValidGeocodingData].
     */
    private fun Incident.toLatLng(): LatLng = LatLng(geocoding!!.lat!!.toDouble(), geocoding.long!!.toDouble())
}
