package de.ubo.fx.fahrten.business;

/**
 * Created by ulric on 08.08.2016.
 */
public enum NebenkostenKategorie {
    GRUNDSTEUER(1, "NK Grundsteuer",  "Grundsteuer"),
    STRASSENREINIGUNG(2, "NK Straße",  "Straßenreinigung"),
    WINTERDIENST(3, "NK Winterdienst",  "Winterdienst"),
    VERSICHERUNGEN(4, "NK Versicherungen",  "Mietshäuser:Instandhaltung"),
    STROM(5, "NK Strom",  "Steuer.*"),
    WASSER(6, "NK Wasser", ".*"),
    ABWASSER(7, "NK Abwasser", ".*"),
    MUELL(8, "NK Müllabfuhr", ".*"),
    HEIZKOSTEN(10, "NK Heizkosten", ".*")
    ;

    int id;
    String beschreibung;
    String suchBegriff;

    NebenkostenKategorie(int id, String beschreibung, String kurzBeschreibung) {
        this.id = id;
        this.beschreibung=beschreibung;
        this.suchBegriff=kurzBeschreibung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getsuchBegriff() {
        return suchBegriff;
    }

    public void setsuchBegriff(String suchBegriff) {
        this.suchBegriff = suchBegriff;
    }

}

