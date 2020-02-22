package de.ubo.fx.fahrten.helper;

import de.ubo.fx.fahrten.business.MietVertrag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ulric on 27.12.2016.
 */
public class DatumHelper {
    private static DateFormat dateInternational = new SimpleDateFormat("yyyy-MM-dd");

    public static Date stringToDate(String datumStr) {
        int tag;
        int monat;
        int jahr;

        if (datumStr.contains(".")) {
            String[] teile = datumStr.split(".");
            tag = Integer.valueOf(teile[0]);
            monat = Integer.valueOf(teile[1]);
            jahr = Integer.valueOf(teile[2]);
        } else {
            String[] teile = datumStr.split("-");
            jahr = Integer.valueOf(teile[0]);
            monat = Integer.valueOf(teile[1]);
            tag = Integer.valueOf(teile[2]);
        }
        Calendar cal = new GregorianCalendar(jahr, monat - 1, tag);
        return cal.getTime();
    }

    public static String getDatumInternational(Date date) {
        return dateInternational.format(date);
    }
}
