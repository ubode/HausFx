package de.ubo.fx.fahrten.helper;

/**
 * Created by ulric on 08.08.2016.
 */
public enum Kriterium {
    KEIN_KRITERIUM("... Kriterium"),
    HAUS("Haus"),
    DATUM("Datum"),
    EMPFAENGER("Empfänger"),
    VERWENDUNG("Verwendung"),
    KATEGORIE("Kategorie"),
    BETRAG("Betrag")
    ;

    String name;

    Kriterium(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
