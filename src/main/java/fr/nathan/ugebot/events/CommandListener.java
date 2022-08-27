package fr.nathan.ugebot.events;

import fr.nathan.ugebot.fonction.PermissionError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

public class CommandListener extends ListenerAdapter {

    private PermissionError permError;

    public CommandListener(PermissionError permError) {
        this.permError = permError;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {

            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong ! ").setEphemeral(true)
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong : %d ms", System.currentTimeMillis() - time))
                        .queue();
                break;

            case "verify":
                Member mbr = event.getOption("utilisateur").getAsMember();
                if (mbr != event.getMember()) {
                    boolean hasStudentRole = false;
                    for (Role role : mbr.getRoles()) {
                        if (role.equals(event.getGuild().getRoleById(1003689153877246022L))) {
                            hasStudentRole = true;
                        }
                    }

                    if (!hasStudentRole) {
                        try {
                            event.getGuild().addRoleToMember(mbr, event.getGuild().getRoleById(1003689153877246022L)).queue();
                            event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
                            event.getGuild().getTextChannelById(1010540662581641337L).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                    + mbr.getAsMention() + " a été vérifié par **" + event.getUser().getAsTag() + "** sans demande.").queue();
                            mbr.getUser().openPrivateChannel().queue((chan) -> {
                                chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsTag() + "").queue();
                            });
                        } catch (Exception ex) {
                            event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                        }
                    }else{
                        event.reply("L'utilisateur " + mbr.getAsMention() + " est déjà vérifié.").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Tu ne peux pas te vérifier toi même !").setEphemeral(true).queue();
                }
                break;

            case "verifyme":
                Integer numEtudiant = event.getOption("numetudiant").getAsInt();
                event.reply("Demande envoyé à l'équipe d'administration.\nUne réponse vous sera donné sous 48h maximum.").setEphemeral(true).queue();
                event.getGuild().getTextChannelById(1003729944951664782L).sendMessage("✉ `[" + getDate() + "]` L'utilisateur " +
                        event.getUser().getAsMention() + " a demandé à être vérifié avec le numéro d'étudiant suivant : **" + numEtudiant + "**.")
                        .setActionRow(Button.success("okStudent", "Accepter"), Button.danger("notOkStudent", "Refuser"),
                                Button.secondary("nameReq", "Prenom/Nom"))
                        .queue();
                break;

            case "session":
                Integer numSession = event.getOption("numero").getAsInt();
                Long messageID = event.getOption("messageid").getAsLong();

                PreRentree.setMessageId(numSession, messageID);

                event.getJDA().getTextChannelById(event.getChannel().getId()).retrieveMessageById(messageID).queue((msg) -> {
                    event.deferReply().setEphemeral(true).queue();
                    msg.addReaction(Emoji.fromFormatted("1⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("2⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("3⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("4⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("5⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("6⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("7⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("8⃣")).queue();
                    msg.addReaction(Emoji.fromFormatted("9⃣")).queue();
                    msg.addReaction(Emoji.fromUnicode("\uD83D\uDD1F")).queue();
                    for (long l : new long[]{1005119634191683694L, 1005119635605164124L, 1005119636687294574L,
                            1005119637782007899L, 1005119639094825082L, 1005119639975641119L, 1010198262315229194L,
                            1010198263170879609L, 1010198264932483132L, 1010198266408878110L}) {
                        msg.addReaction(Emoji.fromCustom(event.getGuild().getEmojiById(l))).queue();
                    }
                    event.getHook().editOriginal("Réactions ajoutés au message !").queue();
                });
                break;

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
