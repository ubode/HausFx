package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by ulrichbode on 04.12.16.
 */
@Entity(name = "Zimmer")
@Table(name = "bode_haus.zimmer")
public class Zimmer implements Persistable, Comparable<Zimmer> {

    @Basic
    @Column(name = "zim_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Basic
    @Column(name = "zim_name")
    private String name;
    @Basic
    @Column(name = "zim_flaeche")
    private Double flaeche;
    @Basic
    @Column(name = "zim_renovierung")
    private Integer renovierung;
    @Basic
    @Column(name = "zim_bemerkung")
    private String bemerkung;
    @ManyToOne
    @JoinColumn(name = "zim_woh_id")
    private Wohnung wohnung;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFlaeche() {
        return flaeche;
    }

    public void setFlaeche(Double flaeche) {
        this.flaeche = flaeche;
    }

    public Integer getRenovierung() {
        return renovierung;
    }

    public void setRenovierung(Integer renovierung) {
        this.renovierung = renovierung;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Wohnung getWohnung() {
        return wohnung;
    }

    public void setWohnung(Wohnung wohnung) {
        this.wohnung = wohnung;
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
    public int compareTo(Zimmer zimmer) {
        return this.getName().compareTo(zimmer.getName());
    }
}
