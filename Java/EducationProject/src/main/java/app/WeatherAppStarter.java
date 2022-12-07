package app;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class WeatherAppStarter {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi test = new TelegramBotsApi(DefaultBotSession.class);
        test.registerBot(new WeatherTelegramBot());

    }
}
