package withoutHandler;

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
                handler.write("We are ready to log in!");
//                logIn(String name, String password);
            } else {
                signIn(handler);
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void signIn(Handler handler) {
        boolean isNameVacant = validate(handler);
    }

    private static boolean validate(Handler handler) {
        System.out.println("Please enter you name, we would firs validate it: ");
        String name = null;
        try {
            name = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.write(name);
        String isVacant = handler.read();
        if (isVacant.equalsIgnoreCase("true")) {
            System.out.println("The name " + name + " is vacant!");
            return true;
        } else {
            System.out.println("The name " + name + " is used, try another one!");
            return false;
        }
    }

    private static boolean checkIfRegistered(Handler handler) {
        System.out.println("SIGN IN (S) / LOG IN (L)");
        String request = null;
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
        return false;
    }


}

            /*boolean isNewUser = isNewUser();
            if(isNewUser) {
                userName = getUserName();
                JSONArray allUsers = Server.getJsonArray();
                System.out.println(allUsers);
                boolean isNameVacant =  isNameVacant(userName, allUsers);
                while (isNameVacant == false) {
                    userName = getUserName();
                    isNameVacant =  isNameVacant(userName, allUsers);
                }
                System.out.println("Vacant!!!");
                System.out.println(userName);
                password = getPassword();
                String newUserJSONString = "{\"password\":\"" + password +
                        "\",\"name\":\"" + userName + "\",\"isLogged\":1}";
//                        {"password":"112233","name":"alex_1986","isLogged":0,"id-user":1}
                out.writeUTF(newUserJSONString);*/
         /*   }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
//    }

/*    private static String getPassword() {
        System.out.println("Enter your password: ");
        try {
            return (new BufferedReader(new InputStreamReader(System.in)).readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static boolean isNameVacant(String currentUserName, JSONArray allUsers) {
        boolean flag = false;
        for (int i = 0; i < allUsers.length(); i++) {
            JSONObject user = allUsers.getJSONObject(i);
            if (currentUserName.equals(user.getString("name"))) {
                flag = false;
                System.out.println("These nickname is already in use. Try another one.");
                return flag;
            } else flag = true;
        }
        return flag;
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
    private static String getUserName() {
        System.out.print("Please enter your nickname: ");
        try {
            return (new BufferedReader(new InputStreamReader(System.in))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/


