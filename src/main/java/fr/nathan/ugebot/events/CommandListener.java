package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        JDA api = event.getJDA();
        api.upsertCommand("ping", "Calcul ta latence avec le robot").queue();

        api.upsertCommand("poll", "Permet de faire un sondage.")
                .addOption(OptionType.STRING, "question", "Renseignez la question que vous souhaitez poser.", true)
                .addOption(OptionType.STRING, "choices", "Mettez vos choix à la suite" +
                        "séparés par un \";\". Si il n'y a pas de choix, cela est un oui/non.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).queue();
    }

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

            case "poll" -> {
                String qst = Objects.requireNonNull(event.getOption("question")).getAsString();
                OptionMapping choicesOption = event.getOption("choices");
                StringBuilder message = new StringBuilder(qst + "\n\n");
                if (choicesOption == null) {
                    event.reply("Sondage créé").setEphemeral(true).queue();
                    event.getChannel().sendMessage(message).queue((msg) -> {
                        msg.addReaction(Emoji.fromUnicode("✅")).queue();
                        msg.addReaction(Emoji.fromUnicode("❌")).queue();
                    });
                    break;
                }
                if (!choicesOption.getAsString().contains(";")) {
                    event.reply("Merci d'insérer au moins 2 choix en respectant la syntaxe " +
                            "décrite par la description de l'option.").setEphemeral(true).queue();
                    break;
                }
                String[] choices = choicesOption.getAsString().split(";");

                List<String> nbr = List.of("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "\uD83D\uDD1F",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
                int i = 0;
                for (String choix : choices) {
                    if (i < 10) {
                        message.append(nbr.get(i));
                    } else {
                        message.append(Objects.requireNonNull(event.getGuild()).getEmojisByName(nbr.get(i), true)
                                .get(0).getFormatted());
                    }
                    message.append(" ").append(choix).append("\n");
                    i++;
                    if (i == 20) {
                        break;
                    }
                }

                event.reply("Sondage créé").setEphemeral(true).queue();
                event.getChannel().sendMessage(message).queue((msg) -> {
                    int nb = 0;
                    while (nb != choices.length) {
                        if (nb < 10) {
                            msg.addReaction(Emoji.fromFormatted(nbr.get(nb))).queue();
                        } else {
                            msg.addReaction(Objects.requireNonNull(event.getGuild()).getEmojisByName(nbr.get(nb), true).get(0)).queue();
                        }
                        nb++;
                    }
                });
            }
        }
    }
}
