package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashMap;

public class SessionListener extends ListenerAdapter {

    public static long msgId1;
    public static long msgId2;
    public static HashMap<String, String> emojisRole;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("session")) {
            Integer numSession = event.getOption("numero").getAsInt();
            Long messageID = event.getOption("messageid").getAsLong();
            if (numSession == 1)
                msgId1 = messageID;
            else if (numSession == 2)
                msgId2 = messageID;
            else
                return;

            event.getJDA().getTextChannelById(event.getChannel().getId()).retrieveMessageById(messageID).queue((msg) -> {
                event.deferReply().setEphemeral(true).queue();
                for (String l : new String[]{"1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "\uD83D\uDD1F",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"}) {
                    msg.addReaction(event.getGuild().getEmojisByName(l, true).get(0)).queue();
                }
                event.getHook().editOriginal("Réactions ajoutés au message !").queue();
            });

            EnumSet<Permission> studentPerms = event.getGuild().getRolesByName("étudiant", true).get(0).getPermissions();
            for (int j = 1; j < 21; j++) {
                if (event.getGuild().getRolesByName("Session " + numSession + " Gr. " + j, true).isEmpty()) {
                    event.getGuild().createRole().setPermissions(studentPerms).setName("Session " + numSession + " Gr. " + j).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getChannel().getIdLong() == event.getGuild().getTextChannelsByName("sessions-informatiques", true).get(0).getIdLong()) {
            String numSession = null;
            if (event.getMessageIdLong() == msgId1) {
                numSession = "1";
            } else if (event.getMessageIdLong() == msgId2) {
                numSession = "2";
            } else {
                return;
            }

            int nbMbr = event.getGuild().getMembersWithRoles(event.getGuild()
                    .getRolesByName(emojisRole.get(event.getEmoji().getName()).replace("x", numSession), true)
                    .get(0)).size();
            if (nbMbr > 16) {
                event.getReaction().removeReaction(event.getUser()).queue();
            } else {
                for (int i = 0; i < 21; i++) {
                    if (event.getMember().getRoles().contains(event.getGuild().getRolesByName("Session ", true).get(0))) {

                    }
                }
            }

            event.getMember().getRoles();

            event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(46467674L));
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {


    }

    public static void initEmojisRole(){
        emojisRole = new HashMap<>();
        emojisRole.put("1⃣", "Session x Gr. 1"); emojisRole.put("2⃣", "Session x Gr. 2");
        emojisRole.put("3⃣", "Session x Gr. 3"); emojisRole.put("4⃣", "Session x Gr. 4");
        emojisRole.put("5⃣", "Session x Gr. 5"); emojisRole.put("6⃣", "Session x Gr. 6");
        emojisRole.put("7⃣", "Session x Gr. 7"); emojisRole.put("8⃣", "Session x Gr. 8");
        emojisRole.put("9⃣", "Session x Gr. 9"); emojisRole.put("\uD83D\uDD1F", "Session x Gr. 10");
        emojisRole.put("11", "Session x Gr. 11"); emojisRole.put("12", "Session x Gr. 12");
        emojisRole.put("13", "Session x Gr. 13"); emojisRole.put("14", "Session x Gr. 14");
        emojisRole.put("15", "Session x Gr. 15"); emojisRole.put("16", "Session x Gr. 16");
        emojisRole.put("17", "Session x Gr. 17"); emojisRole.put("18", "Session x Gr. 18");
        emojisRole.put("19", "Session x Gr. 19"); emojisRole.put("20", "Session x Gr. 20");
    }

}
