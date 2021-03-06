package com.blacklivesmatter.policebrutality.ui.moreinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.blacklivesmatter.policebrutality.BuildConfig
import com.blacklivesmatter.policebrutality.R
import com.blacklivesmatter.policebrutality.analytics.Analytics
import com.blacklivesmatter.policebrutality.config.PB_LINK_FACEBOOK
import com.blacklivesmatter.policebrutality.config.PB_LINK_INSTAGRAM
import com.blacklivesmatter.policebrutality.config.PB_LINK_REDDIT
import com.blacklivesmatter.policebrutality.config.PB_LINK_TWITTER
import com.blacklivesmatter.policebrutality.config.PB_LINK_WEB
import com.blacklivesmatter.policebrutality.ui.extensions.LiveEvent
import timber.log.Timber

class MoreInfoViewModel @ViewModelInject constructor(
    private val analytics: Analytics
) : ViewModel() {
    private val _openExternalUrl = LiveEvent<String>()
    val openExternalUrl: LiveData<String> = _openExternalUrl

    fun onExternalResourceClicked(viewId: Int) {
        when (viewId) {
            R.id.pb2020_ext_link_reddit -> _openExternalUrl.value = PB_LINK_REDDIT
            R.id.pb2020_ext_link_twitter -> _openExternalUrl.value = PB_LINK_TWITTER
            R.id.pb2020_ext_link_instagram -> _openExternalUrl.value = PB_LINK_INSTAGRAM
            R.id.pb2020_ext_link_facebook -> _openExternalUrl.value = PB_LINK_FACEBOOK
            R.id.pb2020_ext_link_web -> _openExternalUrl.value = PB_LINK_WEB
            else -> Timber.w("Unhandled new item?")
        }
    }

    fun onShareAndroidAppLink() {
        analytics.logEvent(Analytics.ACTION_SHARE_APP)
        analytics.logShare(BuildConfig.BUILD_TYPE, BuildConfig.APPLICATION_ID)
    }
}
