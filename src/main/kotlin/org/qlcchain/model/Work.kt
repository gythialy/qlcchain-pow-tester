package org.qlcchain.model

import com.squareup.moshi.Json

/**
 *
 * @property action String
 * @property hash String
 * @constructor
 */
data class Work (
    @Json(name = "action") val action: String,
    @Json(name = "hash") val hash: String
)