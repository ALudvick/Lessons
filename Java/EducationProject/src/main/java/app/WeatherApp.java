package app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class WeatherApp {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Properties properties = new Properties();
    private static final int LIMIT = 15;

    static {
        try {
            properties.load(WeatherApp.class.getClassLoader().getResourceAsStream("weather.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.print("Enter city name: ");
        String cityName = reader.readLine();
        JsonObject object = getJsonByCityName(cityName);
        String lat = object.get("lat").toString();
        String lon = object.get("lon").toString();
        getWeatherByCoordinates(lat, lon);
    }

    private static void getWeatherByCoordinates(String lat, String lon) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new URL("https://api.openweathermap.org/data/2.5/weather?" +
                        "lat=" + lat +
                        "&lon=" +  lon +
                        "&appid=" + properties.getProperty("token") +
                        "&units=metric"))
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        Gson gson = new Gson();

        JsonObject mainJson = gson.fromJson(result, JsonObject.class);
        JsonObject tempJson = gson.fromJson(mainJson.get("main"), JsonObject.class);
        System.out.printf("Температура: %sC°. Ощущается как: %sC°", tempJson.get("temp"), tempJson.get("feels_like"));
    }

    private static JsonObject getJsonByCityName(String cityName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new URL("http://api.openweathermap.org/geo/1.0/direct?" +
                        "q=" + cityName +
                        "&limit=" + LIMIT +
                        "&appid=" + properties.getProperty("token")))
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        Gson gson = new Gson();
        JsonArray mainJson = gson.fromJson(result, JsonArray.class);

        System.out.println("Пожалуйста, конкретизируйте ваш выбор: ");

        int elementNumber = 1;
        for (JsonElement element : mainJson) {
            System.out.printf("%d: %s - %s - %s (%s : %s)\n",
                    elementNumber,
                    element.getAsJsonObject().get("name").getAsString(),
                    element.getAsJsonObject().get("country").getAsString(),
                    element.getAsJsonObject().get("state").getAsString(),
                    element.getAsJsonObject().get("lat").getAsString(),
                    element.getAsJsonObject().get("lon").getAsString()
            );
            elementNumber++;
        }

        int userChoice = getUserChoice(mainJson.size()) - 1;
        return mainJson.getAsJsonArray().get(userChoice).getAsJsonObject();
    }

    private static int getUserChoice(int arraySize) throws IOException {
        System.out.print("\nВыбор: ");
        int userChoice = Integer.parseInt(reader.readLine());

        if (userChoice > 0 && userChoice <= arraySize) {
            return userChoice;
        } else {
            System.out.print("К сожалению, я не понял ваш выбор или была допущена ошибка. Пожалуйста, повторите снова");
            getUserChoice(arraySize);
        }

        return 1;
    }


}
