package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DecimalFormat;

/**
 * Created by ulric on 09.08.2016.
 */
@Entity(name = "AusgabePosition")
@Table(name = "bode_haus.ausgabeposition")
public class AusgabePosition implements Persistable {
    private static DecimalFormat betragFormat = new DecimalFormat("###,##0.00");

    @Basic
    @Column(name = "apo_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "apo_positionsnummer")
    private int positionsNummer;

    @Basic
    @Column(name = "apo_ausgabenart")
    private AusgabeArt ausgabeArt;
    @Basic
    @Column(name = "apo_nebenkosten")
    private String nebenkosten;
    @Basic
    @Column(name = "apo_ausgabenposition")
    private String beschreibung;
    @Basic
    @Column(name = "apo_betrag")
    private double betrag;
    @ManyToOne
    @JoinColumn(name="apo_aus_id")
    private Ausgabe ausgabe;

    public AusgabePosition() {
    }

    public AusgabePosition(Ausgabe ausgabe) {
        this.ausgabe = ausgabe;
    }

    public String getNebenkosten() {
        return nebenkosten;
    }

    public void setNebenkosten(String nebenkosten) {
        this.nebenkosten = nebenkosten;
    }

    public Ausgabe getAusgabe() {
        return ausgabe;
    }

    public void setAusgabe(Ausgabe ausgabe) {
        this.ausgabe = ausgabe;
    }

    public int getPositionsNummer() {
        return positionsNummer;
    }

    public void setPositionsNummer(int positionsNummer) {
        this.positionsNummer = positionsNummer;
    }

    public AusgabeArt getAusgabeArt() {
        return ausgabeArt;
    }

    public void setAusgabeArt(AusgabeArt ausgabeArt) {
        this.ausgabeArt = ausgabeArt;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id=id;
    }

    public String getBetragFormatiert() {
        return betragFormat.format(getBetrag());
    }

    @Override
    public AusgabePosition clone() {
        AusgabePosition clone = new AusgabePosition();
        clone.setAusgabe(this.getAusgabe());
        clone.setAusgabeArt(this.getAusgabeArt());
        clone.setBeschreibung(this.getBeschreibung());
        clone.setBetrag(this.getBetrag());
        clone.setNebenkosten(this.getNebenkosten());
        clone.setPositionsNummer(this.getPositionsNummer() + 1);
        return clone;
    }
}
