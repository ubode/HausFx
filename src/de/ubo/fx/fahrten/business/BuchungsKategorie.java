package de.ubo.fx.fahrten.business;

/**
 * Created by ulric on 08.08.2016.
 */
public enum BuchungsKategorie {
    MIETE(1, "Miete + Nebenkosten",  "Miete + Nebenkosten"),
    MIETKAUTION(2, "Mietkaution",  "Mietkaution"),
    ABGABEN(3, "Mietshäuser:Abgaben",  "Mietshäuser:Abgaben"),
    INSTANDHALTUNG(4, "Mietshäuser:Instandhaltung",  "Mietshäuser:Instandhaltung"),
    STEUER(5, "Steuer",  "Steuer.*"),
    ALLE(6, "Alle", ".*")
    ;

    int id;
    String beschreibung;
    String suchBegriff;

    BuchungsKategorie(int id, String beschreibung, String kurzBeschreibung) {
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

