package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by Bode on 22.02.2016.
 */
public class Haus implements Persistable {
    @Basic
    @Column(name = "hau_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "hau_name")
    private String name;

    @Basic
    @Column(name = "hau_kurzname")
    private String kurzname;

    @ManyToOne
    @JoinColumn(name = "hau_adr_id")
    private Adresse adresse;

    @ManyToOne
    @JoinColumn(name = "hau_per_id")
    private Person hausmeister;

    @Basic
    @Column(name = "hau_baujahr")
    private int baujahr;

    @Basic
    @Column(name = "hau_regex")
    private String regex;

    public Haus() {
    }

    public Haus(Long id) {
        this.id = id;
    }

    public int getBaujahr() {
        return baujahr;
    }

    public void setBaujahr(int baujahr) {
        this.baujahr = baujahr;
    }

    public Person getHausmeister() {
        return hausmeister;
    }

    public void setHausmeister(Person hausmeister) {
        this.hausmeister = hausmeister;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKurzname() {
        return kurzname;
    }

    public void setKurzname(String kurzname) {
        this.kurzname = kurzname;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getRegex() { return regex; }

    public void setRegex(String regex) { this.regex = regex; }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


}
