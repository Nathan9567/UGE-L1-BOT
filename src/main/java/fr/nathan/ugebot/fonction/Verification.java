package fr.nathan.ugebot.fonction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Verification {

    public void getFile() throws IOException {
        String filename = "verif.csv";

        FileReader reader = new FileReader(filename);
        BufferedReader br = new BufferedReader(reader);

        br.read();
        String line;
        while((line = br.readLine()) != null) {
            String[] list = line.split(";");
            String userid = list[0];
            String numetu = list[1];

        }
    }

    public static void addToFile(String append) throws IOException {
        String filename = "verif.csv";

        FileWriter writer = new FileWriter(filename, true);

        writer.write("\n" + append);
        writer.close();

    }
}
