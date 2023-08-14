/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.sources.mf

import io.reactivex.rxjava3.core.Observable
import org.breezyweather.sources.mf.json.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * API Météo France
 */
interface MfApi {

    @GET("v2/forecast")
    fun getForecast(
        @Header("User-Agent") userAgent: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("formatDate") formatDate: String,
        @Query("token") token: String
    ): Observable<MfForecastResult>

    @GET("v2/observation")
    fun getCurrent(
        @Header("User-Agent") userAgent: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("formatDate") formatDate: String,
        @Query("token") token: String
    ): Observable<MfCurrentResult>

    @GET("v3/nowcast/rain")
    fun getRain(
        @Header("User-Agent") userAgent: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("formatDate") formatDate: String,
        @Query("token") token: String
    ): Observable<MfRainResult>

    @GET("ephemeris")
    fun getEphemeris(
        @Header("User-Agent") userAgent: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("formatDate") formatDate: String,
        @Query("token") token: String
    ): Observable<MfEphemerisResult>

    @GET("v2/normals")
    fun getNormals(
        @Header("User-Agent") userAgent: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("token") token: String
    ): Observable<MfNormalsResult>

    @GET("v3/warning/full")
    fun getWarnings(
        @Header("User-Agent") userAgent: String,
        @Query(encoded = true, value = "domain") domain: String,
        @Query("formatDate") formatDate: String,
        @Query("token") token: String
    ): Observable<MfWarningsResult>
}