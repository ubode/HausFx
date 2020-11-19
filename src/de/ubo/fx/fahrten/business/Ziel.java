package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;

/**
 * Created by Bode on 22.02.2016.
 */
public class Ziel implements Comparable, Persistable {

    @Basic
    @Column(name = "zie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "zie_position")
    private Integer position;

    @Basic
    @Column(name = "zie_name")
    private String name;

    @Basic
    @Column(name = "zie_text")
    private String text;

    @Basic
    @Column(name = "zie_kurztext")
    private String kurzText;

    @Basic
    @Column(name = "zie_ort")
    private String ort;

    @Basic
    @Column(name = "zie_anlass_default")
    private Long anlassId;

    public Ziel() {
    }
    public Ziel(Long id) {
        this.id = id;
    }

    public String getOrt() {
        if (ort == null) {
            return text;
        }
        return ort;
    }
    
    public void setOrt(String ort) {
        this.ort = ort;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKurzText() {
        return kurzText;
    }

    public void setKurzText(String kurzText) {
        this.kurzText = kurzText;
    }

    public Long getAnlassId() {
        return anlassId;
    }

    public void setAnlassId(Long anlassId) {
        this.anlassId = anlassId;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Ziel) {
            return this.getPosition().compareTo(((Ziel) o).getPosition());
        }
        return 0;
    }
}
