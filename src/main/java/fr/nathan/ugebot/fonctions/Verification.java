package fr.nathan.ugebot.fonctions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Verification {

    public static HashMap<String, String> getFile() throws IOException {
//        ArrayList<String> enseignants = new ArrayList<>();
        HashMap<String, String> verified = new HashMap<>();
        String filename = "verif.csv";

        FileReader reader = new FileReader(filename);
        BufferedReader br = new BufferedReader(reader);

        br.readLine();
        String line;
        while((line = br.readLine()) != null) {
//            if (line.equals("Enseignants:")) break;
            String[] list = line.split(";");
            String userid = list[0];
            String numetu = list[1];
            verified.put(userid, numetu);
        }
/*
        while ((line = br.readLine()) != null) {
            if (line.equals("Tuteurs:")) break;
            enseignants.add(line);
        }

        while ((line = br.readLine()) != null) {
            enseignants.add(line);
        }
        br.close();*/
        return verified;
    }

    public static void dicToCsv(HashMap<String, String> hashMap, String filepath) throws IOException{
        FileWriter writer = new FileWriter(filepath);

         writer.write("userid;numetudiant");
//        writer.write("Etudiants:");
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            writer.append("\n").append(entry.getKey()).append(";").append(entry.getValue());
        }
//        writer.write("Enseignants:");
        writer.close();
    }

    public static boolean checkUser(String userId) throws IOException {
        // If already verified return false else return true
        HashMap<String, String> verified = getFile();
        if (verified.containsKey(userId)) {
            if (verified.get(userId).equals("0")){
                verified.remove(userId);
                dicToCsv(verified, "verif.csv");
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean checkStudent(String numEtud) throws IOException {
        HashMap<String, String> verified = getFile();
        return verified.containsValue(numEtud);
    }

    public static void addToFile(String filepath, String append) throws IOException {
        FileWriter writer = new FileWriter(filepath, true);

        writer.write("\n" + append);
        writer.close();
    }
}
