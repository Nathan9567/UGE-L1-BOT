package fr.nathan.ugebot.events.setup;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SetupListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getJDA().upsertCommand("setup", "Permet de configurer le serveur")
                .addSubcommands(new SubcommandData("tutoriel", "Créer le message de tutoriel dans les salons"))
                .addSubcommands(new SubcommandData("welcome", "Créer le message d'arrivée dans le salon dédié"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
    }
}
