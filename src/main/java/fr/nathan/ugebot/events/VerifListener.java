package fr.nathan.ugebot.events;

import fr.nathan.ugebot.functions.Verification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static fr.nathan.ugebot.functions.DateFunction.getDate;

public class VerifListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        JDA api = event.getJDA();
        api.upsertCommand("verifyme", "Demande à un admin de te vérifier")
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro se trouvant sur ta carte étudiante.", true).queue();

        api.upsertCommand("verify", "Verifier un utilisateur manuellement")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur à valider", true)
                .addOption(OptionType.INTEGER, "numetudiant", "Numéro de l'étudiant a valider.", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();

        api.upsertCommand("numetu", "Obtenir le numéro d'étudiant d'une personne")
                .addOption(OptionType.USER, "utilisateur", "Utilisateur souhaité", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "verify" -> {
                Member mbr = Objects.requireNonNull(event.getOption("utilisateur")).getAsMember();
                OptionMapping numEtuOption = event.getOption("numetudiant");
                int numetu = 0;

                if (numEtuOption != null) {
                    numetu = numEtuOption.getAsInt();
                }

                if (mbr != event.getMember()) {
                    try {
                        assert mbr != null;
                        if (!Verification.checkUser(mbr.getId())) {
                            try {
                                Objects.requireNonNull(event.getGuild()).addRoleToMember(mbr, Objects.requireNonNull(event.getGuild().getRolesByName("étudiant(e)", true).get(0))).queue();
                                event.getGuild().removeRoleFromMember(mbr, Objects.requireNonNull(event.getGuild().getRolesByName("Non vérifié", true).get(0))).queue();
                                event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
                                event.getGuild().getTextChannelsByName("verifications-logs", true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                        + mbr.getAsMention() + " a été vérifié **manuellement** par **" + event.getUser().getAsMention() + "**.").queue();
                                mbr.getUser().openPrivateChannel().queue((chan) -> chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsMention() + ".").queue());
                                Verification.addToFile("verif.csv", mbr.getId() + ";" + numetu);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            event.reply("L'utilisateur " + mbr.getAsMention() + " est déjà vérifié.").setEphemeral(true).queue();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    event.reply("Tu ne peux pas te vérifier toi même !").setEphemeral(true).queue();
                }
            }

            case "verifyme" -> {
                int numEtudiant = Objects.requireNonNull(event.getOption("numetudiant")).getAsInt();

                try {
                    if (!Verification.checkUser(event.getUser().getId())) {
                        event.deferReply().setEphemeral(true).queue();
                        event.getHook().sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                        "Une réponse vous sera donné dans les plus brefs délais.")
                                .setEphemeral(true).queue();
                        event.getUser().openPrivateChannel().queue((chan) -> chan.sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                "Une réponse vous sera donné dans les plus brefs délais.").queue());

                        Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications", true).get(0)
                                .sendMessage("✉ `[" + getDate() + "]` L'utilisateur " + event.getUser().getAsMention() +
                                        " a demandé à être vérifié avec le numéro d'étudiant suivant : **" + numEtudiant + "**.")
                                .setActionRow(Button.success("okStudent", "Accepter"), Button.danger("notOkStudent", "Refuser"),
                                        Button.secondary("nameReq", "Prenom/Nom"))
                                .queue();
                    } else {
                        event.deferReply().setEphemeral(true).queue();
                        event.getHook().sendMessage("Vous êtes déjà vérifié !").queue();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case "numetu" -> {
                Member mbrEtu = Objects.requireNonNull(event.getOption("utilisateur")).getAsMember();
                try {
                    HashMap<String, String> verifList = Verification.getFile();
                    assert mbrEtu != null;
                    if (verifList.containsKey(mbrEtu.getId())) {
                        event.reply("Le numéro d'étudiant de " + mbrEtu.getAsMention() +
                                        " est **" + verifList.get(mbrEtu.getId()) + "**.")
                                .setEphemeral(true).queue();
                    } else {
                        event.reply("Action impossible. L'utilisateur " + mbrEtu.getAsMention() +
                                " n'est pas vérifié !").setEphemeral(true).queue();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
