package fr.nathan.ugebot.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.nathan.ugebot.managers.VerifManager.safeAddRole;

public class GroupManager {

    public static boolean updateUser(Member member) {
        List<Map<String, String>> listeCSV = CSVManager.chargerDonneesCSV("liste.csv");
        List<Map<String, String>> verif = CSVManager.chargerDonneesCSV("verif.csv");

        String numEtudiant = VerifManager.obtenirNumEtudiant(verif, member.getId());
        if (numEtudiant == null)
            return false;
        List<Map<String, String>> dataCSV = CSVManager.rechercherDonnees(listeCSV, "n°etudiant", numEtudiant);
        if (dataCSV.isEmpty()) {
            System.out.println("dataCSV is empty for " + member.getEffectiveName());
            return false;
        }
        Map<String, String> groupes = dataCSV.get(0);

        String formation = groupes.get("formation");
        if (!formation.equalsIgnoreCase("MI")) {
            safeAddRole(member, formation);
        }

        safeAddRole(member, "TD-" + groupes.getOrDefault("td", "azertyuiop"));
        safeAddRole(member, "TP-" + groupes.getOrDefault("tp", "azertyuiop"));
        safeAddRole(member, "Amphi " + groupes.getOrDefault("amphi", "azertyuiop"));

        if (groupes.getOrDefault("trans. energ.", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Transition énergétique");

        if (groupes.getOrDefault("phys 1", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Physique");

        if (groupes.getOrDefault("electro", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Electronique");

        if (groupes.getOrDefault("italien", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Italien");

        String japonais = groupes.getOrDefault("gr jap", "azertyuiop");
        if (!Objects.equals(japonais, "") && !japonais.equalsIgnoreCase("VAL")) {
            safeAddRole(member, "Japonais Gr. " + japonais);
        }

        String anglaisRenf = groupes.getOrDefault("gr ang renf", "azertyuiop");
        if (!Objects.equals(anglaisRenf, "") && !anglaisRenf.equalsIgnoreCase("VAL")) {
            safeAddRole(member, "Anglais renforcé Gr. " + anglaisRenf);
        }

        if (groupes.getOrDefault("anglais debutant", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Anglais débutant");

        if (groupes.getOrDefault("ecriture", "azertyuiop").equalsIgnoreCase("X"))
            safeAddRole(member, "Atelier d'écriture");

        return true;
    }

    public static void updateAllUsers(@NotNull Guild guild) {
        guild.loadMembers().onSuccess(members -> members.forEach(GroupManager::updateUser));
    }
}
