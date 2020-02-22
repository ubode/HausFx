package de.ubo.fx.fahrten.business;

/**
 * Created by ulric on 08.08.2016.
 */
public enum ZahlungsKategorie {
    MIETZINS(1, "Mietzins"),
    NEBENKOSTENABRECHNUNG(2, "Nebenkostenabrechnung"),
    KAUTION(3, "Mietkaution"),
    ;

    int id;
    String beschreibung;

    ZahlungsKategorie(int id, String beschreibung) {
        this.id = id;
        this.beschreibung=beschreibung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

}
