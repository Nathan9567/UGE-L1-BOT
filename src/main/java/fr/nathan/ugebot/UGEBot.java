package fr.nathan.ugebot;

import fr.nathan.ugebot.events.CommandListener;
import fr.nathan.ugebot.events.PreRentree;
import fr.nathan.ugebot.events.VerifListener;
import fr.nathan.ugebot.events.setup.GroupListener;
import fr.nathan.ugebot.events.setup.SetupListener;
import fr.nathan.ugebot.events.setup.TutorialListener;
import fr.nathan.ugebot.events.setup.WelcomeListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UGEBot {


    public static void main(String[] args) throws Exception {
        // UGE BOT
        String token = Files.readString(Paths.get("token.txt"));
        JDA api = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES).enableCache(CacheFlag.MEMBER_OVERRIDES)
                .build();
        api.addEventListener(new PreRentree());
        api.addEventListener(new CommandListener());
        api.addEventListener(new TutorialListener());
        api.addEventListener(new WelcomeListener());
        api.addEventListener(new SetupListener());
        api.addEventListener(new VerifListener());
        api.addEventListener(new GroupListener());

        api.awaitReady();

        if (System.console() != null)
            if (System.console().readLine().equals("restart"))
                System.exit(0);

    }
}
