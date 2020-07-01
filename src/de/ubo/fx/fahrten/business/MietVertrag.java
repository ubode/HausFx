package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.helper.DatumHelper;
import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by ulric on 13.12.2016.
 */
public class MietVertrag implements Persistable {

    @Basic
    @Column(name = "mvg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mvg_per_id")
    private Person mieter;

    @ManyToOne
    @JoinColumn(name = "mvg_woh_id")
    private Wohnung wohnung;

    @Basic
    @Column(name = "mvg_beginn")
    private Date beginn;

    @Basic
    @Column(name = "mvg_ende")
    private Date ende;

    @Basic
    @Column(name = "mvg_mietzins")
    private double mietzins;

    @Basic
    @Column(name = "mvg_nebenkosten")
    private double nebenkosten;

    @Basic
    @Column(name = "mvg_heizkosten")
    private double heizkosten;

    @Basic
    @Column(name = "mvg_regexp")
    private String regularExpression;

    @Basic
    @Column(name = "mvg_kaution")
    private double kaution;

    private static String betragToString(double betrag) {
        DecimalFormat betragFormat = new DecimalFormat("###,##0.00");
        return betragFormat.format(betrag);
    }

    public static void berechneEndeDaten(List<MietVertrag> mietVertraege) {
        mietVertraege.sort(Comparator.comparing(MietVertrag::getBeginn));
        MietVertrag vorgaenger = null;
        for (MietVertrag vertrag: mietVertraege) {
            if (vorgaenger != null) {
                Calendar cal =  new GregorianCalendar();
                cal.setTime(vertrag.getBeginn());
                cal.add(Calendar.DATE, -1);
                Date ende = cal.getTime();
                vorgaenger.setEnde(ende);
                vertrag.setEnde(null);
            }
            vorgaenger = vertrag;
        }
    }

    public static String getBetragFormatiert(Double betrag) { return betragToString(betrag);}

    public double getKaution() {
        return kaution;
    }

    public void setKaution(double kaution) {
        this.kaution = kaution;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public Person getMieter() {
        return mieter;
    }

    public void setMieter(Person mieter) {
        this.mieter = mieter;
    }

    public Wohnung getWohnung() {
        return wohnung;
    }

    public void setWohnung(Wohnung wohnung) {
        this.wohnung = wohnung;
    }

    public Date getBeginn() {
        return beginn;
    }

    public void setBeginn(Date beginn) {
        this.beginn = beginn;
    }

    public Date getEnde() {
        return ende;
    }

    public void setEnde(Date ende) {
        this.ende = ende;
    }

    public double getMietzins() {
        return mietzins;
    }

    public void setMietzins(double mietzins) {
        this.mietzins = mietzins;
    }

    public double getNebenkosten() {
        return nebenkosten;
    }

    public void setNebenkosten(double nebenkosten) {
        this.nebenkosten = nebenkosten;
    }

    public double getHeizkosten() {
        return heizkosten;
    }

    public void setHeizkosten(double heizkosten) {
        this.heizkosten = heizkosten;
    }

    public double getGesamtkosten() {
        return getMietzins() + getNebenkosten() + getHeizkosten();
    }

    public boolean isGueltigAm(Date datum) {
        if (datum.before(getBeginn())) return false;
        if (getEnde() != null && datum.after(getEnde())) return false;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(DatumHelper.getDatumInternational(getBeginn()));
        if (getEnde() != null) {
            sb.append("  bis  ");
            sb.append(DatumHelper.getDatumInternational(getEnde()));
        }
        sb.append(" ");
        sb.append(getMieter().getName());
        sb.append(", ");
        sb.append(getMieter().getVorname());
        return sb.toString();
    }
}
