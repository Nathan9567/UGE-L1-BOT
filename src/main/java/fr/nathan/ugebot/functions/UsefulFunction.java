package fr.nathan.ugebot.functions;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Objects;

public class UsefulFunction {

    public static void addRole(Guild g, String userid, String role) {
        if (!Objects.requireNonNull(g.getMemberById(userid)).getRoles().contains(g.getRolesByName(role, true).get(0))) {
            g.addRoleToMember(Objects.requireNonNull(g.getMemberById(userid)), g.getRolesByName(role, true).get(0)).queue();
        }
    }
}
