package fr.nathan.ugebot.fonctions;

import java.util.Calendar;
import java.util.Locale;

public class DateFonction {

    public static String getDate(){
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        String heure = "" + cal.get(Calendar.HOUR_OF_DAY);
        String min = "" + cal.get(Calendar.MINUTE);
        String sec = "" + cal.get(Calendar.SECOND);

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
