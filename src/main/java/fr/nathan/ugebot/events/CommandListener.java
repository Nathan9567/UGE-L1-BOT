package fr.nathan.ugebot.events;

import fr.nathan.ugebot.fonctions.Verification;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static fr.nathan.ugebot.fonctions.DateFonction.getDate;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {

            case "ping" -> {
                long time = System.currentTimeMillis();
                event.reply("Pong !").setEphemeral(true)
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong : %d ms", System.currentTimeMillis() - time))
                        .queue();
            }

            case "verify" -> {
                Member mbr = event.getOption("utilisateur").getAsMember();
                OptionMapping numEtuOption = event.getOption("numetudiant");
                int numetu = 0;
                if (numEtuOption != null) {
                    numetu = numEtuOption.getAsInt();
                }
                if (mbr != event.getMember()) {
                    try {
                        if (!Verification.checkUser(mbr.getId())) {
                            try {
                                // event.getGuild().addRoleToMember(mbr, event.getGuild().getRoleById(1012973566104453170L)).queue(); // vérifié
                                event.getGuild().addRoleToMember(mbr, event.getGuild().getRolesByName("étudiant(e)", true).get(0)).queue(); // étudiant
                                event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
                                event.getGuild().getTextChannelsByName("verifications-logs",true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                        + mbr.getAsMention() + " a été vérifié **manuellement** par **" + event.getUser().getAsTag() + "**.").queue();
                                mbr.getUser().openPrivateChannel().queue((chan) -> {
                                    chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsTag() + ".").queue();
                                });
                                Verification.addToFile("verif.csv", mbr.getId() + ";" + numetu);
                            } catch (Exception ex) {
                                event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                                event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("L'utilisateur " + mbr.getAsMention() + " est déjà vérifié.").setEphemeral(true).queue();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    event.reply("Tu ne peux pas te vérifier toi même !").setEphemeral(true).queue();
                }
            }

            case "verifyme" -> {
                Integer numEtudiant = event.getOption("numetudiant").getAsInt();
                try {
                    if (!Verification.checkUser(event.getUser().getId())) {
                        event.deferReply().setEphemeral(true).queue();
                        event.getHook().sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                        "Une réponse vous sera donné dans les plus brefs délais.")
                                .setEphemeral(true).queue();
                        event.getUser().openPrivateChannel().queue((chan) -> {
                            chan.sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                    "Une réponse vous sera donné dans les plus brefs délais.").queue();
                        });
                        event.getGuild().getTextChannelsByName("verifications", true).get(0)
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
                Member mbrEtu = event.getOption("utilisateur").getAsMember();
                try {
                    HashMap<String, String> verifList = Verification.getFile();
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

            case "poll" -> {
                String qst = event.getOption("question").getAsString();
                OptionMapping choicesOption = event.getOption("choices");
                StringBuilder message = new StringBuilder(qst + "\n\n");
                if (choicesOption == null) {
                    event.reply("Sondage créé").setEphemeral(true).queue();
                    event.getChannel().sendMessage(message).queue((msg) -> {
                        msg.addReaction(Emoji.fromUnicode("✅")).queue();
                        msg.addReaction(Emoji.fromUnicode("❌")).queue();
                    });
                    break;
                }
                if (!choicesOption.getAsString().contains(";")) {
                    event.reply("Merci d'insérer au moins 2 choix en respectant la syntaxe " +
                                    "décrite par la description de l'option.").setEphemeral(true).queue();
                    break;
                }
                String[] choices = choicesOption.getAsString().split(";");

                List<String> nbr = List.of("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "\uD83D\uDD1F",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
                int i = 0;
                for (String choix : choices) {
                    if (i<10) {
                        message.append(nbr.get(i));
                    } else {
                        message.append(event.getGuild().getEmojisByName(nbr.get(i), true)
                                .get(0).getFormatted());
                    }
                    message.append(" ").append(choix).append("\n");
                    i++;
                    if (i == 20){
                        break;
                    }
                }

                event.reply("Sondage créé").setEphemeral(true).queue();
                event.getChannel().sendMessage(message).queue((msg) -> {
                    int nb = 0;
                    while (nb != choices.length) {
                        if (nb<10) {
                            msg.addReaction(Emoji.fromFormatted(nbr.get(nb))).queue();
                        } else {
                            msg.addReaction(event.getGuild().getEmojisByName(nbr.get(nb), true).get(0)).queue();
                        }
                        nb++;
                    }
                });
            }
        }
    }
}
