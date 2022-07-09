package withoutHandler;

import org.json.JSONObject;

import java.io.*;
import java.net.UnknownHostException;

public class Client {
    private static final int PORT = 8189;
    private static final String IP = "localhost";

    public static void main(String[] args) {
        try (
                Handler handler = new Handler(IP, PORT);
                )
        {
            System.out.println("Connected to server!");
           boolean isRegistered = checkIfRegistered(handler);

            if (isRegistered) {
                handler.write("/logIn");
                logIn(handler);
            } else {
                handler.write("/checkIn");
                signIn(handler);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logIn(Handler handler) {
        System.out.println("Please enter your nickname: ");
        String name = "";
        try {name = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Please enter your password: ");
        String password = "";
        try {password = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject user = new JSONObject();
        user.put("name", name);
        user.put("password", password);
        user.put("isLogged", 1);

        handler.write(user.toString());
        String response = handler.read();
        System.out.println(response);
    }
    private static void signIn(Handler handler) {
       String name = validateName(handler);
        checkIn(name, handler);

    }
    private static void checkIn(String name, Handler handler) {
       String password = "";
        System.out.println("Please enter your password: ");
       try {
           password = new BufferedReader(new InputStreamReader(System.in)).readLine();
       } catch (IOException e) {
           e.printStackTrace();
       }
        JSONObject newUser = new JSONObject();
       newUser.put("name", name);
       newUser.put("password", password);
       newUser.put("isLogged", 1);
       handler.write(newUser.toString());
       String response = handler.read();
        System.out.println(response);
    }
    private static String validateName(Handler handler) {
        String isVacant = "";
        while (!isVacant.equalsIgnoreCase("true")) {
            System.out.println("Please enter you name, we would first validate it: ");
            String name = "";
            try {
                name = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.write(name);
            isVacant = handler.read();

            if (isVacant.equalsIgnoreCase("true")) {
                System.out.println("The name \"" + name + "\" is vacant!");
                return name;
            } else {
                System.out.println("The name \"" + name + "\" is used, try another one!");
            }
        }
        return null;
    }
    private static boolean checkIfRegistered(Handler handler) {
        String request = "";
        while (!request.equalsIgnoreCase("S") ||
                !request.equalsIgnoreCase("L") ) {
            System.out.println("SIGN IN (S) / LOG IN (L)");
            try {
                request = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (request.equalsIgnoreCase("S")) {
                return false;
            } else if (request.equalsIgnoreCase("L")) {
                return true;
            }
        }
        return false;
    }

}


