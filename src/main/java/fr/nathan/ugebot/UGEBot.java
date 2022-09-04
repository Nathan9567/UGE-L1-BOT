package fr.nathan.ugebot;

import fr.nathan.ugebot.events.ButtonListener;
import fr.nathan.ugebot.events.CommandListener;
import fr.nathan.ugebot.events.JoinListener;
import fr.nathan.ugebot.events.PreRentree;
import fr.nathan.ugebot.fonction.PermissionError;
import fr.nathan.ugebot.tasks.Save;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Timer;

public class UGEBot {


    public static void main(String[] args) throws Exception {
        // UGE BOT
        JDA api = JDABuilder.createDefault("ODgzMzgwNTI3NDE0MDE4MDc4.GWBo-b.2kkmxnTZetoWmlJDtf9Bams-9AJSthvDcG12jE").build();

        PermissionError permError = new PermissionError();
        api.addEventListener(new CommandListener(permError));
        api.addEventListener(new PreRentree());
        api.addEventListener(new ButtonListener());
        api.addEventListener(new JoinListener());

        api.upsertCommand("ping", "Calcul ta latence avec le robot").queue();
        api.upsertCommand("verifyme", "Demande a un admin de te vérifier")
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro se trouvant sur ta carte étudiante.", true).queue();
        api.upsertCommand("verify", "Verifier un utilisateur manuellement")
                .addOption(OptionType.USER, "utilisateur", "@personne a valider", true)
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro de l'étudiant a valider.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MENTION_EVERYONE)).queue();
        api.upsertCommand("session", "Initialise la session de votre choix")
                .addOption(OptionType.INTEGER, "numero", "Numéro de session", true)
                .addOption(OptionType.STRING, "messageid", "Identifiant du message", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        api.upsertCommand("students", "Met le rôle étudiant à tout le discord !")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        api.awaitReady();

//        Timer task = new Timer();
//        task.schedule(new Save(), 0, 300000);
    }
}
