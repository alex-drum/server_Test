package withoutHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import com.google.gson.Gson;
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
                    checkUser(handler);
                    signIn(handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("WithHandler_Error.Server error");
        }
    }

    private void signIn(Handler handler) {
        String newUserJSONString = handler.read();
        System.out.println(newUserJSONString);
        JsonObject newUser = new JsonParser().parse(newUserJSONString).getAsJsonObject();
        System.out.println("JSON Object: " + newUser.get("name").getAsString());

        String query = "insert into `testDB`.`user` (`name`, `password`, `isLogged`) VALUES " +
                "('" + newUser.get("name").getAsString() + "','"
                + newUser.get("password").getAsString()
                + "','" + newUser.get("isLogged") + "')";
//        System.out.println(query);

        try {
            connection = DriverManager.getConnection(url,dbUser,dbPassword);
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
            isNameVacant = validateName(request);
            String message = new Boolean(isNameVacant).toString();
            handler.write(message);
        }
    }
    private static boolean validateName(String name) {
        JSONArray users = getJSONArray(fetchUserNames);
//        System.out.println(users.toString());
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
//            System.out.println(user.toString());
//            System.out.println(user.getString("name"));
            if (name.equals(user.getString("name"))) {
                flag = false;
//                System.out.println("Nickname \"" + name + "\" is already in use. Try another one.");
                return flag;
            } else {
//                System.out.println("Nickname \"" + name + "\" is vacant.");
                flag = true;
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
            Integer i = 0;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    jsonObject.put(key, resultSet.getObject(key));
                    System.out.println(jsonObject.toString());
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