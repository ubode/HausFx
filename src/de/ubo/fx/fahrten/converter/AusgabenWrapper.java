package de.ubo.fx.fahrten.converter;

import de.ubo.fx.fahrten.business.Ausgabe;
import de.ubo.fx.fahrten.business.AusgabeArt;
import de.ubo.fx.fahrten.business.AusgabePosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

public class AusgabenWrapper {
    private final static Logger LOGGER = Logger.getLogger(AusgabenWrapper.class.getName());

    public static Collection<Ausgabe> wrap(Collection<Object[]> ausgabenZeilen) {
        Collection<Ausgabe> ausgaben = new ArrayList<>(100);
        Ausgabe aktAusgabe = null;
        long ausgabeId = 1l;

        for (Object[] ausgabenZeile : ausgabenZeilen) {
            Ausgabe newAusgabe = wrap(aktAusgabe, ausgabenZeile);
            if (newAusgabe != aktAusgabe) {
                newAusgabe.setId(ausgabeId++);
                ausgaben.add(newAusgabe);
                aktAusgabe = newAusgabe;
            }
        }

        return ausgaben;
    }

    /**
     * Ausgabe und Ausgabepositionen aus der uebergebenen Zeile ermitteln.
     * Datum gefuellt => neue Ausgabe erzeugen und Positionen hinzufuegen
     * sonst =>  Positionen der uebergebenen Ausgabe hinzufuegen
     *
     * @param ausgabe - aktuelle Ausgabe
     * @param teile - zu scannende Zeile
     */
    private static Ausgabe wrap(Ausgabe ausgabe, Object[] teile) {

        Date datum = (Date) teile[0];
        String referenz = (String) teile[1];
        String empfaenger = (String) teile[2];
        String art = (String) teile[3];

        // neues Datum => neue Ausgabe m
        if (datum != null) {
            ausgabe = new Ausgabe();
            ausgabe.setDatum(datum);
            ausgabe.setEmpfaenger(empfaenger);
            ausgabe.setReferenz(referenz);
            LOGGER.info("neu Ausgabe: " + ausgabe.getReferenz());
        } else {
            if (!empfaenger.isEmpty()) {
                ausgabe.setVertragsNummer(empfaenger);
            }
        }

        ergaenzeAusgabePostionen(ausgabe, teile);

        return ausgabe;
    }

    /**
     * Ausgabepositionen ermitteln und zur Ausgabe hinzufuegen
     *
     * @param ausgabe - aktuelle Ausgabe
     * @param teile - String[] der Eigenschaften der Zeile
     */
    private static void ergaenzeAusgabePostionen(Ausgabe ausgabe, Object[] teile) {
/*
            String empfaenger = teile[2];
            String art = teile[3];
            String schulden = teile[4];
            String laufend = teile[5];
            String verteilt = teile[6];
            String steuer = teile[7];
            String strom = teile[8];
            String heizung = teile[9];
            String versicherung = teile[10];
            String hauswart = teile[11];
            String sonstiges = teile[12];
            String ruecklagen = teile[13];
            String entnahmen = teile[14];
*/
        for (AusgabeArt ausgabeArt : AusgabeArt.values()) {
            int i = ausgabeArt.ordinal() + 4;
            int j = 1;
            double betrag = (Double) teile[i];
            if (betrag > 0.0) {
                AusgabePosition ausgabePosition = new AusgabePosition();
                ausgabePosition.setBeschreibung((String) teile[3]);
                ausgabePosition.setPositionsNummer(j++);
                ausgabePosition.setAusgabeArt(ausgabeArt);
                ausgabePosition.setBetrag(betrag);
                ausgabe.addPosition(ausgabePosition);
            }
        }
    }

}
