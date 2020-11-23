package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by ulrichbode on 16.02.16.
 */
public class Anlass implements Comparable, Persistable {

    @Basic
    @Column(name = "anl_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "anl_position")
    private Integer position;

    @Basic
    @Column(name = "anl_name")
    private String name;

    @Basic
    @Column(name = "anl_text")
    private String text;

    @Basic
    @Column(name = "anl_kurztext")
    private String kurzText;

    public Anlass() {
    }

    public Anlass(Long id, Integer position, String name, String text, String kurzText) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.text = text;
        this.kurzText = kurzText;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKurzText() {
        return kurzText;
    }

    public void setKurzText(String kurzText) {
        this.kurzText = kurzText;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Anlass) {
            return this.getPosition().compareTo(((Anlass) o).getPosition());
        }
        return 0;
    }
}
