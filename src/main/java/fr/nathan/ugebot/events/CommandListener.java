package fr.nathan.ugebot.events;

import fr.nathan.ugebot.fonction.Verification;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
                    boolean hasStudentRole = false;
                    for (Role role : mbr.getRoles()) {
                        if (role.equals(event.getGuild().getRoleById(1012973566104453170L))) {
                            hasStudentRole = true;
                        }
                    }

                    if (!hasStudentRole) {
                        try {
                            event.getGuild().addRoleToMember(mbr, event.getGuild().getRoleById(1012973566104453170L)).queue();
                            event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
                            event.getGuild().getTextChannelById(1010540662581641337L).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                    + mbr.getAsMention() + " a été vérifié **manuellement** par **" + event.getUser().getAsTag() + "**.").queue();
                            mbr.getUser().openPrivateChannel().queue((chan) -> {
                                chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsTag() + ".").queue();
                            });
                            Verification.addToFile(mbr.getId() + ";" + numetu);
                        } catch (Exception ex) {
                            event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("L'utilisateur " + mbr.getAsMention() + " est déjà vérifié.").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Tu ne peux pas te vérifier toi même !").setEphemeral(true).queue();
                }
            }

            case "verifyme" -> {
                Integer numEtudiant = event.getOption("numetudiant").getAsInt();
                event.deferReply().setEphemeral(true).queue();
                event.getHook().sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                "Une réponse vous sera donné dans les plus brefs délais.")
                        .setEphemeral(true).queue();
                event.getUser().openPrivateChannel().queue((chan) -> {
                    chan.sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                            "Une réponse vous sera donné dans les plus brefs délais.").queue();
                });
                event.getJDA().getGuildById(1003689153768214569L).getTextChannelById(1003729944951664782L)
                        .sendMessage("✉ `[" + getDate() + "]` L'utilisateur " + event.getUser().getAsMention() +
                                " a demandé à être vérifié avec le numéro d'étudiant suivant : **" + numEtudiant + "**.")
                        .setActionRow(Button.success("okStudent", "Accepter"), Button.danger("notOkStudent", "Refuser"),
                                Button.secondary("nameReq", "Prenom/Nom"))
                        .queue();
            }

            case "students" -> {
                event.deferReply().setEphemeral(true).queue();
                for (Member student : event.getGuild().getMembers()) {
                    if (student.getRoles().get(0) != event.getGuild().getRoleById(1003689153877246022L)) {
                        try {
                            event.getGuild().addRoleToMember(student, event.getGuild().getRoleById(1003689153877246022L)).queue();
                            System.out.println(student.getNickname() + " étudiant");
                        } catch (Exception exception) {
                        }
                    }
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
        }
    }

    private String getDate(){
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        String heure = "" + cal.get(Calendar.HOUR_OF_DAY);
        String min = "" + cal.get(Calendar.MINUTE);
        String sec = "" + cal.get(Calendar.SECOND);

        if (heure.length() == 1) {
            heure = 0 + heure;
        }
        if (min.length() == 1) {
            min = 0 + min;
        }
        if (sec.length() == 1) {
            sec = 0 + sec;
        }
        return heure + ":" + min + ":" + sec;
    }
}
