package fr.nathan.ugebot.events;

import fr.nathan.ugebot.managers.DateFormatter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class StatsListener extends ListenerAdapter {

    // Hashmap avec key = memberid et value = time
    public static HashMap<Long, Long> usersInChan = new HashMap<Long, Long>();
    public static HashMap<Long, Long> finalTimeInChan = new HashMap<Long, Long>();
    public static boolean isClasses = false;
    public static long startClasse = 0;

    private static boolean isStudent(Member member) {
        return member.getRoles().contains(member.getGuild().getRolesByName("étudiant", true).get(0));
    }

    private static String formatTimeMs(long time) {
        String res;
        long s = time / 1000;

        long sec = s % 60;
        long min = (s / 60) % 60;
        long hours = (s / 60) / 60;

        String strSec = (sec < 10) ? "0" + Long.toString(sec) : Long.toString(sec);
        String strmin = (min < 10) ? "0" + Long.toString(min) : Long.toString(min);
        String strHours = (hours < 10) ? "0" + Long.toString(hours) : Long.toString(hours);

        res = strHours + "h" + strmin + " et " + strSec + "sec";
        return res;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        // Commande de stats de cours
        SubcommandData startData = new SubcommandData("start", "Démarrer le calcul des stats du cours de Python.");
        SubcommandData stopData = new SubcommandData("stop", "Arrêter le calcul des stats du cours de Python.");
        event.getJDA().upsertCommand("cours", "Démarrer le calcul des stats du cours de Python.")
                .addSubcommands(startData, stopData)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("cours")) {
            if (Objects.equals(event.getSubcommandName(), "start") && !isClasses) {
                usersInChan.clear();
                Date date = new Date();
                startClasse = date.getTime();
                for (Member mbr : Objects.requireNonNull(Objects.requireNonNull(
                        event.getGuild()).getVoiceChannelById(1016791211220160512L)).getMembers()) {
                    // étudiant ou vérifié :
                    if (isStudent(mbr)) {
                        usersInChan.put(mbr.getIdLong(), date.getTime());
                    }
                }
                event.reply("Le cours a bien été commencé !").setEphemeral(true).queue();
                isClasses = true;
            } else if (Objects.equals(event.getSubcommandName(), "stop") && isClasses) {
                Date date = new Date();
                for (Member mbr : Objects.requireNonNull(Objects.requireNonNull(
                        event.getGuild()).getVoiceChannelById(1016791211220160512L)).getMembers()) {
                    if (isStudent(mbr)) {
                        long mbrid = mbr.getIdLong();
                        long finalTimeUser = date.getTime() - usersInChan.get(mbrid);
                        if (finalTimeInChan.getOrDefault(mbrid, 0L) == 0L) {
                            finalTimeInChan.put(mbrid, finalTimeUser);
                        } else {
                            long previousTime = finalTimeInChan.get(mbrid);
                            finalTimeInChan.remove(mbrid);
                            finalTimeInChan.put(mbrid, finalTimeUser + previousTime);
                        }
                    }
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM", Locale.FRANCE);
                long totalTimeUser = 0L;
                int underTenMinutes = 0;
                int aHalfHour = 0;
                int underAnHour = 0;
                for (long userTime : finalTimeInChan.values()) {
                    totalTimeUser = totalTimeUser + userTime;
                    if (userTime < 600000L) {
                        underTenMinutes++;
                    } else if (userTime < 1800000L) {
                        aHalfHour++;
                    } else if (userTime < 3600000L) {
                        underAnHour++;
                    }
                }
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
                long timeAverage = totalTimeUser / finalTimeInChan.size();

                Objects.requireNonNull(event.getGuild().getTextChannelById(1003689158201577496L)).sendMessage(
                                "Voici quelques statistiques concernant le **cours du " + dateFormat.format(date)
                                        + "**.\n\n> Le cours a commencé à **" + timeFormat.format(startClasse)
                                        + "** et fini à **" + DateFormatter.getDate() + "**. Soit un total de **"
                                        + formatTimeMs(date.getTime() - startClasse) + "**.\n"
                                        + "> **" + finalTimeInChan.size() + "** étudiants ont assisté au cours.\n"
                                        + "> En moyenne, les étudiants sont restés **" + formatTimeMs(timeAverage)
                                        + "**.\n\n __**Stats détaillés :**__\n Nombre d'étudiants étant restés un certain temps"
                                        + " (sans redondance) :\n" + "> moins de 10 minutes : **" + underTenMinutes + "**\n"
                                        + "> moins de 30 minutes : **" + aHalfHour + "**\n"
                                        + "> moins d'une heure : **" + underAnHour + "**\n"
                                        + "> plus d'une heure : **" + (finalTimeInChan.size() - (underAnHour + aHalfHour + underTenMinutes))
                                        + "**\n\n **Ces statistiques sont seulement là à titre indicatif.**")
                        .queue();
                event.reply("Le cours a bien été arrêté !").setEphemeral(true).queue();
                isClasses = false;
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        // Si c'est un étudiant ou vérifié :
        if (isStudent(event.getMember())) {
            if (isClasses && Objects.requireNonNull(event.getChannelJoined()).getIdLong() == 1016791211220160512L) {
                Date date = new Date();
                long mbrid = event.getMember().getIdLong();
                if (usersInChan.getOrDefault(mbrid, 0L) == 0L) {
                    usersInChan.put(mbrid, date.getTime());
                } else {
                    usersInChan.remove(mbrid);
                    usersInChan.put(mbrid, date.getTime());
                }
            } else if (isClasses && Objects.requireNonNull(event.getChannelLeft()).getIdLong() == 1016791211220160512L) {
                Date date = new Date();
                long mbrid = event.getMember().getIdLong();
                long finalTimeUser = date.getTime() - usersInChan.get(mbrid);
                if (finalTimeInChan.getOrDefault(mbrid, 0L) == 0L) {
                    finalTimeInChan.put(mbrid, finalTimeUser);
                } else {
                    long previousTime = finalTimeInChan.get(mbrid);
                    finalTimeInChan.remove(mbrid);
                    finalTimeInChan.put(mbrid, finalTimeUser + previousTime);
                }
            }
        }
    }
}
