package com.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WeatherClient {

    // Using Open-Meteo API as it is free and does not require an API key
    // Coordinates for New York City
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=40.7128&longitude=-74.0060&current_weather=true&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";

    public static void main(String[] args) {
        System.out.println("Fetching weather data from Open-Meteo API...");

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Data fetched successfully! Parsing JSON...\n");
                parseAndDisplayWeatherData(response.body());
            } else {
                System.err.println("Failed to fetch data. HTTP Status Code: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("An error occurred while fetching the data.");
            e.printStackTrace();
        }
    }

    private static void parseAndDisplayWeatherData(String jsonResponse) {
        // Parse the JSON representation
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Get Current Weather
        JSONObject currentWeather = jsonObject.getJSONObject("current_weather");
        double temperature = currentWeather.getDouble("temperature");
        double windSpeed = currentWeather.getDouble("windspeed");
        
        System.out.println("==================================================");
        System.out.println("CURRENT WEATHER (New York City)");
        System.out.println("==================================================");
        System.out.println(String.format("%-25s : %.1f °C", "Temperature", temperature));
        System.out.println(String.format("%-25s : %.1f km/h", "Wind Speed", windSpeed));
        System.out.println("==================================================");

        // Get Hourly Data
        System.out.println("\nHOURLY FORECAST (Next 5 hours)");
        System.out.println("==================================================");
        System.out.println(String.format("%-25s | %-10s | %-10s", "Time", "Temp (°C)", "Wind (km/h)"));
        System.out.println("--------------------------------------------------");

        JSONObject hourly = jsonObject.getJSONObject("hourly");
        JSONArray times = hourly.getJSONArray("time");
        JSONArray temperatures = hourly.getJSONArray("temperature_2m");
        JSONArray windSpeeds = hourly.getJSONArray("wind_speed_10m");

        // Display just the first 5 hours for brevity
        int hoursToDisplay = Math.min(5, times.length());
        for (int i = 0; i < hoursToDisplay; i++) {
            System.out.println(String.format("%-25s | %-10.1f | %-10.1f", 
                    times.getString(i).replace("T", " "), 
                    temperatures.getDouble(i), 
                    windSpeeds.getDouble(i)));
        }
        System.out.println("==================================================");
    }
}
