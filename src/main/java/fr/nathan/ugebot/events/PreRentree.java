package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class PreRentree implements EventListener {

    public static HashMap<String, Integer> reactionsMapS1 = new HashMap<String, Integer>();
    public static HashMap<String, Integer> reactionsMapS2 = new HashMap<String, Integer>();
    private Long chanId = 1009474765913862295L;
    private static Long messageIdS1 = 1010523177190957107L;
    private static Long messageIdS2 = 1010523262863802469L;

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent e = (MessageReactionAddEvent) event;
            if(e.getMessageIdLong() == 1014683838103957514L){
                try {
                    e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(1003689153877246022L)).queue();
                }catch (Exception exception){}
            }
//            String emoji = e.getEmoji().getFormatted();
//            long msgid = e.getChannel().getIdLong();
//            List<String> Grp1 = Arrays.asList("1⃣", "2⃣", "3⃣", "4⃣", "5⃣");
//            List<String> Grp2 = Arrays.asList("6⃣", "7⃣", "8⃣", "9⃣", "\uD83D\uDD1F");
//            List<String> Grp3 = Arrays.asList("<:11:1005119634191683694>", "<:12:1005119635605164124>", "<:13:1005119636687294574>", "<:14:1005119637782007899>", "<:15:1005119639094825082>");
//            List<String> Grp4 = Arrays.asList("<:16:1005119639975641119>", "<:17:1010198262315229194>", "<:18:1010198263170879609>", "<:19:1010198264932483132>", "<:20:1010198266408878110>");
            try {
                getReactionFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (e.getMessageIdLong() == messageIdS1){
                sessionReacAdd(e, reactionsMapS1, chanId, messageIdS1);
            } else if (e.getMessageIdLong() == messageIdS2) {
                sessionReacAdd(e, reactionsMapS2, chanId, messageIdS2);
            }
        }

        if (event instanceof MessageReactionRemoveEvent) {
            MessageReactionRemoveEvent e = (MessageReactionRemoveEvent) event;
            if (e.getMessageIdLong() == messageIdS1){
                sessionReacDel(e, reactionsMapS1, chanId);
            } else if (e.getMessageIdLong() == messageIdS2) {
                sessionReacDel(e, reactionsMapS2, chanId);
            }
        }
    }

    public static void getReactionFile() throws IOException {
        String fileName = "sessions.txt";

        FileReader reader = new FileReader(fileName);
        BufferedReader br = new BufferedReader(reader);

        reactionsMapS1.clear();
        reactionsMapS2.clear();

        String line;
        int cmpt = 1;
        while ((line = br.readLine()) != null) {
            line = line.replace("{", "").replace("}", "");
            String parts[] = line.split(",");
            for (String part : parts) {
                //split the reactions data by : to get id and number of reactions
                String empdata[] = part.split("=");

                String strId = empdata[0].trim();
                String strName = empdata[1].trim();

                //set map
                if (cmpt == 1) {
                    reactionsMapS1.put(strId, Integer.valueOf(strName));
                } else if (cmpt == 2) {
                    reactionsMapS2.put(strId, Integer.valueOf(strName));
                }
            }
            cmpt++;
        }
        br.close();
    }

    public static void setReactionFile() throws IOException {
        String fileName = "sessions.txt";

        FileWriter fw = new FileWriter(fileName);

        fw.write(reactionsMapS1.toString() + "\n" + reactionsMapS2.toString());
        fw.close();
    }

    private static void sessionReacAdd (MessageReactionAddEvent e, HashMap < String, Integer > reactionMap, Long
    chanId, Long messageId){
        if (e.getChannel().getIdLong() == chanId && !e.getUser().isBot()) {

            String emoji = getEmojisRoot(e.getEmoji().toString());
            if (reactionMap.containsKey(emoji)) {
                if (reactionMap.get(emoji) > 17) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                } else {
                    reactionMap.replace(emoji, reactionMap.get(emoji) + 1);
                }
            } else {
                reactionMap.put(emoji, 1);
            }

            List<User> userList = new ArrayList<>();

            for (String val : reactionMap.keySet()) {
                if (val.startsWith("U")) {
                    userList.addAll(e.getJDA().getTextChannelById(chanId).retrieveReactionUsersById(messageId, Emoji.fromUnicode(val)).complete());
                } else {
                    userList.addAll(e.getJDA().getTextChannelById(chanId).retrieveReactionUsersById(messageId, e.getJDA().getEmojiById(val)).complete());
                }
            }

            List<String> usersIdList = userList.stream().map((user) -> {
                return user.getId();
            }).toList();

            int count = 0;
            for (String val : usersIdList) {
                if (e.getUser().getId().equals(val)) {
                    count++;
                }
                if (count == 2) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                    break;
                }
            }

            try {
                setReactionFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void sessionReacDel (MessageReactionRemoveEvent e, HashMap < String, Integer > reactionMap, Long
        chanId){
            if (e.getChannel().getIdLong() == chanId) {
                String emoji = getEmojisRoot(e.getEmoji().toString());
                reactionMap.replace(emoji, reactionMap.get(emoji) - 1);
            }
        }

    public static void setMessageId(Integer sessionId, long messageId){
        if (sessionId == 1) {
            messageIdS1 = messageId;
        } else if (messageId == 2) {
            messageIdS2 = messageId;
        }
    }

    private static String getEmojisRoot (String emoji){
            String res = "";
            if (emoji.contains("UnicodeEmoji")) {
                res = emoji.replace("UnicodeEmoji(", "").replace(")", "");
            } else {
                res = emoji.replace("CustomEmoji:", "").replace(")", "");
                res = res.substring(2).replace("(", "");
            }
            return res;
        }

}
