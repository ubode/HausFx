package de.ubo.fx.fahrten.io;

import de.ubo.fx.fahrten.business.Ausgabe;
import de.ubo.fx.fahrten.business.AusgabeArt;
import de.ubo.fx.fahrten.business.AusgabePosition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ulric on 08.08.2016.
 */
public class AusgabenReader {
    private final static Logger LOGGER = Logger.getLogger(AusgabenReader.class.getName());

    public class AusgabenWrapper {

        /**
         * Pruefung auf numerischen Inhalt des uebergebenen Strings
         *
         * @param string - zu pruefender string
         * @return  true, wenn numerisch, sonst false
         */
        boolean isNumeric(String string) {
            try {
                Double.valueOf(string);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        /**
         * Ausgabe und Ausgabepositionen aus der uebergebenen Zeile ermitteln.
         * Datum gefuellt => neue Ausgabe erzeugen und Positionen hinzufuegen
         * sonst =>  Positionen der uebergebenen Ausgabe hinzufuegen
         *
         * @param ausgabe - aktuelle Ausgabe
         * @param zeile - zu scannende Zeile
         */
        Ausgabe wrap(Ausgabe ausgabe, String zeile) {

            String[] teile = zeile.split(";");
            // Ueberschriften-Zeile? ja ==> rausspringen
            if (teile.length < 16) return ausgabe;

            String datumStr = teile[0];
            String referenz = teile[1];
            String empfaenger = teile[2];
            String art = teile[3];
            String betragStr = teile[15];
            betragStr = betragStr.replace(".","");
            betragStr = betragStr.replace(',','.');

            // enthaelt diese Zeile verwertbare Informationen? nein ==> rausspringen
            if (!isNumeric(betragStr) || art.isEmpty()) return ausgabe;

            // neues Datum => neue Ausgabe m
            if (!datumStr.isEmpty()) {
                ausgabe = new Ausgabe();
                ausgabe.setDatum(scanDate(datumStr));
                ausgabe.setEmpfaenger(empfaenger);
                ausgabe.setReferenz(referenz);
                LOGGER.info("neu Ausgabe: " + ausgabe.getReferenz());
            } else {
                if (!teile[2].isEmpty()) {
                    ausgabe.setVertragsNummer(teile[2]);
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
        private void ergaenzeAusgabePostionen(Ausgabe ausgabe, String[] teile) {
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
                String betragStr = teile[i];
                betragStr = betragStr.replace(".","");
                betragStr = betragStr.replace(',','.');
                if (isNumeric(betragStr)) {
                    AusgabePosition ausgabePosition = new AusgabePosition();
                    ausgabePosition.setBeschreibung(teile[3]);
                    ausgabePosition.setPositionsNummer(j++);
                    ausgabePosition.setAusgabeArt(ausgabeArt);
                    ausgabePosition.setBetrag(Double.valueOf(betragStr));
                    ausgabe.addPosition(ausgabePosition);
                }
            }
        }

        /**
         * Date aus String ermitteln
         *
         * @param aString
         * @return Date, ermitteltes Datum
         */
        private Date scanDate(String aString) {
            String[] teile = aString.split("\\.");
            int day = Integer.parseInt(teile[0]);
            int month = Integer.parseInt(teile[1]) - 1; // Monate starten bei 0 = Januar
            int year = Integer.parseInt("20" + teile[2]);
            GregorianCalendar calendar = new GregorianCalendar(year, month, day);
            return new Date(calendar.getTimeInMillis());
        }

    }

    public List<Ausgabe> readAusgaben(String fileName) throws IOException {
        AusgabenWrapper wrapper = new AusgabenWrapper();
        List<Ausgabe> ausgaben = new ArrayList<>();
        long ausgabeId = 1l;
        Ausgabe aktAusgabe = null;

        Reader fileReader;
        BufferedReader bufferedReader;

        fileReader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1.name());
        bufferedReader = new BufferedReader(fileReader);

        for (String zeile = bufferedReader.readLine(); zeile != null; zeile = bufferedReader.readLine()) {
            LOGGER.info("Zeile: " + zeile);
            Ausgabe newAusgabe = wrapper.wrap(aktAusgabe, zeile);
            if (newAusgabe != aktAusgabe) {
                newAusgabe.setId(ausgabeId++);
                ausgaben.add(newAusgabe);
                aktAusgabe = newAusgabe;
            }
        }

        // Buchungen zuordnen
        // ordneBuchungenZu(ausgaben);

        if (bufferedReader != null)
            bufferedReader.close();

        return ausgaben;
    }
}
