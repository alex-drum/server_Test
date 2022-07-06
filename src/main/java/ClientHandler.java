import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        new Thread(() -> {
            try {
                this.in = new DataInputStream(socket.getInputStream());
                this.out = new DataOutputStream(socket.getOutputStream());
               while (true) {
                   if (in.available() > 0) {
                       String nameToCheck = in.readUTF();
                       boolean isNameVacant = isNameVacant(nameToCheck);
                       System.out.println(nameToCheck + "is vacant? - " + isNameVacant);
                   }
               }
//                        authenticate();
//                        readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                    finally {             // TODO потом надо понять, в какой момент раскомментить
//                        closeConnection();
//                    }
        }).start();
    }


    private boolean isNameVacant(String userName) {
        JSONArray jsonArray = server.getResultsJSONArray(server.fetchQuery);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (userName.equals(jsonObject.getString("name"))) {
                System.out.println("This name is occupied");
                return false;
            }
        }
        System.out.println("This name is vacant");
        return true;
    }

    private void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//            myServer.unsubscribe(this);
//            myServer.broadcast("User " + name + " left");
    }

}
