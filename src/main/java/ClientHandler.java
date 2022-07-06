import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

        private Server server;
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
//        private String name;

//        public String getName() {
//            return name;
//        }

        public ClientHandler(Server server, Socket socket) {
            this.server = server;
            this.socket = socket;
//            this.name = "";
            try {
                this.in = new DataInputStream(socket.getInputStream());
                this.out = new DataOutputStream(socket.getOutputStream());
                new Thread(()-> {
                    try {
//                        authenticate();
//                        readMessages();
                    }
                    finally {
                        closeConnection();
                    }
                }).start();
            } catch (IOException ex) {
                throw new RuntimeException("Client creation error");
            }
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
