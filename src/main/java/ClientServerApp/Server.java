package ClientServerApp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/testDB";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";
    private static final int COUNTS = 10;
    private static int counter = COUNTS;
    public static String fetchUserNames = "SELECT name FROM testDB.user";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started.");

            while (true) {
                try (
                        Handler handler = new Handler(server);
                ) {
                    String request = handler.read();
                    System.out.println("Request: " + request);
                    if (request.equals("/signIn")) {
                        checkUser(handler);
                        signIn(handler);
                    } else if (request.equals("/logIn")) {
                        logIn(handler);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("WithHandler_Error.Server error");
        }
    }


    private static boolean checkPassword(String name, String password) {
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT password FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }
            if (password.equals(user.getString("password"))) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static void logIn(Handler handler) {
        String name = getUserName(handler);

        boolean isUserNameValid = validateOldUserName(name);
        if (isUserNameValid) {
            boolean isUserNotRestricted = checkIfUserIsNotRestricted(name);
            if (!isUserNotRestricted) {
                int minutesLeft = calcMinutesLeft(name);
                if (minutesLeft > 0) {
                    System.out.println("You have no attempts left. Please try again in " + minutesLeft + " minutes.");
                    handler.write("You have no attempts left. Please try again in " + minutesLeft + " minutes.");
                }
            }
        }

        boolean isPasswordValid = false;

        if (isUserNameValid) {
            System.out.println("Username is valid, please proceed with password.");
            handler.write("Username is valid, please proceed with password.");
            String password = getPassword(handler);
            long firstAttemptTime = System.currentTimeMillis();
            updateLogInTime(name, firstAttemptTime);

            while (!isPasswordValid && counter > 1) {
                isPasswordValid = checkPassword(name, password);
                if (!isPasswordValid) {
                    counter--;
                    System.out.println("Password is invalid, please try again.");
                    handler.write("Password is invalid, please try again (Attempts left: " + counter + ").");
                    password = getPassword(handler);
                }

            }

        } else {
            System.out.println("User nickname is invalid, please try again.");
            handler.write("User nickname is invalid, please try again.");
            logIn(handler);
        }

        if (isPasswordValid && counter > 0) {
            String query = "UPDATE `testDB`.`user` SET `isLogged` = '1', `LogInFirstAttemptTime` = NULL WHERE (`name` = '"
                    + name + "')";

            try {
                connection = DriverManager.getConnection(url, dbUser, dbPassword);
                statement = connection.createStatement();
                int i = statement.executeUpdate(query);
                if (i > 0) {
                    System.out.println("ROW UPDATED");
                    handler.write("Log-in successful");
                    counter = COUNTS;
                } else {
                    System.out.println("ROW NOT UPDATED");
                    handler.write("Log-in failed");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            System.out.println("You have already used all attempts. Please try in 1 hour.");
            handler.write("You have already used all attempts. Please try in 1 hour.");
        }
    }

    private static int calcMinutesLeft(String name) {
        long minutesLeft = 0;
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LogInFirstAttemptTime FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }

            long firstAttemptTime = user.getLong("LogInFirstAttemptTime");
            long currentTime = System.currentTimeMillis();
            if (currentTime - firstAttemptTime < 3600000) {
                minutesLeft = 60 - TimeUnit.MILLISECONDS.toMinutes(currentTime - firstAttemptTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Long l = new Long(minutesLeft);
        int intMinutesLeft = l.intValue();
        return intMinutesLeft;
    }

    private static boolean checkIfUserIsNotRestricted(String name) {
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LogInFirstAttemptTime FROM testDB.user WHERE (`name` = '" + name + "')");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            System.out.println(rsmd.toString());
            int columnCount = rsmd.getColumnCount();
            System.out.println(columnCount);

            JSONObject user = new JSONObject();
            if (resultSet.next()) {
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    user.put(key, resultSet.getObject(key));
                }
            }
            if (user.has("LogInFirstAttemptTime")) {

                return false;
            } else {
                System.out.println("Return True");
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static void updateLogInTime(String name, long firstAttemptTime) {
        String query = "UPDATE `testDB`.`user` SET `LogInFirstAttemptTime` = '" + firstAttemptTime + "' WHERE (`name` = '"
                + name + "')";
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            System.out.println("UpdateLogInTime: " + i);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getPassword(Handler handler) {
        String userJSONString = handler.read();
        JsonObject user = new JsonParser().parse(userJSONString).getAsJsonObject();
        String password = user.get("password").getAsString();
        return password;
    }

    private static String getUserName(Handler handler) {
        String userJSONString = handler.read();
        JsonObject user = new JsonParser().parse(userJSONString).getAsJsonObject();
        String name = user.get("name").getAsString();
        return name;
    }

    private static void signIn(Handler handler) {
        String newUserJSONString = handler.read();
        JsonObject newUser = new JsonParser().parse(newUserJSONString).getAsJsonObject();

        String query = "insert into `testDB`.`user` (`name`, `password`, `isLogged`) VALUES " +
                "('" + newUser.get("name").getAsString() + "','"
                + newUser.get("password").getAsString()
                + "','" + newUser.get("isLogged") + "')";

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (i > 0) {
                System.out.println("ROW INSERTED");
                handler.write("Check-in successful");
            } else {
                System.out.println("ROW NOT INSERTED");
                handler.write("Check-in failed");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void checkUser(Handler handler) {
        boolean isNameVacant = false;
        while (!isNameVacant) {
            String request = handler.read();
            System.out.println("Request: " + request);
            isNameVacant = validateNewUserName(request);
            String message = new Boolean(isNameVacant).toString();
            handler.write(message);
        }
    }

    private static boolean validateNewUserName(String name) {
        JSONArray users = getJSONArray(fetchUserNames);
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (name.equals(user.getString("name"))) {
                flag = false;
                return flag;
            } else {
                flag = true;
            }
        }
        return flag;
    }

    private static boolean validateOldUserName(String name) {
        JSONArray users = getJSONArray(fetchUserNames);
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (name.equals(user.getString("name"))) {
                flag = true;
                return flag;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    private static JSONArray getJSONArray(String query) {
        JSONArray jsonArray = new JSONArray();

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int i = 0;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    jsonObject.put(key, resultSet.getObject(key));
                }
                jsonArray.put(i, jsonObject);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

}