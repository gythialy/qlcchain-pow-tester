package org.qlcchain.model

import com.squareup.moshi.Json

/**
 *
 * @property work String
 * @constructor
 */
data class WorkData(
        @Json(name = "work") val work: String
)
