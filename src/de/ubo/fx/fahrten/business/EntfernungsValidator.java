package de.ubo.fx.fahrten.business;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bode on 01.03.2016.
 */
public class EntfernungsValidator {
    public static Map<Entfernung, EntfernungMeldung> validateEntfernung(Entfernung entfernung, Integer wert){
        Map<Entfernung, EntfernungMeldung> meldungen = new HashMap<>();
        if (wert == null) {
            meldungen.put(entfernung, EntfernungMeldung.ENTFERNUNG_NUMERISCH);
        }
        return meldungen;
    }

    public enum EntfernungMeldung
    {
        IO("Daten sind in Ordnung."),
        ENTFERNUNG_NUMERISCH("Die Entfernung muss numerisch ganzzahlig sein."),
        ;

        private final String text;

        EntfernungMeldung(String text){this.text = text;}

        public String getText() {
            return text;
        }
    }

}
