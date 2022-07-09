package withoutHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/testDB";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";

    public static String fetchQuery = "SELECT * FROM testDB.user";
    public static String insertQuery = "INSERT INTO `testDB`.`user` (`name`, `password`, `isLogged`) VALUES ('sdffsd', 'asdadf', '1');";
    public static String deleteQuery = "INSERT INTO `testDB`.`user` (`name`, `password`, `isLogged`) VALUES (NULL, 'arkjga', '1');";

    private static Connection con;
    private static Statement stmt;

/*    public static JSONArray getJsonArray() {
        return getResultsJSONArray(fetchQuery);
    }*/

    private static JSONArray jsonArray;

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started.");

            while (true) {
                try(
                    Handler handler = new Handler(server);
                ) {
                    String request = handler.read();
                    boolean isRegistered = checkUser(request);
                    String message = new Boolean(isRegistered).toString();
                    handler.write(message);
                    System.out.println(isRegistered);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("WithHandler_Error.Server error");
        }
    }

    private static boolean checkUser (String name) {
        JSONArray users = getJSONArray(fetchQuery);
        boolean flag = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (name.equals(user.getString("name"))) {
                flag = false;
                System.out.println("These nickname is already in use. Try another one.");
                return flag;
            } else flag = true;
        }
        return flag;
    }
    private static JSONArray getJSONArray(String sql){
        Statement statement;
        JSONArray jsonArray = new JSONArray();

        try {
            con = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            Integer i = 0;
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()){
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j < columnCount + 1; j++ ) {
                    String key = rsmd.getColumnLabel(j);
                    jsonObject.put(key, rs.getObject(key));
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