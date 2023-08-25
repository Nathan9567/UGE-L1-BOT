package fr.nathan.ugebot.events;

import fr.nathan.ugebot.functions.Verification;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static fr.nathan.ugebot.functions.DateFunction.getDate;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        User user = null;
        if (event.getChannel().getName().equals("verifications")) {
            user = event.getMessage().getMentions().getUsers().get(0);
        }
        switch (event.getComponentId()) {
            case "okStudent" -> {
                String msg = event.getMessage().getContentRaw();
                String res = msg.substring(msg.length() - 9, msg.length() - 3);
                try {
                    assert user != null;
                    if (!Verification.checkUser(user.getId())) {
                        if (!Verification.checkStudent(res)) {
                            Objects.requireNonNull(event.getGuild()).addRoleToMember(user, Objects.requireNonNull(event.getGuild().getRolesByName("étudiant(e)", true).get(0))).queue();
                            event.getGuild().removeRoleFromMember(user, Objects.requireNonNull(event.getGuild().getRolesByName("Non vérifié", true).get(0))).queue();
                            Verification.addToFile("verif.csv", user.getId() + ";" + res);
                            user.openPrivateChannel().queue((chan -> chan.sendMessage("Votre demande a été **accepté**. Vous avez désormais accès a l'ensemble des salons utiles a votre année.").queue()));
                            event.getMessage().delete().queue();
                            event.getGuild().getTextChannelsByName("verifications-logs", true).get(0).sendMessage("✅ `[" + getDate() + "]` L'utilisateur "
                                    + user.getAsMention() + " a été vérifié par " + event.getUser().getAsMention() + ".").queue();
                        } else {
                            event.getMessage().delete().queue();
                            Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("Un utilisateur a" +
                                    " déjà été vérifié avec le numéro d'étudiant : **" + res + "** ! (Demande de " + user.getAsMention() + ")").queue();
                        }
                    } else {
                        event.getMessage().delete().queue();
                        event.getChannel().sendMessage("L'utilisateur " + user.getAsMention() + " a déjà été vérifié !\n*(Ce message s'auto-supprime au bout de 30 secondes !)*").queue((message) -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "notOkStudent" -> {
                assert user != null;
                user.openPrivateChannel().queue((chan -> chan.sendMessage("""
                        Votre demande a été **rejeté**. Plusieurs raisons peuvent en être la cause tel qu'un numéro d'étudiant non valide ou encore un non respect des règles.
                        Si vous pensez que cela n'est pas normal, vous pouvez réitérer votre demande.
                        Si celle-ci est de nouveau rejeté, merci de contacter un administrateur en privé.""").queue()));
                event.getMessage().delete().queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("❌ `[" + getDate() + "]` L'utilisateur "
                        + user.getAsMention() + " a été refusé par **" + event.getUser().getName() + "**.").queue();
            }
            case "nameReq" -> {
                assert user != null;
                user.openPrivateChannel().queue((chan -> chan.sendMessage("""
                        L'équipe d'adminstrateurs du discord a constaté que vous ne vous n'étiez pas renommé avec la syntaxe `Prenom Nom`. Pour pouvoir être validé, c'est une **obligation**.
                        Pour se renommer, il suffit de cliquer en haut a gauche sur **L1 MI 2022/23 S1**, puis *Modifier le profil du serveur* et enfin écrivez votre prénom et nom dans *Pseudo*.
                        Une fois fini, vous pouvez refaire la commande /verifyme sur le discord et vous serez accepté.""").queue()));
                event.getMessage().delete().queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("verifications-logs", true).get(0).sendMessage("\uD83D\uDCDD `[" + getDate() + "]` Demande de changement " +
                        "de pseudo envoyé à " + user.getAsMention() + " par **" + event.getUser().getName() + "**.").queue();
            }
            case "help" -> {
                event.getUser().openPrivateChannel().queue((chan ->
                        chan.sendMessage("Votre message a été pris en compte, un administrateur" +
                                " va revenir vers vous dans les plus brefs délais.").queue()));
                event.reply("Votre message a été pris en compte, un administrateur " +
                        "va revenir vers vous dans les plus brefs délais.").setEphemeral(true).queue();
                Objects.requireNonNull(event.getGuild()).getTextChannelsByName("demande-d-aide", true).get(0).sendMessage("\uD83D\uDCDD `[" + getDate() + "]` " +
                                event.getUser().getAsMention() + " a demandé de l'aide. Merci de bien vouloir le contacter au plus vite. " +
                                event.getGuild().getRolesByName("Administrateur Discord", true).get(0).getAsMention())
                        .setActionRow(Button.success("helpReq", "Prise en charge")).queue();
            }
            case "helpReq" -> {
                event.getMessage().editMessage("**Demande d'aide prise en charge par " + event.getUser().getAsMention() + ":**\n"
                                + event.getMessage().getContentRaw())
                        .setActionRow(Button.danger("helpClose", "Clôturer")).queue();
                event.reply("Tu as bien pris en charge l'étudiant.").setEphemeral(true).queue();
            }
            case "helpClose" -> {
                event.getChannel().sendMessage("✅ `[" + getDate() + "]` La demande de "
                        + event.getMessage().getMentions().getUsers().get(1).getAsMention()
                        + " a été cloturé par " + event.getUser().getAsMention()).queue();
                event.getMessage().delete().queue();
                event.reply("La demande a été cloturé !").setEphemeral(true).queue();
            }
        }
    }

}
