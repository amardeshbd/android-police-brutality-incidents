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

package com.blacklivesmatter.policebrutality.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blacklivesmatter.policebrutality.config.CHARITY_DATA_FILENAME
import com.google.gson.annotations.SerializedName

/**
 * Charitable or non-profit organization that helps for #BlackLivesMatter or #PoliceBrutality causes.
 *
 * NOTE: Currently list of charity is bundled with [CHARITY_DATA_FILENAME] JSON file.
 *
 * Example object:
 * ```
 *  {
 *    "name": "Black Lives Matter",
 *    "description": "Black Lives Matter was founded in the wake of Trayvon Martin's murder back in 2013 ......",
 *    "org_url": "https://blacklivesmatter.com/",
 *    "donate_url": "https://secure.actblue.com/donate/ms_blm_homepage_2019",
 *    "logo_url": "https://blacklivesmatter.com/wp-content/themes/blm/dist/images/logo-black-lives-matter.png",
 *    "item_weight": 10
 *  }
 * ```
 */
@Keep
@Entity(tableName = "charity")
data class CharityOrg(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("org_url") @ColumnInfo(name = "org_url") val org_url: String = "",
    @SerializedName("name") @ColumnInfo(name = "name") val name: String = "",
    @SerializedName("description") @ColumnInfo(name = "description") val description: String = "",
    @SerializedName("donate_url") @ColumnInfo(name = "donate_url") val donate_url: String = "",
    @SerializedName("logo_url") @ColumnInfo(name = "logo_url") val logo_url: String? = null,
    @SerializedName("item_weight") @ColumnInfo(name = "item_weight") val item_weight: Int = 100
)
