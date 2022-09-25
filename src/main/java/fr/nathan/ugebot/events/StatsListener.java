package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

public class StatsListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            if (event.getChannelJoined().getIdLong() == 1016791211220160512L){
                event.getChannelJoined().getMembers();
            }
        }
    }
}
