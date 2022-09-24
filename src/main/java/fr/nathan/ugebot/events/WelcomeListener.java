package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class WelcomeListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if (e.getJDA().getGuildById(1003689153768214569L).getTextChannelById(1003689157656330366L) == e.getChannel()){
            if (e.getReaction().getEmoji().equals(Emoji.fromUnicode("\uD83D\uDC68\u200D\uD83C\uDFEB"))){
                e.getGuild().getTextChannelById(1003689157656330371L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
                e.getGuild().getTextChannelById(1003689157937340526L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
            } else if (e.getReaction().getEmoji().equals(Emoji.fromUnicode("✅"))){
                e.getGuild().getTextChannelById(1021473979619348581L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
                e.getGuild().getTextChannelById(1021498368666636528L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannel().asTextChannel().getIdLong() == 1021473979619348581L) {
            event.getChannel().asTextChannel().getPermissionContainer().getManager()
                    .putMemberPermissionOverride(event.getUser().getIdLong(),
                            null, Collections.singleton(Permission.MESSAGE_SEND)).queue();
        }
    }
}
