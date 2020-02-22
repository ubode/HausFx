package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ulric on 01.08.2016.
 */
public class Buchung implements Comparable, Persistable {
    private Long id;
    private String buchungsNummer;
    private Date datum;
    private String empfaenger;
    private double betrag;
    private String verwendung;
    private String kategorie;
    private String buchungsArt;
    private static DateFormat dateInternational = new SimpleDateFormat("yyyy-MM-dd");

    public Buchung() {
    }

    public Buchung(Long id, String buchungsNummer, Date datum, String empfaenger, double betrag, String verwendung, String kategorie, String buchungsArt) {
        this.id = id;
        this.buchungsNummer = buchungsNummer;
        this.datum = datum;
        this.empfaenger = empfaenger;
        this.betrag = betrag;
        this.verwendung = verwendung;
        this.kategorie = kategorie;
        this.buchungsArt = buchungsArt;
    }

    private static String dateToString(Date datum) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(datum);
    }

    private static String betragToString(double betrag) {
        DecimalFormat betragFormat = new DecimalFormat("###,##0.00");
        return betragFormat.format(betrag);
    }

    public String getBuchungsNummer() {
        return buchungsNummer;
    }

    public void setBuchungsNummer(String buchungsNummer) {
        this.buchungsNummer = buchungsNummer;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getEmpfaenger() {
        return empfaenger;
    }

    public void setEmpfaenger(String empfaenger) {
        this.empfaenger = empfaenger;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public String getVerwendung() {
        return verwendung;
    }

    public void setVerwendung(String verwendung) {
        this.verwendung = verwendung;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getBuchungsArt() {
        return buchungsArt;
    }

    public void setBuchungsArt(String buchungsArt) {
        this.buchungsArt = buchungsArt;
    }

    public String getDatumFormatiert() {
        return dateToString(getDatum());
    }

    public String getDatumInternational() {
        return dateInternational.format(getDatum());
    }

    public String getBetragFormatiert() { return betragToString(getBetrag());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        Buchung buchung = (Buchung) o;
        if (this.datum.equals(buchung.datum)) return this.buchungsNummer.compareTo(buchung.buchungsNummer);
        return this.datum.compareTo(buchung.datum);
    }
}
