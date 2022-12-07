package app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;


public class WeatherApp {
    private final String weatherApiToken;
    private static final int LIMIT = 15;

    public WeatherApp(String weatherApiToken) {
        this.weatherApiToken = weatherApiToken;
    }

    public Map<Long, String> getWeatherByCoordinates(String lat, String lon) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new URL("https://api.openweathermap.org/data/2.5/weather?" +
                        "lat=" + lat +
                        "&lon=" +  lon +
                        "&appid=" + weatherApiToken +
                        "&units=metric"))
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        Gson gson = new Gson();
        JsonObject mainJson = gson.fromJson(result, JsonObject.class);
        JsonObject tempJson = gson.fromJson(mainJson.get("main"), JsonObject.class);
        return Map.of(mainJson.get("id").getAsLong(), String.format("Температура: %sC°. Ощущается как: %sC°\n", tempJson.get("temp"), tempJson.get("feels_like")));
    }

    public JsonArray getJsonByCityName(String cityName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new URL("http://api.openweathermap.org/geo/1.0/direct?" +
                        "q=" + cityName +
                        "&limit=" + LIMIT +
                        "&appid=" + weatherApiToken))
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        Gson gson = new Gson();
        return gson.fromJson(result, JsonArray.class);
    }

    public String getCitiesList(JsonArray mainJson) {
        StringBuilder resultMessage = new StringBuilder();
        if (mainJson.size() != 0) {
            resultMessage.append("Пожалуйста, конкретизируйте ваш выбор: \n");
            int elementNumber = 1;
            for (JsonElement element : mainJson) {
                resultMessage.append(String.format("%d: %s - %s - %s (%s : %s)\n",
                        elementNumber,
                        element.getAsJsonObject().get("name").getAsString(),
                        element.getAsJsonObject().get("country").getAsString(),
                        element.getAsJsonObject().get("state").getAsString(),
                        element.getAsJsonObject().get("lat").getAsString(),
                        element.getAsJsonObject().get("lon").getAsString()
                ));
                elementNumber++;
            }
            return resultMessage.toString();
        } else {
            throw new IllegalArgumentException("Неизвестный город");
        }
    }
}
