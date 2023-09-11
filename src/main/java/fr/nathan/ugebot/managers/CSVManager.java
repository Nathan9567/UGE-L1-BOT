package fr.nathan.ugebot.managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVManager {
    public static List<Map<String, String>> chargerDonneesCSV(String nomFichier) {
        List<Map<String, String>> donnees = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(nomFichier));
            String headerLine = reader.readLine(); // Première ligne pour obtenir les noms de colonnes

            if (headerLine != null) {
                headerLine = headerLine.replaceAll("\uFEFF", ""); // Suppression du caractère BOM
                String[] colonnes = headerLine.split(";");
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    String[] valeurs = ligne.split(";", -1);
                    if (valeurs.length == colonnes.length) {
                        Map<String, String> ligneMap = new HashMap<>();
                        for (int i = 0; i < colonnes.length; i++) {
                            ligneMap.put(colonnes[i].toLowerCase(), valeurs[i]);
                        }
                        donnees.add(ligneMap);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Une erreur est survenue : " + e.getMessage());
        }
        return donnees;
    }

    public static List<Map<String, String>> rechercherDonnees(List<Map<String, String>> donnees, String colonne, String valeur) {
        for (Map<String, String> ligne : donnees) {
            if (ligne.get(colonne).equals(valeur)) {
                return List.of(ligne);
            }
        }
        return List.of();
    }

    public static void ajouterLigneCSV(String nomFichier, String ligne) {
        try {
            FileWriter writer = new FileWriter(nomFichier, true);
            writer.append(ligne);
            writer.append('\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("Une erreur est survenue : " + e.getMessage());
        }
    }
}
