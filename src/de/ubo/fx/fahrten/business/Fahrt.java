package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Bode on 28.01.2016.
 */
public class Fahrt implements Persistable {

    @Basic
    @Column(name = "fab_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "fab_datum")
    private Date datum;

    @Basic
    @Column(name = "fab_route")
    private String route;

    @Basic
    @Column(name = "fab_anlass")
    private String anlass;

    @Basic
    @Column(name = "fab_km")
    private int strecke;

    public Fahrt() {
    }

    public Fahrt(Long id, Date datum, String route, String anlass, int strecke) {
        this.id = id;
        this.datum = datum;
        this.route = route;
        this.anlass = anlass;
        this.strecke = strecke;
    }

    public Fahrt(Long id, String datum, String route, String anlass, int strecke) {
        this(id, stringToDate(datum), route, anlass, strecke);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getDatum() {
        return datum;
    }

    public String getDatumFormatiert() {
        return dateToString(getDatum());
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public void setDatum(String datum) {
        this.datum = stringToDate(datum);
    }

    private static Date stringToDate(String datumStr) {
        int year = Integer.valueOf(datumStr.substring(6));
        int month = Integer.valueOf(datumStr.substring(3,5));
        int day = Integer.valueOf(datumStr.substring(0,2));
        long millis = new GregorianCalendar(year, month - 1,day).getTimeInMillis();
        return new Date(millis);
    }

    private static String dateToString(Date datum) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(datum);
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAnlass() {
        return anlass;
    }

    public void setAnlass(String anlass) {
        this.anlass = anlass;
    }

    public int getStrecke() {
        return strecke;
    }

    public void setStrecke(int strecke) {
        this.strecke = strecke;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}

