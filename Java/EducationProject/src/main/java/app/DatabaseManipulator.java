package app;

import java.sql.*;

public class DatabaseManipulator {
    private Connection connection;
    private Statement statement;

    private PreparedStatement cityPreparedStatement;
    private PreparedStatement userPreparedStatement;
    private PreparedStatement queryPreparedStatement;

    private final String host;
    private int port;

    public DatabaseManipulator(String host, int port) {
        this.host = host;
        this.port = port;
        connectToSQLiteDatabase();
    }

    public DatabaseManipulator(String host) {
        this.host = host;
        connectToSQLiteDatabase();
    }

    private void connectToSQLiteDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + host);
            statement = connection.createStatement();
            System.out.println("Connection to SQLite has been established.");
            System.out.println("Try to create all tables...");
            System.out.println("Result: " + createTables());
            prepareStatements();
            System.out.println("Statements prepared");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int createTables() {
        String[] queries = {
                "PRAGMA foreign_keys=on;",
                "CREATE TABLE IF NOT EXISTS CITIES (city_id integer primary key, city_name text, cord_lat realm, cord_lon real);",
                "CREATE TABLE IF NOT EXISTS USERS (register_date text, user_id integer primary key, user_name text, user_role text)",
                "CREATE TABLE IF NOT EXISTS USER_QUERIES (log_id INTEGER PRIMARY KEY AUTOINCREMENT, query_id text NOT NULL, query_date text, answer_time INTEGER, city_id INTEGER, user_id INTEGER, user_query text, response text, result_code INTEGER, FOREIGN KEY (city_id) REFERENCES cities (city_id), FOREIGN KEY (user_id) REFERENCES users (user_id));",
                "INSERT OR IGNORE INTO CITIES(city_id, city_name, cord_lat, cord_lon) VALUES (-1, 'UNKNOWN', 0, 0)"
        };

        try {
            for (String query : queries) {
                // System.out.println(query);
                statement.execute(query);
            }
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return -666;
        }
    }

    private void prepareStatements() {
        try {
            String prepareCityInsert = "INSERT OR IGNORE INTO cities (city_id, city_name, cord_lat, cord_lon) VALUES (?, ?, ?, ?);";
            cityPreparedStatement = connection.prepareStatement(prepareCityInsert);

            String prepareUserInsert = "INSERT OR IGNORE INTO USERS (register_date, user_id, user_name, user_role) VALUES (?, ?, ?, ?);";
            userPreparedStatement = connection.prepareStatement(prepareUserInsert);

            String prepareUserQueryInsert = "INSERT INTO USER_QUERIES (log_id, query_id, query_date, answer_time, city_id, user_id, user_query, response, result_code) VALUES ((SELECT MAX(log_id) + 1 FROM USER_QUERIES), ?, ?, ?, ?, ?, ?, ?, ?);";
            queryPreparedStatement = connection.prepareStatement(prepareUserQueryInsert);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void addCity(long cityId, String cityName, double lat, double lon) {
        try {
            cityPreparedStatement.setLong(1, cityId);
            cityPreparedStatement.setString(2, cityName);
            cityPreparedStatement.setDouble(3, lat);
            cityPreparedStatement.setDouble(4, lon);
            cityPreparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void addUser(String registerDate, int userId, String userName, String userRole) {
        try {
            userPreparedStatement.setString(1, registerDate);
            userPreparedStatement.setInt(2, userId);
            userPreparedStatement.setString(3, userName);
            userPreparedStatement.setString(4, userRole);
            userPreparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void addQuery(String queryId, String queryDate, long answerTime, long cityId, int userId, String userQuery, String response, int resultCode) {
        try {
            queryPreparedStatement.setString(1, queryId);
            queryPreparedStatement.setString(2, queryDate);
            queryPreparedStatement.setLong(3, answerTime);
            queryPreparedStatement.setLong(4, cityId);
            queryPreparedStatement.setInt(5, userId);
            queryPreparedStatement.setString(6, userQuery);
            queryPreparedStatement.setString(7, response);
            queryPreparedStatement.setInt(8, resultCode);
            queryPreparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
