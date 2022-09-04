package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class JoinListener implements EventListener {

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof GuildMemberJoinEvent){
            GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(1003689153877246022L)).queue();
        }
    }
}
