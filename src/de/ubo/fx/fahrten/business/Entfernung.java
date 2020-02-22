package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

/**
 * Created by Bode on 08.03.2016.
 * Entfernung zwischen zwei Zielen in Kilometern (ganzzahlig).
 */
public class Entfernung implements Persistable {
    private Long id;
    private Ziel startZiel;
    private Ziel endZiel;
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
