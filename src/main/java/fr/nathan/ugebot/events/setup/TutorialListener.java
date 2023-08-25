package fr.nathan.ugebot.events.setup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class TutorialListener extends ListenerAdapter {

    @NotNull
    private static EmbedBuilder getEmbedBuilderTuto1() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDCDC Vous êtes prêt à continuer ! \uD83D\uDE01");
        eb.setDescription("[Cliquez ici](https://discord.com/channels/1129032736766701690/1129032738712866937) pour retourner à l'accueil ! \n" +
                "[Cliquez ici](https://discord.com/channels/1129032736766701690/1129032738712866939) pour découvrir encore plus de chose sur discord ! :D");
        eb.setFooter("Vous pourrez revenir à ce tutoriel quand bon vous semble ! (pas de panique)");
        return eb;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("setup") && Objects.equals(event.getSubcommandName(), "tutoriel")) {

            String channel = "comprendre-discord";
            String channel2 = "comprendre-discord²";
            long idAdminDiscord = 1129032736921890854L;

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessage("""
                            **Vous venez de rejoindre le discord de la L1 MI de l'Université Gustave Eiffel, et vous êtes légèrement perdu?**

                            > Laissez-vous guider à travers les élémentaires de discord ⭐""").addFiles(FileUpload.fromData(new File("src/main/resources/images/tutoriel.png"))).queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessage("""
                            Vous connaissez désormais l'architecture de base d'un discord 😉
                            > **Passons maintenant aux informations sonores !** 🎙️""").addFiles(FileUpload.fromData(new File("src/main/resources/images/tutoriel1.png"))).queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessage("> Vous avez assimilé les bases de la communication vocale de discord ! **Passons désormais à l'écrit ! 🖊️**").addFiles(FileUpload.fromData(new File("src/main/resources/images/tutoriel2.png"))).queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessage("> Vous voici maintenant apte à intervenir dans les discussions écrites ! " +
                            "Peut être souhaitez-vous maintenant en apprendre plus sur les autres **membres discords ? \uD83D\uDD75️️**").addFiles(FileUpload.fromData(new File("src/main/resources/images/tutoriel3.png"))).queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessage("""
                            > Comme vous pouvez l'observer, Discord se présente comme étant un outil de discussion offrant diverses possibilités. Il permet à tous d'échanger librement et simplement ! 👍
                                                        
                            Vous avez désormais toutes les cartes en main pour devenir un as de discord ! Il ne vous reste plus qu'à **sélectionner vos groupes** dans le salon <#1003689157656330367> afin d'avoir accès aux salons de votre TD et TP.
                                                        
                            💠 Si vous avez la moindre question n'hésitez pas à mentionner un <idAdmin> pour obtenir des renseignements.
                                                        
                            ```
                            Pour mentionner quelqu'un :
                            ```
                            `@pseudo`
                                                        
                            Un grand merci à Antonin pour ce tutoriel.""".replace("<idAdmin>", "<@" + idAdminDiscord + ">")
                    ).queue();

            EmbedBuilder eb = getEmbedBuilderTuto1();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel, false).get(0)
                    .sendMessageEmbeds(eb.build()).queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel2, false).get(0)
                    .sendMessage("""
                            ```=== Pour aller plus loin OwO ===```
                            **Le Markdown sur Discord :**
                            Comme vous le savez peut être, il est possible sur discord d'écrire sous différents formats:
                            > **gras**
                            > *italic*
                            > __souligné__
                            > ~~barré~~
                            > ||spoiler||
                            > `sans format`
                                                        
                            Je vais utiliser le dernier format pour vous dévoiler les astuces pour écrire tout ça, ainsi a-t-on:
                            **gras** -> `**gras**`
                            *italic* -> `*italic*`
                            __souligné__ -> `__souligné__`
                            ~~barré~~-> `~~barré~~`
                            ||spoiler|| -> `||spoiler||`
                            `sans format` -> \\`sansformat\\`              (\\` = ALTGR+6 ou +7 selon les pcs)
                                                        
                            > pour indenter votre texte : `> votre texte`
                                                        
                            Notez que vous pouvez combiner des balises par exemple : ||__~~***un beau bordel***~~__|| = `||__~~***un beau bordel***~~__||`
                                                        
                            Pour plus d'information vous pouvez accéder directement à l'aide donnée par discord [ici](https://support.discord.com/hc/fr/articles/210298617-Les-bases-du-Markdown-de-Texte-Formatage-de-la-conversation-gras-italique-souligné-).""")
                    .queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel2, false).get(0)
                    .sendMessage("""
                            --
                                                        
                                                        
                                                        
                                                        
                                                        
                            **Ecrire du code sur discord :**
                            Il vous est également possible d'écrire du code sur discord, pour cela vous devez utiliser le format suivant:
                                                        
                            \\```"nom-de-votre-langage"
                            votre code ici
                            \\```
                                                        
                                                        
                            ```Quelques exemples pour votre L2:```
                                                        
                            __Python__
                            ```py
                            from copy import deepcopy
                            def nom(text): #fonction complexe, en recherche depuis 3 ans
                                return f"ton nom est {text}"
                            ```
                                                        
                                                        
                            ||\\```py
                            from copy import deepcopy
                            def nom(text): #fonction complexe, en recherche depuis 3 ans
                                return f"ton nom est {text}"\\```||
                                                        
                            vous pouvez également écrire "python".
                                                        
                            __Markdown__
                                                        
                            ```md
                            $\\forall n in {N}, 2n \\in 2{N}$
                            ###commentaire```
                                                        
                                                        
                            ||\\```md
                            $\\forall n in {N}, 2n \\in 2{N}$
                            ###commentaire\\```||
                                                        
                            vous pouvez également écrire "markdown" à la place de md.""").queue();

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel2, false).get(0)
                    .sendMessage("""
                            __**A noter :**__\s
                            Discord n'exécutera pas votre code, il l'affichera simplement, mais rien ne vous empêche d'afficher les retours vous même !
                                                        
                            __Exemple: __
                            ```py
                            from copy import deepcopy
                            def nom(text): #fonction complexe, en recherche depuis 3 ans
                                return f"ton nom est {text}"
                                                        
                            >>> nom('antonin')
                            ton nom est antonin```
                                                        
                            A nouveau, merci à Antonin pour son travail !""").queue();

            eb = new EmbedBuilder();
            eb.setTitle("\uD83D\uDCDC Bravo, vous êtes fin prêt ! \uD83C\uDF89");
            eb.setDescription("Maintenant, que vous connaissez un tas de choses sur Discord, vous pouvez retourner à l'accueil !\n" +
                    "[Cliquez ici](https://discord.com/channels/1129032736766701690/1129032738712866937) pour retourner à l'accueil !");
            eb.setFooter("Vous pourrez revenir à ce tutoriel quand bon vous semble ! (pas de panique)");

            Objects.requireNonNull(event.getGuild()).getTextChannelsByName(channel2, false).get(0).sendMessageEmbeds(eb.build()).queue();
            event.reply("Le tutoriel a bien été mis en place !").setEphemeral(true).queue();
        }
    }
}
