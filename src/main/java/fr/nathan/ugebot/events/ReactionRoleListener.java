package fr.nathan.ugebot.events;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ReactionRoleListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getJDA().upsertCommand("rolemessage", "Gere les messages d'attribution de roles")
                .addOption(OptionType.CHANNEL, "channel", "Channel du message (par défaut: actuel)", false)
                .addOption(OptionType.STRING, "messageid", "Identifiant du message à modifier", true)
                .addSubcommands(
                        new SubcommandData("add", "Permet d'ajouter une réaction au message")
                        .addOption(OptionType.STRING, "reaction", "Réaction à ajouter", true)
                        .addOption(OptionType.ROLE, "role", "Role associé au clic sur la réaction", true),
                        new SubcommandData("remove", "Permet de supprimer une réaction du message")
                                .addOption(OptionType.STRING, "reaction", "Réaction à retirer"),
                        new SubcommandData("reset", "Permet de supprimer toutes les réactions du message")
                ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("role")) {
            event.reply("Commande en cours de développement").queue();
        }
    }
}
