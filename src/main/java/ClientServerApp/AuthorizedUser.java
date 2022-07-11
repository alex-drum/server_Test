package ClientServerApp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class AuthorizedUser {
    private final String userName;
    private final Handler handler;

    public AuthorizedUser(String name, Handler handler) {
        this.userName = name;
        this.handler = handler;
        System.out.println("User \"" + this.userName + "\" is activated");
        boolean exit = false;

        while (!exit) {
            int action = getActionNumber();
            System.out.println("Action: " + action);

            switch (action) {
                case 1:
                    handler.write("/getPetsArray");
                    printPetsInfo(getPetsArray());
                    break;
                case 2:
                    handler.write("/getPetOnID");
                    System.out.println("Please enter pet ID: ");
                    int petID = new Scanner(System.in).nextInt();
                    printPet(getPetOnID(petID));
                    break;
                case 3:
                    handler.write("/createNewPet");
                    createNewPet();
                    break;
                case 4:
                    handler.write("/editPetInfo");
                    editPetInfo();
                    break;
                case 5:
                    handler.write("/deletePet");
                    deletePet();
                    break;
                case 9:
                    System.out.println("Good bye!");
                    exit = true;
                    handler.write("/closeConnection");
                    break;

            }
        }
    }

    private void deletePet() {
        JsonArray pets = getPetsArray();
        printPetsInfo(pets);
        System.out.print("\n" + "Which pet you want to delete? Please enter pet ID: ");
        int petID = new Scanner(System.in).nextInt();
        String petJSONString = new String("{\"idPet\":" + petID + "}");

        handler.write(petJSONString);
        String response = handler.read();
        System.out.println(response);

    }

    private void editPetInfo() {
        JsonArray pets = getPetsArray();
        printPetsInfo(pets);
        System.out.print("\n" + "Which pet you want to edit? Please enter pet ID: ");
        int petID = new Scanner(System.in).nextInt();

        String petName = "";
        String birthday = "";
        String sex = "";

        JsonObject pet = new JsonObject();
        try {
            System.out.print("Please enter new name: ");
            petName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Please enter new birthday (YYYY-MM-DD): ");
            birthday = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Please enter new sex (M/F): ");
            sex = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pet.addProperty("idPet", petID);
        pet.addProperty("petName", petName);
        pet.addProperty("birthday", birthday);
        pet.addProperty("sex", sex);
        pet.addProperty("petOwner", userName);
        System.out.println("L84 pet: " + pet);

        handler.write(pet.toString());
        String response = handler.read();
        System.out.println(response);

    }

    private void createNewPet() {
        String petName = null;
        String birthday = null;
        String sex = null;

        try {
            System.out.print("Enter pet name: ");
            petName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Enter pet birthday (YYYY-MM-DD): ");
            birthday = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Enter pet sex (M/F): ");
            sex = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject newPet = new JsonObject();
        newPet.addProperty("petOwner", userName);
        newPet.addProperty("petName", petName);
        newPet.addProperty("birthday", birthday);
        newPet.addProperty("sex", sex);

        handler.write(newPet.toString());
        String response = handler.read();
        System.out.println("\n" + response + "\n");

    }

    private void printPet(JsonObject pet) {
        System.out.println("Your pet info:\n" + pet);
    }

    private JsonObject getPetOnID(int petID) {
        JsonObject pet = new JsonObject();
        pet.addProperty("idPet", petID);
        handler.write(pet.toString());

        String petString = handler.read();
        petString = petString.replace("[", "");
        petString = petString.replace("]", "");
        pet = new JsonParser().parse(petString).getAsJsonObject();
        return pet;
    }

    private JsonArray getPetsArray() {
        JsonArray petsArray = new JsonArray();
        JSONObject user = new JSONObject();
        user.put("petOwner", this.userName);
        System.out.println("Line69: " + user);
        handler.write(user.toString());

        String petsString = handler.read();
        System.out.println("Line73: " + petsString);
        petsString = petsString.replace("},{", "};{");
        petsString = petsString.replace("[", "");
        petsString = petsString.replace("]", "");
        String[] pets = petsString.split(";");

        for (int i = 0; i < pets.length; i++) {
            JsonObject petJson = new JsonParser().parse(pets[i]).getAsJsonObject();
            petsArray.add(petJson);
        }
        return petsArray;
    }

    private void printPetsInfo(JsonArray petsArray) {
        System.out.println("Below is your pets info:");
        for (JsonElement pet : petsArray
        ) {
            System.out.println(pet);
        }
        System.out.println("");
    }

    private int getActionNumber() {
        System.out.println("What action would you like to perform? (press 9 to exit)\n" +
                "- Get a list of your pets (1)\n" +
                "- Get a pet info (pet ID is required (2)\n" +
                "- Create a new pet (3)\n" +
                "- Edit your pet info (4)\n" +
                "- Delete your pet (5)\n");

        return new Scanner(System.in).nextInt();
    }
}
