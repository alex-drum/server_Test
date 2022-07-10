package withoutHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.sql.*;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/testDB";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";
    private static final int COUNTS = 5;
    private static int counter = COUNTS;


    public static String fetchUserNames = "SELECT name FROM testDB.user";
    public static String insertQuery = "INSERT INTO `testDB`.`user` (`name`, `password`, `isLogged`) VALUES ('sdffsd', 'asdadf', '1');";
    public static String deleteQuery = "INSERT INTO `testDB`.`user` (`name`, `password`, `isLogged`) VALUES (NULL, 'arkjga', '1');";

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

/*    private static boolean checkUserNameAndPassword(String name, String password, Handler handler) {
        // Check if such user exists
        while(counter > 0) {
            boolean isNameValid = validateOldUserName(name);
            System.out.println("isNameValid: " + isNameValid);
            // If user exists, proceed to check password
            if (isNameValid) {
                boolean isPasswordValid = checkPassword(name, password);
                if (isPasswordValid) {
                    return true;
                } else {
                    System.out.println("Password is invalid, please try again.");
                    handler.write("Password is invalid, please try again.");
                    counter--;
                    System.out.println("Counter: " + counter);
                    logIn(handler);
                }
            } else {
                System.out.println("User nickname is invalid, please try again.");
                handler.write("User nickname is invalid, please try again.");
                counter--;
                System.out.println("Counter: " + counter);
                logIn(handler);
            }
        }
        return false;
    }*/
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
            System.out.println("Line 96 password: " + password);
            System.out.println("Line 97 password: " + user.getString("password"));
            if (password.equals(user.getString("password"))) {
                System.out.println("Return True");
                return true;
            } else {
                System.out.println("Return false");
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static void logIn(Handler handler) {
        String name= getUserName(handler);

        boolean isUserNameValid = validateOldUserName(name);
        if (isUserNameValid) {
            System.out.println("Username is valid, please proceed with password.");
            handler.write("Username is valid, please proceed with password.");
            String password = getPassword(handler);
            System.out.println("Password: " + password);

            boolean isPasswordValid = false;
            while (isPasswordValid == false && counter > 0) {
                isPasswordValid = checkPassword(name, password);
                if (isPasswordValid == false) {
                    counter--;
                    System.out.println("Password is invalid, please try again.");
                    handler.write("Password is invalid, please try again (Attempts left: " + counter + ").");
                    System.out.println("Counter: " + counter);
                    password = getPassword(handler);
                }

            }

        } else {
            System.out.println("User nickname is invalid, please try again.");
            handler.write("User nickname is invalid, please try again.");
            logIn(handler);
        }

            String query = "UPDATE `testDB`.`user` SET `isLogged` = '1' WHERE (`name` = '"
                    + name + "')";
            System.out.println(query);

            try {
                connection = DriverManager.getConnection(url, dbUser, dbPassword);
                statement = connection.createStatement();
                int i = statement.executeUpdate(query);
                if (i > 0) {
                    System.out.println("ROW UPDATED");
                    handler.write("Log-in successful");
                } else {
                    System.out.println("ROW NOT UPDATED");
                    handler.write("Log-in failed");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
    private static JSONArray getJSONArray(String sql) {
        JSONArray jsonArray = new JSONArray();

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
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