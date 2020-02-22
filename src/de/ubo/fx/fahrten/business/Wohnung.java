package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by ulrichbode on 04.12.16.
 */
@Entity(name = "Wohnung")
@Table(name = "bode_haus.wohnung")
public class Wohnung implements Persistable, Comparable<Wohnung> {

    @Basic
    @Column(name = "woh_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "woh_nummer")
    private String nummer;

    @Basic
    @Column(name = "woh_wohnflaeche")
    private String wohnflaeche;

    @Basic
    @Column(name = "woh_geschoss")
    private String geschoss;

    @Basic
    @Column(name = "woh_lage")
    private String lage;

    @Basic
    @Column(name = "woh_lageBeschreibung")
    private String lageBeschreibung;


    @ManyToOne
    @JoinColumn(name = "woh_hau_id")
    private Haus haus;

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public String getWohnflaeche() {
        return wohnflaeche;
    }

    public void setWohnflaeche(String wohnflaeche) {
        this.wohnflaeche = wohnflaeche;
    }

    public String getGeschoss() {
        return geschoss;
    }

    public void setGeschoss(String geschoss) {
        this.geschoss = geschoss;
    }

    public String getLage() {
        return lage;
    }

    public void setLage(String lage) {
        this.lage = lage;
    }

    public String getLageBeschreibung() {
        return lageBeschreibung;
    }

    public void setLageBeschreibung(String lageBeschreibung) {
        this.lageBeschreibung = lageBeschreibung;
    }

    public Haus getHaus() {
        return haus;
    }

    public void setHaus(Haus haus) {
        this.haus = haus;
    }

    @Override
    public String toString() {
        return getNummer() + " - " + getLageBeschreibung();
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
    public int compareTo(Wohnung wohnung) {
        return this.getNummer().compareTo(wohnung.getNummer());
    }
}
