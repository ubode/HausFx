package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import java.io.Serializable;

/**
 * Created by Bode on 22.02.2016.
 */
public class Adresse implements Persistable, Serializable {
    private Long id;
    private String postleitzahl;
    private String ort;
    private String strasse;
    private String hausnummer;
    private String land;
    private String kuerzel;

    public Adresse() {
    }

    public Adresse(Long id) {
        this.id = id;
    }

    public Adresse(Long id, String postleitzahl, String ort, String strasse, String hausnummer, String land, String kuerzel) {
        this.id = id;
        this.postleitzahl = postleitzahl;
        this.ort = ort;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.land = land;
        this.kuerzel = kuerzel;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public void setKuerzel(String kuerzel) {
        this.kuerzel = kuerzel;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Adresse)) return false;
        Adresse otherAdresse = (Adresse) other;
        if (!otherAdresse.getPostleitzahl().equals(this.getPostleitzahl())) return false;
        if (!otherAdresse.getOrt().equals(this.getOrt())) return false;
        if (!otherAdresse.getStrasse().equals(this.getStrasse())) return false;
        if (!otherAdresse.getHausnummer().equals(this.getHausnummer())) return false;
        return true;
    }
}
