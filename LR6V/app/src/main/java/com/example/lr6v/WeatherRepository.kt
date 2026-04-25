package com.example.lr6v

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

class WeatherRepository {

    fun getWeatherByCity(city: String): WeatherData? {
        val location = getCoordinates(city) ?: return null
        val weather = getCurrentWeather(location.latitude, location.longitude) ?: return null

        return WeatherData(
            cityName = location.name,
            country = location.country,
            temperature = weather.temperature,
            windSpeed = weather.windSpeed,
            weatherCode = weather.weatherCode,
            time = weather.time
        )
    }

    private fun getCoordinates(city: String): LocationData? {
        val encodedCity = URLEncoder.encode(city, "UTF-8")
        val url =
            "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=en&format=json"

        val response = getJson(url) ?: return null
        val results = response.optJSONArray("results") ?: return null
        if (results.length() == 0) return null

        val item = results.getJSONObject(0)

        return LocationData(
            name = item.optString("name", city),
            country = item.optString("country", ""),
            latitude = item.optDouble("latitude", 0.0),
            longitude = item.optDouble("longitude", 0.0)
        )
    }

    private fun getCurrentWeather(latitude: Double, longitude: Double): WeatherData? {
        val url =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$latitude" +
                    "&longitude=$longitude" +
                    "&current=temperature_2m,wind_speed_10m,weather_code" +
                    "&timezone=auto"

        val response = getJson(url) ?: return null
        val current = response.optJSONObject("current") ?: return null

        return WeatherData(
            cityName = "",
            country = "",
            temperature = current.optDouble("temperature_2m", 0.0),
            windSpeed = current.optDouble("wind_speed_10m", 0.0),
            weatherCode = current.optInt("weather_code", -1),
            time = current.optString("time", "")
        )
    }

    private fun getJson(urlString: String): JSONObject? {
        var connection: HttpURLConnection? = null

        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }

            reader.close()
            JSONObject(result.toString())
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}