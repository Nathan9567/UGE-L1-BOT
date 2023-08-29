package fr.nathan.ugebot.events.setup;

import fr.nathan.ugebot.managers.VerifManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class SetupListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getJDA().upsertCommand("setup", "Permet de configurer le serveur")
                .addSubcommands(new SubcommandData("tutoriel", "Créer le message de tutoriel dans les salons"))
                .addSubcommands(new SubcommandData("welcome", "Créer le message d'arrivée dans le salon dédié"))
                .addSubcommands(new SubcommandData("verification", "Reset les rôles étudiants temporaires"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        event.getJDA().upsertCommand("reload", "Permet de recharger les commandes")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("reload")) {
            event.getJDA().updateCommands().queue();
            event.reply("Les commandes ont été rechargées.").setEphemeral(true).queue();
            return;
        }
        if (event.getName().equals("setup") && Objects.equals(event.getSubcommandName(), "verification")) {
            event.reply("Les rôles étudiants temporaires ont été réinitialisés.\n" +
                    "Penser à changer le code dans le WelcomeListener.").setEphemeral(true).queue();
            for (Member mbr : Objects.requireNonNull(event.getGuild()).getMembers()) {
                if (mbr.getRoles().contains(event.getGuild().getRolesByName("Non vérifié", true).get(0))) {
                    VerifManager.safeRemoveRole(mbr, "étudiant(e)");
                }
            }

        }
    }
}
