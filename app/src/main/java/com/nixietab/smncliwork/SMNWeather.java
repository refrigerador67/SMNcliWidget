package com.nixietab.smncliwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import org.json.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SMNWeather {

    private static final String API_BASE_LOCATIONS = "https://ws.smn.gob.ar/map_items/weather";
    private static final String API_BASE_WEATHER = "https://ws1.smn.gob.ar/v1/weather/location/";

    // this gets the list of IDs for the locations using the old api thats stuck in 2022
    public static Map<String, Integer> getAllLocationsWithIds(String jwtToken) throws IOException, JSONException {
        handleSSLHandshake();
        URL url = new URL(API_BASE_LOCATIONS);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("GET");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "JWT " + jwtToken);
        }

        Map<String, Integer> locations = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            JSONArray data = new JSONArray(response.toString());
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                String name = item.optString("name", null);
                int id = item.optInt("id", -1);
                if (name != null && id != -1) {
                    locations.put(name, id);
                }
            }
        }

        return locations;
    }

    // actually fetch the data
    public static JSONObject fetchWeatherData(int locationId, String jwtToken) throws IOException, JSONException {
        handleSSLHandshake();
        URL url = new URL(API_BASE_WEATHER + locationId);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("GET");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "JWT " + jwtToken);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    // returns the stuff based on the location ID
    public static Map<String, String> getWeatherInfoByLocationId(int locationId, String jwtToken) throws IOException, JSONException {
        JSONObject w = fetchWeatherData(locationId, jwtToken);
        if (w == null) return null;

        Map<String, String> info = new HashMap<>();
        JSONObject location = w.optJSONObject("location");
        info.put("name", location != null ? location.optString("name", "Unknown") : "Unknown");

        JSONObject weather = w.optJSONObject("weather");
        info.put("description", weather != null ? capitalize(weather.optString("description", "N/A")) : "N/A");

        info.put("temp", String.valueOf(w.optDouble("temperature", Double.NaN)));
        info.put("humidity", String.valueOf(w.optDouble("humidity", Double.NaN)));
        info.put("pressure", String.valueOf(w.optDouble("pressure", Double.NaN)));

        JSONObject wind = w.optJSONObject("wind");
        info.put("wind_speed", wind != null ? String.valueOf(wind.optDouble("speed", Double.NaN)) : "N/A");
        info.put("wind_dir", wind != null ? wind.optString("direction", "") : "");

        return info;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void handleSSLHandshake() { // mmm yeah tasty oh yeas tasty code
        try { 
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        } catch (Exception ignored) {}
        // no but really dont do this in production
    }
}
