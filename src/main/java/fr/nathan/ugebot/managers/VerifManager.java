package fr.nathan.ugebot.managers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class VerifManager {

    public static void safeAddRole(@NotNull Member mbr, String roleName) {
        if (mbr.getRoles().stream().noneMatch(role -> role.getName().equals(roleName))) {
            List<Role> roles = mbr.getGuild().getRolesByName(roleName, true);
            if (!roles.isEmpty())
                mbr.getGuild().addRoleToMember(mbr, roles.get(0)).queue();
        }
    }

    public static void safeRemoveRole(@NotNull Member mbr, String roleName) {
        if (mbr.getRoles().stream().anyMatch(role -> role.getName().equals(roleName))) {
            List<Role> roles = mbr.getGuild().getRolesByName(roleName, true);
            if (!roles.isEmpty())
                mbr.getGuild().removeRoleFromMember(mbr, roles.get(0)).complete();
        }
    }

    public static void safeVerify(@NotNull Member mbr, String numEtudiant) {
        safeRemoveRole(mbr, "Non vérifié");
        safeAddRole(mbr, "étudiant(e)");
        ajouterUtilisateur("verif.csv", mbr.getId(), numEtudiant);
        GroupManager.updateUser(mbr);
    }

    public static void ajouterUtilisateur(String nomFichier, String userID, String numEtudiant) {
        CSVManager.ajouterLigneCSV(nomFichier, userID + ";" + numEtudiant);
    }

    public static boolean utilisateurExiste(@NotNull List<Map<String, String>> donnees, String userID) {
        return !CSVManager.rechercherDonnees(donnees, "userid", userID).isEmpty();
    }

    public static boolean numEtudiantExiste(@NotNull List<Map<String, String>> donnees, String numEtudiant) {
        return !CSVManager.rechercherDonnees(donnees, "numetudiant", numEtudiant).isEmpty();
    }

    public static String obtenirNumEtudiant(List<Map<String, String>> donnees, String userID) {
        List<Map<String, String>> userData = CSVManager.rechercherDonnees(donnees, "userid", userID);
        if (userData.isEmpty())
            return null;
        return userData.get(0).get("numetudiant");
    }

    public static String obtenirUtilisateur(List<Map<String, String>> donnees, String numEtudiant) {
        return CSVManager.rechercherDonnees(donnees, "numetudiant", numEtudiant).get(0).get("userid");
    }
}
