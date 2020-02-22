package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ulric on 08.08.2016.
 */
@Entity(name = "Ausgabe")
@Table(name = "bode_haus.ausgabe")
public class Ausgabe implements Persistable {

    private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static DateFormat dateInternational = new SimpleDateFormat("yyyy-MM-dd");
    private static DecimalFormat betragFormat = new DecimalFormat("###,##0.00");

    @Basic
    @Column(name = "aus_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "aus_empfaenger")
    private String empfaenger;

    @Basic
    @Column(name = "aus_referenz_nr")
    private String referenz;

    @Basic
    @Column(name = "aus_datum")
    private Date datum;

    @Basic
    @Column(name = "aus_vertrags_nr")
    private String vertragsNummer;

    @ManyToOne
    @JoinColumn(name = "aus_buc_id")
    private Buchung buchung;

    @ManyToOne
    @JoinColumn(name = "aus_hau_id", nullable = false)
    private Haus haus;

    @OneToMany(mappedBy = "ausgabe", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = AusgabePosition.class)
    private final List<AusgabePosition> positionen;

    //Constructor
    public Ausgabe() {
        this.positionen = new ArrayList<>(5);
    }

    public String getVertragsNummer() {
        return vertragsNummer;
    }

    public void setVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
    }

    public List<AusgabePosition> getPositionen() {
        return positionen;
    }

    public void addPosition(AusgabePosition position) {
        this.positionen.add(position);
        position.setAusgabe(this);
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpfaenger() {
        return empfaenger;
    }

    public void setEmpfaenger(String empfaenger) {
        this.empfaenger = empfaenger;
    }

    public String getReferenz() {
        return referenz;
    }

    public void setReferenz(String referenz) {
        this.referenz = referenz;
    }

    public Haus getHaus() {
        return haus;
    }

    public void setHaus(Haus haus) {
        this.haus = haus;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
    }

    public String getDatumFormatiert() {
        return dateFormat.format(getDatum());
    }

    public String getDatumInternational() {
        return dateInternational.format(getDatum());
    }

    public String getBetragFormatiert() {
        return betragFormat.format(getBetrag());
    }

    public double getBetrag() {
        double summe = 0.0d;
        for (AusgabePosition ausgabePosition : getPositionen()) {
            summe += ausgabePosition.getBetrag();
        }
        return summe;
    }
}
