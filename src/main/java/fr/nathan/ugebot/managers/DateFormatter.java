package fr.nathan.ugebot.managers;

import java.util.Calendar;
import java.util.Locale;

public class DateFormatter {

    public static String getDate() {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        String heure = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(cal.get(Calendar.MINUTE));
        String sec = String.valueOf(cal.get(Calendar.SECOND));

        if (heure.length() == 1) {
            heure = 0 + heure;
        }
        if (min.length() == 1) {
            min = 0 + min;
        }
        if (sec.length() == 1) {
            sec = 0 + sec;
        }
        return heure + ":" + min + ":" + sec;
    }

}
