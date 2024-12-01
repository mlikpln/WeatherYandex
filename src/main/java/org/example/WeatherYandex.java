package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherYandex {
    public static void main(String[] args) {
        // мой API ключ
        String apiKey = "5d1128e2-7dad-46bd-acd7-0cd777a5f3b7";

        // Координаты Краснодара
        double lat = 45.0358;
        double lon = 38.9763;

        // URL для запроса с передачей параметров lat, lon и limit
        String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&lang=ru&limit=7";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", apiKey);  //Передача ключа в заголовке запроса

            // Код ответа
            int statusCode = connection.getResponseCode();

            if (statusCode == 200) {
                // Чтение данных из потока
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Преобразования строки ответа в JSONObject
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Выводи всего JSON-ответа
                System.out.println("Ответ от сервиса (JSON):");
                System.out.println(jsonResponse.toString(4));  // Pretty print JSON

                // Извлечение текущей температуры из поля "fact"
                JSONObject fact = jsonResponse.getJSONObject("fact");
                int currentTemp = fact.getInt("temp");  // Текущая температура
                System.out.println("\nТекущая температура в Краснодаре: " + currentTemp + "°C");

                // Данные о прогнозах на 7 будущих дней из поля "forecasts"
                JSONArray forecasts = jsonResponse.getJSONArray("forecasts");

                // Переменные для вычисления средней температуры
                double totalTemperature = 0;
                int count = 0;

                // Извлечение данных средней температуры на каждый день
                for (int i = 0; i < forecasts.length(); i++) {
                    JSONObject dayForecast = forecasts.getJSONObject(i);
                    JSONObject dayPart = dayForecast.getJSONObject("parts").getJSONObject("day");
                    // Извлекаем среднюю температуру для дня
                    int tempForDay = dayPart.getInt("temp_avg");
                    totalTemperature += tempForDay;
                    count++;
                }

                // Вычисление средней температуры за 7 дней
                if (count > 0) {
                    double averageTemperature = totalTemperature / count;
                    System.out.println("\nСредняя температура на 7 дней в Краснодаре: " + averageTemperature + "°C");
                } else {
                    System.out.println("\nНе удалось вычислить среднюю температуру.");
                }
            } else {
                System.out.println("Ошибка при запросе данных. Код ответа: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}