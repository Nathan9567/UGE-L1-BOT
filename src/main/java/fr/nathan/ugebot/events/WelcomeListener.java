package fr.nathan.ugebot.events;

import fr.nathan.ugebot.fonction.Verification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class WelcomeListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if (e.getChannel().getIdLong() == 1003689157656330366L){
            if (e.getReaction().getEmoji().equals(Emoji.fromFormatted("\uD83D\uDC68\u200D\uD83C\uDFEB"))){
                e.getGuild().getTextChannelById(1003689157656330371L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
                e.getGuild().getTextChannelById(1003689157937340526L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
            } else if (e.getReaction().getEmoji().equals(Emoji.fromUnicode("✅"))){
                e.getGuild().getTextChannelById(1021473979619348581L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();
                /*e.getGuild().getTextChannelById(1021498368666636528L).getPermissionContainer().getManager()
                        .putMemberPermissionOverride(e.getUserIdLong(),
                                Collections.singleton(Permission.VIEW_CHANNEL), null).queue();*/
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannel().asTextChannel().getIdLong() == 1021473979619348581L) {
            try {
                if (Verification.checkUser(event.getUser().getId())){
                    event.getGuild().addRoleToMember(event.getUser(), event.getGuild().getRoleById(1012973566104453170L)).queue(); // vérifié
                    event.getGuild().addRoleToMember(event.getUser(), event.getGuild().getRoleById(1003689153877246022L)).queue(); // étudiant
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(event.getName().equals("welcome")) {
            event.reply("Le message d'arrivé est mis en place.").setEphemeral(true).queue();
            EmbedBuilder embed = new EmbedBuilder();
            EmbedBuilder embed2 = new EmbedBuilder();
            embed.setTitle("\uD83D\uDCDC Ce discord est une plateforme Universitaire");
            embed.setDescription("Un comportement adapté est donc attendu");
            embed.addField("Merci d'éviter tout propos injurieux ou déplacés",
                    "Les documents à caractère NSFW sont également interdits \uD83D\uDE09", false);
            embed.addField("Merci de vous renommer : \"Prenom Nom\" sur le discord",
                    "__Pour se renommer :__ cliquez sur **L1 MI 2022/23 S1** en haut a gauche, puis " +
                            "**modifier le profil du serveur**, et enfin entrer votre **Prénom Nom** " +
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

            event.getChannel().sendMessage("**Bienvenue sur le serveur discord de la L1 Maths-Info !**\n" +
                            "> L'objectif principal de ce Discord est de permettre la bonne circulation des informations" +
                            " en ligne, en période présentielle et distancielle. Sur Discord, tous les messages sont archivés," +
                            " et tout est fait pour que votre expérience soit la plus agréable possible. Enfin, ce Discord est un " +
                            "espace d'échange intra-promotion permettant l'obtention d'informations entre enseignant(e)s et étudiant(e)s. \n" +
                            "> Cela veut aussi dire que toute personne étrangère à la faculté n'a pas sa place ici et se verra banni si découvert.")
                    .setEmbeds(embed.build(), embed2.build()).queue((msg) -> {
                        msg.addReaction(Emoji.fromUnicode("\uD83E\uDDD1\u200D\uD83C\uDFEB")).queue();
                    });

            EmbedBuilder embed3 = new EmbedBuilder();
            embed3.setTitle(":eyes: Comment avoir accès à tous les salons du Discord L1 MI ?");
            embed3.setDescription("Pour cela, vous allez devoir être vérifié par un administrateur.\n" +
                    "\"Etre vérifier\", signifie que vous serez valider comme étudiant inscrit en L1.\n" +
                    "Pour savoir si vous l'êtes déjà, il vous suffit de regarder si <@883380527414018078>" +
                    " vous a envoyé un message de confirmation.");
            embed3.addField("Sinon, pour se faire vérifier :",
                    "Pour avoir accès au salon de vérification, cliquez sur la réaction  :white_check_mark:",false);
            embed3.setColor(0x2ccb73);
            event.getChannel().sendMessageEmbeds(embed3.build()).queue((msg) -> msg.addReaction(Emoji.fromUnicode("✅")).queue());

            EmbedBuilder embed4 = new EmbedBuilder();
            embed4.setTitle(":sob: Besoin d'aide ? Vous êtes perdu ?");
            embed4.setDescription("Cliquez sur le bouton rouge \"**Help**\" si vous en ressentez le besoin.");
            embed4.setColor(0xc93030);

            event.getChannel().sendMessageEmbeds(embed4.build()).setActionRow(Button.danger("help", "Help")).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getChannel().getName().equals("verifyme")) {
            if (!e.getMessage().getType().equals(MessageType.SLASH_COMMAND)){
                e.getMessage().delete().queue();
            }
        }
    }
}
