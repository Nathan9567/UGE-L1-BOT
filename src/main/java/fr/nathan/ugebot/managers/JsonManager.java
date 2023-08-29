package fr.nathan.ugebot.managers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonManager {

    public static void addToMapListInFile(String filePath, String mapName, Integer key, Long newValue) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(filePath);
            Map<String, Map<Integer, List<Long>>> loadedData = objectMapper.readValue(inputFile, new TypeReference<Map<String, Map<Integer, List<Long>>>>() {
            });

            // Vérifier si la map spécifiée existe dans les données chargées
            if (loadedData.containsKey(mapName)) {
                Map<Integer, List<Long>> targetMap = loadedData.get(mapName);

                // Ajouter la nouvelle valeur à la liste associée à la clé cible
                List<Long> targetList = targetMap.computeIfAbsent(key, k -> new ArrayList<>());
                targetList.add(newValue);

                // Sauvegarder les données mises à jour dans le fichier JSON
                objectMapper.writeValue(inputFile, loadedData);
            } else {
                System.out.println("La map spécifiée n'existe pas dans les données chargées.");
            }
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture " +
                    "du fichier JSON " + filePath + ".\n" + e.getMessage());
        }
    }

    public static boolean removeIdFromMapLists(String filePath, String mapName, Long idToRemove) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(filePath);
            Map<String, Map<Integer, List<Long>>> loadedData = objectMapper.readValue(inputFile, new TypeReference<Map<String, Map<Integer, List<Long>>>>() {
            });

            if (loadedData.containsKey(mapName)) {
                Map<Integer, List<Long>> targetMap = loadedData.get(mapName);

                for (List<Long> list : targetMap.values()) {
                    if (list.contains(idToRemove)) {
                        list.remove(idToRemove);
                        // Sauvegarder les données mises à jour dans le fichier JSON
                        objectMapper.writeValue(inputFile, loadedData);
                        return true; // Sortir dès que l'ID est trouvé et supprimé
                    }
                }
            } else {
                System.out.println("La map spécifiée n'existe pas dans les données chargées.");
            }
            return false;
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture " +
                    "du fichier JSON " + filePath + ".\n" + e.getMessage());
        }
        return false;
    }

    public static List<Long> getListFromMap(String filePath, String mapName, Integer key) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(filePath);
            Map<String, Map<Integer, List<Long>>> loadedData = objectMapper.readValue(inputFile, new TypeReference<Map<String, Map<Integer, List<Long>>>>() {
            });

            if (loadedData.containsKey(mapName)) {
                Map<Integer, List<Long>> targetMap = loadedData.get(mapName);
                List<Long> targetList = targetMap.get(key);

                return targetList != null ? targetList : List.of(); // Retourne une liste vide si null
            }
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture " +
                    "du fichier JSON " + filePath + ".\n" + e.getMessage());
        }

        return null; // Retourne null en cas d'erreur ou si la clé n'est pas présente dans la map
    }

    public static int getListSizeFromMap(String filePath, String mapName, Integer key) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(filePath);
            Map<String, Map<Integer, List<Long>>> loadedData = objectMapper.readValue(inputFile, new TypeReference<Map<String, Map<Integer, List<Long>>>>() {
            });

            if (loadedData.containsKey(mapName)) {
                Map<Integer, List<Long>> targetMap = loadedData.get(mapName);
                List<Long> targetList = targetMap.get(key);

                if (targetList != null) {
                    return targetList.size();
                } else {
                    return 0;
                }
            }
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture " +
                    "du fichier JSON " + filePath + ".\n" + e.getMessage());
        }

        return -1; // Valeur de retour indiquant une erreur
    }

    public static Integer findKeyContainingId(String filePath, String mapName, Long idToFind) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(filePath);
            Map<String, Map<Integer, List<Long>>> loadedData = objectMapper.readValue(inputFile, new TypeReference<Map<String, Map<Integer, List<Long>>>>() {
            });

            if (loadedData.containsKey(mapName)) {
                Map<Integer, List<Long>> targetMap = loadedData.get(mapName);

                for (Map.Entry<Integer, List<Long>> entry : targetMap.entrySet()) {
                    if (entry.getValue().contains(idToFind)) {
                        return Integer.parseInt(String.valueOf(entry.getKey())); // Renvoie la clé où l'ID a été trouvé
                    }
                }
            } else {
                System.out.println("La map spécifiée n'existe pas dans les données chargées.");
            }
            return null; // L'ID n'a pas été trouvé dans les listes de la map
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture " +
                    "du fichier JSON " + filePath + ".\n" + e.getMessage());
            return null;
        }
    }
}
