package fr.nathan.ugebot;

import fr.nathan.ugebot.events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UGEBot {


    public static void main(String[] args) throws Exception {
        // UGE BOT
        String token = Files.readString(Paths.get("token.txt"));
        JDA api = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
//        api.addEventListener(new PreRentree());
//        api.addEventListener(new SessionListener());
        api.addEventListener(new CommandListener());
        api.addEventListener(new ButtonListener());
        api.addEventListener(new JoinListener());
        api.addEventListener(new WelcomeListener());
        api.addEventListener(new UpdateGroups());
//        api.addEventListener(new StatsListener());

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
        api.upsertCommand("numetu", "Obtenir le numéro d'étudiant d'une personne")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur souhaité", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();

        // Initialise le message de bienvenue (mauvaise réaction teacher)
        api.upsertCommand("welcome", "Créer le message du salon arrivé")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        // Commande de stats de cours
        SubcommandData startData = new SubcommandData("start", "Démarrer le calcul des stats du cours de Python.");
        SubcommandData stopData = new SubcommandData("stop", "Arrêter le calcul des stats du cours de Python.");
        api.upsertCommand("cours", "Démarrer le calcul des stats du cours de Python.")
                .addSubcommands(startData, stopData)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();

        api.upsertCommand("poll", "Permet de faire un sondage.")
                .addOption(OptionType.STRING, "question", "Renseignez la question que vous souhaitez poser.", true)
                .addOption(OptionType.STRING, "choices", "Mettez vos choix à la suite" +
                        "séparés par un \";\". Si il n'y a pas de choix, cela est un oui/non.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();

        // Commande update groupe
        SubcommandData updateData = new SubcommandData("update", "Mettre à jour les groupes.");
        api.upsertCommand("role", "Mettre à jour les groupes.")
                .addSubcommands(updateData)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        api.awaitReady();

        if (System.console().readLine().equals("restart")) {
            System.exit(0);
        }

//        SessionListener.initEmojisRole();
    }
}
