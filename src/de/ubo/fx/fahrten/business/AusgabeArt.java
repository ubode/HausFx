package de.ubo.fx.fahrten.business;

/**
 * Created by ulric on 08.08.2016.
 */
public enum AusgabeArt {
    SCHULDZINS(1, "Schuldzinsen",  "Schuldzins"),
    AUFWAND_VOLL(2, "Aufwendungen für laufende Instandhaltung und Instandsetzung -Abrechnung im Rechnungsjahr voll-",  "Aufwendung voll"),
    AUFWAND_TEIL(3, "Aufwendungen für laufende Instandhaltung und Instandsetzung -Absetzung verteilt auf 2 bis 5 Jahre-",  "Aufwendung verteilt"),
    STEUER(4, "Grundsteuer, Strassenreinigung, Abfallbeseitigung",  "Steuer, Reinigung"),
    STROM(5, "Stromkosten, Wasserversorgung, Entwässerung",  "Strom + Wasser"),
    HEIZUNG(6, "Kosten für Zentralheizung, Warmwasserversorgung",  "Heizung"),
    VERSICHERUNG(7, "Versicherungsbeiträge",  "Versicherung"),
    HAUSWART(8, "Hauswart, Hausverwaltung, Hausreinigung",  "Hausverwaltung"),
    SONSTIGES(9, "Sonstiges (Telefon, Porti, Bankspesen, Fahrtkosten, Beitrag Haus + Grund)",  "Sonstiges"),
    RUECKLAGE(10, "Rücklagen",  "Rücklagen"),
    ENTNAHME(11, "Entnahmen",  "Entnahmen"),
    ;

    int id;
    String beschreibung;
    String kurzBeschreibung;

    AusgabeArt(int id, String beschreibung, String kurzBeschreibung) {
        this.id = id;
        this.beschreibung=beschreibung;
        this.kurzBeschreibung=kurzBeschreibung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getKurzBeschreibung() {
        return kurzBeschreibung;
    }

    public void setKurzBeschreibung(String kurzBeschreibung) {
        this.kurzBeschreibung = kurzBeschreibung;
    }

    public static AusgabeArt getByBeschreibung(String beschreibung) {
        for (AusgabeArt ausgabeArt : AusgabeArt.values()) {
            if (ausgabeArt.getKurzBeschreibung().equals(beschreibung)) return ausgabeArt;
        }
        return null;
    }
}
