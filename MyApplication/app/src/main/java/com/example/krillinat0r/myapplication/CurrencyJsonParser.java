package com.example.krillinat0r.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Krillinat0r on 13-04-2018.
 */

public class CurrencyJsonParser {

    public static String parseCityWeatherJson(String jsonString) {
        String weatherString = null;
        try {
            JSONObject cityWeatherJson = new JSONObject(jsonString);
            JSONObject measurements = cityWeatherJson.getJSONObject("main");

            int tempInt = (int)(measurements.getDouble("temp")); //Set temp
            String temp = String.valueOf(tempInt);
            String[] names = cityWeatherJson.getString("name").split(" ", 2);

            JSONObject wind = cityWeatherJson.getJSONObject("wind");
            String windSpeed = String.valueOf((int)wind.getDouble("speed"));

            //JSONObject weather = cityWeatherJson.getJSONObject("description");
            JSONArray weather = cityWeatherJson.getJSONArray("weather");
            JSONObject obj = weather.getJSONObject(0);
            String description = obj.getString("description");

            String iconUrl = obj.getString("icon");

            weatherString = names[0] + " " + temp + " " + windSpeed + " " + iconUrl + " " + description; // measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main") + " : " + measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
        } catch (JSONException e) {
        }
        return weatherString;
    }

}
