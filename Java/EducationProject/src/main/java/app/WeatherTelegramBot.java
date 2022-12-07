package app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class WeatherTelegramBot extends TelegramLongPollingBot {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Properties properties = new Properties();
    private static WeatherApp weatherApp;
    private User user;
    private static final DatabaseManipulator databaseManipulator = new DatabaseManipulator("src/main/resources/weather.db");
    private boolean isCalling = true;
    JsonArray mainJson;
    JsonObject jsonObject;

    static {
        try {
            properties.load(WeatherApp.class.getClassLoader().getResourceAsStream("weather.properties"));
            databaseManipulator.connectToSQLiteDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Ludvick";
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("telegram");
    }

    @Override
    public void onUpdateReceived(Update update) {
        weatherApp = new WeatherApp(properties.getProperty("weather"));
        long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getUserName();
        String userRole;
        if (userId != 354382653) userRole = "User";
        else userRole = "Admin";

        user = new User(userId, userName, userRole);
        databaseManipulator.addUser(DATE_FORMAT.format(new Date()), user.getId(), user.getName(), user.getRole());

        user.setMessageId(UUID.randomUUID().toString());
        user.setMessageText(update.getMessage().getText());
        sendMessage(update.getMessage().getChatId(), user.getMessageText());
    }

    private void sendMessage(long chatId, String userMessage) {
        String resultMessage;
        int resultCode;
        long cityId;

        long start = System.currentTimeMillis();
        if (!userMessage.equals("/start")) {
            if (isCalling) {
                cityId = -1;
                try {
                    mainJson = weatherApp.getJsonByCityName(userMessage);
                    resultMessage = weatherApp.getCitiesList(mainJson);
                    resultCode = 1;
                    isCalling = false;
                } catch (IllegalArgumentException | IOException e) {
                    resultCode = -666;
                    resultMessage = "Неизвестный город";
                }
                long end = System.currentTimeMillis();

                databaseManipulator.addQuery(user.getMessageId(),
                        DATE_FORMAT.format(new Date()),
                        (end - start),
                        cityId,
                        user.getId(),
                        user.getMessageText(),
                        resultMessage,
                        resultCode
                );

            } else {
                try {
                    System.out.println(mainJson);
                    int idx = Integer.parseInt(userMessage) - 1;
                    if (idx >= 0 && idx < mainJson.getAsJsonArray().size()) {
                        jsonObject = mainJson.getAsJsonArray().get(Integer.parseInt(String.valueOf(idx))).getAsJsonObject();
                        String lat = jsonObject.get("lat").toString();
                        String lon = jsonObject.get("lon").toString();
                        cityId = (long) weatherApp.getWeatherByCoordinates(lat, lon).keySet().toArray()[0];
                        resultMessage = weatherApp.getWeatherByCoordinates(lat, lon).values().toArray()[0].toString();
                        resultCode = 1;

                        databaseManipulator.addCity(cityId,
                                jsonObject.get("name").getAsString(),
                                Double.parseDouble(lat),
                                Double.parseDouble(lon)
                        );

                        isCalling = true;
                    } else {
                        resultCode = -666;
                        cityId = -1;
                        resultMessage = "Я не понял ваш выбор";
                    }
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | IOException e) {
                    cityId = -1;
                    resultCode = -666;
                    resultMessage = "Я не понял ваш выбор";
                }

                long end = System.currentTimeMillis();

                databaseManipulator.addQuery(user.getMessageId(),
                        DATE_FORMAT.format(new Date()),
                        (end - start),
                        cityId,
                        user.getId(),
                        user.getMessageText(),
                        resultMessage,
                        resultCode
                );
            }

            try {
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text(resultMessage)
                        .build();
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
