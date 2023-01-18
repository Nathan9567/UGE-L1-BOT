package fr.nathan.ugebot.events;

import fr.nathan.ugebot.functions.UsefulFunction;
import fr.nathan.ugebot.functions.Verification;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UpdateGroups extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getName().equals("role") && e.getSubcommandName().equals("update")) {
            try {
                HashMap<String, String> verifList = Verification.getFile();
                FileReader reader = new FileReader("liste2022-2023.csv");
                BufferedReader br = new BufferedReader(reader);

                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    String userid = "";
                    String[] list = line.split(";");

                    if (!list[0].equals("") && verifList.containsValue(list[0])) {
                        for (String key : verifList.keySet()) {
                            if (verifList.get(key).equals(list[0])) {
                                userid = key;
                            }
                        }
                    }

                    // Conditions d'erreurs
                    if (userid.equals("")) continue;
                    try {
                        e.getGuild().getMemberById(userid);
                    } catch (NullPointerException exception) {
                        continue;
                    }
                    if (!Verification.checkUser(userid)) continue;

                    String[] roles = {"Atelier d'écriture", "Physique", "Electronique", "Communication 2",
                            "Italien", "Jap", "Anglais renforcé", "Communication 1", "Logique"};
                    for (int i = 1; i < 10 && list[i].equalsIgnoreCase("X"); i++) {
                        if (i==6) {
                            if (list[15].equals("1"))
                                UsefulFunction.addRole(e.getGuild(), userid, "Japonais Gr. 1");
                            else
                                UsefulFunction.addRole(e.getGuild(), userid, "Japonais Gr. 2");
                        } else {
                            UsefulFunction.addRole(e.getGuild(), userid, roles[i - 1]);
                        }
                    }

                    if (!list[10].equals(""))
                        UsefulFunction.addRole(e.getGuild(), userid, "Amphi " + list[10]);
                    if (!list[11].equals(""))
                        UsefulFunction.addRole(e.getGuild(), userid, "TD-" + list[11]);
                    if (!list[12].equals(""))
                        UsefulFunction. addRole(e.getGuild(), userid, "TP-" + list[12]);
                    else if (!list[13].equals(""))
                        UsefulFunction.addRole(e.getGuild(), userid, "TP-" + list[13]);
                    if (!list[14].equals(""))
                        UsefulFunction.addRole(e.getGuild(), userid, "Anglais Gr" + list[14]);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
