package fr.nathan.ugebot.events;

import fr.nathan.ugebot.fonction.Verification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

import static fr.nathan.ugebot.fonction.DateFonction.getDate;

public class CommandListener extends ListenerAdapter {

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

            case "verify" -> {
                Member mbr = event.getOption("utilisateur").getAsMember();
                OptionMapping numEtuOption = event.getOption("numetudiant");
                int numetu = 0;
                if (numEtuOption != null) {
                    numetu = numEtuOption.getAsInt();
                }
                if (mbr != event.getMember()) {
                    try {
                        if (!Verification.checkUser(mbr.getId())) {
                            try {
                                event.getGuild().addRoleToMember(mbr, event.getGuild().getRoleById(1012973566104453170L)).queue();
                                event.reply("L'utilisateur " + mbr.getAsMention() + " est désormais vérifié.").setEphemeral(true).queue();
                                event.getGuild().getTextChannelById(1010540662581641337L).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                        + mbr.getAsMention() + " a été vérifié **manuellement** par **" + event.getUser().getAsTag() + "**.").queue();
                                mbr.getUser().openPrivateChannel().queue((chan) -> {
                                    chan.sendMessage("Vous venez d'être vérifié par " + event.getUser().getAsTag() + ".").queue();
                                });
                                Verification.addToFile(mbr.getId() + ";" + numetu);
                            } catch (Exception ex) {
                                event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                                event.reply("L'utilisateur n'est pas sur le discord.").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("L'utilisateur " + mbr.getAsMention() + " est déjà vérifié.").setEphemeral(true).queue();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    event.reply("Tu ne peux pas te vérifier toi même !").setEphemeral(true).queue();
                }
            }

            case "verifyme" -> {
                Integer numEtudiant = event.getOption("numetudiant").getAsInt();
                event.deferReply().setEphemeral(true).queue();
                event.getHook().sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                                "Une réponse vous sera donné dans les plus brefs délais.")
                        .setEphemeral(true).queue();
                event.getUser().openPrivateChannel().queue((chan) -> {
                    chan.sendMessage("Demande envoyé à l'équipe d'administration.\n" +
                            "Une réponse vous sera donné dans les plus brefs délais.").queue();
                });
                event.getJDA().getGuildById(1003689153768214569L).getTextChannelById(1003729944951664782L)
                        .sendMessage("✉ `[" + getDate() + "]` L'utilisateur " + event.getUser().getAsMention() +
                                " a demandé à être vérifié avec le numéro d'étudiant suivant : **" + numEtudiant + "**.")
                        .setActionRow(Button.success("okStudent", "Accepter"), Button.danger("notOkStudent", "Refuser"),
                                Button.secondary("nameReq", "Prenom/Nom"))
                        .queue();
            }

            case "students" -> {
                event.deferReply().setEphemeral(true).queue();
                for (Member student : event.getGuild().getMembers()) {
                    if (student.getRoles().get(0) != event.getGuild().getRoleById(1003689153877246022L)) {
                        try {
                            event.getGuild().addRoleToMember(student, event.getGuild().getRoleById(1003689153877246022L)).queue();
                            System.out.println(student.getNickname() + " étudiant");
                        } catch (Exception exception) {
                        }
                    }
                }
            }

            case "numetu" -> {
                Member mbrEtu = event.getOption("utilisateur").getAsMember();
                try {
                    HashMap<String, String> verifList = Verification.getFile();
                    if (verifList.containsKey(mbrEtu.getId())) {
                        event.reply("Le numéro d'étudiant de " + mbrEtu.getAsMention() +
                                        " est **" + verifList.get(mbrEtu.getId()) + "**.")
                                .setEphemeral(true).queue();
                    } else {
                        event.reply("Action impossible. L'utilisateur " + mbrEtu.getAsMention() +
                                " n'est pas vérifié !").setEphemeral(true).queue();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case "welcome" -> {
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
    }
}
