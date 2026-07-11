package com.example.swiftride.data.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class NominatimService {
    private val userAgent = "SwiftRideBookingApp/1.0 (support@swiftride.com)"

    suspend fun searchLocations(query: String): List<LocationData> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val urlString = "https://nominatim.openstreetmap.org/search?q=" +
                URLEncoder.encode(query, "UTF-8") +
                "&format=json&limit=8&addressdetails=1"
        
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", userAgent)
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonArray = JSONArray(response.toString())
                val locations = mutableListOf<LocationData>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")
                    val displayName = obj.getString("display_name")
                    
                    val addressObj = obj.optJSONObject("address")
                    val name = addressObj?.optString("road", null as String?)
                        ?: addressObj?.optString("suburb", null as String?)
                        ?: addressObj?.optString("city", null as String?)
                        ?: obj.optString("name", "").takeIf { it.isNotEmpty() }
                        ?: displayName.split(",").firstOrNull()?.trim()
                        ?: "Unknown Place"
                    
                    locations.add(LocationData(name, displayName, lat, lon))
                }
                return@withContext locations
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return@withContext emptyList()
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): LocationData? = withContext(Dispatchers.IO) {
        val urlString = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json&addressdetails=1"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", userAgent)
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val obj = JSONObject(response.toString())
                val displayName = obj.getString("display_name")
                
                val addressObj = obj.optJSONObject("address")
                val name = addressObj?.optString("road", null as String?)
                    ?: addressObj?.optString("suburb", null as String?)
                    ?: addressObj?.optString("city", null as String?)
                    ?: displayName.split(",").firstOrNull()?.trim()
                    ?: "Current Location"

                return@withContext LocationData(name, displayName, lat, lon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return@withContext null
    }
}
