package app;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws IOException {
        App app = new App();
        app.weatherSimpleRequest();
    }

    // standard method
    public static void standardMethod() throws IOException {
        URL url = new URL("https://www.google.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }

        System.out.println(stringBuilder.toString());
    }

    // lib method
    public static void libMethod() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(new URL("https://www.google.com")).build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    // get weather method
    public void weatherSimpleRequest() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("weather.properties"));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(new URL("https://api.openweathermap.org/data/2.5/weather?" +
                        "lat=44.34" +
                        "&lon=10.99" +
                        "&appid=" + properties.getProperty("token") +
                        "&units=metric"))
                .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

//        System.out.println(result);

//        Pattern pattern = Pattern.compile("\"temp\":(.*?),");
//        Matcher matcher = pattern.matcher(result);
//        if (matcher.find()) System.out.println("Температура: " + matcher.group(1));

        Gson gson = new Gson();

        JsonObject mainJson = gson.fromJson(result, JsonObject.class);
        JsonObject tempJson = gson.fromJson(mainJson.get("main"), JsonObject.class);
        System.out.println("Температура: " + tempJson.get("temp"));
    }
}
