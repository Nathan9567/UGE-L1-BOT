package fr.nathan.ugebot.events;

import fr.nathan.ugebot.functions.Verification;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        try {
            if (Verification.checkUser(event.getUser().getId())) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName("étudiant(e)", true).get(0)).queue();
            } else {
                event.getUser().openPrivateChannel().queue((chan -> chan.sendMessage("Bonjour, tu n'as surement pas été vérifié (ou sans numéro d'étudiant)" +
                        "lors du S1 cette année.\nPar conséquent, merci de passer par le salon " +
                        event.getGuild().getTextChannelsByName("arrivée", true).get(0).getAsMention() +
                        " pour remédier à cela ! Sans cela, tu n'auras pas accès au Discord :wink:").queue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
