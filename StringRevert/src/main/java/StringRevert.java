import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringRevert {

    public static void main(String[] args) {
        String initialString = "";
        String revertedString = "";

        try {
            System.out.println("Please enter your string:");
            initialString = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        revertedString = new StringBuilder(initialString).reverse().toString();

        System.out.println("Initial string: " + initialString);
        System.out.println("Reverted string: " + revertedString);

        revertXTimes(initialString, 1000);
        revertXTimes(initialString, 10000);
        revertXTimes(initialString, 100000);

    }

    private static void revertXTimes(String initialString, int iterations) {
        int duration = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
        String revertedString = new StringBuilder(initialString).reverse().toString();
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("Program is finished after " + (finishTime - startTime) + " ms.");
    }
}
