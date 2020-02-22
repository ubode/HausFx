package de.ubo.fx.fahrten.business;

import java.util.*;

/**
 * Created by Bode on 01.03.2016.
 */
public class AnlassValidator {

    public static Map<Anlass, AnlassMeldung> validateAnlass(Anlass anlass){
        Map<Anlass, AnlassMeldung> meldungen = new HashMap<>();
        if (anlass.getPosition() == null) {
            meldungen.put(anlass, AnlassMeldung.POSITION_FEHLT);
        }
        if (anlass.getText().isEmpty()) {
            meldungen.put(anlass, AnlassMeldung.TEXT_FEHLT);
        }
        if (anlass.getKurzText().isEmpty()) {
            meldungen.put(anlass, AnlassMeldung.KURZ_TEXT_FEHLT);
        }

        return meldungen;
    }

    public static Map<Anlass, AnlassMeldung> validateAnlaesse(Collection<Anlass> anlaesse){
        Map<Anlass, AnlassMeldung> meldungen = new HashMap<>();
        Set<Integer> positionen = new HashSet<>();
        for (Anlass anlass: anlaesse) {
            meldungen.putAll(validateAnlass(anlass));
            if (positionen.contains(anlass.getPosition())) {
                meldungen.put(anlass, AnlassMeldung.POSITION_MEHRFACH);
            } else {
                positionen.add(anlass.getPosition());
            }
        }

        return meldungen;
    }

    public enum AnlassMeldung
    {
        IO("Daten sind in Ordnung."),
        POSITION_FEHLT("Für den Anlass ist keine Position angegeben."),
        POSITION_MEHRFACH("Die Position ist mehrfach vergeben. Sie muss eindeutig sein."),
        TEXT_FEHLT("Bitte einen Text eingeben."),
        KURZ_TEXT_FEHLT("Bitte einen Kurztext eingeben.");

        private final String text;

        AnlassMeldung(String text){this.text = text;}

        public String getText() {
            return text;
        }
    }

}
