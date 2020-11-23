package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by Bode on 08.03.2016.
 * Entfernung zwischen zwei Zielen in Kilometern (ganzzahlig).
 */
public class Entfernung implements Persistable {

    @Basic
    @Column(name = "ent_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "ent_zie_id_1")
    private Ziel startZiel;

    @OneToOne
    @JoinColumn(name = "ent_zie_id_2")
    private Ziel endZiel;

    @Basic
    @Column(name = "ent_entfernung")
    private int entfernung;

    public Entfernung() {
    }

    public Entfernung(Ziel startZiel, Ziel endZiel, int entfernung) {
        this.id = null;
        this.startZiel = startZiel;
        this.endZiel = endZiel;
        this.entfernung = entfernung;
    }

    public Ziel getStartZiel() {
        return startZiel;
    }

    public void setStartZiel(Ziel startZiel) {
        this.startZiel = startZiel;
    }

    public Ziel getEndZiel() {
        return endZiel;
    }

    public void setEndZiel(Ziel endZiel) {
        this.endZiel = endZiel;
    }

    public int getEntfernung() {
        return entfernung;
    }

    public void setEntfernung(int entfernung) {
        this.entfernung = entfernung;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
