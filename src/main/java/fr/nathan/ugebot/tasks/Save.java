package fr.nathan.ugebot.tasks;

import fr.nathan.ugebot.events.PreRentree;

import java.io.IOException;
import java.util.TimerTask;

public class Save extends TimerTask {
    @Override
    public void run() {
        try {
            PreRentree.getReactionFile();
            PreRentree.setReactionFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
