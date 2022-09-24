package fr.nathan.ugebot;

import fr.nathan.ugebot.events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class UGEBot {


    public static void main(String[] args) throws Exception {
        // UGE BOT
        JDA api = JDABuilder.createDefault("ODgzMzgwNTI3NDE0MDE4MDc4.GWBo-b.2kkmxnTZetoWmlJDtf9Bams-9AJSthvDcG12jE").build();

        api.addEventListener(new CommandListener());
        api.addEventListener(new PreRentree());
        api.addEventListener(new ButtonListener());
        api.addEventListener(new JoinListener());
        api.addEventListener(new SessionListener());
        api.addEventListener(new WelcomeListener());

        api.upsertCommand("ping", "Calcul ta latence avec le robot").queue();
        api.upsertCommand("verifyme", "Demande à un admin de te vérifier")
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro se trouvant sur ta carte étudiante.", true).queue();
        api.upsertCommand("verify", "Verifier un utilisateur manuellement")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur à valider", true)
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro de l'étudiant a valider.", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
        api.upsertCommand("session", "Initialise la session de votre choix")
                .addOption(OptionType.INTEGER, "numero", "Numéro de session", true)
                .addOption(OptionType.STRING, "messageid", "Identifiant du message", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        api.upsertCommand("students", "Met le rôle étudiant à tout le discord !")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        api.upsertCommand("numetu", "Obtenir le numéro d'étudiant d'une personne")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur souhaité", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
        api.upsertCommand("welcome", "Créer le message du salon arrivé")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        api.awaitReady();

        SessionListener.initEmojisRole();
    }
}
