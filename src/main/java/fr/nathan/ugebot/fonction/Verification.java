package fr.nathan.ugebot.fonction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Verification {

    public static HashMap<String, String> getFile() throws IOException {
        HashMap<String, String> verified = new HashMap<>();
        String filename = "verif.csv";

        FileReader reader = new FileReader(filename);
        BufferedReader br = new BufferedReader(reader);

        br.read();
        String line;
        while((line = br.readLine()) != null) {
            String[] list = line.split(";");
            String userid = list[0];
            String numetu = list[1];
            verified.put(userid, numetu);
        }
        return verified;
    }

    public static boolean checkUser(String userId) throws IOException {
        // If already verified return false else return true
        HashMap<String, String> verified = getFile();
        return verified.containsKey(userId);
    }

    public static boolean checkStudent(String numEtud) throws IOException {
        HashMap<String, String> verified = getFile();
        return verified.containsValue(numEtud);
    }

    public static void addToFile(String append) throws IOException {
        String filename = "verif.csv";

        FileWriter writer = new FileWriter(filename, true);

        writer.write("\n" + append);
        writer.close();

    }
}
