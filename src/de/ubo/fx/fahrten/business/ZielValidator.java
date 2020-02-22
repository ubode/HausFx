package de.ubo.fx.fahrten.business;

import java.util.*;

/**
 * Created by Bode on 01.03.2016.
 */
public class ZielValidator {
    public static Map<Ziel, ZielMeldung> validateZiel(Ziel ziel){
        Map<Ziel, ZielMeldung> meldungen = new HashMap<>();
        if (ziel.getPosition() == null || ziel.getPosition() == 0) {
            meldungen.put(ziel, ZielMeldung.POSITION_FEHLT);
        }
        if (ziel.getText().isEmpty()) {
            meldungen.put(ziel, ZielMeldung.TEXT_FEHLT);
        }
        if (ziel.getKurzText().isEmpty()) {
            meldungen.put(ziel, ZielMeldung.KURZ_TEXT_FEHLT);
        }

        return meldungen;
    }

    public static Map<Ziel, ZielMeldung> validateZiele(Collection<Ziel> ziele){
        Map<Ziel, ZielMeldung> meldungen = new HashMap<>();
        Set<Integer> positionen = new HashSet<>();
        for (Ziel ziel: ziele) {
            meldungen.putAll(validateZiel(ziel));
            if (positionen.contains(ziel.getPosition())) {
                meldungen.put(ziel, ZielMeldung.POSITION_MEHRFACH);
            } else {
                positionen.add(ziel.getPosition());
            }
        }

        return meldungen;
    }

    public enum ZielMeldung
    {
        IO("Daten sind in Ordnung."),
        POSITION_FEHLT("Für das Ziel ist keine Position angegeben."),
        POSITION_MEHRFACH("Die Position ist mehrfach vergeben. Sie muss eindeutig sein."),
        TEXT_FEHLT("Bitte einen Text eingeben."),
        KURZ_TEXT_FEHLT("Bitte einen Kurztext eingeben.");

        private final String text;

        ZielMeldung(String text){this.text = text;}

        public String getText() {
            return text;
        }
    }

}
