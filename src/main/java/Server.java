import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/testDB";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
//    private List<ClientHandler> clients;
//    private AuthService authService;
//
//    public AuthService getAuthService() {
//        return authService;
//    }

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Server awaits clients");
                Socket socket = server.accept();
                System.out.println("Client connected");
//                new ClientHandler(this, socket);
            }
        } catch (IOException ex) {
            System.out.println("Server error");
        }
    }

    public JSONArray getResultsJSONArray(String sql){
        java.sql.Statement statement;
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


//    public synchronized void unsubscribe(ClientHandler clientHandler) {
//        clients.remove(clientHandler);
//    }

//    public synchronized void subscribe(ClientHandler clientHandler) {
//        clients.add(clientHandler);
//    }

//    public synchronized void broadcast(String s) {
//        for(ClientHandler client: clients) {
//            client.sendMsg(s);
//        }
//    }
//
//    public synchronized boolean isNickLogged(String nick) {
//        for(ClientHandler client: clients) {
//            if (client.getName().equals(nick)) {
//                return true;
//            }
//        }
//        return false;
//    }

}