package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.nathan.ugebot.functions.JsonManager.*;

public class PreRentree extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        JDA api = event.getJDA();

        // Gérer les inscriptions aux sessions de pré-rentrée
        api.upsertCommand("partie", "Permet l'incription ou la désincription aux parties de pré-rentrée")
                .addSubcommands(new SubcommandData("info", "Liste les parties dans lesquels vous êtes inscrits"))
                .addSubcommands(new SubcommandData("inscription", "Inscription aux parties de pré-rentrée")
                        .addOption(OptionType.STRING, "partie", "Partie de pré-rentrée", true, true)
                        .addOption(OptionType.INTEGER, "tp", "TP voulu pour la partie", true, true))
                .addSubcommands(new SubcommandData("desinscription", "Se désincrire d'une partie de pré-rentrée")
                        .addOption(OptionType.STRING, "partie", "Partie de pré-rentrée", true, true))
                .queue();

        // Obtenir les inscriptions aux sessions de pré-rentrée
        api.upsertCommand("inscrits", "Obtenir les inscrits aux sessions de pré-rentrée par partie et par TP")
                .addOption(OptionType.STRING, "partie", "Partie de pré-rentrée", true, true)
                .addOption(OptionType.INTEGER, "tp", "TP voulu pour la partie", true, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        String[] parties = {"ent", "terminal", "outilogique"};
        if (event.getName().equals("partie") || event.getName().equals("inscrits")) {
            if (event.getFocusedOption().getName().equals("partie")) {
                List<Command.Choice> options = Stream.of(parties)
                        .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                        .map(word -> new Command.Choice(word, word)) // map the words to choices
                        .collect(Collectors.toList());
                event.replyChoices(options).queue();
            }
            if (event.getFocusedOption().getName().equals("tp")) {
                Integer[] tps = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                        11, 12, 13, 14, 15, 16, 17};
                String partie = Objects.requireNonNull(event.getOption("partie")).getAsString();
                List<Command.Choice> options = Stream.of(tps)
                        .filter(word -> word.toString().startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                        .filter(word -> getListSizeFromMap("sessions.json", partie, word) < 20)
                        .map(word -> new Command.Choice(String.valueOf(word), word)) // map the words to choices
                        .collect(Collectors.toList());
                event.replyChoices(options).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String filePath = "sessions.json";

        if (event.getName().equals("partie")) {
            if (Objects.equals(event.getSubcommandName(), "info")) {
                Integer ent = findKeyContainingId(filePath, "ent", event.getUser().getIdLong());
                Integer terminal = findKeyContainingId(filePath, "terminal", event.getUser().getIdLong());
                Integer outilogique = findKeyContainingId(filePath, "outilogique", event.getUser().getIdLong());

                StringBuilder sb = new StringBuilder();

                if (ent != null || terminal != null || outilogique != null)
                    sb.append("Vous êtes inscrit aux parties suivantes : ");
                else
                    sb.append("Vous n'êtes inscrit à aucune partie ! Pensez à vous inscrire !");
                if (ent != null)
                    sb.append("\n__ENT :__ **TP ").append(ent).append("**");
                if (terminal != null)
                    sb.append("\n__Terminal :__ **TP ").append(terminal).append("**");
                if (outilogique != null)
                    sb.append("\n__Outilogique :__ **TP ").append(outilogique).append("**");

                event.reply(sb.toString()).setEphemeral(true).queue();

            } else if (Objects.equals(event.getSubcommandName(), "inscription")) {
                int nb = Objects.requireNonNull(event.getOption("tp")).getAsInt();
                if (nb < 1 || nb > 17) {
                    event.reply("Le TP doit être compris entre 1 et 17 !").setEphemeral(true).queue();
                    return;
                }

                String partie = Objects.requireNonNull(event.getOption("partie")).getAsString();
                if (!partie.equals("ent") && !partie.equals("terminal") && !partie.equals("outilogique")) {
                    event.reply("La partie doit être \"ent\", \"terminal\" ou \"outilogique\" !").setEphemeral(true).queue();
                    return;
                }

                // si l'utilisateur est déjà inscrit à un créneau
                Integer idTP = findKeyContainingId(filePath, partie, event.getUser().getIdLong());
                if (idTP != null) {
                    event.reply("Vous êtes déjà inscrit au TP " + idTP + " pour cette partie !").setEphemeral(true).queue();
                    return;
                }

                // si le créneau n'est pas plein
                if (getListSizeFromMap(filePath, partie, nb) < 20) {
                    addToMapListInFile(filePath, partie, nb, event.getUser().getIdLong());
                    event.reply("Vous êtes inscrit au TP " + nb + " de la partie " + partie + " !").setEphemeral(true).queue();
                } else {
                    event.reply("Le TP " + nb + " est plein !").setEphemeral(true).queue();
                }

            } else {
                String partie = Objects.requireNonNull(event.getOption("partie")).getAsString();

                if (removeIdFromMapLists(filePath, partie, event.getUser().getIdLong()))
                    event.reply("Vous êtes désinscrit de la partie " + partie + " !").setEphemeral(true).queue();
                else
                    event.reply("Vous n'êtes pas inscrit à la partie " + partie + " !").setEphemeral(true).queue();

            }

        } else if (event.getName().equals("inscrits")) {
            String partie = Objects.requireNonNull(event.getOption("partie")).getAsString();
            Integer nb = Objects.requireNonNull(event.getOption("tp")).getAsInt();

            if (nb < 1 || nb > 17) {
                event.reply("Le TP doit être compris entre 1 et 17 !").setEphemeral(true).queue();
                return;
            }

            if (!partie.equals("ent") && !partie.equals("terminal") && !partie.equals("outilogique")) {
                event.reply("La partie doit être \"ent\", \"terminal\" ou \"outilogique\" !").setEphemeral(true).queue();
                return;
            }

            List<Long> ids = getListFromMap(filePath, partie, nb);
            if (ids != null) {
                if (ids.isEmpty()) {
                    event.reply("Personne n'est inscrit au **TP " + nb + "** de la partie **" + partie + "** !").setEphemeral(true).queue();
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Les inscrits au **TP ").append(nb).append("** de la partie **").append(partie).append("** sont :");
                for (Long id : ids) {
                    sb.append("\n- <@").append(id).append(">");
                }
                event.reply(sb.toString()).setEphemeral(true).queue();
            } else {
                event.reply("Le TP " + nb + " de la partie " + partie + " n'existe pas !").setEphemeral(true).queue();
            }
        }
    }
}
