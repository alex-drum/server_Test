package WithHandler_Error;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final int PORT = 8189;
    public static boolean authorized = false;
    public static String password;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            boolean isNewUser = isNewUser();
            while (authorized == false) {
                if (isNewUser == true) {
                    boolean isNameVacant = isNameVacant();
                }
                passPassword();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void passPassword () {
        try {
            Socket socket = new Socket("localhost", PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            password = new BufferedReader(new InputStreamReader(System.in)).readLine();
            out.writeUTF(password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNameVacant() {
        while (authorized == false) {
            System.out.println("Please enter your nickname: ");
            String userName = null;
            try {
                userName = new BufferedReader(new InputStreamReader(System.in)).readLine();
                Socket socket = new Socket("localhost", PORT);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(userName);
                boolean response = in.readBoolean();
                System.out.println(response);
                if (response == true) {
                    authorized = true;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static boolean isNewUser() {
        System.out.println("Are you a registered user? (Y/N)");
        String answer = null;
        try {
            answer = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (answer.equalsIgnoreCase("N")) {
            return true;
        } else return false;
    }

    public static String getPassword () {
        return password;
    }

//    private static Thread runOutputThread(DataOutputStream out) {
//        Thread thread = new Thread(() -> {
//            while (!Thread.currentThread().isInterrupted()) {
//                Scanner scanner = new Scanner(System.in);
//                while (true) {
//                    String message = scanner.nextLine();
//                    try {
//                        out.writeUTF(message);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (message.equals("/end")) {
//                        break;
//                    }
//                }
//            }
//        });
//        thread.start();
//        return thread;
//    }
}

