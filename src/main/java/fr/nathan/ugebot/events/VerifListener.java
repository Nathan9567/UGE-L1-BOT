package fr.nathan.ugebot.events;

import fr.nathan.ugebot.managers.CSVManager;
import fr.nathan.ugebot.managers.VerifManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static fr.nathan.ugebot.managers.CSVManager.rechercherDonnees;
import static fr.nathan.ugebot.managers.DateFormatter.getDate;
import static fr.nathan.ugebot.managers.VerifManager.*;

public class VerifListener extends ListenerAdapter {

    static void verifyMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Member mbr) {
        event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
        Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                + mbr.getAsMention() + " a été vérifié **manuellement** par **" + event.getUser().getAsMention() + "**.").queue();
        mbr.getUser().openPrivateChannel().queue((chan) -> chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsMention() + ".").queue());
    }

    static void verifymeMessage(@NotNull SlashCommandInteractionEvent event, int numEtudiant) {
        List<Map<String, String>> listeEtu = CSVManager.chargerDonneesCSV("liste.csv");
        StringBuilder sb = new StringBuilder();

        event.reply("Demande envoyé à l'équipe d'administration.\n" +
                        "Une réponse vous sera donné dans les plus brefs délais.")
                .setEphemeral(true).queue();
        event.getUser().openPrivateChannel().queue((chan) -> chan.sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                "Une réponse vous sera donné dans les plus brefs délais.").queue());

        List<Map<String, String>> etudiant = rechercherDonnees(listeEtu, "n°etudiant", String.valueOf(numEtudiant));
        sb.append(":envelope: `[").append(getDate()).append("]` L'utilisateur ").append(event.getUser().getAsMention())
                .append(" a demandé à être vérifié avec le numéro d'étudiant suivant : **").append(numEtudiant).append("**. ");
        if (etudiant.isEmpty())
            sb.append("(il ne figure pas dans la liste)");
        else
            sb.append("(il figure dans la liste sous le nom de **").append(etudiant.get(0).get("prenom")).append(" ").append(etudiant.get(0).get("nom")).append("**)");

        Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications", true).get(0)
                .sendMessage(sb.toString())
                .setActionRow(Button.success("okStudent", "Accepter"), Button.danger("notOkStudent", "Refuser"),
                        Button.secondary("nameReq", "Prenom/Nom"))
                .queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        JDA api = event.getJDA();
        api.upsertCommand("verifyme", "Demande à un admin de te vérifier")
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro se trouvant sur ta carte étudiante.", true).queue();
        api.upsertCommand("verify", "Verifier un utilisateur manuellement")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur à valider", true)
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro de l'étudiant à valider.", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
        api.upsertCommand("numetu", "Obtenir le numéro d'étudiant d'une personne")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur souhaité", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        List<Map<String, String>> verified;
        if (event.getName().equals("verify") || event.getName().equals("verifyme") || event.getName().equals("numetu"))
            verified = CSVManager.chargerDonneesCSV("verif.csv");
        else
            return;

        switch (event.getName()) {
            case "numetu" -> {
                Member mbrEtu = Objects.requireNonNull(event.getOption("utilisateur")).getAsMember();
                assert mbrEtu != null;
                String numEtu = VerifManager.obtenirNumEtudiant(verified, mbrEtu.getId());
                if (numEtu == null)
                    event.reply("Action impossible. L'utilisateur " + mbrEtu.getAsMention() +
                            " n'est pas vérifié !").setEphemeral(true).queue();
                else
                    event.reply("Le numéro d'étudiant de " + mbrEtu.getAsMention() +
                            " est **" + numEtu + "**.").setEphemeral(true).queue();
            }

            case "verifyme" -> {
                int numEtudiant = Objects.requireNonNull(event.getOption("numetudiant")).getAsInt();
                Member mbr = event.getMember();
                if (event.getGuild() == null) {
                    event.reply("Merci de faire cette commande sur le serveur !").setEphemeral(true).queue();
                    return;
                }

                assert mbr != null;
                if (VerifManager.utilisateurExiste(verified, mbr.getId())) {
                    event.reply("Vous êtes déjà vérifié !").setEphemeral(true).queue();
                    return;
                }

                // Demande automatique
                String[] name = mbr.getEffectiveName().split(" ");
                StringBuilder nameBuilder = new StringBuilder();
                StringBuilder nameBuilder2 = new StringBuilder();
                if (name.length < 2) {
                    event.reply("Tu n'as pas l'air d'être renommé en `Prenom Nom` !\n" +
                            "Merci de bien vouloir te renommer.").setEphemeral(true).queue();
                    return;
                }

                List<Map<String, String>> listeEtu = CSVManager.chargerDonneesCSV("liste.csv");

                rechercherDonnees(listeEtu, "n°etudiant", String.valueOf(numEtudiant)).forEach((ligne) -> {
                    nameBuilder.append(ligne.get("prenom")).append(" ").append(ligne.get("nom"));
                    nameBuilder2.append(ligne.get("nom")).append(" ").append(ligne.get("prenom"));
                });

                if (nameBuilder.toString().equalsIgnoreCase(mbr.getEffectiveName()) || nameBuilder2.toString().equalsIgnoreCase(mbr.getEffectiveName())) {
                    safeVerify(mbr, String.valueOf(numEtudiant));
                    event.getUser().openPrivateChannel().queue((chan -> chan.sendMessage("Votre demande a été **acceptée**. " +
                            "Vous avez désormais accès a l'ensemble des salons utiles à votre année.").queue()));
                    event.reply("Votre demande a été **acceptée**. Vous avez désormais accès a l'ensemble " +
                            "des salons utiles à votre année.").setEphemeral(true).queue();
                    event.getGuild().getTextChannelsByName("verifications-logs", true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                            + mbr.getAsMention() + " a été vérifié **automatiquement** à l'aide de la liste.").queue();
                    return;
                }

                // Sinon, demande manuelle
                verifymeMessage(event, numEtudiant);
            }

            case "verify" -> {
                Member mbr = Objects.requireNonNull(event.getOption("utilisateur")).getAsMember();
                OptionMapping numEtuOption = event.getOption("numetudiant");
                assert mbr != null;

                int numetu = numEtuOption != null ? numEtuOption.getAsInt() : 0;
                if (event.getGuild() == null) {
                    event.reply("Merci de faire cette commande sur le serveur !").setEphemeral(true).queue();
                    return;
                }
                if (VerifManager.numEtudiantExiste(verified, String.valueOf(numetu))) {
                    event.reply("Ce numéro d'étudiant est déjà utilisé par <@" +
                            obtenirUtilisateur(verified, String.valueOf(numetu)) + "> !").setEphemeral(true).queue();
                    return;
                }

                safeVerify(mbr, String.valueOf(numetu));
                verifyMessage(event, mbr);
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user;
        if (event.getChannel().getName().equals("verifications")) {
            user = event.getMessage().getMentions().getUsers().get(0);
        } else {
            user = null;
        }
        switch (event.getComponentId()) {
            case "okStudent" -> {
                List<Map<String, String>> verified = CSVManager.chargerDonneesCSV("verif.csv");
                String msg = event.getMessage().getContentRaw();
                String res = msg.substring(msg.length() - 9, msg.length() - 3);
                assert user != null;

                if (utilisateurExiste(verified, user.getId())) {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("L'utilisateur " + user.getAsMention() + " a déjà été vérifié !\n*(Ce message s'auto-supprime au bout de 30 secondes !)*").queue((message) -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return;
                }

                if (numEtudiantExiste(verified, res)) {
                    event.getMessage().delete().queue();
                    Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true)
                            .get(0).sendMessage(obtenirUtilisateur(verified, user.getId()) + " a déjà été vérifié avec " +
                                    "le numéro d'étudiant : **" + res + "** ! (Demande de " + user.getAsMention() + ")").queue();
                    return;
                }
                safeVerify(Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).retrieveMember(user).complete()), res);

                user.openPrivateChannel().queue((chan -> chan.sendMessage("Votre demande a été **accepté**. Vous avez désormais accès a l'ensemble des salons utiles a votre année.").queue()));
                event.getMessage().delete().queue();
                event.getGuild().getTextChannelsByName("verifications-logs", true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                        + user.getAsMention() + " a été vérifié par " + event.getUser().getAsMention() + ".").queue();
            }
            case "notOkStudent" -> {
                assert user != null;
                user.openPrivateChannel().queue((chan -> chan.sendMessage("""
                        Votre demande a été **rejeté**. Plusieurs raisons peuvent en être la cause tel qu'un numéro d'étudiant non valide ou encore un non respect des règles.
                        Si vous pensez que cela n'est pas normal, vous pouvez réitérer votre demande.
                        Si celle-ci est de nouveau rejetée, merci de contacter un administrateur en privé.""").queue()));
                event.getMessage().delete().queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("❌ `[" + getDate() + "]` L'utilisateur "
                        + user.getAsMention() + " a été refusé par " + event.getUser().getAsMention() + ".").queue();
            }
            case "nameReq" -> {
                assert user != null;
                user.openPrivateChannel().queue((chan -> chan.sendMessage("""
                        L'équipe d'adminstrateurs du discord a constatée que vous ne vous étiez pas renommé avec la syntaxe `Prenom Nom`. Pour pouvoir être validé, c'est une **obligation**.
                        Pour se renommer, il suffit de cliquer en haut a gauche sur **L1 MI 2023/24 S1**, puis *Modifier le profil du serveur* et enfin écrivez votre prénom et nom dans *Pseudo*.
                        Une fois fini, vous pouvez refaire la commande /verifyme sur le discord et votre demande sera réétudiée.""").queue()));
                event.getMessage().delete().queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("\uD83D\uDCDD `[" + getDate() + "]` Demande de changement " +
                        "de pseudo envoyé à " + user.getAsMention() + " par " + event.getUser().getAsMention() + ".").queue();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getChannel().getName().equals("verifyme")) {
            if (!e.getMessage().getType().equals(MessageType.SLASH_COMMAND) && !Objects.requireNonNull(e.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
                e.getMessage().delete().queue();
            }
        }
    }
}
