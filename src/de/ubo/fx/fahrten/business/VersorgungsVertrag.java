package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by ulric on 13.12.2016.
 */
public class VersorgungsVertrag implements Persistable {

    @Basic
    @Column(name = "vvg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @JoinColumn(name = "vvg_nummer")
    private String vertragsNummer;

    @Basic
    @JoinColumn(name = "vvg_beginn")
    private Date vertragsBeginn;

    @Basic
    @JoinColumn(name = "vvg_ende")
    private Date vertragsEnde;

    @Basic
    @JoinColumn(name = "vvg_faelligkeit")
    private Faelligkeit faelligkeit;

    @Basic
    @JoinColumn(name = "vvg_betrag")
    private double betrag;

    @ManyToOne
    @JoinColumn(name = "vvg_hau_id")
    private Haus haus;

    private static String betragToString(double betrag) {
        DecimalFormat betragFormat = new DecimalFormat("###,##0.00");
        return betragFormat.format(betrag);
    }

    public static String getBetragFormatiert(Double betrag) { return betragToString(betrag);}

    public String getVertragsNummer() {
        return vertragsNummer;
    }

    public void setVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
    }

    public Date getVertragsBeginn() {
        return vertragsBeginn;
    }

    public void setVertragsBeginn(Date vertragsBeginn) {
        this.vertragsBeginn = vertragsBeginn;
    }

    public Date getVertragsEnde() {
        return vertragsEnde;
    }

    public void setVertragsEnde(Date vertragsEnde) {
        this.vertragsEnde = vertragsEnde;
    }

    public Faelligkeit getFaelligkeit() {
        return faelligkeit;
    }

    public void setFaelligkeit(Faelligkeit faelligkeit) {
        this.faelligkeit = faelligkeit;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public Haus getHaus() {
        return haus;
    }

    public void setHaus(Haus haus) {
        this.haus = haus;
    }

    public boolean isGueltigAm(Date datum) {
        if (datum.before(getVertragsBeginn())) return false;
        if (getVertragsEnde() != null && datum.after(getVertragsEnde())) return false;
        return true;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

private enum Faelligkeit {TAG, WOCHE, MONAT, QUARTAL, JAHR}
}
