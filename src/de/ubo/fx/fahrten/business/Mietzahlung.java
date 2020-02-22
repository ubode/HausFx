package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by ulric on 30.01.2017.
 */
public class Mietzahlung implements Persistable, Cloneable {

    @Basic
    @Column(name = "mzg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mzg_mvg_id")
    private MietVertrag mietVertrag;

    @ManyToOne
    @JoinColumn(name = "mzg_buc_id")
    private Buchung buchung;

    @Basic
    @Column(name = "mzg_kategorie")
    private ZahlungsKategorie zahlungsKategorie;

    @Basic
    @Column(name = "mzg_jahr")
    private Integer jahr;

    @Basic
    @Column(name = "mzg_betrag")
    private Double betrag;

    public Mietzahlung(MietVertrag mietVertrag, Buchung buchung, ZahlungsKategorie zahlungsKategorie, Integer jahr) {
        this.mietVertrag = mietVertrag;
        this.buchung = buchung;
        this.zahlungsKategorie = zahlungsKategorie;
        this.jahr = jahr;
        this.betrag = buchung.getBetrag();
    }

    public Mietzahlung() {
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public MietVertrag getMietVertrag() {
        return mietVertrag;
    }

    public void setMietVertrag(MietVertrag mietVertrag) {
        this.mietVertrag = mietVertrag;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
    }

    public ZahlungsKategorie getZahlungsKategorie() {
        return zahlungsKategorie;
    }

    public void setZahlungsKategorie(ZahlungsKategorie kahlungsKategorie) {
        this.zahlungsKategorie = kahlungsKategorie;
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
    public Mietzahlung clone() {
        Mietzahlung clone = new Mietzahlung(this.getMietVertrag(), this.getBuchung(), this.getZahlungsKategorie(), this.getJahr());
        clone.setBetrag(this.getBuchung().getBetrag() - this.getBetrag());
        return clone;
    }
}
