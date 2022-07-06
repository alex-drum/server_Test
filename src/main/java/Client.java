import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
//    public static void setAuthorized(boolean authorized) {
//        Client.authorized = authorized;
//    }

//    static boolean authorized;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8189);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            boolean isNewUser = isNewUser();
            if (isNewUser) {
                boolean isNameVacant = isNameVacant();
            }
            out.writeUTF("/auth login1 pass1");
//            setAuthorized(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            if (in.available() > 0) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.startsWith("/authOk")) {
//                                    setAuthorized(true);
                                    System.out.println("Authorized on server");
                                    Client.runOutputThread(out);
                                    break;
                                }
                                System.out.println(strFromServer + "\n");
                            }
                        }
                        while (true) {
                            if (in.available() > 0) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.equalsIgnoreCase("/end")) {
                                    break;
                                }
                                System.out.println(strFromServer);
                                System.out.println("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNameVacant() {
        System.out.println("Please enter your nickname: ");
        String userName = null;
        try {
            userName = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(userName);
        return false;
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


    private static Thread runOutputThread(DataOutputStream out) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String message = scanner.nextLine();
                    try {
                        out.writeUTF(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (message.equals("/end")) {
                        break;
                    }
                }
            }
        });
        thread.start();
        return thread;
    }
}

