package databases;

import java.sql.*;
import java.util.List;

public class DBTest {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/java/databases/test.db");
            //connection.setAutoCommit(false);
            statement = connection.createStatement();
            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students(student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "register_time INTEGER, " +
                    "first_name TEXT, " +
                    "second_name TEXT, " +
                    "nickname TEXT, " +
                    "birthday TEXT);";
            statement.execute(createStudentsTable);

            String createLectureTable = "CREATE TABLE IF NOT EXISTS lectures(lecture_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER, " +
                    "lecture_title TEXT, " +
                    "lecture_date TEXT, " +
                    "FOREIGN KEY (student_id) REFERENCES students (student_id));";

            statement.execute(createLectureTable);

            String[] aleksandr = {
                    String.valueOf(System.currentTimeMillis()),
                    "Aleksandr",
                    "Agalarov",
                    "Ludvick",
                    "1988-03-05 00:00:00.000"
            };
            String[] sergey = {
                    String.valueOf(System.currentTimeMillis()),
                    "Sergey",
                    "Zizdok",
                    "ZiS",
                    "1988-04-23 00:00:00.000"
            };
            String[] igor = {
                    String.valueOf(System.currentTimeMillis()),
                    "Igor",
                    "Dronov",
                    "Pentaigor1999",
                    "1990-09-22 00:00:00.000"
            };

//            List<String[]> students = new ArrayList<>();
//            students.add(aleksandr);
//            students.add(sergey);
//            students.add(igor);

            List<String[]> students = List.of(aleksandr, sergey, igor);
            simpleInsertMethod(students);

            String selectCurrentStudent = "SELECT first_name, birthday FROM students;";
            ResultSet resultSet = statement.executeQuery(selectCurrentStudent);
            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    System.out.print(resultSet.getMetaData().getColumnName(i) + " -> " + resultSet.getString(i) + "; \t");
                }
                System.out.println("\n=======================");
            }

//            while (resultSet.next()) {
//                System.out.println(resultSet.getString(1)
//                        + " -> " + resultSet.getString(2));
//            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void simpleInsertMethod(List<String[]> students) throws SQLException {
        for (String[] st : students) {
            String firstInsert = "INSERT INTO students (register_time, first_name, second_name, nickname, birthday) " +
                    "VALUES (" + st[0] + ", '" + st[1] + "', '" + st[2] + "', '" + st[3] + "', '" + st[4] + "')";
            statement.execute(firstInsert);
//            connection.commit();
        }
    }
}
