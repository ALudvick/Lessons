package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static BufferedReader reader;
    private static Duck[] players;

    public static void main(String[] args) throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите количество игроков: ");
        int numberOfPlayers = Integer.parseInt(reader.readLine());
        players = createPlayersList(numberOfPlayers);
        startGame();
    }

    private static void startGame() {
        boolean isActive = true;
        System.out.println("\nИгра началась!");

        try {
            String userInput = "";

            while (isActive) {
                System.out.print("Введите номер игрока: ");
                userInput = reader.readLine();
                if (userInput.toLowerCase().equals("exit")) isActive = false;
                else {
                    int number = Integer.parseInt(userInput) - 1;
                    if (number >= 0 && number < players.length) {
                        System.out.print("Введите слово: ");
                        players[number].setScore(reader.readLine());
                    }
                }
            }
        } catch (IOException ignored) {}


        System.out.println("\nРезультаты: ");
        for (Duck player : players) {
            System.out.printf("Очков у утки %s: %d\n", player.getNickname(), player.getScore());
        }
    }

    private static Duck[] createPlayersList(int playersCount) {
        Duck[] players = new Duck[playersCount];

        for (int i = 0; i < players.length; i++) {
            players[i] = createPlayer();
        }

        System.out.println("\nВсе персонажи созданы: ");
        for (Duck pl : players) {
            System.out.println(pl);
        }

        return players;
    }

    private static Duck createPlayer() {
        try {
            System.out.println("Создается игрок...");
            System.out.print("Введите имя: ");
            String nickname = reader.readLine();
            System.out.print("Введите возраст: ");
            int age = Integer.parseInt(reader.readLine());
            System.out.print("Ты танк или не танк (y/n): ");
            String userPosition = reader.readLine();
            boolean position;
            if (userPosition.toLowerCase().equals("y")) position = true;
            else if (userPosition.toLowerCase().equals("n")) position = false;
            else {
                System.out.println("Я так и не понял, кто ты, поэтому ты хил...");
                position = false;
            }

            Duck duck = new Duck(nickname, age, position);
            System.out.println("Персонаж создан!");
            return duck;

        } catch (IOException ignored) {
            return null;
        }
    }
}
