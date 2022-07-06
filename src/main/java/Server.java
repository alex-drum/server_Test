import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int PORT = 8189;
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