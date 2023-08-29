package fr.nathan.ugebot.events.setup;

import fr.nathan.ugebot.managers.GroupManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class GroupListener extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getJDA().upsertCommand("groupe", "Permet la gestion automatique des groupes")
                .addSubcommands(new SubcommandData("update", "Permet d'actualiser les rôles des membres en leur ajoutant leurs groupes.")
                        .addOption(OptionType.USER, "user", "Permet de mettre à jour le rôle d'un utilisateur en particulier.", false)
                        .addOption(OptionType.BOOLEAN, "all", "Permet de mettre à jour les rôles de tous les utilisateurs.", false))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("groupe") && Objects.equals(event.getSubcommandName(), "update")) {

            OptionMapping allOpt = event.getOption("all");
            OptionMapping userOpt = event.getOption("user");
            if (allOpt == null && userOpt == null) {
                event.reply("Vous devez forcément utiliser une des 2 options !").setEphemeral(true).queue();
                return;
            }

            if (allOpt != null && userOpt != null) {
                event.reply("Vous ne pouvez pas utiliser les 2 options en même temps !").setEphemeral(true).queue();
                return;
            }

            if (allOpt != null && allOpt.getAsBoolean()) {
                event.reply("Les rôles de tous les utilisateurs ont été mis à jour.").setEphemeral(true).queue();
                GroupManager.updateAllUsers(Objects.requireNonNull(event.getGuild()));
            } else if (allOpt != null && !allOpt.getAsBoolean()) {
                event.reply("Rien à été fait.").setEphemeral(true).queue();
            }

            if (userOpt != null) {
                Member mbr = userOpt.getAsMember();
                assert mbr != null;
                if (!GroupManager.updateUser(mbr))
                    event.reply("L'utilisateur " + mbr.getAsMention() + " n'a pas été trouvé dans la base de données.").setEphemeral(true).queue();
                else
                    event.reply("Les rôles de l'utilisateur " + mbr.getAsMention() + " ont été mis à jour.").setEphemeral(true).queue();
            }

        }
    }
}
