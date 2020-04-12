package de.ubo.fx.fahrten.io;

import de.ubo.fx.fahrten.business.Buchung;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ulric on 01.08.2016.
 */
public class BuchungsReader {
    private final static Logger LOGGER = Logger.getLogger(BuchungsReader.class.getName());

    public class BuchungsWrapper {
        Buchung wrap(Buchung buchung, String zeile) {
            Character kennung = zeile.charAt(0);
            String inhalt = zeile.substring(1).trim();

            switch (kennung) {
                case 'D': //Datum
                    buchung = new Buchung();
                    buchung.setDatum(scanDate(inhalt));
                    break;

                case 'U': // Betrag
                    buchung.setBetrag(scanBetrag(inhalt));
                    break;

                case 'P': // Empfaenger
                    buchung.setEmpfaenger(inhalt);
                    break;

                case 'M': // Verwendung
                    buchung.setVerwendung(inhalt);
                    break;

                case 'L': // Kategorie
                    buchung.setKategorie(inhalt);
                    break;

                case 'N': // Nummer
                    buchung.setBuchungsNummer(scanNummer(buchung, inhalt));
                    break;

                default:
                    break;
            }

            return buchung;
        }

        /**
         * Calendar aus String ermitteln
         * @param aString
         * @return Calendar, ermitteltes Datum
         */
        Date scanDate(String aString) {
            String[] teile = aString.split("\\.");
            int month = Integer.parseInt(teile[0]) - 1; // Monate starten bei 0 = Januar
            int day = Integer.parseInt(teile[1]);
            int year = Integer.parseInt("20" + teile[2]);
            GregorianCalendar calendar = new GregorianCalendar(year, month, day);
            return new Date(calendar.getTimeInMillis());
        }

        /**
         * Betrag aus String ermitteln
         * @param betragStr umzuwandelnder String
         * @return double, ermittelter Betrag
         */
        double scanBetrag(String betragStr) {

            betragStr = betragStr.replace(",", "");
            return Double.valueOf(betragStr);
        }


        /**
         * Buchungsnummer aus String ermitteln
         * @param buchung Buchung deren Buchungsnummer erzeugt werden soll
         * @param aString umzuwandelnder String
         * @return String, Buchungsnummer ergaenzt um Jahresangabe
         */
        String scanNummer(Buchung buchung, String aString) {

            StringBuilder buchungsNummerStr = new StringBuilder();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(buchung.getDatum());
            Integer jahr = calendar.get(Calendar.YEAR);

            /* ab dem Kalenderjahr 2019 wird der Buchungsnummer das Jahr hinzugefügt, um
            doppelt vergebene Buchungsnummern eindeutig zu machen */
            if (jahr > 2018) {
                buchungsNummerStr.append(jahr);
                buchungsNummerStr.append('-');
            }

            buchungsNummerStr.append(aString);

            return buchungsNummerStr.toString();
        }

    }

    public List<Buchung> readBuchungen(String fileName) throws IOException {
        BuchungsWrapper wrapper = new BuchungsWrapper();
        List<Buchung> buchungen = new ArrayList<>();
        Buchung aktBuchung = null;

        Reader fileReader;
        BufferedReader bufferedReader;

        fileReader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1.name());
        bufferedReader = new BufferedReader(fileReader);

        for (String zeile = bufferedReader.readLine(); zeile != null; zeile = bufferedReader.readLine()) {
            Buchung newBuchung = wrapper.wrap(aktBuchung, zeile);
            if (newBuchung != aktBuchung) {
                buchungen.add(newBuchung);
                aktBuchung = newBuchung;
            }
        }

        // Buchungsnummer eindeutig machen
        pruefeBuchungsNummern(buchungen);

        if (bufferedReader != null)
            bufferedReader.close();

        return buchungen;
    }

    /**
     * Buchungsnummer eindeutig machen
     * @param buchungen Liste der eingelesenen Buchungen
     */
    private void pruefeBuchungsNummern(List<Buchung> buchungen) {
        HashSet<String> buchungsNummern = new HashSet<>(2000);

        for (Buchung buchung: buchungen) {

            String buchungsNummer = buchung.getBuchungsNummer();
            if (buchungsNummer == null || buchungsNummer.isEmpty()) {
                buchungsNummer = erzeugeBuchungsNummer(buchung);
            }
            if (buchungsNummern.contains(buchungsNummer)){
                LOGGER.warning("doppelte Buchungsnummer: " + buchungsNummer);
                for (int i = 1; i < 99; i++) {
                    String buchungsNummerNeu = buchungsNummer + "-" + i;
                    if (!buchungsNummern.contains(buchungsNummerNeu)) {
                        buchung.setBuchungsNummer(buchungsNummerNeu);
                        LOGGER.info("neue Buchungsnummer: " + buchungsNummerNeu);
                        break;
                    }
                }
            }

            // endgültige Buchungsnummer in den Speicher
            buchungsNummern.add(buchung.getBuchungsNummer());
        }
    }

    /**
     * Aus Datum, Betrag und Empfänger wird eine neue Buchungsnummer erzeugt
     * @param buchung
     * @return
     */
    private String erzeugeBuchungsNummer(Buchung buchung) {
        StringBuilder buchungsNummer = new StringBuilder("GEN");
        buchungsNummer.append(buchung.getDatumInternational().substring(0,4));
        buchungsNummer.append(buchung.getDatumInternational().substring(5,7));
        buchungsNummer.append(buchung.getDatumInternational().substring(8));
        buchungsNummer.append(buchung.getEmpfaenger().substring(0,3).toUpperCase());
        buchungsNummer.append((int) buchung.getBetrag());
        buchung.setBuchungsNummer(buchungsNummer.toString());
        return buchung.getBuchungsNummer();
    }
}
