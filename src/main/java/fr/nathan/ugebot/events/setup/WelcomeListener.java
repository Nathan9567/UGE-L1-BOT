package fr.nathan.ugebot.events.setup;

import fr.nathan.ugebot.managers.VerifManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static fr.nathan.ugebot.managers.DateFormatter.getDate;

public class WelcomeListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        VerifManager.safeAddRole(event.getMember(), "Non vérifié");
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if (e.getChannel().getName().equalsIgnoreCase("arrivée")) {
            if (e.getReaction().getEmoji().equals(Emoji.fromFormatted("\uD83E\uDDD1\u200D\uD83C\uDFEB"))) {
                e.getGuild().addRoleToMember(Objects.requireNonNull(e.getMember()), e.getGuild().getRolesByName("tuto", true).get(0)).queue();
            }
        }
        // Seulement jusqu'au 1er octobre
        if (e.getChannel().getName().equalsIgnoreCase("verifyme")) {
            if (e.getReaction().getEmoji().equals(Emoji.fromFormatted("✅"))) {
                VerifManager.safeAddRole(Objects.requireNonNull(e.getMember()), "étudiant(e)");
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("setup") && Objects.equals(event.getSubcommandName(), "welcome")) {
            event.reply("Le message d'arrivé est mis en place.").setEphemeral(true).queue();
            EmbedBuilder embed = new EmbedBuilder();
            EmbedBuilder embed2 = new EmbedBuilder();
            embed.setTitle("\uD83D\uDCDC Ce discord est une plateforme Universitaire");
            embed.setDescription("Un comportement adapté est donc attendu");
            embed.addField("Merci d'éviter tout propos injurieux ou déplacés",
                    "Les documents à caractère NSFW sont également interdits \uD83D\uDE09", false);
            embed.addField("Merci de vous renommer : \"Prenom Nom\" sur le discord",
                    "__Pour se renommer :__ cliquez sur **L1 MI 2023/24 S1** en haut à gauche, puis " +
                            "**modifier le profil du serveur**, et enfin entrez votre **Prénom Nom** " +
                            "dans la section **Pseudo**.", false);
            embed.setFooter("Un non respect des règles entrainera des sanctions");
            embed.setThumbnail("https://media.discordapp.net/attachments/644935102094376980" +
                    "/884218889125371915/logo-universite-gustave-eiffel-1579874878.png");
            embed.setColor(0x007aff);

            embed2.setTitle(":scroll: Formation Discord de la L1 MI :grin:");
            embed2.setDescription("Cliquez sur la réaction :teacher: sous ce message" +
                    " pour accéder à un tutoriel sur le fonctionnement de discord !");
            embed2.addField("Si vous n'êtes pas habitués à utiliser Discord allez-y.", "", false);
            embed2.setFooter("Temps de lecture : ~5 min");
            embed2.setThumbnail("https://media.discordapp.net/attachments/644935102094376980" +
                    "/884218889125371915/logo-universite-gustave-eiffel-1579874878.png");
            embed2.setColor(0xf4c40c);
            event.getChannel().sendMessage("""
                            **Bienvenue sur le serveur discord de la L1 Maths-Info !**
                            - L'objectif principal de ce Discord est de permettre la bonne circulation des informations en ligne, en période présentielle et distancielle. Sur Discord, tous les messages sont archivés, et tout est fait pour que votre expérience soit la plus agréable possible. Enfin, ce Discord est un espace d'échange intra-promotion permettant l'obtention d'informations entre enseignant(e)s et étudiant(e)s.\s
                            - Cela veut aussi dire que toute personne étrangère à la faculté n'a pas sa place ici et se verra banni si découvert.""")
                    .setEmbeds(embed.build(), embed2.build()).queue((msg) -> msg.addReaction(Emoji.fromUnicode("\uD83E\uDDD1\u200D\uD83C\uDFEB")).queue());

            EmbedBuilder embed3 = new EmbedBuilder();
            embed3.setTitle(":eyes: Comment avoir accès à tous les salons du Discord L1 MI ?");
            embed3.setDescription("Pour cela, vous allez devoir être vérifié par un administrateur.\n" +
                    "\"Etre vérifier\", signifie que vous serez validés comme étudiant inscrit en L1.\n" +
                    "Pour savoir si vous l'êtes déjà, il vous suffit de regarder si vous avez toujours accès au salon " +
                    Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifyme", true).get(0).getAsMention() +
                    ".");
            embed3.addField("Si vous n'êtes pas vérifié",
                    "Merci de suivre la procédure indiquée dans le salon " +
                            Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifyme", true).get(0).getAsMention() + ".\n"
                            + "(Vous pouvez cliquer sur le salon indiqué dans le message pour vous y rendre directement)", false);
            embed3.setColor(0x2ccb73);

            EmbedBuilder embed4 = new EmbedBuilder();
            embed4.setTitle(":sob: Besoin d'aide ? Vous êtes perdu ?");
            embed4.setDescription("Cliquez sur le bouton rouge \"**Help**\" si vous en ressentez le besoin.");
            embed4.setColor(0xc93030);

            event.getChannel().sendMessageEmbeds(embed3.build(), embed4.build()).setActionRow(Button.danger("help", "Help")).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "help" -> {
                event.getUser().openPrivateChannel().queue((chan ->
                        chan.sendMessage("Votre message a été pris en compte, un administrateur" +
                                " va revenir vers vous dans les plus brefs délais.").queue()));
                event.reply("Votre message a été pris en compte, un administrateur " +
                        "va revenir vers vous dans les plus brefs délais.").setEphemeral(true).queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("demande-d-aide", true)
                        .get(0).sendMessage("\uD83D\uDCDD `[" + getDate() + "]` " +
                                event.getUser().getAsMention() + " a demandé de l'aide. Merci de bien vouloir le contacter au plus vite. " +
                                event.getGuild().getRolesByName("Administrateur Discord", true).get(0).getAsMention())
                        .setActionRow(Button.success("helpOpen", "Prise en charge")).queue();
            }

            case "helpOpen" -> {
                event.getMessage().editMessage("**Demande d'aide prise en charge par " + event.getUser().getAsMention() + ":**\n"
                                + event.getMessage().getContentRaw())
                        .setActionRow(Button.danger("helpClose", "Clôturer")).queue();
                event.reply("Tu as bien pris en charge l'étudiant.").setEphemeral(true).queue();
            }

            case "helpClose" -> {
                event.getChannel().sendMessage("✅ `[" + getDate() + "]` La demande de "
                        + event.getMessage().getMentions().getUsers().get(1).getAsMention()
                        + " du *" + event.getMessage().getTimeCreated().getDayOfMonth() + "/" +
                        event.getMessage().getTimeCreated().getMonthValue() + " à "
                        + event.getMessage().getTimeCreated().getHour() + ":" + event.getMessage().getTimeCreated().getMinute()
                        + ":" + event.getMessage().getTimeCreated().getSecond() + "* a été cloturé par "
                        + event.getUser().getAsMention()).queue();
                event.getMessage().delete().queue();
                event.reply("La demande a été cloturé !").setEphemeral(true).queue();
            }
        }
    }

}