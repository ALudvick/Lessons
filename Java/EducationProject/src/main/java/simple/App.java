package simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Введите число A: ");
            int userInputA = Integer.parseInt(reader.readLine());
            System.out.print("Введите число B: ");
            int userInputB = Integer.parseInt(reader.readLine());
            double result = div(userInputA, userInputB);
            System.out.println("RESULT: " + result);

        } catch (NumberFormatException | IOException e) {
            System.out.println("Вы ввели не число...");
        } catch (ArithmeticException e) {
            System.out.println("Поймал!");
        }
    }

    private static int div(int userInputA, int userInputB)  throws ArithmeticException {
        return userInputA / userInputB;
    }
}
