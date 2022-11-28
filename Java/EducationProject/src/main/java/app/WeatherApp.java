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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class WeatherApp {
    private static DatabaseManipulator databaseManipulator;
    // TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Properties properties = new Properties();
    private static final int LIMIT = 15;

    private static User user;

    static {
        try {
            properties.load(WeatherApp.class.getClassLoader().getResourceAsStream("weather.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        databaseManipulator = new DatabaseManipulator("src/main/resources/weather.db");
        JsonObject object = startWeatherApp();
        String lat = object.get("lat").toString();
        String lon = object.get("lon").toString();
        getWeatherByCoordinates(lat, lon);
    }

    private static JsonObject startWeatherApp() {
        user = new User(354382653, "Ludvick", "User");
        databaseManipulator.addUser(sdf.format(new Date()), user.getId(), user.getName(), user.getRole());
        String queryId = UUID.randomUUID().toString();

        try {
            long start = System.currentTimeMillis();
            System.out.print("Enter city name: ");
            String cityName = reader.readLine();
            user.setMessage(cityName);
            user.setQueryId(queryId);
            try {
                return getJsonByCityName(cityName);
            } catch (IllegalArgumentException e) {
                long end = System.currentTimeMillis();
                System.out.println(e.getMessage());

                databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), -1, user.getId(), user.getMessage(), e.getMessage(), -666);
                return startWeatherApp();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return startWeatherApp();
        }
    }

    private static void getWeatherByCoordinates(String lat, String lon) throws IOException {
        long start = System.currentTimeMillis();
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

        databaseManipulator.addCity(mainJson.get("id").getAsLong(), mainJson.get("name").getAsString(), Double.parseDouble(lat), Double.parseDouble(lon));

        String messageForOutput = String.format("Температура: %sC°. Ощущается как: %sC°\n", tempJson.get("temp"), tempJson.get("feels_like"));
        System.out.print(messageForOutput);

        long end = System.currentTimeMillis();
        databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), mainJson.get("id").getAsLong(), user.getId(), user.getMessage(), result, 1);
    }

    private static JsonObject getJsonByCityName(String cityName) throws IOException {
        long start = System.currentTimeMillis();

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

        long end = System.currentTimeMillis();
        databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), -1, user.getId(), user.getMessage(), result, 1);

        if (mainJson.size() != 0) {
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
        } else {
            throw new IllegalArgumentException("Неизвестный город");
        }
    }

    private static int getUserChoice(int arraySize) {
        try {
            long start = System.currentTimeMillis();
            System.out.print("\nВыбор: ");
            String userChoiceStr = reader.readLine();
            user.setQueryId(UUID.randomUUID().toString());
            user.setMessage(userChoiceStr);

            try {
                int userChoiceInt = Integer.parseInt(userChoiceStr);
                if (userChoiceInt > 0 && userChoiceInt <= arraySize) {
                    long end = System.currentTimeMillis();
                    databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), -1, user.getId(), user.getMessage(), "OK", 1);
                    return userChoiceInt;
                } else {
                    String errorMessage = "К сожалению, я не понял ваш выбор или была допущена ошибка. Пожалуйста, повторите снова";
                    System.out.print(errorMessage);
                    long end = System.currentTimeMillis();
                    databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), -1, user.getId(), user.getMessage(), errorMessage, -666);
                    return getUserChoice(arraySize);
                }
            } catch (NumberFormatException e) {
                String errorMessage = "К сожалению, я не понял ваш выбор или была допущена ошибка. Пожалуйста, повторите снова";
                System.out.print(errorMessage);
                long end = System.currentTimeMillis();
                databaseManipulator.addQuery(user.getQueryId(), sdf.format(new Date()), (end - start), -1, user.getId(), user.getMessage(), errorMessage, -666);
                return getUserChoice(arraySize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }


}
