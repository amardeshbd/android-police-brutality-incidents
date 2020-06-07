package com.github.policebrutality.data.model

import androidx.annotation.DrawableRes
import com.github.policebrutality.R

data class LinkInfo(
    /**
     * Drawable resource for the link.
     *
     * For example - twitter, facebook, youtube etc.
     */
    @DrawableRes
    val iconResId: Int = R.drawable.ic_outline_web,
    /**
     * Social network name or domain name for external links.
     */
    val name: String
)